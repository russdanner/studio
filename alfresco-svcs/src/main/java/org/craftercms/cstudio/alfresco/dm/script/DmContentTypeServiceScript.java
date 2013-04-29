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

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentTypeService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.ContentTypeConfigTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A wrapper class of WcmContentTypeService that exposes WCM ContentTypeService
 * in Alfresco javascript API
 * 
 * @author hyanghee
 * 
 */
public class DmContentTypeServiceScript extends BaseProcessorExtension {

	private static final Logger logger = LoggerFactory
			.getLogger(DmContentTypeServiceScript.class);

	protected ServicesManager _servicesManager;

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

	/**
	 * change the content type of the content at the given path
	 * 
	 * @param site
	 * @param sub
	 * @param path
	 * @param contentType
	 * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
	 */
	public void changeContentType(String site, String sub, String path,
			String contentType) throws ServiceException {
		try {
			_servicesManager.getService(DmContentTypeService.class)
					.changeContentType(site, sub, path, contentType);
		} catch (ServiceException e) {
			logger.error("Error while changing template", e);
		} catch (RuntimeException r) {
			logger.error("Error while changing template", r);
		}
	}

	/**
	 * return all content types available in the given site
	 * 
	 * @param site
	 * @return content types
	 */
	public List<ContentTypeConfigTO> getAllContentTypes(String site) {
		return _servicesManager.getService(DmContentTypeService.class).getAllContentTypes(site);
	}

	/**
	 * get all searchable content types
	 * 
	 * @param site
	 * @param user
	 * @return searchable content types
	 * @throws ServiceException
	 */
	public List<ContentTypeConfigTO> getAllSearchableContentTypes(String site,
			String user) throws ServiceException {
		return _servicesManager.getService(DmContentTypeService.class)
				.getAllSearchableContentTypes(site, user);
	}

	/**
	 * get allowed content types
	 * 
	 * @param site
	 * @param relativePath
	 * @return allowed content types
	 * @throws ServiceException
	 */
	public List<ContentTypeConfigTO> getAllowedContentTypes(String site,
			String relativePath) throws ServiceException {
		return _servicesManager.getService(DmContentTypeService.class)
				.getAllowedContentTypes(site, relativePath);
	}

	/**
	 * get content type configuration of the content at the given path
	 * 
	 * @param site
	 * @param sub
	 * @param path
	 *            the path to the content
	 * @return Content type configuration
	 * @throws ServiceException
	 */
	public ContentTypeConfigTO getContentTypeByPath(String site, String sub,
			String path) throws ServiceException {
		return _servicesManager.getService(DmContentTypeService.class)
				.getContentTypeByRelativePath(site, sub, path);
	}

	/**
	 * get content type configuration of the given type
	 * 
	 * @param site
	 * @param type
	 * @return Content type configuration
	 * @throws ServiceException
	 */
	public ContentTypeConfigTO getContentType(String site, String type)
			throws ServiceException {
		return _servicesManager.getService(DmContentTypeService.class)
				.getContentType(site, type);
	}
}
