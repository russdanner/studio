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

// Java imports
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

// 3rd party imports
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.to.DmContentItemTO;
import org.craftercms.cstudio.alfresco.dm.to.DmVersionDetailTO;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.alfresco.repo.processor.BaseProcessorExtension;

import org.craftercms.cstudio.alfresco.service.ServicesManager;
// CStudio imports
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.service.api.DmVersionService;


/**
 * Version Script for DM 
 * 
 * @author subhasha
 *
 */
public class DmVersionServiceScript extends BaseProcessorExtension {
	private static final Logger LOGGER = LoggerFactory.getLogger(DmVersionServiceScript.class);
	
	protected ServicesManager _servicesManager;

    public String getVersionHistory(String site, String path, int maxHistory, boolean showMinor) throws ServiceException {
        PersistenceManagerService persistenceManagerService = _servicesManager.getService(PersistenceManagerService.class);
        ServicesConfig servicesConfig = _servicesManager.getService(ServicesConfig.class);
        String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
		List<DmVersionDetailTO> versions=_servicesManager.getService(DmVersionService.class).getVersionHistory(site, path, maxHistory, showMinor);
        DmContentItemTO contentItem = persistenceManagerService.getContentItem(fullPath);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("versions", versions);
        jsonObject.put("item", contentItem);
        return jsonObject.toString();
		
		//return versions;
	}
	
	public String revert(String site, String path, String version) throws ServiceException {

		String result="Success";
		try
		{
			_servicesManager.getService(DmVersionService.class).restore(site, path, version);
		}
		catch(Exception e)
		{
			result = "Failed";
		}
		return result;					
}

    public void createNextMajorVersion(String site, String path) {
        _servicesManager.getService(DmVersionService.class).createNextMajorVersion(site, path);
    }

    public void createNextMinorVersion(String site, String path) {
        _servicesManager.getService(DmVersionService.class).createNextMinorVersion(site, path);
    }
		
	

    public String getContentByVersion(String site,String relativePath, String version) throws ServiceException
    {
        Document document = _servicesManager.getService(DmContentService.class).getContentXmlByVersion(site, null, relativePath, version);
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

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}
    
    
}
