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

package org.craftercms.cstudio.alfresco.publishing;

import javolution.util.FastMap;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.GeneralLockService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class CStudioCopyToLiveAreaPublishingPostProcessor implements CrafterCMSPublishingProcessor {

    protected Logger logger = LoggerFactory.getLogger(CStudioCopyToLiveAreaPublishingPostProcessor.class);

    protected ProcessorType _type = ProcessorType.POSTPROCESSOR;

    protected CrafterCMSRemoteDeploymentChannelType _channelType;
    public CrafterCMSRemoteDeploymentChannelType getChannelType() {
        return _channelType;
    }
    public void setChannelType(CrafterCMSRemoteDeploymentChannelType channelType) {
        this._channelType = channelType;
    }

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    @Override
    public void doProcess(Set<NodeRef> publishedNodes, boolean publish) {
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        GeneralLockService lockService = getServicesManager().getService(GeneralLockService.class);
        String fullPath;
        String site;
        if (publish) {
            for (NodeRef nodeRef : publishedNodes) {
                lockService.lock(nodeRef.getId());
                try {
                    fullPath = persistenceManagerService.getNodePath(nodeRef);
                    Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
                    if (m.matches()) {
                        site = m.group(2).length() != 0 ? m.group(2) : "";
                    } else {
                        site = "";
                    }
                    copyToLiveRepo(site, nodeRef);
                } finally {
                    lockService.unlock(nodeRef.getId());
                }
            }
        }
    }

    protected void copyToLiveRepo(String site, NodeRef nodeRef) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String liveRepoPath = servicesConfig.getLiveRepositoryPath(site);
        NodeRef liveRepoRoot = persistenceManagerService.getNodeRef(liveRepoPath);
        if (liveRepoRoot == null)
            liveRepoRoot = createLiveRepository(site, DmConstants.DM_LIVE_REPO_FOLDER);
        String fullPath = persistenceManagerService.getNodePath(nodeRef);
        Matcher m = DmConstants.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
        if (m.matches()) {
            String relativePath = m.group(4);
            NodeRef liveNode = persistenceManagerService.getNodeRef(liveRepoRoot, relativePath);
            if (liveNode == null) {
                liveNode = createLiveRepositoryCopy(liveRepoRoot, relativePath, nodeRef);
            } else {
                persistenceManagerService.copy(nodeRef, liveNode);
            }
        }
    }

    protected NodeRef createLiveRepository(String site, String liveRepoName) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String siteRepoPath = servicesConfig.getRepositoryRootPath(site);
        NodeRef siteRepoRoot = persistenceManagerService.getNodeRef(siteRepoPath);
        NodeRef result = persistenceManagerService.createNewFolder(siteRepoRoot, liveRepoName);
        return result;
    }

    protected NodeRef createLiveRepositoryCopy(NodeRef liveRepoRoot, String relativePath, NodeRef nodeRef) {
        if (logger.isDebugEnabled()) {
            logger.debug("[PUBLISHING POST PROCESSOR] creating live repository copy of " + relativePath);
        }
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        NodeRef result = null;

        String[] pathSegments = relativePath.split("/");
        NodeRef helperNode = liveRepoRoot;
        NodeRef parent = null;
        for(int i = 0; i < pathSegments.length - 1; i++) {
            if(!"".equals(pathSegments[i])) {
                parent = helperNode;
                helperNode = persistenceManagerService.getChildByName(helperNode, ContentModel.ASSOC_CONTAINS, pathSegments[i]);
                if (helperNode == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] creating a node with name: " + pathSegments[i]);
                    }
                    Map<QName, Serializable> properties = new FastMap<QName, Serializable>();
                    properties.put(ContentModel.PROP_NAME, pathSegments[i]);
                    helperNode = persistenceManagerService.createNewFolder(parent, pathSegments[i], properties);
                }
            }
        }
        String nodeName = (String)persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName assocQName = QName.createQName(ContentModel.TYPE_CONTENT.getNamespaceURI(), QName.createValidLocalName(nodeName));
        result = persistenceManagerService.copy(nodeRef, helperNode, ContentModel.ASSOC_CONTAINS, assocQName);
        persistenceManagerService.setProperty(result, ContentModel.PROP_NAME, nodeName);
        return result;
    }

    @Override
    public ProcessorType getProcessorType() {
        return this._type;
    }

    @Override
    public void register() {
        this._channelType.registerPostProcessor(this);
    }
}
