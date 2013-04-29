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

import java.io.IOException;
import java.util.List;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.dom4j.Document;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmVersionService;
import org.craftercms.cstudio.alfresco.dm.to.DmVersionDetailTO;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//

public class DmVersionServiceScript extends BaseProcessorExtension {
	
private static final Logger LOGGER = LoggerFactory.getLogger(DmVersionServiceScript.class);

	protected ServicesManager _servicesManager;
	
	/**
	 * Get version History
	 * 
	 * @param site
	 * @throws ServiceException 
	 */
	public List<DmVersionDetailTO> getVersionHistory(String site,String relativePath,int maxHistory) throws ServiceException {

		List<DmVersionDetailTO> versions=_servicesManager.getService(DmVersionService.class).getVersionHistory(site, relativePath,maxHistory);
		
		return versions;
	}

	public String revert(String site,String relativePath,int version) throws ServiceException {

		String result="Success";
		try {
			_servicesManager.getService(DmVersionService.class).restore(site, relativePath, String.valueOf(version), false);
		} catch(Exception e) {
			result="Failed";
		}
		return result;
	}
	
	public String getContentByVersion(String site,String relativePath,String version) throws ServiceException {
		Document document = _servicesManager.getService(DmContentService.class).getContentXmlByVersion(site,null, relativePath,version);
		
		if (document != null) {
			try {
				return XmlUtils.convertDocumentToString(document);
			} catch (IOException e) {
				throw new ServiceException("Failed to get content - site: " + site + ", version: " + version + ", relativePath: "
						+ relativePath, e);
			}
		} else {
			throw new ServiceException("Failed to get content for - site: " + site + ", version: " + version + ", relativePath: "
					+ relativePath);
		}
	}
	
	/**
	 * Creates a new version of <code>nodeRef</code> instance using alfresco version service  and returns a new <code>DmVersionDetailTO</code> instance.
	 * 
	 * @param nodeRef used to create a new version
	 * 
	 * 
	 * @return a new <code>DmVersionDetailTO</code> instance
	 */
	public DmVersionDetailTO createVersion(String nodeRef) throws ServiceException {
		return _servicesManager.getService(DmVersionService.class).createVersion(nodeRef, null);
		
	}
	
	/**
	 * Gets the current version of a node.
	 * 
	 * @param nodeRef queried
	 * 
	 * 
	 */
	public DmVersionDetailTO getCurrentVersion(String nodeRef) throws ServiceException {
		return _servicesManager.getService(DmVersionService.class).getCurrentVersion(nodeRef);
	}
	
	/**
	 * Deletes version of the node passed as argument
	 * 
	 * @param nodeRef used to delete a version
	 * 
	 * @param version to be deleted
	 * 
	 */
	void deleteVersion(NodeRef nodeRef, Version version) throws ServiceException {
		_servicesManager.getService(DmVersionService.class).deleteVersion(nodeRef, version);
	}
	
    public void createNextMajorVersion(String site, String path) {
    	_servicesManager.getService(DmVersionService.class).createNextMajorVersion(site, path);
    }

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

    
}
