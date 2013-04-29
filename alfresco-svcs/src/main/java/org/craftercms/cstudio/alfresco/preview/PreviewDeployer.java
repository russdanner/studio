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
package org.craftercms.cstudio.alfresco.preview;

import javolution.util.FastList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alfonso Vásquez
 */
public class PreviewDeployer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewDeployer.class);

    public static final String METADATA_EXTENSION = ".depmeta";
    public static String DEPLOYER_SERVLET_URL = "/publish";
    public static String DEPLOYER_PASSWORD_PARAM = "password";
    public static String DEPLOYER_TARGET_PARAM = "target";
    public static String DEPLOYER_SITE_PARAM = "siteId";
    public static String DEPLOYER_DELETED_FILES_PARAM = "deletedFiles";
    public static String DEPLOYER_CONTENT_LOCATION_PARAM = "contentLocation";
    public static String DEPLOYER_CONTENT_FILE_PARAM = "contentFile";
    public static String DEPLOYER_METADATA_FILE_PARAM = "metadataFile";
    public static String FILES_SEPARATOR = ",";
    public static final Pattern DM_REPO_PATH_PATTERN_STRING = Pattern.compile("/(wem-projects)/([-\\w]*)/([-\\w]*)/(work-area|live|draft)(/.*)");
    
    private File deployRoot;
    private String metaDataRoot;
    private Map<String, Properties> cachedMetaData;

    private String deployServer;
    private int deployPort;
    private String deployTarget;
    private String deployPassword;
    private boolean remoteDeployEnabled;
    
    public PreviewDeployer() {
        cachedMetaData = new ConcurrentHashMap<String, Properties>();
    }
    
    public void setDeployServer(String server) { this.deployServer = server; }
    public void setDeployPort(int port) { this.deployPort = port; }
    public void setDeployTarget(String target) { this.deployTarget = target; }
    public void setDeployPassword(String password) { this.deployPassword = password; }
    public void setRemoteDeployEnabled(boolean remoteDeployEnabled) { this.remoteDeployEnabled = remoteDeployEnabled; }
    
    public void setDeployRoot(File deployRoot) {
        this.deployRoot = deployRoot;
    }

    public void setMetaDataRoot(String metaDataRoot) {
        this.metaDataRoot = metaDataRoot;
    }

    public DeployedPreviewFile getFile(String path) throws IOException {
        File file = new File(deployRoot, path);
        if (file.exists()) {
            return new DeployedPreviewFile(path, file, getFileMetaData(path, file));
        } else {
            return null;
        }
    }

    public List<DeployedPreviewFile> getChildren(String path) throws IOException {
        File dir = new File(deployRoot, path);
        File[] listing = dir.listFiles();

        if (listing == null) {
            return null;
        } else if (listing.length == 0) {
            return Collections.emptyList();
        } else {
            List<DeployedPreviewFile> children = new ArrayList<DeployedPreviewFile>(listing.length);
            for (File child : listing) {
                String childPath = path + "/" + child.getName();
                children.add(new DeployedPreviewFile(childPath, child, getFileMetaData(childPath, child)));
            }

            return children;
        }
    }

    public void createDirectory(String path) throws IOException {
        File dir = new File(deployRoot, path);
        FileUtils.forceMkdir(dir);
    }

    public void deploy(String path, InputStream content) throws IOException {
        if (remoteDeployEnabled) {
            try {
                remoteDeploy(path, content, null, false);
            } catch (Exception e) {
                LOGGER.error("Error while deploying preview content: " + path, e);
            }
        } else {
            fileSystemDeploy(path, content, null);
        }
    }

    public void deploy(String path, InputStream content, Properties metaData) throws IOException {
        if (remoteDeployEnabled) {
            try {
                remoteDeploy(path, content, metaData, false);
            } catch (Exception e) {
                LOGGER.error("Error while deploying preview content: " + path, e);
            }
        } else {
            fileSystemDeploy(path, content, metaData);
        }
    }

    protected void fileSystemDeploy(String path, InputStream content, Properties metaData) throws IOException {
        File file = new File(deployRoot, path);
        FileUtils.copyInputStreamToFile(content, file);

        if (metaData != null) {
            File metaDataFile = new File(metaDataRoot, path + METADATA_EXTENSION);
            OutputStream metaDataOutput = new BufferedOutputStream(FileUtils.openOutputStream(metaDataFile));
            try {
                metaData.store(metaDataOutput, null);
            } finally {
                try {
                    metaDataOutput.close();
                } catch (IOException e) {
                }
            }
            cachedMetaData.put(path, metaData);
        }
    }

    protected void remoteDeploy(String path, InputStream content, Properties metaData, boolean delete) throws Exception {
        String server = this.deployServer;
        int port = this.deployPort;
        String password = this.deployPassword;
        String target = this.deployTarget;

        //int siteStartPos = path.indexOf("/wem-projects")+13;
        String site = "";
        String relativePath = "";
        Matcher matcher = DM_REPO_PATH_PATTERN_STRING.matcher(path);
        if (matcher.matches()) {
            site = matcher.group(3);
            relativePath = matcher.group(5).length() != 0 ? matcher.group(5) : "/";
        }

        //boolean publishMetadata = true;
        URL requestUrl = null;
        //String origPath = path;
        //path = path.substring(path.indexOf("/site"));

        try {
            String url = DEPLOYER_SERVLET_URL;
            requestUrl = new URL("http", server, port, url);

            List<Part> formParts = new FastList<Part>();
            formParts.add(new StringPart(DEPLOYER_PASSWORD_PARAM, password));
            formParts.add(new StringPart(DEPLOYER_TARGET_PARAM, target));

            if(delete == true) {
                formParts.add(new StringPart(DEPLOYER_DELETED_FILES_PARAM, relativePath));
            }

            if(content != null) {
                ByteArrayPartSource baps = null;
                byte[] byteArray = null;
                byteArray = IOUtils.toByteArray(content);

                baps = new ByteArrayPartSource(relativePath, byteArray);
                formParts.add(new FilePart(DEPLOYER_CONTENT_FILE_PARAM, baps));
            }

            if (metaData != null) {
                StringWriter writer = new StringWriter();
                metaData.store(writer, null);
                PartSource metadataPart = new ByteArrayPartSource(relativePath + ".meta", writer.getBuffer().toString().getBytes());
                formParts.add(new FilePart(DEPLOYER_METADATA_FILE_PARAM, metadataPart));
            }

            formParts.add(new StringPart(DEPLOYER_CONTENT_LOCATION_PARAM, relativePath));
            formParts.add(new StringPart(DEPLOYER_SITE_PARAM, site));

            PostMethod postMethod = new PostMethod(requestUrl.toString());
            postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

            Part[] parts = new Part[formParts.size()];

            for (int i = 0; i < formParts.size(); i++) parts[i] = formParts.get(i);
            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
            HttpClient client = new HttpClient();
            int status = client.executeMethod(postMethod);
            postMethod.releaseConnection();
        }
        catch(Exception err) {
            throw new Exception("error while preview deploying '" + path + "'" + err);
        }
    }

    public void delete(String path) throws IOException {
        if (remoteDeployEnabled) {
            try {
                remoteDeploy(path, null, null, true);
            } catch (Exception e) {
                LOGGER.error("Error while deleting preview content: " + path, e);
            }
        } else {
            fileSystemDelete(path);
        }
    }

    protected void fileSystemDelete(String path) throws IOException {
        File file = new File(deployRoot, path);
        if (file.isFile()) {
            File metaDataFile = new File(metaDataRoot, path + METADATA_EXTENSION);
            metaDataFile.delete();

            cachedMetaData.remove(path);
        }
        FileUtils.forceDelete(file);
    }

    private Properties getFileMetaData(String path, File file) throws IOException {
        if (file.isDirectory()) {
            return null;
        }

        Properties metaData = cachedMetaData.get(path);
        if (metaData == null) {
            File metaDataFile = new File(metaDataRoot, path + METADATA_EXTENSION);
            if (metaDataFile.exists()) {
                Reader metaDataReader = new BufferedReader(new FileReader(metaDataFile));

                try {
                    metaData = new Properties();
                    metaData.load(metaDataReader);
                } finally {
                    try {
                        metaDataReader.close();
                    } catch (IOException e) {
                    }
                }

                cachedMetaData.put(path, metaData);
            }
        }

        return metaData;
    }

}
