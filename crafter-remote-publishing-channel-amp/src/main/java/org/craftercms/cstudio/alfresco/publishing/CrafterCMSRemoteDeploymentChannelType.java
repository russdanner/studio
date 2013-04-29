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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.publishing.AbstractChannelType;
import org.alfresco.repo.publishing.PublishingEventHelper;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.publishing.PublishingEvent;
import org.alfresco.service.cmr.publishing.PublishingService;
import org.alfresco.service.cmr.publishing.Status;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crafter Studio Receiver channel type
 * @author Dejan Brkic
 */
public class CrafterCMSRemoteDeploymentChannelType extends AbstractChannelType {

    private static final Logger logger = LoggerFactory.getLogger(CrafterCMSRemoteDeploymentChannelType.class);

    public static String DEPLOYER_SERVLET_URL = "/publish";
    public static String DEPLOYER_PASSWORD_PARAM = "password";
    public static String DEPLOYER_TARGET_PARAM = "target";
    public static String DEPLOYER_DELETED_FILES_PARAM = "deletedFiles";
    public static String DEPLOYER_CONTENT_LOCATION_PARAM = "contentLocation";
    public static String DEPLOYER_CONTENT_FILE_PARAM = "contentFile";
    public static String DEPLOYER_METADATA_FILE_PARAM = "metadataFile";
    public static String FILES_SEPARATOR = ",";

    protected List<CrafterCMSPublishingProcessor> _postProcessors;

    protected List<CrafterCMSPublishingProcessor> _preProcessors;

    public CrafterCMSRemoteDeploymentChannelType() {
        _postProcessors = new ArrayList<CrafterCMSPublishingProcessor>();
        _preProcessors  = new ArrayList<CrafterCMSPublishingProcessor>();
    }

    protected ContentService _contentService;
    public ContentService getContentService() {
        return _contentService;
    }
    public void setContentService(ContentService contentService) {
        this._contentService = contentService;
    }

    protected PublishingEventHelper _publishingEventHelper;
    public PublishingEventHelper getPublishingEventHelper() {
        return _publishingEventHelper;
    }
    public void setPublishingEventHelper(PublishingEventHelper publishingEventHelper) {
        this._publishingEventHelper = publishingEventHelper;
    }

    protected PublishingService _publishingService;
    public PublishingService getPublishingService() {
        return _publishingService;
    }
    public void setPublishingService(PublishingService publishingService) {
        this._publishingService = publishingService;
    }

    protected FileFolderService _fileFolderService;
    public FileFolderService getFileFolderService() {
        return _fileFolderService;
    }
    public void setFileFolderService(FileFolderService fileFolderService) {
        this._fileFolderService = fileFolderService;
    }

    protected CompanyHomeNodeLocator _companyHomeNodeLocator;
    public CompanyHomeNodeLocator getCompanyHomeNodeLocator() {
        return _companyHomeNodeLocator;
    }
    public void setCompanyHomeNodeLocator(CompanyHomeNodeLocator companyHomeNodeLocator) {
        this._companyHomeNodeLocator = companyHomeNodeLocator;
    }

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

    protected int _processedFilesCount = 0;
    protected PostMethod _postMethod = null;
    protected List<Part> _formParts = null;
    protected HttpClient _client = null;
    protected StringBuilder _sbDeletedFiles = null;

    @Override
    public void publish(NodeRef nodeToPublish, Map<QName, Serializable> channelProperties) {

        NodeService nodeService = getNodeService();

        StringBuilder sb = new StringBuilder();
        sb.append((String)channelProperties.get(ContentModel.PROP_STORE_PROTOCOL));
        sb.append("://").append((String)channelProperties.get(ContentModel.PROP_STORE_IDENTIFIER));
        sb.append("/").append((String)channelProperties.get(ContentModel.PROP_NODE_UUID));
        NodeRef channelNode = new NodeRef(sb.toString());

        NodeRef eventNode = _publishingEventHelper.getLastPublishEvent(nodeToPublish, channelNode);
        //PublishingEvent event = _publishingEventHelper.getPublishingEvent(eventNode);

        RegexQNamePattern eventRegexQNamePattern = new RegexQNamePattern(PublishingModel.ASSOC_LAST_PUBLISHING_EVENT.getNamespaceURI(), PublishingModel.ASSOC_LAST_PUBLISHING_EVENT.getLocalName());
        List<AssociationRef> eventAssociationRefList = nodeService.getTargetAssocs(nodeToPublish, eventRegexQNamePattern);
        if (eventAssociationRefList.size() > 0) {
            eventNode = eventAssociationRefList.get(0).getTargetRef();
        }
        PublishingEvent event = _publishingEventHelper.getPublishingEvent(eventNode);

        RegexQNamePattern regexQNamePattern = new RegexQNamePattern(PublishingModel.ASSOC_SOURCE.getNamespaceURI(), PublishingModel.ASSOC_SOURCE.getLocalName());
        List<AssociationRef> associationRefList = nodeService.getTargetAssocs(nodeToPublish, regexQNamePattern);
        NodeRef contentNode = null;
        if (associationRefList.size() > 0) {
            contentNode = associationRefList.get(0).getTargetRef();
        }
        String fullPath = getNodePath(contentNode);
        String fileName = (String)nodeService.getProperty(contentNode, ContentModel.PROP_NAME);
        String relativePath = fullPath;
        Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
        if (m.matches()) {
            relativePath = m.group(4).length() != 0 ? m.group(4) : "/";
            if (!StringUtils.startsWith(relativePath, "/"))
                relativePath = "/" + relativePath;
        }

        ContentReader reader = _contentService.getReader(nodeToPublish, ContentModel.PROP_CONTENT);

        InputStream input = reader.getContentInputStream();
        ByteArrayPartSource baps = null;
        byte[] byteArray = null;

        if (reader.exists()) {
            String server = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_SERVER);
            int port = (Integer)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PORT);
            String password = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PASSWORD);
            String target = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_TARGET);
            boolean publishMetadata = (Boolean)channelProperties.get(CrafterCMSPublishingModel.PROP_PUBLISH_METADATA);

            try {
                byteArray = IOUtils.toByteArray(input);
            } catch (IOException e) {
                logger.error("Error while converting input stream to byte array", e);
                throw new RuntimeException("Can not get content for: " + fullPath, e);
            }
            baps = new ByteArrayPartSource(fileName, byteArray);
            if (_processedFilesCount == 0 && _formParts == null ) {
                _formParts = new ArrayList<Part>();
                _formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, password));
                _formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, target));
                _sbDeletedFiles = new StringBuilder();
            }

            /** Using file nam stored in cm:name property instead of node name
             *
             *  reference: https://issues.alfresco.com/jira/browse/ALF-13967
             */
            int idx = relativePath.lastIndexOf("/");
            relativePath = relativePath.substring(0, idx+1) + fileName;

            _formParts.add(new StringPart(DEPLOYER_CONTENT_LOCATION_PARAM + _processedFilesCount, relativePath));
            _formParts.add(new FilePart(DEPLOYER_CONTENT_FILE_PARAM + _processedFilesCount, baps));
            if (publishMetadata) {
                try {
                InputStream metadataStream = getMetadataStream(nodeToPublish);
                PartSource metadataPart = new ByteArrayPartSource(fileName + ".meta", IOUtils.toByteArray(metadataStream));
                _formParts.add(new FilePart(DEPLOYER_METADATA_FILE_PARAM + _processedFilesCount, metadataPart));
                } catch (IOException e) {
                    logger.error("Error while creating input stream with content metadata", e);
                    throw new RuntimeException("Can not get content metadata for: " + fullPath, e);
                }
            }

            _processedFilesCount++;
            Set<NodeRef> nodesToPublish = event.getPackage().getNodesToPublish();
            if (_processedFilesCount == nodesToPublish.size()) {
                try {
                    doPreProcessing(nodesToPublish, true);

                    URL requestUrl = null;
                    try {
                        requestUrl = new URL("http", server, port, DEPLOYER_SERVLET_URL);
                    } catch (MalformedURLException e) {
                        logger.error("Invalid URL ", e);
                        throw new RuntimeException("Publish failed", e);
                    }
                    _postMethod = new PostMethod(requestUrl.toString());
                    _postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
                    Part[] parts = new Part[_formParts.size()];
                    for (int i = 0; i < _formParts.size(); i++) parts[i] = _formParts.get(i);
                    _postMethod.setRequestEntity(new MultipartRequestEntity(parts, _postMethod.getParams()));
                    _client = new HttpClient();
                    int status = _client.executeMethod(_postMethod);
                    if (status == HttpStatus.SC_OK) {
                        logger.info("Publish success");
                        doPostProcessing(nodesToPublish, true);
                    } else {
                        logger.info("Publish failed: " + HttpStatus.getStatusText(status));
                        throw new RuntimeException("Receiver Status Response: " + status + " " + HttpStatus.getStatusText(status));
                    }
                    _postMethod.releaseConnection();

                } catch (HttpException e) {
                    logger.info("Publish failed: ", e);
                    throw new RuntimeException("Publish failed", e);
                } catch (IOException e) {
                    logger.info("Publish failed: ", e);
                    throw new RuntimeException("Publish failed", e);
                } finally {
                    IOUtils.closeQuietly(input);
                    _client = null;
                    _postMethod = null;
                    _processedFilesCount = 0;
                    _formParts = null;
                    _sbDeletedFiles = null;
                    Runtime r = Runtime.getRuntime();
                    r.gc();
                }
            }
        }

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

    @Override
    public boolean canUnpublish() {
        return true;
    }

    @Override
    public void unpublish(NodeRef nodeToUnpublish, Map<QName, Serializable> channelProperties) {
        NodeService nodeService = getNodeService();

        RegexQNamePattern regexQNamePattern = new RegexQNamePattern(PublishingModel.ASSOC_SOURCE.getNamespaceURI(), PublishingModel.ASSOC_SOURCE.getLocalName());
        List<AssociationRef> associationRefList = nodeService.getTargetAssocs(nodeToUnpublish, regexQNamePattern);
        NodeRef contentNode = null;
        if (associationRefList.size() > 0) {
            contentNode = associationRefList.get(0).getTargetRef();
        }
        List<PublishingEvent> eventNodes = _publishingService.getUnpublishEventsForNode(contentNode);
        PublishingEvent event = null;
        for (PublishingEvent ev : eventNodes) {
            if (ev.getStatus().equals(Status.IN_PROGRESS)) {
                event = ev;
            }
        }

        String fullPath = getNodePath(contentNode);
        String relativePath = fullPath;
        String fileName = (String)nodeService.getProperty(contentNode, ContentModel.PROP_NAME);
        Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
        if (m.matches()) {
            relativePath = m.group(4).length() != 0 ? m.group(4) : "/";
            if (!StringUtils.startsWith(relativePath, "/"))
                relativePath = "/" + relativePath;
        }

        String server = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_SERVER);
        int port = (Integer)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PORT);
        String password = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_PASSWORD);
        String target = (String)channelProperties.get(CrafterCMSPublishingModel.PROP_REMOTE_TARGET);

        if (_processedFilesCount == 0 && _formParts == null ) {
            _formParts = new ArrayList<Part>();
            _formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, password));
            _formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, target));
            _sbDeletedFiles = new StringBuilder();
        }
        /** Using file nam stored in cm:name property instead of node name
         *
         *  reference: https://issues.alfresco.com/jira/browse/ALF-13967
         */
        int idx = relativePath.lastIndexOf("/");
        relativePath = relativePath.substring(0, idx+1) + fileName;
        _sbDeletedFiles.append(relativePath);
        NodeRef parentNode = nodeService.getPrimaryParent(contentNode).getParentRef();
        if (!(_fileFolderService.list(parentNode).size() > 1)) {
            _sbDeletedFiles.append(",").append(getParentUrl(relativePath));
        }

        _processedFilesCount++;
        if (_processedFilesCount == event.getPackage().getNodesToUnpublish().size()) {
            _formParts.add(new StringPart(DEPLOYER_DELETED_FILES_PARAM, _sbDeletedFiles.toString()));
            try {
                URL requestUrl = null;
                try {
                    requestUrl = new URL("http", server, port, DEPLOYER_SERVLET_URL);
                } catch (MalformedURLException e) {
                    logger.error("Invalid URL ", e);
                    throw new RuntimeException("Unpublish failed", e);
                }
                _postMethod = new PostMethod(requestUrl.toString());
                _postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
                Part[] parts = new Part[_formParts.size()];
                for (int i = 0; i < _formParts.size(); i++) parts[i] = _formParts.get(i);
                _postMethod.setRequestEntity(new MultipartRequestEntity(parts, _postMethod.getParams()));
                _client = new HttpClient();

                int status = _client.executeMethod(_postMethod);
                if (status == HttpStatus.SC_OK) {
                    logger.info("Unpublish success");
                    doPostProcessing(event.getPackage().getNodesToUnpublish(), false);
                } else {
                    logger.info("Unpublish failed: " + HttpStatus.getStatusText(status));
                    throw new RuntimeException("Receiver Status Response: " + status + " " + HttpStatus.getStatusText(status));
                }
                _postMethod.releaseConnection();

            } catch (HttpException e) {
                logger.info("Unpublish failed: ", e);
                throw new RuntimeException("Unpublish failed", e);
            } catch (IOException e) {
                logger.info("Unpublish failed: ", e);
                throw new RuntimeException("Unpublish failed", e);
            } finally {
                _client = null;
                _postMethod = null;
                _processedFilesCount = 0;
                _formParts = null;
                _sbDeletedFiles = null;
                Runtime r = Runtime.getRuntime();
                r.gc();
            }
        } else {
            _sbDeletedFiles.append(FILES_SEPARATOR);
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
        this._postProcessors.add(processor);
    }

    public void setPostProcessors(List<CrafterCMSPublishingProcessor> processors) {
        this._postProcessors = processors;
    }

    public void registerPreProcessor(CrafterCMSPublishingProcessor processor) {
        this._preProcessors.add(processor);
    }

    public void setPreProcessors(List<CrafterCMSPublishingProcessor> processors) {
        this._preProcessors = processors;
    }

    protected void doPostProcessing(Set<NodeRef> publishedNodes, boolean publish) {
        for (CrafterCMSPublishingProcessor postProcessor : _postProcessors) {
            postProcessor.doProcess(publishedNodes, publish);
        }
    }

    protected void doPreProcessing(Set<NodeRef> publishedNodes, boolean publish) {
        for (CrafterCMSPublishingProcessor preProcessor : _preProcessors) {
            preProcessor.doProcess(publishedNodes, publish);
        }
    }
}
