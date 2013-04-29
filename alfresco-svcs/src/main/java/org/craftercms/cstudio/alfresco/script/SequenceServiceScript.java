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
import org.craftercms.cstudio.alfresco.service.api.SequenceService;
import org.craftercms.cstudio.alfresco.service.exception.SequenceException;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;

/**
 * A wrapper class of CStudio SequenceService to expose the service to Alfresco javascript layer
 * 
 * @author hyanghee
 * 
 */
public class SequenceServiceScript extends BaseProcessorExtension {

	protected ServicesManager _servicesManager;

	/**
	 * get the next identity from the default name space. since this is a
	 * default namespace no assumptions should be made about what ID is returned
	 * beyond the fact that it will be unique over the lifespan of the sequence.
	 * 
	 * @return next available id
	 * @throws ServiceException
	 */
	public String next() throws ServiceException {
		return String.valueOf(_servicesManager.getService(SequenceService.class).next());
	}

	/**
	 * get the next identity for the given namespace. this method will error if
	 * the given name space does not exist.
	 * 
	 * @param namespace
	 *            the spoke of the sequence.
	 * @return next available id
	 * @throws ServiceException
	 */
	public String next(String namespace) throws SequenceException {
		return String.valueOf(_servicesManager.getService(SequenceService.class).next(namespace));
	}

	/**
	 * get the next identity for the given namespace. this method will create
	 * the if it does not exist and create is true
	 * 
	 * @param namespace
	 *            the spoke of the sequence.
	 * @param create
	 * @return next available id
	 * @throws ServiceException
	 */
	public String next(String namespace, boolean create) throws SequenceException {
		return String.valueOf(_servicesManager.getService(SequenceService.class).next(namespace, create));
	}

	/**
	 * create a sequence for the given namespace. This method will return true
	 * if a sequence already exists for the given namespace
	 * 
	 * @param namespace
	 *            the spoke of the sequence.
	 * @return true if sequence created
	 */
	public String createSequence(String namespace) {
		return String.valueOf(_servicesManager.getService(SequenceService.class).createSequence(namespace));
	}

	/**
	 * does a sequence exist for the given namespace?
	 * 
	 * @param namespace
	 *            the spoke of the sequence.
	 * @return true if sequence exists
	 */
	public String sequenceExists(String namespace) {
		return String.valueOf(_servicesManager.getService(SequenceService.class).sequenceExists(namespace));
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

	
}
