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
package org.craftercms.cstudio.alfresco.deployment;


import javolution.util.FastList;
import net.sf.json.JSONObject;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.event.EventService;
import org.craftercms.cstudio.alfresco.service.api.DeploymentService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CrafterCMSDeploymentCommand extends AbstractDeploymentCommand {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrafterCMSDeploymentCommand.class);

    public final static String DEPLOYER_PASSWORD_PARAM = "password";
    public final static String DEPLOYER_TARGET_PARAM = "target";
    public final static String DEPLOYER_SITE_PARAM = "siteId";
    public final static String DEPLOYER_DELETED_FILES_PARAM = "deletedFiles";
    public final static String DEPLOYER_CONTENT_LOCATION_PARAM = "contentLocation";
    public final static String DEPLOYER_CONTENT_FILE_PARAM = "contentFile";
    public final static String DEPLOYER_METADATA_FILE_PARAM = "metadataFile";
    public final static String FILES_SEPARATOR = ",";




    @Override
    public void deploy() throws DeploymentException {
        if (StringUtils.isNotEmpty(endpoint.getStatusUrl())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Check deployment agent status for deployment batch [%s], endpoint [%s]", batchId, endpoint.getName()));
            }
            URL statusUrl = null;
            try {
                statusUrl = new URL(endpoint.getStatusUrl());
            } catch (MalformedURLException e) {
                LOGGER.error(String.format("Invalid endpoint status URL for publishing channel [%s]", endpoint.getName()), e);
                throw new DeploymentException(String.format("Deployment failed: Invalid status URL (%s) for endpoint [%s]", endpoint.getStatusUrl(), endpoint.getName()), e);
            }
            if (!checkDeployingAgentStatus(statusUrl.toString())) {
                LOGGER.error(String.format("Endpoint (%s) is not available. Status check failed for url %s", endpoint.getName(), endpoint.getStatusUrl()));
                throw new DeploymentException(String.format("Deployment failed: Endpoint (%s) is not available. Status check failed for url %s", endpoint.getName(), endpoint.getStatusUrl()));
            }
        }

        URL requestUrl = null;
        try {
            requestUrl = new URL(endpoint.getServerUrl());
        } catch (MalformedURLException e) {
            LOGGER.error(String.format("Invalid server URL for endpoint [%s]", endpoint.getName()), e);
            throw new DeploymentException(String.format("Deployment failed: Invalid server URL (%s) for endpoint [%s]", endpoint.getStatusUrl(), endpoint.getName()), e);
        }

        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        DeploymentBatchDAO batch = deploymentService.getDeploymentBatch(batchId);
        List<DeploymentItemDAO> batchItems = deploymentService.getDeploymentBatchItems(batchId);
        List<DeploymentItemDAO> batchDeleteItems = deploymentService.getDeploymentBatchDeleteItems(batchId);
        String rootPath = servicesConfig.getRepositoryRootPath(batch.getSite());
        List<DeploymentEventItem> eventItems = new FastList<DeploymentEventItem>();
        Date eventDate = new Date();

        ByteArrayPartSource baps = null;
        PartSource metadataPart = null;
        StringPart stringPart = null;
        FilePart filePart = null;

        int numberOfBuckets = batchItems.size() / endpoint.getBucketSize() + 1;
        Iterator<DeploymentItemDAO> iter = batchItems.iterator();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Divide all deployment items into %d bucket(s) for deployment batch [%s], endpoint [%s]", numberOfBuckets, batchId, endpoint.getName()));
        }
        for (int bucketIndex = 0; bucketIndex < numberOfBuckets; bucketIndex++) {
            int cntFiles = 0;
            StringBuilder sbDeletedFiles = new StringBuilder();
            List<Part> formParts = new FastList<Part>();

            formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, endpoint.getPassword()));
            formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, endpoint.getTarget()));
            formParts.add(new StringPart(DEPLOYER_SITE_PARAM, batch.getSite()));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Preparing deployment items (bucket %d) for deployment batch [%s], endpoint [%s]", bucketIndex + 1, batchId, endpoint.getName()));
            }
            int loopSize = (batchItems.size() - (bucketIndex * endpoint.getBucketSize()) > endpoint.getBucketSize()) ? endpoint.getBucketSize() : batchItems.size() - bucketIndex * endpoint.getBucketSize();
            for (int j = 0; j < loopSize; j++) {
                if (iter.hasNext()) {
                    DeploymentItemDAO batchItem = iter.next();
                    DeploymentEventItem item = new DeploymentEventItem();
                    item.setSite(batchItem.getSite());
                    item.setPath(batchItem.getPath());
                    item.setUser(batchItem.getUser());
                    item.setDateTime(eventDate);

                    String fullPath = rootPath + batchItem.getPath();
                    NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
                    if (persistenceManagerService.isNew(nodeRef)) {
                        item.setState(DeploymentEventItem.STATE_NEW);
                    } else {
                        item.setState(DeploymentEventItem.STATE_UPDATED);
                    }
                    String fileName = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME));
                    ContentReader reader = persistenceManagerService.getReader(nodeRef, ContentModel.PROP_CONTENT);

                    InputStream input = reader.getContentInputStream();

                    byte[] byteArray = null;

                    try {
                        byteArray = IOUtils.toByteArray(input);
                    } catch (IOException e) {
                        LOGGER.error("Error while converting input stream to byte array", e);
                        baps = null;
                        stringPart = null;
                        filePart = null;
                        formParts = null;
                        throw new DeploymentException("Can not get content for: " + fullPath, e);
                    }
                    finally {
                        IOUtils.closeQuietly(input);
                        input = null;
                        reader = null;
                    }
                    baps = new ByteArrayPartSource(fileName, byteArray);

                    /** Using file nam stored in cm:name property instead of node name
                     *
                     *  reference: https://issues.alfresco.com/jira/browse/ALF-13967
                     */
                    int idx = batchItem.getPath().lastIndexOf("/");
                    String relativePath = batchItem.getPath().substring(0, idx + 1) + fileName;
                    stringPart = new StringPart(DEPLOYER_CONTENT_LOCATION_PARAM + cntFiles, relativePath);
                    formParts.add(stringPart);
                    filePart = new FilePart(DEPLOYER_CONTENT_FILE_PARAM + cntFiles, baps);
                    formParts.add(filePart);
                    if (persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED)) {
                        String oldPath = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, CStudioContentModel.PROP_RENAMED_OLD_URL));
                        if (!StringUtils.equalsIgnoreCase(batchItem.getPath(), oldPath)) {
                            item.setOldPath(oldPath);
                            item.setState(DeploymentEventItem.STATE_MOVED);
                            if (sbDeletedFiles.length() > 0) {
                                sbDeletedFiles.append(",").append(oldPath);
                            } else {
                                sbDeletedFiles.append(oldPath);
                            }
                            if (oldPath.endsWith(DmConstants.INDEX_FILE)) {
                                sbDeletedFiles.append(FILES_SEPARATOR).append(oldPath.replace("/" + DmConstants.INDEX_FILE, ""));
                            }
                        }
                    }

                    if (endpoint.isSendMetadata()) {
                        InputStream metadataStream = null;
                        try {
                            metadataStream = getMetadataStream(nodeRef);
                            metadataPart = new ByteArrayPartSource(fileName + ".meta", IOUtils.toByteArray(metadataStream));
                            formParts.add(new FilePart(DEPLOYER_METADATA_FILE_PARAM + cntFiles, metadataPart));
                        } catch (IOException e) {
                            LOGGER.error("Error while creating input stream with content metadata", e);
                            baps = null;
                            stringPart = null;
                            filePart = null;
                            formParts = null;
                            throw new DeploymentException("Can not get content metadata for: " + fullPath, e);
                        }
                        finally {
                            IOUtils.closeQuietly(metadataStream);
                            metadataPart = null;
                        }
                    }
                    cntFiles++;
                    eventItems.add(item);
                }
            }

            if (!iter.hasNext()) {
                for (DeploymentItemDAO batchItem : batchDeleteItems) {
                    DeploymentEventItem item = new DeploymentEventItem();
                    item.setSite(batchItem.getSite());
                    item.setPath(batchItem.getPath());
                    item.setUser(batchItem.getUser());
                    item.setDateTime(eventDate);
                    item.setState(DeploymentEventItem.STATE_DELETED);
                    if (sbDeletedFiles.length() > 0) {
                        sbDeletedFiles.append(FILES_SEPARATOR).append(batchItem.getPath());
                    } else {
                        sbDeletedFiles.append(batchItem.getPath());
                    }
                    String fullPath = rootPath + batchItem.getPath();
                    NodeRef parentNode = persistenceManagerService.getNodeRef(getParentUrl(fullPath));
                    if (parentNode == null || !(persistenceManagerService.list(parentNode).size() > 1) || batchItem.getPath().endsWith(DmConstants.INDEX_FILE)) {
                        sbDeletedFiles.append(FILES_SEPARATOR).append(getParentUrl(batchItem.getPath()));
                    }
                    eventItems.add(item);
                }
            }

            if (sbDeletedFiles.length() > 0) {
                formParts.add(new StringPart(DEPLOYER_DELETED_FILES_PARAM, sbDeletedFiles.toString()));
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Create http request to deploy bucket %d for deployment batch [%s], endpoint [%s]", bucketIndex + 1, batchId, endpoint.getName()));
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
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Successfully deployed bucket number %d of batch [%s] on endpoint [%s]", bucketIndex + 1, batchId, endpoint.getName()));
                    }
                } else {
                    LOGGER.error(String.format("Deployment failed for bucket number %d of batch [%s] on endpoint [%s]. Deployment agent returned status [%s]", bucketIndex + 1, batchId, endpoint.getName(), HttpStatus.getStatusText(status)));
                    throw new DeploymentException(String.format("Deployment failed for bucket number %d of batch [%s] on endpoint [%s] - Receiver Status Response: [%s]", bucketIndex, batchId, endpoint.getName(), HttpStatus.getStatusText(status)));
                }
            } catch (HttpException e) {
                String message = String.format("Publish failed for batch [%s], endpoint [%s] due to http protocol exception", batchId, endpoint.getName());
                LOGGER.error(message, e);
                throw new DeploymentException(message, e);
            } catch (IOException e) {
                String message = String.format("Publish failed for batch [%s], endpoint [%s] due to I/O (transport) exception", batchId, endpoint.getName());
                LOGGER.error(message, e);
                throw new DeploymentException(message, e);
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
                    client = null;
                }
                baps = null;
                stringPart = null;
                filePart = null;
                formParts = null;
            }
        }
        publishDeployEvent(endpoint.getName(), eventItems);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Deployment successful for batch [%s] on endpoint [%s]", batch.getBatchId(), endpoint.getName()));
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
            if (client != null) {
                HttpConnectionManager mgr = client.getHttpConnectionManager();
                if (mgr instanceof SimpleHttpConnectionManager) {
                    ((SimpleHttpConnectionManager)mgr).shutdown();
                }
            }
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
            postMethod = null;
            client = null;

        }
        return toRet;
    }

    protected void publishDeployEvent(String endpoint, List<DeploymentEventItem> items) {
        EventService eventService = servicesManager.getService(EventService.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endpoint", endpoint);
        jsonObject.put("items", items);
        eventService.publish(DeploymentEngineConstants.EVENT_DEPLOYMENT_ENGINE_DEPLOY, jsonObject);
    }

    protected InputStream getMetadataStream(NodeRef nodeToPublish) {
        NodeService nodeService = servicesManager.getService(NodeService.class);
        Map<QName, Serializable> contentProperties = nodeService.getProperties(nodeToPublish);
        Document metadataDoc = DocumentHelper.createDocument();
        Element root = metadataDoc.addElement("metadata");
        for (Map.Entry<QName, Serializable> property : contentProperties.entrySet()) {
            Element elem = root.addElement(property.getKey().getLocalName());
            elem.addText(String.valueOf(property.getValue()));
        }

        return IOUtils.toInputStream(metadataDoc.asXML());
    }

    protected String getParentUrl(String url) {
        int lastIndex = url.lastIndexOf("/");
        return url.substring(0, lastIndex);
    }
}
