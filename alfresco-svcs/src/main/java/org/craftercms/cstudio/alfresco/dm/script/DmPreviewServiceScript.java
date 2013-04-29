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
package org.craftercms.cstudio.alfresco.dm.script;


import javax.xml.transform.Result;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.craftercms.cstudio.alfresco.dm.service.api.DmPreviewService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmTransactionService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.InputStreamContent;

/**
 * A wrapper class of WcmPreviewService that exposes WcmPreviewService in
 * Alfresco javascript API
 *
 * @author hyanghee
 * @author Dejan Brkic
 *
 */
public class DmPreviewServiceScript extends BaseProcessorExtension {

    private static final Logger logger = LoggerFactory.getLogger(DmPreviewServiceScript.class);

    protected ServicesManager _servicesManager;

    public void deleteContent(String site, String path) throws ServiceException {
        try {
            _servicesManager.getService(DmPreviewService.class).deleteContent(site, path);
        } catch (ServiceException e) {
            logger.error("Failed to clean content from Preview", e);
        }
    }
    
    /**
	 * write content to the given path in the preview layer
	 * 
	 * @param site
	 *            site to write the content to
	 * @param path
	 *            path to write the content to
	 * @param fileName
	 *            content name
	 * @param contentType
	 * 				content type
	 * @param input
	 *            content input stream
	 * @throws ServiceException
	 */
	public void writeContent(final String site, final String path, final String fileName, final String contentType, final InputStreamContent input) throws ServiceException {
        TransactionHelper txHelper = _servicesManager.getService(DmTransactionService.class).getTransactionHelper();
        try {
            txHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Result>(){
                @Override
                public Result execute() throws Throwable {
                	_servicesManager.getService(DmPreviewService.class).writeContent(site, path, fileName, contentType, input.getInputStream());
                    return null;
                }
                
            });
        } catch (ServiceException e) {
            logger.error("unable to write content at path ["+path+"] fileName["+fileName+"]",e);
        } catch (RuntimeException e){
            logger.error("unable to write content at path ["+path+"] fileName["+fileName+"]",e);
        }

    }
	
	 /**
	 * clean the given content from the preview layer
	 *  
	 * @param site
	 * @param path
	 * @throws ServiceException
	 */
	public void cleanContent(String site, String path) throws ServiceException {
        try {
        	_servicesManager.getService(DmPreviewService.class).cleanContent(site, path);
        } catch (ServiceException e) {
            logger.error("Failed to clean content from Preview",e);
        }
    }

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

}
