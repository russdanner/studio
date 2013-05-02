/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.impl.repository.alfresco;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.deployment.DeploymentEndpointConfigTO;
import org.craftercms.cstudio.alfresco.deployment.DeploymentEngineConstants;
import org.craftercms.cstudio.alfresco.deployment.DeploymentEventItem;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.filter.DmFilterWrapper;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.event.EventService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.*;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.PublishingChannelConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelGroupConfigTO;
import org.craftercms.cstudio.api.log.Logger;
import org.craftercms.cstudio.api.log.LoggerFactory;
import org.craftercms.cstudio.api.service.deployment.PublishingTargetItem;
import org.craftercms.cstudio.api.service.fsm.TransitionEvent;
import org.craftercms.cstudio.impl.repository.AbstractContentRepository;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Alfresco repository implementation.  This is the only point of contact with Alfresco's API in
 * the entire system under the org.craftercms.cstudio.impl package structure
 * @author russdanner
 *
 */

public class AlfrescoContentRepository extends AbstractContentRepository {

	protected static final String MSG_ERROR_RUN_AS_FAILED = "err_alfresco_run_as_failure";
	protected static final String MSG_NODE_REF_IS_NULL_FOR_PATH = "alfresco_noderef_null_for_path";
	
	private static final Logger logger = LoggerFactory.getLogger(AlfrescoContentRepository.class);

	/**
	 * perform operation as a specific user
	 * @param userName the name of the user account performing the operation
	 * @param obj the object that contains the method to executre
	 * @param work the method that represents the work to perform
	 * @param args any number of arguments to pass to the method
	 */
    private static final String SITE_REPO_ROOT_PATTERN = "/wem-projects/{site}/{site}/work-area";
    private static final String SITE_ENVIRONMENT_ROOT_PATTERN = "/wem-projects/{site}/{site}/{environment}";
    private static final String SITE_REPLACEMENT_PATTERN = "\\{site\\}";
    private static final String ENVIRONMENT_REPLACEMENT_PATTERN = "\\{environment\\}";
    private static final String WORK_AREA_REPOSITORY = "work-area";
    private static final String LIVE_REPOSITORY = "live";

	public Object runAs(final String userName, final Object obj, final Method work, final Object ... args) {
		
		Object retObject = null;
		
		// need to check params for nulls
		
		try {
			retObject = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
				public Object doWork() {
					Object retValue = null;
					
					try {
						retValue =  work.invoke(obj, null);
					}
					catch(Exception err) {
						logger.error(MSG_ERROR_RUN_AS_FAILED, err, userName, obj.getClass().getName());
					}
					
					return retValue;
		    	}
			}, userName);
		}
		catch(Exception err) {
			logger.error(MSG_ERROR_RUN_AS_FAILED, err, userName, obj.getClass().getName());
		}
		
		return retObject;
	}
	
	/** 
	 * get transaction
	 */
	public UserTransaction getTransaction() {
		return _transactionService.getUserTransaction();
	}

	/**
	 * @return true if site has content object at path
	 */
	public boolean contentExists(String site, String path) {
	    
		return _dmContentService.contentExists(site, path, path);
	}

    /**
     * get document from wcm content
     *
     * @param path
     * @return document
     * @throws ServiceException
     */
    public InputStream getContent(String path) {
    	InputStream retStream = null;
    	PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
	    NodeRef nodeRef = persistenceManagerService.getNodeRef(path);

	    if (nodeRef != null) {
	    	ContentReader reader = persistenceManagerService.getReader(nodeRef);
	    	retStream = reader.getContentInputStream();
	    }
	    else {
	    	logger.info(MSG_NODE_REF_IS_NULL_FOR_PATH, path);
	    }

	    return retStream;
	}

    public void writeContent(String path, InputStream content) {
    	PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
    	//NodeRef nodeRef = persistenceManagerService.getNodeRef(path);
    	 
    	//if (nodeRef != null) {
 	    	ContentWriter writer = persistenceManagerService.getWriter(path);
 	    	writer.putContent(content);
 	    //}
    }

    @Override
    public void stateTransition(String site, List<String> paths, TransitionEvent event) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        List<String> objectIds = new ArrayList<String>();
        String rootPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        for (String relativePath : paths) {
            NodeRef nodeRef = persistenceManagerService.getNodeRef(rootPath, relativePath);
            if (nodeRef != null) {
                objectIds.add(nodeRef.getId());
            }
        }
        ObjectStateService.TransitionEvent convertedEvent = eventConversionMap.get(event);
        ObjectStateService.State defaultState = defaultStateForEvent.get(event);
        persistenceManagerService.transitionBulk(objectIds, convertedEvent, defaultState);
    }

    @Override
    public void stateTransition(String site, String path, TransitionEvent event) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        List<String> objectIds = new ArrayList<String>();
        String rootPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(rootPath, path);
        if (nodeRef != null) {
            ObjectStateService.TransitionEvent convertedEvent = eventConversionMap.get(event);
            persistenceManagerService.transition(nodeRef, convertedEvent);
        }


    }

    private final Map<TransitionEvent, ObjectStateService.TransitionEvent> eventConversionMap = new HashMap<TransitionEvent, ObjectStateService.TransitionEvent>() {{
            put(TransitionEvent.SCHEDULED_DEPLOYMENT, ObjectStateService.TransitionEvent.SUBMIT_WITHOUT_WORKFLOW_SCHEDULED);
            put(TransitionEvent.DEPLOYMENT, ObjectStateService.TransitionEvent.DEPLOYMENT);
    }};

    private final Map<TransitionEvent, ObjectStateService.State> defaultStateForEvent = new HashMap<TransitionEvent, ObjectStateService.State>() {{
            put(TransitionEvent.SCHEDULED_DEPLOYMENT, ObjectStateService.State.NEW_SUBMITTED_NO_WF_SCHEDULED);
            put(TransitionEvent.DEPLOYMENT, ObjectStateService.State.EXISTING_UNEDITED_UNLOCKED);
    }};

    @Override
    public void setSystemProcessing(String site, List<String> paths, boolean isSystemProcessing) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        List<String> objectIds = new ArrayList<String>();
        String rootPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        for (String relativePath : paths) {
            NodeRef nodeRef = persistenceManagerService.getNodeRef(rootPath, relativePath);
            if (nodeRef != null) {
                objectIds.add(nodeRef.getId());
            }
        }
        persistenceManagerService.setSystemProcessingBulk(objectIds, isSystemProcessing);
    }

    @Override
    public void setSystemProcessing(String site, String path, boolean isSystemProcessing) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        String rootPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(rootPath, path);
        if (nodeRef != null) {
            persistenceManagerService.setSystemProcessing(nodeRef, isSystemProcessing);
        }

    }

    @Override
    public void createNewVersion(String site, String path, String submissionComment, boolean isMajorVersion) {
        DmVersionService dmVersionService = _servicesManager.getService(DmVersionService.class);
        if (isMajorVersion) {
            dmVersionService.createNextMajorVersion(site, path, submissionComment);
        } else {
            dmVersionService.createNextMinorVersion(site, path);
        }
    }

    @Override
    public void copyToEnvironment(String site, String environment, String path) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String siteRepoRootPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);

        String envRepoPath = SITE_ENVIRONMENT_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        envRepoPath = envRepoPath.replaceAll(ENVIRONMENT_REPLACEMENT_PATTERN, environment);

        NodeRef envRepoRoot = persistenceManagerService.getNodeRef(envRepoPath);
        if (envRepoRoot == null) {
            envRepoRoot = createEnvRepository(site, environment);
        }
        NodeRef envNode = persistenceManagerService.getNodeRef(envRepoRoot, path);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(siteRepoRootPath, path);
        if (envNode == null) {
            envNode = createLiveRepositoryCopy(envRepoRoot, path, nodeRef);
        } else {
            persistenceManagerService.copy(nodeRef, envNode);
        }
        Map<QName, Serializable> nodeProps = persistenceManagerService.getProperties(envNode);
        for (QName propName : DmConstants.SUBMITTED_PROPERTIES) {
            nodeProps.remove(propName);
        }
        persistenceManagerService.setProperties(envNode, nodeProps);
    }

    protected NodeRef createEnvRepository(String site, String envRepoName) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String siteRepoPath = servicesConfig.getRepositoryRootPath(site);
        NodeRef siteRepoRoot = persistenceManagerService.getNodeRef(siteRepoPath);
        NodeRef result = persistenceManagerService.createNewFolder(siteRepoRoot, envRepoName);
        return result;
    }

    protected NodeRef createLiveRepositoryCopy(NodeRef liveRepoRoot, String relativePath, NodeRef nodeRef) {
        logger.debug("[PUBLISHING POST PROCESSOR] creating live repository copy of " + relativePath);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        NodeRef result = null;

        String[] pathSegments = relativePath.split("/");
        NodeRef helperNode = liveRepoRoot;
        NodeRef parent = null;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if (!"".equals(pathSegments[i])) {
                parent = helperNode;
                helperNode = persistenceManagerService.getChildByName(helperNode, ContentModel.ASSOC_CONTAINS, pathSegments[i]);
                if (helperNode == null) {
                    logger.debug("[WORKFLOW] creating a node with name: " + pathSegments[i]);
                    Map<QName, Serializable> properties = new FastMap<QName, Serializable>();
                    properties.put(ContentModel.PROP_NAME, pathSegments[i]);
                    helperNode = persistenceManagerService.createNewFolder(parent, pathSegments[i], properties);
                }
            }
        }
        String nodeName = (String) persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName assocQName = QName.createQName(ContentModel.TYPE_CONTENT.getNamespaceURI(), QName.createValidLocalName(nodeName));
        result = persistenceManagerService.copy(nodeRef, helperNode, ContentModel.ASSOC_CONTAINS, assocQName);
        persistenceManagerService.setProperty(result, ContentModel.PROP_NAME, nodeName);
        return result;
    }

    @Override
    public Set<String> getAllAvailableSites() {
        ServicesConfig servicesConfig = _servicesManager.getService(ServicesConfig.class);
        return servicesConfig.getAllAvailableSites();
    }

    @Override
    public Set<PublishingTargetItem> getAllTargetsForSite(String site) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        SiteService siteService = _servicesManager.getService(SiteService.class);
        Map<String, PublishingChannelGroupConfigTO> groupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        Set<PublishingTargetItem> targets = new HashSet<PublishingTargetItem>();
        if (groupConfigTOs != null && groupConfigTOs.size() > 0) {
            for (PublishingChannelGroupConfigTO groupConfigTO : groupConfigTOs.values()) {
                List<PublishingChannelConfigTO> channelConfigTOs = groupConfigTO.getChannels();
                if (channelConfigTOs != null && channelConfigTOs.size() > 0) {
                    for (PublishingChannelConfigTO channelConfigTO : channelConfigTOs) {
                        DeploymentEndpointConfigTO endpoint = siteService.getDeploymentEndpoint(site, channelConfigTO.getName());
                        if (endpoint != null) {
                            PublishingTargetItem targetItem = new PublishingTargetItem();
                            targetItem.setId(endpoint.getName());
                            targetItem.setName(endpoint.getName());
                            targetItem.setTarget(endpoint.getTarget());
                            targetItem.setType(endpoint.getType());
                            targetItem.setServerUrl(endpoint.getServerUrl());
                            targetItem.setVersionUrl(endpoint.getVersionUrl());
                            targetItem.setStatusUrl(endpoint.getStatusUrl());
                            targetItem.setPassword(endpoint.getPassword());
                            targetItem.setExcludePattern(endpoint.getExcludePattern());
                            targetItem.setIncludePattern(endpoint.getIncludePattern());
                            targetItem.setBucketSize(endpoint.getBucketSize());
                            targets.add(targetItem);
                        }
                    }
                }
            }
        }
        return targets;
    }

    @Override
    public String getCurrentUser() {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        return persistenceManagerService.getCurrentUserName();
    }

    @Override
    public boolean isNew(String site, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        String fullPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site) + path;
        return persistenceManagerService.isNew(fullPath);
    }

    @Override
    public String getFilename(String site, String path) {
        if (path != null && !path.isEmpty()) {
            PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
            NodeRef nodeRef = persistenceManagerService.getNodeRef(SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site), path);
            if (nodeRef != null) {
                return DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME));
            } else {
                int idx = path.lastIndexOf("/");
                if (idx > 0) {
                    return path.substring(idx + 1);
                } else {
                    return path;
                }
            }
        } else {
            return "";
        }
    }

    @Override
    public boolean isRenamed(String site, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site), path);
        if (nodeRef != null) {
            return persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED);
        } else {
            return false;
        }
    }

    @Override
    public String getOldPath(String site, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site), path);
        if (nodeRef != null) {
            if (persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED)) {
                String oldPath = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, CStudioContentModel.PROP_RENAMED_OLD_URL));
                return oldPath;
            }
        }
        return null;
    }

    @Override
    public InputStream getMetadataStream(String site, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site), path);
        Map<QName, Serializable> contentProperties = persistenceManagerService.getProperties(nodeRef);
        Document metadataDoc = DocumentHelper.createDocument();
        Element root = metadataDoc.addElement("metadata");
        for (Map.Entry<QName, Serializable> property : contentProperties.entrySet()) {
            Element elem = root.addElement(property.getKey().getLocalName());
            elem.addText(String.valueOf(property.getValue()));
        }

        return IOUtils.toInputStream(metadataDoc.asXML());
    }

    public void publishDeployEvent(String endpoint, List<DeploymentEventItem> items) {
        EventService eventService = _servicesManager.getService(EventService.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endpoint", endpoint);
        jsonObject.put("items", items);
        eventService.publish(DeploymentEngineConstants.EVENT_DEPLOYMENT_ENGINE_DEPLOY, jsonObject);
    }

    @Override
    public void deleteContent(String site, String environment, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        String fullPath = SITE_ENVIRONMENT_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        fullPath = fullPath.replaceAll(ENVIRONMENT_REPLACEMENT_PATTERN, environment);
        fullPath = fullPath + path;
        NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
        if (nodeRef != null) {
            NodeRef parentNode = persistenceManagerService.getPrimaryParent(nodeRef).getParentRef();
            persistenceManagerService.deleteNode(nodeRef);
            List<FileInfo> children = persistenceManagerService.list(parentNode);
            while ( children == null || children.size() < 1) {
                NodeRef helpNode = parentNode;
                parentNode = persistenceManagerService.getPrimaryParent(helpNode).getParentRef();
                persistenceManagerService.deleteNode(helpNode);
                children = persistenceManagerService.list(parentNode);
            }
        }
    }

    @Override
    public void deleteContent(String site, String path) {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        String fullPath = SITE_ENVIRONMENT_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        fullPath = fullPath.replaceAll(ENVIRONMENT_REPLACEMENT_PATTERN, WORK_AREA_REPOSITORY);
        fullPath = fullPath + path;
        NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
        if (nodeRef != null) {
            try {
                _dmContentService.deleteContent(site, path, true, true, null);
                return;
            } catch (ServiceException e) {
                logger.debug("Failed to delete content at path: " + path + " site " + site);
            }
            NodeRef parentNode = persistenceManagerService.getPrimaryParent(nodeRef).getParentRef();
            persistenceManagerService.deleteNode(nodeRef);
            List<FileInfo> children = persistenceManagerService.list(parentNode);
            while ( children == null || children.size() < 1) {
                NodeRef helpNode = parentNode;
                parentNode = persistenceManagerService.getPrimaryParent(helpNode).getParentRef();
                persistenceManagerService.deleteNode(helpNode);
                children = persistenceManagerService.list(parentNode);
            }
        }
    }

    @Override
    public void clearRenamed(String site, String path) {
        String fullPath = SITE_REPO_ROOT_PATTERN.replaceAll(SITE_REPLACEMENT_PATTERN, site);
        fullPath = fullPath + path;
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
        if (nodeRef != null) {
            persistenceManagerService.removeAspect(fullPath, CStudioContentModel.ASPECT_RENAMED);
            Map<QName, Serializable> props = persistenceManagerService.getProperties(nodeRef);
            props.remove(CStudioContentModel.PROP_RENAMED_OLD_URL);
            persistenceManagerService.setProperties(nodeRef, props);
        }
    }

    @Override
    public String getContentTypeClass(String site,  String path) {
        if (_dmFilterWrapper.accept(site, path, DmConstants.CONTENT_TYPE_COMPONENT)) {
            return DmConstants.CONTENT_TYPE_COMPONENT;
        }else if  (_dmFilterWrapper.accept(site, path, DmConstants.CONTENT_TYPE_ASSET)){
            return DmConstants.CONTENT_TYPE_ASSET;
        }else if  (_dmFilterWrapper.accept(site, path, DmConstants.CONTENT_TYPE_RENDERING_TEMPLATE)){
            return DmConstants.CONTENT_TYPE_RENDERING_TEMPLATE;
        }else if  (_dmFilterWrapper.accept(site, path, DmConstants.CONTENT_TYPE_PAGE)){
            return DmConstants.CONTENT_TYPE_PAGE;
        }else if  (_dmFilterWrapper.accept(site, path, DmConstants.CONTENT_TYPE_DOCUMENT)){
            return DmConstants.CONTENT_TYPE_DOCUMENT;
        } else {
            return "";
        }
    }

    /** dmContentService getter */
	public DmContentService getDmContentService() { return _dmContentService; }
	/** dmContentService setter */
	public void setDmContentService(DmContentService service) { _dmContentService = service; }

    public ServicesManager getServicesManager() { return _servicesManager; }
    public void setServicesManager(ServicesManager servicesManager) {  this._servicesManager = servicesManager; }

    public org.alfresco.service.transaction.TransactionService getTransactionService() { return _transactionService; }
    public void setTransactionService(org.alfresco.service.transaction.TransactionService transactionService) { _transactionService = transactionService; }

    public DmFilterWrapper getDmFilterWrapper() { return _dmFilterWrapper; }
    public void setDmFilterWrapper(DmFilterWrapper dmFilterWrapper) { this._dmFilterWrapper = dmFilterWrapper; }
    
    protected ServicesManager _servicesManager;
    protected DmContentService _dmContentService;
    protected org.alfresco.service.transaction.TransactionService _transactionService;
    protected DmFilterWrapper _dmFilterWrapper;
}
