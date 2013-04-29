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
package org.craftercms.cstudio.alfresco.script;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.FormService;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * A wrapper class of CStudio FormService to expose the service to Alfresco
 * javascript layer
 * 
 * @author videepkumar1
 * 
 */
public class FormServiceScript extends BaseProcessorExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormServiceScript.class);

	protected ServicesManager servicesManager;

	/**
	 * Load form definition given form id
	 * 
	 * @param formId
	 * @return {@link Document}
	 */
	public Document loadForm(String formId) throws Exception {
		Document document = null;
		try {
			document = servicesManager.getService(FormService.class).loadForm(formId);
		} catch (ServiceException e) {
			throw e;
		}
		return document;
	}

	/**
	 * Load form definition given form id
	 * 
	 * @param formId
	 * @return {@link String}
	 */
	public String loadFormAsString(String formId) throws Exception {
		String formString = null;
		try {
			formString = servicesManager.getService(FormService.class).loadFormAsString(formId);
		} catch (ServiceException e) {
			throw e;
		}
		return formString;
	}

	/**
	 * generic method for getting a given form asset as a string
	 * 
	 * @param formId
	 * @param componentName
	 * @return component as string
	 */
	public String loadComponentAsString(String formId, String componentName) throws ServiceException {

		return servicesManager.getService(FormService.class).loadComponentAsString(formId, componentName);
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	

	
}
