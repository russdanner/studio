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
package org.craftercms.cstudio.alfresco.dm.webscript.content;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmTransactionService;
import org.craftercms.cstudio.alfresco.dm.util.WebScriptUtils;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.GeneralLockService;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.service.exception.ContentNotFoundException;
import org.craftercms.cstudio.alfresco.util.ContentUtils;
import org.craftercms.cstudio.alfresco.webscript.constant.CStudioWebScriptConstants;
import org.craftercms.cstudio.api.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Webscript for downloading files from DM
 * 
 * @author hyanghee
 * @author Dejan Brkic
 */
public class GetContentWebScript extends AbstractWebScript {

	private static final Logger logger = LoggerFactory.getLogger(GetContentWebScript.class);

    protected ServicesManager _servicesManager;

    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		// get parameters and check for mandatory parameters
		String site = req.getParameter(CStudioWebScriptConstants.PARAM_SITE);
		WebScriptUtils.checkMandatoryParameter(CStudioWebScriptConstants.PARAM_SITE, site);
		String path = req.getParameter(CStudioWebScriptConstants.PARAM_PATH);
		WebScriptUtils.checkMandatoryParameter(CStudioWebScriptConstants.PARAM_PATH, path);
		String edit = req.getParameter(CStudioWebScriptConstants.PARAM_EDIT);
		String draft = req.getParameter(CStudioWebScriptConstants.PARAM_DRAFT);
		String changeTemplate = req.getParameter(CStudioWebScriptConstants.PARAM_CHANGETEMPLATE);
        Boolean isDraft = Boolean.valueOf(draft);
        Boolean isChangeTemplate = Boolean.valueOf(changeTemplate);
        if (logger.isDebugEnabled()) {
			logger.debug("dm content requested by site: " + site + " and path: " + path + ", edit: " + edit);
		}
		boolean isEdit = (StringUtils.isEmpty(edit)) ? false : edit.equalsIgnoreCase("true");
		
		String[] levels = path.split("/");
		// if no file name in the path, throw an error
		if (levels.length < 1 && StringUtils.isEmpty(levels[levels.length - 1])) {
			throw new WebScriptException("Failed to get content. No file name specified in the path: " + path);
		} else {
			String fileName = levels[levels.length - 1];
			InputStream input = null;
			OutputStream output = null;
			try {

                PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
                GeneralLockService generalLockService = getServicesManager().getService(GeneralLockService.class);
                DmContentService dmContentService = getServicesManager().getService(DmContentService.class);
                DmTransactionService dmTransactionService = getServicesManager().getService(DmTransactionService
                    .class);
                ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
                String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
                NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
                UserTransaction tx = dmTransactionService.getNonPropagatingUserTransaction();
                if (nodeRef != null) {
                    generalLockService.lock(nodeRef.getId());
                }
                try {
                    if (isEdit) {
                        persistenceManagerService.setSystemProcessing(fullPath, true);
                    }

                    tx.begin();
                    input = dmContentService.getContent(site, path, isEdit, !isChangeTemplate);
                    tx.commit();
                    if (isEdit) {
                        persistenceManagerService.transition(fullPath, ObjectStateService.TransitionEvent.EDIT);
                        persistenceManagerService.setSystemProcessing(fullPath, false);
                    }
                    /***************************************/
                    String[] values = path.split("\\.");
                    String fileType = values[values.length - 1];
                    res.setHeader("Content-Type", "application/" + fileType.toLowerCase());
                    res.setHeader("Content-Disposition", "inline;filename=\"" + fileName + "\"");
                    res.setHeader("Cache-Control", "max-age=0");
                    res.setHeader("Pragma", "public");
                    // write the file
                    if (logger.isDebugEnabled()) {
                        logger.debug("content asset found. Transmitting the file.");
                    }
                    output = res.getOutputStream();
                    byte[] buffer = new byte[CStudioWebScriptConstants.READ_BUFFER_LENGTH];
                    int read = 0;
                    while ((read = input.read(buffer)) > 0) {
                        output.write(buffer, 0, read);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("file transmission completed.");
                    }
                } catch (HeuristicRollbackException e) {
                    handleTransactionError(site, path, tx, res);
                } catch (RollbackException e) {
                    handleTransactionError(site, path, tx, res);
                } catch (SystemException e) {
                    handleTransactionError(site, path, tx, res);
                } catch (HeuristicMixedException e) {
                    handleTransactionError(site, path, tx, res);
                } catch (NotSupportedException e) {
                    handleTransactionError(site, path, tx, res);
                } finally {
                    if (nodeRef != null) {
                        generalLockService.unlock(nodeRef.getId());
                    }
                }
            } catch (AccessDeniedException e) {
				res.setStatus(Status.STATUS_CONFLICT);
				Writer writer = res.getWriter();
				writer.append(e.getMessage());
				writer.close();
			} catch (ContentNotFoundException e) {
				res.setStatus(Status.STATUS_BAD_REQUEST);
				Writer writer = res.getWriter();
				writer.append("Failed to get content by site: " + site + " and path: " + path);
				writer.close();
			} finally {
				ContentUtils.release(input, output);
			}
		}
	}

    private void handleTransactionError(String site, String path, UserTransaction tx, WebScriptResponse res) throws
            IOException {
        logger.warn("Error rolling executing transaction for get content; site " + site + ", path " + path);
        res.setStatus(Status.STATUS_BAD_REQUEST);
        Writer writer = res.getWriter();
        writer.append("Failed to get content by site: " + site + " and path: " + path);
        writer.close();

        try {
            tx.rollback();
        } catch (SystemException e1) {
            logger.warn("Error rolling back transaction for get content action");
        }
    }
}
