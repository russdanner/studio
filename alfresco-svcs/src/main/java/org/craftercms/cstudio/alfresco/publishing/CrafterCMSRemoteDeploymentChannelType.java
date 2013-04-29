/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.cstudio.alfresco.publishing;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.publishing.AbstractChannelType;
import org.alfresco.repo.publishing.PublishingEventHelper;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.publishing.PublishingEvent;
import org.alfresco.service.cmr.publishing.Status;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.craftercms.cstudio.alfresco.cache.cstudioCacheManager;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.deploymenthistory.DeploymentHistoryDAO;
import org.craftercms.cstudio.alfresco.deploymenthistory.DeploymentHistoryDaoService;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmTransactionService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.GeneralLockService;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.SiteService;
import org.craftercms.cstudio.alfresco.to.PublishingChannelConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelGroupConfigTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Crafter Studio Receiver channel type
 * @author Dejan Brkic
 */
public class CrafterCMSRemoteDeploymentChannelType extends AbstractChannelType {

    private static final Logger logger = LoggerFactory.getLogger(CrafterCMSRemoteDeploymentChannelType.class);

    public static String DEPLOYER_SERVLET_URL = "/publish";
    public static String DEPLOYER_STATUS_URL = "/api/1/monitoring/status";
    public static String DEPLOYER_PASSWORD_PARAM = "password";
    public static String DEPLOYER_TARGET_PARAM = "target";
    public static String DEPLOYER_SITE_PARAM = "siteId";
    public static String DEPLOYER_DELETED_FILES_PARAM = "deletedFiles";
    public static String DEPLOYER_CONTENT_LOCATION_PARAM = "contentLocation";
    public static String DEPLOYER_CONTENT_FILE_PARAM = "contentFile";
    public static String DEPLOYER_METADATA_FILE_PARAM = "metadataFile";
    public static String FILES_SEPARATOR = ",";

    protected List<CrafterCMSPublishingProcessor> _postProcessors;

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    protected cstudioCacheManager _cacheManager;
    public cstudioCacheManager getCacheManager() {
        return this._cacheManager;
    }
    public void setCacheManager(cstudioCacheManager cacheManager) {
        this._cacheManager = cacheManager;
    }

    protected PublishingEventHelper _publishingEventHelper;
    public PublishingEventHelper getPublishingEventHelper() {
        return _publishingEventHelper;
    }
    public void setPublishingEventHelper(PublishingEventHelper publishingEventHelper) {
        this._publishingEventHelper = publishingEventHelper;
    }

    protected CompanyHomeNodeLocator _companyHomeNodeLocator;
    public CompanyHomeNodeLocator getCompanyHomeNodeLocator() {
        return _companyHomeNodeLocator;
    }
    public void setCompanyHomeNodeLocator(CompanyHomeNodeLocator companyHomeNodeLocator) {
        this._companyHomeNodeLocator = companyHomeNodeLocator;
    }

    protected DeploymentHistoryDaoService _deploymentHistoryDaoService;
    public DeploymentHistoryDaoService getDeploymentHistoryDaoService() {
        return _deploymentHistoryDaoService;
    }
    public void setDeploymentHistoryDaoService(DeploymentHistoryDaoService deploymentHistoryDaoService) {
        this._deploymentHistoryDaoService = deploymentHistoryDaoService;
    }

    protected boolean logPublishedFiles = false;
    public boolean isLogPublishedFiles() {
        return logPublishedFiles;
    }
    public void setLogPublishedFiles(boolean logPublishedFiles) {
        this.logPublishedFiles = logPublishedFiles;
    }

    protected int bucketSize = 10;
    public int getBucketSize() {
        return bucketSize;
    }
    public void setBucketSize(int bucketSize) {
        this.bucketSize = bucketSize;
    }

    Map<String, DeployerRequestContainer> _deployerRequests = new FastMap<String, DeployerRequestContainer>();

    Map<String, Date> processedEvents = Collections.synchronizedMap(new TreeMap<String, Date>());
    Map<String, Integer> processedUnpublishedEvents = Collections.synchronizedMap(new TreeMap<String, Integer>());


    @Override
    public String getId() {
        return CrafterCMSPublishingModel.REMOTE_CHANNEL_TYPE_ID;
    }

    @Override
    public QName getChannelNodeType() {
        return CrafterCMSPublishingModel.TYPE_REMOTE_DELIVERY_CHANNEL;
    }

    @Override
    public boolean canPublish() {
        return true;
    }

    @Override
    public void publish(NodeRef nodeToPublish, Map<QName, Serializable> channelProperties) {
        GeneralLockService generalLockService = getServicesManager().getService(GeneralLockService.class);
        PublishingEvent event = getPublishingEvent(nodeToPublish);
        ByteArrayPartSource baps = null;
        PartSource metadataPart = null;
        ContentReader reader = null;
        InputStream input = null;
        InputStream metadataStream = null;
        generalLockService.lock(event.getId());
        try {
            if (!processedEvents.containsKey(event.getId())) {

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Get publishing channel properties for publishing event [%s], channel [%s]", event.getId(), event.getChannelId()));
                }
                String channelName = DefaultTypeConverter.INSTANCE.convert(String.class, channelProperties.get(ContentModel.PROP_NAME));
                String server = DefaultTypeConverter.INSTANCE.convert(String.class, channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_SERVER));
                int port = DefaultTypeConverter.INSTANCE.convert(Integer.class, channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PORT));
                String password = DefaultTypeConverter.INSTANCE.convert(String.class, channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PASSWORD));
                String target = DefaultTypeConverter.INSTANCE.convert(String.class, channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_TARGET));
                boolean publishMetadata = DefaultTypeConverter.INSTANCE.convert(Boolean.class, channelProperties.get(CrafterCMSPublishingModel.PROP_PUBLISH_METADATA));
                String siteId = "";

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Check deployment agent status for publishing event [%s], channel [%s]", event.getId(), event.getChannelId()));
                }
                URL statusUrl = null;
                try {
                    statusUrl = new URL("http", server, port, DEPLOYER_STATUS_URL);
                } catch (MalformedURLException e) {
                    logger.error(String.format("Invalid endpoint status URL for publishing channel [%s]", channelName), e);
                    signalFailure(event);
                    throw new RuntimeException(String.format("Publish failed: Invalid endpoint URL for publishing channel [%s]", channelName), e);
                }
                if (!checkDeployingAgentStatus(statusUrl.toString())) {
                    logger.error(String.format("Publishing Channel [%s]: Endpoint is not available. Status check failed for url %s", channelName, statusUrl.toString()));
                    signalFailure(event);
                    throw new RuntimeException(String.format("Publishing Channel [%s] - Publish failed: Endpoint is not available. Status check failed for url %s", channelName, statusUrl.toString()));
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Calculate deployment agent's publish URL for publishing event [%s], channel [%s]", event.getId(), event.getChannelId()));
                }
                URL requestUrl = null;
                try {
                    requestUrl = new URL("http", server, port, DEPLOYER_SERVLET_URL);
                } catch (MalformedURLException e) {
                    logger.error(String.format("Invalid endpoint URL for publishing channel [%s]", channelName), e);
                    signalFailure(event);
                    throw new RuntimeException(String.format("Publish failed: Invalid endpoint URL for publishing channel [%s]", channelName), e);
                }

                Set<NodeRef> nodesToPublish = event.getPackage().getNodesToPublish();
                PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);

                int numberOfBuckets = nodesToPublish.size() / bucketSize + 1;
                Iterator<NodeRef> iter = nodesToPublish.iterator();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Divide all content items into [%d] bucket(s) for publishing event [%s], channel [%s]", numberOfBuckets, event.getId(), event.getChannelId()));
                }
                for (int bucketIndex = 0; bucketIndex < numberOfBuckets; bucketIndex++) {
                    StringBuilder sbDeletedFiles = new StringBuilder();
                    List<Part> formParts = new FastList<Part>();
                    formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, password));
                    formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, target));

                    int cntNodes = 0;
                    for (int j = 0; j < bucketSize; j++) {
                        if (iter.hasNext()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Parse next content item and add it into bucket #%d for publishing event [%s], channel [%s]", bucketIndex, event.getId(), event.getChannelId()));
                            }
                            NodeRef nodeRef = iter.next();

                            String fullPath = persistenceManagerService.getNodePath(nodeRef);
                            String fileName = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME));
                            String relativePath = fullPath;
                            Matcher m = DmConstants.DM_REPO_PATH_PATTERN.matcher(fullPath);
                            if (m.matches()) {
                                siteId = m.group(3).length() != 0 ? m.group(3) : "/";
                                relativePath = m.group(5).length() != 0 ? m.group(5) : "/";
                                if (!StringUtils.startsWith(relativePath, "/")) {
                                    relativePath = "/" + relativePath;
                                }
                            }
                            reader = persistenceManagerService.getReader(nodeRef, ContentModel.PROP_CONTENT);

                            input = reader.getContentInputStream();
                            byte[] byteArray = null;

                            try {
                                byteArray = IOUtils.toByteArray(input);
                            } catch (IOException e) {
                                logger.error("Error while converting input stream to byte array", e);
                                throw new RuntimeException("Can not get content for: " + fullPath, e);
                            }
                            baps = new ByteArrayPartSource(fileName, byteArray);

                            /** Using file nam stored in cm:name property instead of node name
                             *
                             *  reference: https://issues.alfresco.com/jira/browse/ALF-13967
                             */
                            int idx = relativePath.lastIndexOf("/");
                            relativePath = relativePath.substring(0, idx+1) + fileName;

                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Add multi part fields for content item [site: %s; path: %s] into bucket #%d for publishing event [%s], channel [%s]", siteId, relativePath, bucketIndex, event.getId(), event.getChannelId()));
                            }
                            formParts.add(new StringPart(DEPLOYER_CONTENT_LOCATION_PARAM + cntNodes, relativePath));
                            formParts.add(new FilePart(DEPLOYER_CONTENT_FILE_PARAM + cntNodes, baps));
                            if (persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED)) {
                                String oldPath = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, CStudioContentModel.PROP_RENAMED_OLD_URL));
                                if (sbDeletedFiles.length() > 0) {
                                    sbDeletedFiles.append(",").append(oldPath);
                                } else {
                                    sbDeletedFiles.append(oldPath);
                                }
                                if (oldPath.endsWith(DmConstants.INDEX_FILE)) {
                                    sbDeletedFiles.append(",").append(oldPath.replace("/" + DmConstants.INDEX_FILE, ""));
                                }
                            }

                            if (publishMetadata) {
                                try {
                                    metadataStream = getMetadataStream(nodeRef);
                                    metadataPart = new ByteArrayPartSource(fileName + ".meta", IOUtils.toByteArray(metadataStream));
                                    formParts.add(new FilePart(DEPLOYER_METADATA_FILE_PARAM + cntNodes, metadataPart));
                                } catch (IOException e) {
                                    logger.error("Error while creating input stream with content metadata", e);
                                    throw new RuntimeException("Can not get content metadata for: " + fullPath, e);
                                }
                            }

                            cntNodes++;
                        }
                    }

                    if (sbDeletedFiles.length() > 0) {
                        formParts.add(new StringPart(DEPLOYER_DELETED_FILES_PARAM, sbDeletedFiles.toString()));
                    }
                    formParts.add(new StringPart(DEPLOYER_SITE_PARAM, siteId));
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Sending bucket #%d for publishing event [%s], channel [%s]", bucketIndex, event.getId(), event.getChannelId()));
                    }
                    PostMethod postMethod = null;
                    HttpClient client = null;

                    try {

                        postMethod = new PostMethod(requestUrl.toString());
                        postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
                        Part[] parts = new Part[formParts.size()];
                        for (int i = 0; i < formParts.size(); i++) parts[i] = formParts.get(i);
                        postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
                        client = new HttpClient();
                        int status = client.executeMethod(postMethod);
                        if (status == HttpStatus.SC_OK) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Publish success: " + event.getId());
                            }
                        } else {
                            String message = String.format("Publishing failed: Deploying agent responded with status: %s", HttpStatus.getStatusText(status));
                            logger.error(message);
                            signalFailure(event);
                            throw new RuntimeException(message);
                        }

                    } catch (HttpException e) {
                        String errorMessage = String.format("Publishing failed due to http protocol errors: endpoint %s, url:$", channelName, requestUrl.toString());
                        logger.error(errorMessage, e);
                        signalFailure(event);
                        throw new RuntimeException(errorMessage, e);
                    } catch (IOException e) {
                        String errorMessage = String.format("Publishing failed due to I/O (transport) errors: endpoint %s, url:$", channelName, requestUrl.toString());
                        logger.error(errorMessage, e);
                        signalFailure(event);
                        throw new RuntimeException(errorMessage, e);
                    } finally {
                        if (client != null) {
                            HttpConnectionManager mgr = client.getHttpConnectionManager();
                            if (mgr instanceof SimpleHttpConnectionManager) {
                                ((SimpleHttpConnectionManager)mgr).shutdown();
                            }
                        }
                        if (postMethod != null) {
                            postMethod.releaseConnection();
                            postMethod = null;
                        }
                        client = null;
                        formParts.clear();
                        formParts = null;
                        metadataPart = null;
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Signal succes and create deployment history for publishing event [%s], channel [%s]", event.getId(), event.getChannelId()));
                }
                processedEvents.put(event.getId(), new Date());
                if (logPublishedFiles) {
                    logPublishedPaths(nodesToPublish);
                }
                addToDeploymentHistory(event);
                doPostProcessing(nodesToPublish, true);

            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping. Event is already processed.");
                }
            }
        } finally {
            generalLockService.unlock(event.getId());
            baps = null;
            if (input != null) {
                IOUtils.closeQuietly(input);
                input = null;
            }
            if (metadataStream != null) {
                IOUtils.closeQuietly(metadataStream);
                metadataPart = null;
            }
            reader = null;
        }
    }

    private boolean checkDeployingAgentStatus(String url) {
        boolean toRet = false;
        PostMethod postMethod = null;
        HttpClient client = null;
        try {
            postMethod = new PostMethod(url);
            client = new HttpClient();
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                toRet = true;
            }

        } catch (Exception e) {
        } finally {
            postMethod.releaseConnection();
            postMethod = null;
            client = null;
        }
        return toRet;
    }

    private void logPublishedPaths(Set<NodeRef> nodesToPublish) {
        StringBuilder sb = new StringBuilder("List of published files:\n");
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        int index = 0;
        for (NodeRef nodeRef : nodesToPublish) {
            String fullPath = persistenceManagerService.getNodePath(nodeRef);
            sb.append(++index).append(":\t").append(fullPath).append("\n");
        }
        logger.info(sb.toString());
    }

    protected void signalFailure(PublishingEvent event) {
        final Set<NodeRef> publishedNodes = event.getPackage().getNodesToPublish();
        final PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        DmTransactionService dmTransactionService = getServicesManager().getService(DmTransactionService.class);
        RetryingTransactionHelper helper = dmTransactionService.getRetryingTransactionHelper();
        helper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback() {
            @Override
            public Object execute() throws Throwable {
                for (NodeRef nodeRef : publishedNodes) {
                    persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.DEPLOYMENT_FAILED);
                }
                return null;
            }
        }, false, true);

    }

    protected PublishingEvent getPublishingEvent(NodeRef nodeToPublish) {
        NodeService nodeService = getNodeService();
        RegexQNamePattern eventRegexQNamePattern = new RegexQNamePattern(PublishingModel.ASSOC_LAST_PUBLISHING_EVENT.getNamespaceURI(), PublishingModel.ASSOC_LAST_PUBLISHING_EVENT.getLocalName());
        List<AssociationRef> eventAssociationRefList = nodeService.getTargetAssocs(nodeToPublish, eventRegexQNamePattern);
        NodeRef eventNode = null;
        if (eventAssociationRefList.size() > 0) {
            eventNode = eventAssociationRefList.get(0).getTargetRef();
        }
        PublishingEvent event = _publishingEventHelper.getPublishingEvent(eventNode);
        return event;
    }

    protected PublishingEvent getUnpublishingEvent(NodeRef nodeToUnpublish) {
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        List<PublishingEvent> eventNodes = persistenceManagerService.getUnpublishEventsForNode(nodeToUnpublish);
        PublishingEvent event = null;
        for (PublishingEvent ev : eventNodes) {
            if (ev.getStatus().equals(Status.IN_PROGRESS)) {
                event = ev;
            }
        }
        return event;
    }

    protected InputStream getMetadataStream(NodeRef nodeToPublish) {
        NodeService nodeService = getNodeService();
        Map<QName, Serializable> contentProperties = nodeService.getProperties(nodeToPublish);
        Document metadataDoc = DocumentHelper.createDocument();
        Element root = metadataDoc.addElement("metadata");
        for (Map.Entry<QName, Serializable> property : contentProperties.entrySet()) {
            Element elem = root.addElement(property.getKey().getLocalName());
            elem.addText(String.valueOf(property.getValue()));
        }

        return IOUtils.toInputStream(metadataDoc.asXML());
    }

    protected void addToDeploymentHistory(PublishingEvent event) {
        Set<NodeRef> publishedNodes = event.getPackage().getNodesToPublish();
        Set<NodeRef> unpublishedNodes = event.getPackage().getNodesToUnpublish();
        List<DeploymentHistoryDAO> deploymentHistoryList = new FastList<DeploymentHistoryDAO>();
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String fullPath, site, relativePath;
        for (NodeRef nodeRef : publishedNodes) {
            fullPath = persistenceManagerService.getNodePath(nodeRef);
            Matcher m = DmConstants.DM_REPO_PATH_PATTERN.matcher(fullPath);
            if (m.matches()) {
                site = m.group(3);
                relativePath = m.group(5);
            } else {
                site = "";
                relativePath = fullPath;
            }
            DeploymentHistoryDAO historyEntry = new DeploymentHistoryDAO();
            historyEntry.setSite(site);
            historyEntry.setPath(relativePath);
            historyEntry.setUser(event.getCreator());
            Channel channel = getChannelService().getChannelById(event.getChannelId());
            historyEntry.setPublishingChannel(channel.getName());
            historyEntry.setDeploymentDate(Calendar.getInstance().getTime());
            deploymentHistoryList.add(historyEntry);
        }
        for (NodeRef nodeRef : unpublishedNodes) {
            fullPath = persistenceManagerService.getNodePath(nodeRef);
            Matcher m = DmConstants.DM_REPO_PATH_PATTERN.matcher(fullPath);
            if (m.matches()) {
                site = m.group(3);
                relativePath = m.group(5);
            } else {
                site = "";
                relativePath = fullPath;
            }
            DeploymentHistoryDAO historyEntry = new DeploymentHistoryDAO();
            historyEntry.setSite(site);
            historyEntry.setPath(relativePath);
            historyEntry.setUser(event.getCreator());
            Channel channel = getChannelService().getChannelById(event.getChannelId());
            historyEntry.setPublishingChannel(channel.getName());
            historyEntry.setDeploymentDate(Calendar.getInstance().getTime());
            deploymentHistoryList.add(historyEntry);
        }
        _deploymentHistoryDaoService.insertEntries(deploymentHistoryList);
    }

    @Override
    public boolean canUnpublish() {
        return true;
    }

    @Override
    public void unpublish(NodeRef nodeToUnpublish, Map<QName, Serializable> channelProperties) {
        GeneralLockService generalLockService = getServicesManager().getService(GeneralLockService.class);
        NodeService nodeService = getNodeService();

        RegexQNamePattern regexQNamePattern = new RegexQNamePattern(PublishingModel.ASSOC_SOURCE.getNamespaceURI(), PublishingModel.ASSOC_SOURCE.getLocalName());
        List<AssociationRef> associationRefList = nodeService.getTargetAssocs(nodeToUnpublish, regexQNamePattern);
        NodeRef contentNode = null;
        if (associationRefList.size() > 0) {
            contentNode = associationRefList.get(0).getTargetRef();
        }
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        List<PublishingEvent> eventNodes = persistenceManagerService.getUnpublishEventsForNode(contentNode);
        PublishingEvent event = null;
        for (PublishingEvent ev : eventNodes) {
            if (ev.getStatus().equals(Status.IN_PROGRESS)) {
                event = ev;
            }
        }
        Set<NodeRef> nodesToUnpublish = event.getPackage().getNodesToUnpublish();
        generalLockService.lock(event.getId());
        if (!processedUnpublishedEvents.containsKey(event.getId())) {
            processedUnpublishedEvents.put(event.getId(), nodesToUnpublish.size());
        }
        processedUnpublishedEvents.put(event.getId(), processedUnpublishedEvents.get(event.getId()) - 1);
        try {
            if (processedUnpublishedEvents.containsKey(event.getId()) && processedUnpublishedEvents.get(event.getId()) < 1) {

                String fullPath = getNodePath(contentNode);
                String relativePath = fullPath;
                String site = "";
                String fileName = (String)nodeService.getProperty(contentNode, ContentModel.PROP_NAME);
                Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
                if (m.matches()) {
                    relativePath = m.group(4).length() != 0 ? m.group(4) : "/";
                    if (!StringUtils.startsWith(relativePath, "/")) {
                        relativePath = "/" + relativePath;
                    }
                    site = m.group(2).length() != 0 ? m.group(2) : "";
                }

                SiteService siteService = getServicesManager().getService(SiteService.class);
                Map<String, PublishingChannelGroupConfigTO> groupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
                List<Channel> channels = new FastList<Channel>();
                for (PublishingChannelGroupConfigTO groupConfigTO : groupConfigTOs.values()) {
                    List<PublishingChannelConfigTO> channelConfigTOs = groupConfigTO.getChannels();
                    for (PublishingChannelConfigTO channelConfigTO : channelConfigTOs) {
                        Channel channel = persistenceManagerService.getChannelByName(channelConfigTO.getName());
                        if (channel != null && !channels.contains(channel)) {
                            Map<QName, Serializable> chProps = channel.getProperties();

                            String channelName = (String)chProps.get(ContentModel.PROP_NAME);
                            String server = (String)chProps.get(CrafterCMSPublishingModel.PROP_REMOTE_SERVER);
                            int port = (Integer)chProps.get(CrafterCMSPublishingModel.PROP_REMOTE_PORT);
                            String password = (String)chProps.get(CrafterCMSPublishingModel.PROP_REMOTE_PASSWORD);
                            String target = (String)chProps.get(CrafterCMSPublishingModel.PROP_REMOTE_TARGET);

                            URL statusUrl = null;
                            try {
                                statusUrl = new URL("http", server, port, DEPLOYER_STATUS_URL);
                            } catch (MalformedURLException e) {
                                logger.error(String.format("Invalid endpoint status URL for publishing channel [%s]", channelName), e);
                                signalFailure(event);
                                throw new RuntimeException(String.format("Unpublish failed: Invalid endpoint URL for publishing channel [%s]", channelName), e);
                            }
                            if (!checkDeployingAgentStatus(statusUrl.toString())) {
                                logger.error(String.format("Publishing Channel [%s]: Endpoint is not available. Status check failed for url %s", channelName, statusUrl.toString()));
                                signalFailure(event);
                                throw new RuntimeException(String.format("Publishing Channel [%s] - Publish failed: Endpoint is not available. Status check failed for url %s", channelName, statusUrl.toString()));
                            }
                            URL requestUrl = null;
                            try {
                                requestUrl = new URL("http", server, port, DEPLOYER_SERVLET_URL);
                            } catch (MalformedURLException e) {
                                logger.error(String.format("Invalid endpoint URL for publishing channel [%s]", channelName), e);
                                signalFailure(event);
                                throw new RuntimeException(String.format("Publish failed: Invalid endpoint URL for publishing channel [%s]", channelName), e);
                            }

                            StringBuilder sbDeletedFiles = new StringBuilder();
                            List<Part> formParts = new FastList<Part>();
                            formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, password));
                            formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, target));
                            formParts.add(new StringPart(DEPLOYER_SITE_PARAM, site));

                            for (NodeRef unpublishNode : nodesToUnpublish) {
                                fullPath = getNodePath(unpublishNode);
                                relativePath = fullPath;
                                fileName = (String)nodeService.getProperty(unpublishNode, ContentModel.PROP_NAME);
                                m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
                                if (m.matches()) {
                                    relativePath = m.group(4).length() != 0 ? m.group(4) : "/";
                                    if (!StringUtils.startsWith(relativePath, "/")) {
                                        relativePath = "/" + relativePath;
                                    }
                                }
                                int idx = relativePath.lastIndexOf("/");
                                relativePath = relativePath.substring(0, idx+1) + fileName;
                                sbDeletedFiles.append(relativePath);
                                NodeRef parentNode = nodeService.getPrimaryParent(unpublishNode).getParentRef();
                                FileFolderService fileFolderService = getServicesManager().getService(FileFolderService.class);
                                if (!(fileFolderService.list(parentNode).size() > 1) || fileName.equalsIgnoreCase(DmConstants.INDEX_FILE)) {
                                    sbDeletedFiles.append(FILES_SEPARATOR).append(getParentUrl(relativePath));
                                }
                                sbDeletedFiles.append(FILES_SEPARATOR);
                            }

                            formParts.add(new StringPart(DEPLOYER_DELETED_FILES_PARAM, sbDeletedFiles.toString()));

                            PostMethod postMethod = null;
                            HttpClient client = null;
                            try {
                                postMethod = new PostMethod(requestUrl.toString());
                                postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
                                Part[] parts = new Part[formParts.size()];
                                for (int i = 0; i < formParts.size(); i++) parts[i] = formParts.get(i);
                                postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
                                client = new HttpClient();

                                int status = client.executeMethod(postMethod);
                                if (status == HttpStatus.SC_OK) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug(String.format("Unpublishing success: event [%], endpoint [%]", event.getId(), channelName));
                                    }
                                } else {
                                    String message = String.format("Unpublishing failed: Deploying agent responded with status: %s", HttpStatus.getStatusText(status));
                                    logger.error(message);
                                    throw new RuntimeException(message);
                                }
                                postMethod.releaseConnection();

                            } catch (HttpException e) {
                                String errorMessage = String.format("Unpublishing failed due to http protocol errors: endpoint %s, url:$", channelName, requestUrl.toString());
                                logger.error(errorMessage, e);
                                throw new RuntimeException(errorMessage, e);
                            } catch (IOException e) {
                                String errorMessage = String.format("Unpublishing failed due to I/O (transport) errors: endpoint %s, url:$", channelName, requestUrl.toString());
                                logger.error(errorMessage, e);
                                throw new RuntimeException(errorMessage, e);
                            } finally {
                                if (client != null) {
                                    HttpConnectionManager mgr = client.getHttpConnectionManager();
                                    if (mgr instanceof SimpleHttpConnectionManager) {
                                        ((SimpleHttpConnectionManager)mgr).shutdown();
                                    }
                                }
                                if (postMethod != null) {
                                    postMethod.releaseConnection();
                                    postMethod = null;
                                }
                                client = null;
                                _deployerRequests.remove(event.getId());
                                formParts = null;
                            }
                        }
                    }
                }
                addToDeploymentHistory(event);
                doPostProcessing(event.getPackage().getNodesToUnpublish(), false);
            } else {
                logger.debug("Skipping. Event is already processed.");
            }
        }  finally {
            generalLockService.unlock(event.getId());
        }

    }

    @Override
    public boolean canPublishStatusUpdates() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getNodePath(NodeRef node) {
        NodeService nodeService = getNodeService();
        StringBuilder sb = new StringBuilder();
        NodeRef companyHomeNode = getCompanyHomeNodeRef();
        Path nodePath = nodeService.getPath(node);
        boolean foundCompanyHome = false;
        for (Path.Element element : nodePath) {
            Path.ChildAssocElement assocElement = (Path.ChildAssocElement)element;
            ChildAssociationRef childAssociationRef = assocElement.getRef();
            NodeRef elementNode = childAssociationRef.getChildRef();
            if (foundCompanyHome)
                sb.append("/").append(childAssociationRef.getQName().getLocalName());
            foundCompanyHome = foundCompanyHome || elementNode.equals(companyHomeNode);
        }
        return sb.toString();
    }

    protected NodeRef getCompanyHomeNodeRef() {
        return _companyHomeNodeLocator.getNode(null, null);
    }

    protected String getParentUrl(String url) {
        int lastIndex = url.lastIndexOf("/");
        return url.substring(0, lastIndex);
    }

    public void registerPostProcessor(CrafterCMSPublishingProcessor processor) {
        if (this._postProcessors == null) {
            this._postProcessors = new FastList<CrafterCMSPublishingProcessor>();
        }
        this._postProcessors.add(processor);
    }

    public void setPostProcessors(List<CrafterCMSPublishingProcessor> processors) {
        if (this._postProcessors == null) {
            this._postProcessors = new FastList<CrafterCMSPublishingProcessor>();
        }
        this._postProcessors = processors;
    }

    protected void doPostProcessing(Set<NodeRef> publishedNodes, boolean publish) {
        for (CrafterCMSPublishingProcessor postProcessor : _postProcessors) {
            try {
                postProcessor.doProcess(publishedNodes, publish);
            } catch (Exception e) {
                logger.error("Postprocessor execution failed:", e);
            }
        }
    }

    class DeployerRequestContainer {

        protected int _processedFilesCount = 0;
        public int getProcessedFilesCount() {
            return _processedFilesCount;
        }
        public void setProcessedFilesCount(int processedFilesCount) {
            this._processedFilesCount = processedFilesCount;
        }

        protected List<Part> _formParts = new FastList<Part>();
        public List<Part> getFormParts() {
            return _formParts;
        }
        public void setFormParts(List<Part> formParts) {
            this._formParts = formParts;
        }

        protected StringBuilder _sbDeletedFiles = new StringBuilder();
        public StringBuilder getDeletedFiles() {
            return _sbDeletedFiles;
        }
        public void setDeletedFiles(StringBuilder sbDeletedFiles) {
            this._sbDeletedFiles = sbDeletedFiles;
        }

        DeployerRequestContainer() { }


    }
}
