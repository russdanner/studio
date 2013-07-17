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
package org.craftercms.cstudio.alfresco.dm.script;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.constant.CStudioConstants;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmRenameService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmTransactionService;
import org.craftercms.cstudio.alfresco.dm.to.DmContentItemTO;
import org.craftercms.cstudio.alfresco.dm.to.DmOrderTO;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.GeneralLockService;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService.State;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.service.exception.ContentNotAllowedException;
import org.craftercms.cstudio.alfresco.service.exception.ContentNotFoundException;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.ContentAssetInfoTO;
import org.craftercms.cstudio.alfresco.to.ResultTO;
import org.craftercms.cstudio.alfresco.util.ScriptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.InputStreamContent;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class of DmContentService that exposes the service in Alfresco
 * javascript API
 *
 * @author Dejan Brkic
 *
 */
public class DmContentServiceScript extends BaseProcessorExtension {

    private static final Logger logger = LoggerFactory.getLogger(DmContentServiceScript.class);

    /** Constants */
    protected static final String JSON_KEY_ITEMS = "items";
    protected static final String JSON_KEY_ITEM = "item";
    protected static final String JSON_KEY_REMOVED_ITEMS = "removedItems";
    protected static final String JSON_KEY_URI = "uri";

    protected ServicesManager servicesManager;
    public ServicesManager getServicesManager() {
        return servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    /**
     * check whether the content exists at the given path
     *
     * @param site
     * @param relativePath
     * @return true if the content exists
     */
    public String contentExists(String site, String relativePath,String originalPath) {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        return String.valueOf(dmContentService.contentExists(site, relativePath, originalPath));
    }

    /**
     * get pages from the given relative path to the given depth
     *
     * @param site
     * @param sub
     * @param relativePath
     * @param depth
     * @param orderName
     * @param checkChildren
     * @return pages
     * @throws org.craftercms.cstudio.alfresco.service.exception.ContentNotFoundException
     */
    public String getFolders(String site, String sub, String relativePath, int depth, String orderName, boolean checkChildren)
            throws ContentNotFoundException {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        DmContentItemTO item = dmContentService.getItems(site, sub, relativePath, depth, true, orderName, checkChildren);

        item = item.filterOutFiles();
        item=item.filterOutHidden();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", item);
        return jsonObject.toString();
    }

    /**
     * get a content item specified at the given path
     *
     * @param site
     * @param sub
     * @param relativePath
     * @return content item
     * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
     */
    public String getItem(String site, String sub, String relativePath, boolean populateDependencies)
            throws ServiceException {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String fullPath = servicesConfig.getRepositoryRootPath(site) + relativePath;
        DmContentItemTO item = persistenceManagerService.getContentItem(fullPath, populateDependencies);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_KEY_ITEM, item);
        return jsonObject.toString();
    }

    /**
     * get pages from the given relative path to the given depth
     *
     * @param site
     * @param sub
     * @param relativePath
     * @param depth
     * @param orderName
     * @param checkChildren
     * @return pages
     * @throws ContentNotFoundException
     */
    public String getPages(String site, String sub, String relativePath, int depth, String orderName, boolean checkChildren)
            throws ContentNotFoundException {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        DmContentItemTO item = dmContentService.getItems(site, sub, relativePath, depth, true, orderName, checkChildren, false);
        item=item.filterOutHidden();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", item);
        return jsonObject.toString();
    }

    /**
     * Return the order value given a re-order request if before and after are
     * not provided, the order will be ORDER_INCREMENT. if before is not provided, the order
     * will be (0 + after) / 2. if after is not provided, the order will be
     * before + ORDER_INCREMENT
     *
     * @param site
     * @param path
     * @param sub
     *            the subordinate site key. This can be used for geo or any
     *            other type of subordination _EN _ASIA _DEPTX
     * @param before
     *            the path to insert after.
     * @param after
     *            the path to insert ahead of.
     * @param orderName
     *            the name of order
     * @return new order value
     * @throws ServiceException
     */
    public double reOrderContent(String site, String path, String sub, String before, String after,
                                 String orderName) throws ServiceException {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        return dmContentService.reOrderContent(site, path, sub, before, after, orderName);
    }

    public void writeContentAndRename(final String site, final String path, final String targetPath, final String fileName, final String contentType, final Content input,
                                      final String createFolders, final  String edit, final String unlock, final boolean createFolder) throws ServiceException {
        String id = site + ":" + path + ":" + fileName + ":" + contentType;
        GeneralLockService generalLockService = servicesManager.getService(GeneralLockService.class);
        if (!generalLockService.tryLock(id)) {
            generalLockService.lock(id);
            generalLockService.unlock(id);
            return;
        }
        try {
            final PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
            final RetryingTransactionHelper txnHelper = servicesManager.getService(DmTransactionService.class).getRetryingTransactionHelper();
            persistenceManagerService.disableBehaviour(CStudioContentModel.ASPECT_PREVIEWABLE);
            RetryingTransactionHelper.RetryingTransactionCallback<String> renameCallBack = new RetryingTransactionHelper.RetryingTransactionCallback<String>() {
                public String execute() throws Throwable {
                    writeContent(site, path, fileName, contentType, input, createFolders, edit, unlock);
                    rename(site, null, path, targetPath, createFolder);
                    return null;
                }
            };
            txnHelper.doInTransaction(renameCallBack, false, true);
            persistenceManagerService.enableBehaviour(CStudioContentModel.ASPECT_PREVIEWABLE);
        } catch (Throwable t) {
            logger.error("Error while write and rename: ", t);
        } finally {
            generalLockService.unlock(id);
        }
    }

    /**
     * write content to WCM
     *
     * @param site
     * @param path
     * @param fileName
     * @param contentType
     * @param input
     * @param createFolders
     * 			create missing folders in path?
     * @param edit
     * @param unlock
     * 			unlock the content upon edit?
     * @throws ServiceException
     */
    public void writeContent(String site, String path, String fileName, String contentType, Content input,
                             String createFolders, String edit, String unlock) throws ServiceException {
        Map<String, String> params = new FastMap<String, String>();
        params.put(DmConstants.KEY_SITE, site);
        params.put(DmConstants.KEY_PATH, path);
        params.put(DmConstants.KEY_FILE_NAME, fileName);
        params.put(DmConstants.KEY_CONTENT_TYPE, contentType);
        params.put(DmConstants.KEY_CREATE_FOLDERS, createFolders);
        params.put(DmConstants.KEY_EDIT, edit);
        params.put(DmConstants.KEY_UNLOCK, unlock);
        String id = site + ":" + path + ":" + fileName + ":" + contentType;
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        GeneralLockService generalLockService = getServicesManager().getService(GeneralLockService.class);
        // processContent will close the input stream
        NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
        String lockKey = id;
        if (nodeRef != null) {
            lockKey = nodeRef.getId();
        }
        generalLockService.lock(lockKey);
        try {
            boolean savaAndClose = (!StringUtils.isEmpty(unlock) && unlock.equalsIgnoreCase("false")) ? false : true;
            DmContentService dmContentService = getServicesManager().getService(DmContentService.class);

            if (nodeRef != null) {
                if (persistenceManagerService.getObjectState(nodeRef) == State.SYSTEM_PROCESSING){
                    logger.error(String.format("Error Content %s is been process (Object State is %s);", fileName, State.SYSTEM_PROCESSING.toString()));
                    throw new RuntimeException(String.format("Content \"%s\" is been processed", fileName));
                }

                persistenceManagerService.setSystemProcessing(fullPath, true);
            }
            //InputStreamContent inputStreamContent = (InputStreamContent)input;
            dmContentService.processContent(id, input.getInputStream(), true, params, DmConstants.CONTENT_CHAIN_FORM);
            persistenceManagerService.setSystemProcessing(fullPath, false);
            String savedFileName = params.get(DmConstants.KEY_FILE_NAME);
            String savedPath = params.get(DmConstants.KEY_PATH);
            fullPath = servicesConfig.getRepositoryRootPath(site) + savedPath;
            if (!savedPath.endsWith(savedFileName)) {
                fullPath = fullPath + "/" + savedFileName;
            }
            fullPath = fullPath.replace("//", "/");
            nodeRef = persistenceManagerService.getNodeRef(fullPath);
            if (nodeRef != null) {
                if (savaAndClose) {
                    persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.SAVE);
                } else {
                    persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.SAVE_FOR_PREVIEW);
                }
            } else {
                persistenceManagerService.insertNewObjectEntry(fullPath);
            }
        } catch (ServiceException e) {
            logger.error("error writing content",e);
            throw e;
        }  catch (RuntimeException e) {
            logger.error("error writing content",e);
            throw e;
        } finally {
            persistenceManagerService.setSystemProcessing(fullPath, false);
            if (nodeRef != null) {
                generalLockService.unlock(lockKey);
            }
        }
    }

    /**
     * write content asset
     *
     * @param site
     * @param path
     * @param assetName
     * @param in
     * @param isImage
     * 			is this asset an image?
     * @param allowedWidth
     * 			specifies the allowed image width in pixel if the asset is an image
     * @param allowedHeight
     * 			specifies the allowed image height in pixel if the asset is an image
     * @param unlock
     * 			unlock the content upon edit?
     * @return content asset info
     * @throws ServiceException
     */
    public ResultTO writeContentAsset(String site, String path, String assetName, InputStreamContent in,
                                      String isImage, String allowedWidth, String allowedHeight, String allowLessSize, String draft, String unlock, String systemAsset) {
        if(assetName != null) {
            assetName = assetName.replace(" ","_");
        }
        /* Disable DRAFT repo Dejan 29.03.2012 */
        boolean isDraft = Boolean.valueOf(draft);
        /*
        if(isDraft) {
        	//path = DmUtils.buildDraftFolder(path);
        	path = DmUtils.getDraftFolder(path);
        }
        */
        boolean isSystemAsset = Boolean.valueOf(systemAsset);
        /***************************************/
        
        Map<String, String> params = new FastMap<String, String>();
        params.put(DmConstants.KEY_SITE, site);
        params.put(DmConstants.KEY_PATH, path);
        params.put(DmConstants.KEY_FILE_NAME, assetName);
        params.put(DmConstants.KEY_IS_IMAGE, isImage);
        params.put(DmConstants.KEY_ALLOW_LESS_SIZE, allowLessSize);
        params.put(DmConstants.KEY_ALLOWED_WIDTH, allowedWidth);
        params.put(DmConstants.KEY_ALLOWED_HEIGHT, allowedHeight);
        params.put(DmConstants.KEY_CONTENT_TYPE, "");
        params.put(DmConstants.KEY_CREATE_FOLDERS, "true");
        
        params.put(DmConstants.KEY_IS_PREVIEW,String.valueOf(isDraft));
        params.put(DmConstants.KEY_UNLOCK, unlock);
        params.put(DmConstants.KEY_SYSTEM_ASSET, String.valueOf(isSystemAsset));

        String id = site + ":" + path + ":" + assetName + ":" + "";
        // processContent will close the input stream
        String fullPath = null;
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        NodeRef nodeRef = null;
        try {
            fullPath = servicesConfig.getRepositoryRootPath(site) + path + "/" + assetName;
            //fullPath = fullPath.replaceAll(" ", "");
            nodeRef = persistenceManagerService.getNodeRef(fullPath);
            
            if (nodeRef != null) {
                if (persistenceManagerService.getObjectState(nodeRef) == State.SYSTEM_PROCESSING){
                    logger.error(String.format("Error Content %s is been process (Object State is %s);", assetName, State.SYSTEM_PROCESSING.toString()));
                    throw new RuntimeException(String.format("Content \"%s\" is been processed", assetName));
                }
                persistenceManagerService.setSystemProcessing(fullPath, true);
            }
            DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
            ResultTO result = dmContentService.processContent(id, in.getInputStream(), false, params, DmConstants.CONTENT_CHAIN_ASSET);
            if (isSystemAsset) {
                ContentAssetInfoTO assetInfoTO = (ContentAssetInfoTO)result.getItem();
                fullPath = fullPath.replace(assetName, assetInfoTO.getFileName());
            }
            nodeRef = persistenceManagerService.getNodeRef(fullPath);
            if (nodeRef != null) {
                persistenceManagerService.transition(fullPath, ObjectStateService.TransitionEvent.SAVE);
            }
            ResultTO resultTO =  ScriptUtils.createSuccessResult(result.getItem());
            return resultTO;
        } catch (ContentNotAllowedException e) {
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_IMAGE_SIZE_ERROR, e.getLocalizedMessage());
        } catch (AlfrescoRuntimeException e) {
            logger.error("Error processing content", e);
            Throwable cause = e.getCause();
            if (cause != null) {
                return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, cause.getLocalizedMessage());
            }
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (Exception e) {
            logger.error("Error processing content", e);
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } finally {
            if (nodeRef != null) {
                persistenceManagerService.setSystemProcessing(fullPath, false);
            }
        }
    }

    /**
     * update content asset from webform (template, css, js)
     *
     * @param site
     * @param path
     * @param content
     * @return content asset info
     * @throws ServiceException
     */
    public ResultTO updateContentAsset(String site, String path, String content) {
        int slashIndex = path.lastIndexOf("/");
        String assetName = path.substring(slashIndex + 1);
        path = path.substring(0, slashIndex);
        /***************************************/

        Map<String, String> params = new FastMap<String, String>();
        params.put(DmConstants.KEY_SITE, site);
        params.put(DmConstants.KEY_PATH, path);
        params.put(DmConstants.KEY_FILE_NAME, assetName);
        params.put(DmConstants.KEY_CONTENT_TYPE, "");
        params.put(DmConstants.KEY_CREATE_FOLDERS, "true");
        params.put(DmConstants.KEY_UNLOCK, "true");

        String id = site + ":" + path + ":" + assetName + ":" + "";
        InputStream in = IOUtils.toInputStream(content);
        // processContent will close the input stream
        String fullPath = null;
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        NodeRef nodeRef = null;
        try {
            fullPath = servicesConfig.getRepositoryRootPath(site) + path + "/" + assetName;
            if (nodeRef != null) {
                persistenceManagerService.setSystemProcessing(fullPath, true);
            }
            DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
            ResultTO result = dmContentService.processContent(id, in, false, params, DmConstants.CONTENT_CHAIN_ASSET);
            persistenceManagerService.transition(fullPath, ObjectStateService.TransitionEvent.SAVE);
            ResultTO resultTO =  ScriptUtils.createSuccessResult(result.getItem());
            return resultTO;
        } catch (ContentNotAllowedException e) {
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_IMAGE_SIZE_ERROR, e.getLocalizedMessage());
        } catch (AlfrescoRuntimeException e) {
            logger.error("Error processing content", e);
            Throwable cause = e.getCause();
            if (cause != null) {
                return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, cause.getLocalizedMessage());
            }
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } catch (Exception e) {
            logger.error("Error processing content", e);
            return ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        } finally {
            if (nodeRef != null) {
                persistenceManagerService.setSystemProcessing(fullPath, false);
            }
        }
    }

    /**
     * Change the location of the content
     * To be used by Rename and Cut/Copy paste operations
     *
     * @param site
     * @param sub
     * @param path
     * @param targetPath
     * @param createFolder
     * @throws ServiceException
     */
    public void renameBulk(final String site, final String sub, final String path, final String targetPath,final boolean createFolder) throws ServiceException {
        String id = site + ":" + path + ":" + targetPath;
        GeneralLockService generalLockService = servicesManager.getService(GeneralLockService.class);
        if (!generalLockService.tryLock(id)) {
            generalLockService.lock(id);
            generalLockService.unlock(id);
            return;
        }
        // have to execute this in transaction
        try {
        RetryingTransactionHelper txnHelper = servicesManager.getService(DmTransactionService.class).getRetryingTransactionHelper();
        RetryingTransactionHelper.RetryingTransactionCallback<String> renameCallBack = new RetryingTransactionHelper.RetryingTransactionCallback<String>() {
            public String execute() throws Throwable {
            	servicesManager.getService(DmRenameService.class).rename(site,sub,path,targetPath,createFolder);
                return null;
            }
        };
        txnHelper.doInTransaction(renameCallBack);
        } catch (Exception ex) {
            logger.error("Error while executing bulk rename for path " + path + " site " + site + ": ", ex);
        } finally {
            generalLockService.unlock(id);
        }
    }

    /**
     * Change the location of the content
     * To be used by Rename and Cut/Copy paste operations
     *
     * @param site
     * @param sub
     * @param path
     * @param targetPath
     * @param createFolder
     * @throws ServiceException
     */
    public void rename(final String site, final String sub, final String path, final String targetPath,final boolean createFolder) throws ServiceException {

        RetryingTransactionHelper txnHelper = servicesManager.getService(DmTransactionService.class).getRetryingTransactionHelper();
        RetryingTransactionHelper.RetryingTransactionCallback<String> renameCallBack = new RetryingTransactionHelper.RetryingTransactionCallback<String>() {
            public String execute() throws Throwable {
                servicesManager.getService(DmRenameService.class).rename(site,sub,path,targetPath,createFolder);
                return null;
            }
        };
        txnHelper.doInTransaction(renameCallBack);
    }

    /**
     * cancel editing on dm content by unlocking the content
     *
     * @param site
     * @param path
     * @throws ContentNotFoundException
     */
    public void cancelEditing(String site, String path) throws ContentNotFoundException {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        dmContentService.cancelEditing(site, path);
    }

    /**
     * create a folder with the name given at the give path
     *
     * @param site
     * 			the target site
     * @param path
     * 			the target location
     * @param name
     * 			folder name to create
     * @return
     */
    public ResultTO createFolder(String site, String path, String name) {
        ResultTO result = null;
        try {
            DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
            dmContentService.createFolder(site, path, name);
            result = ScriptUtils.createSuccessResult(path + "/" + name);
        } catch (ServiceException e) {
            result = ScriptUtils.createFailureResult(CStudioConstants.HTTP_STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return result;
    }

    /**
     * Return page orders at the give path
     *
     * @param site
     * @param path
     * @param sub
     * @param orderName
     * @return DmOrderTO that contains paths, internal names and their order values
     * @throws ServiceException
     */
    public List<DmOrderTO> getOrders(String site, String path, String sub, String orderName)
            throws ServiceException {
        DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
        List<DmOrderTO> dmOrderTOs = dmContentService.getOrders(site, path, sub, orderName, false);
        for (DmOrderTO dmOrderTO : dmOrderTOs) {
            dmOrderTO.setName(StringUtils.escape(dmOrderTO.getName()));
        }
        return dmOrderTOs;
    }
}
