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

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.SiteService;
import org.craftercms.cstudio.alfresco.to.SiteTO;

/**
 * A wrapper class of CStudio SiteService to expose the service to Alfresco
 * javascript layer
 * 
 * @author hyanghee
 * 
 */
public class SiteServiceScript extends BaseProcessorExtension {

	protected ServicesManager _servicesManager;
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }
	
	/**
	 * get the preview sever URL of the given site
	 * 
	 * @param site
	 * @return preview server URL
	 */
	public String getPreviewServerUrl(String site) {
		return _servicesManager.getService(SiteService.class).getPreviewServerUrl(site);
	} 

	/**
	 * CStudio AuthoringServer URL
	 * 
	 * @param site
	 * @return authoring server URL
	 */
	public String getAuthoringServerUrl(String site) {
		return _servicesManager.getService(SiteService.class).getAuthoringServerUrl(site);
	}
	
	/**
	 * @param site
	 * @return the formServerUrl
	 */
	public String getFormServerUrl(String site) {
		return _servicesManager.getService(SiteService.class).getFormServerUrl(site);
	}

	/**
	 * CStudio LiveServer URL
	 * 
	 * @param site
	 * @return live server URL
	 */
	public String getLiveServerUrl(String site) {
		return _servicesManager.getService(SiteService.class).getLiveServerUrl(site);
	}

	/**
	 * CStudio Admin EmailAddress
	 * 
	 * @param site
	 * @return admin email address 
	 */
	public String getAdminEmailAddress(String site) {
		return _servicesManager.getService(SiteService.class).getAdminEmailAddress(site);
	}

	/**
	 * get the collaborative sandbox of the given site 
	 * 
	 * @return
	 */
	public String getCollabSandbox(String site) {
		return _servicesManager.getService(SiteService.class).getCollabSandbox(site);
	}
	
	/**
	 * get configuration at the given path by converting XML to JSON 
	 * 
	 * @param site
	 * @param path
	 * @param applyEnv
	 * 			find from the environment overrides location?
	 * @return configuration in JSON
	 */
	public String getConfiguration(String site, String path, boolean applyEnv) {
		JSON response = null;
		String xml = _servicesManager.getService(SiteService.class).getConfiguration(site, path, applyEnv);
		if (xml != null) {
			xml = xml.replaceAll("\\n([\\s]+)?+", "");
            xml = xml.replaceAll("<!--(.*?)-->", "");
			XMLSerializer xmlSerializer = new XMLSerializer();
			response = xmlSerializer.read(xml);
		} else {
			response = new JSONObject();
		}
		return response.toString();
	}

    public void initializeCache(String site) {
        _servicesManager.getService(PersistenceManagerService.class).initializeCacheScope(site);
    }
	
	public void createObjectStatesforNewSite(ScriptNode siteRoot){
		_servicesManager.getService(SiteService.class).createObjectStatesforNewSite(siteRoot.getNodeRef());
	}

    public void extractDependenciesForNewSite(ScriptNode siteRoot) {
        _servicesManager.getService(SiteService.class).extractDependenciesForNewSite(siteRoot.getNodeRef());
    }
	
	public void addSiteGroupToPublishingRoot(ScriptNode siteRoot,String siteName){
		_servicesManager.getService(SiteService.class).addSiteGroupToPublishingRoot(siteRoot.getNodeRef(),siteName);
	}

    public void extractMetadataForNewSite(ScriptNode siteRoot) {
        _servicesManager.getService(SiteService.class).extractMetadataForNewSite(siteRoot.getNodeRef()); {

        }
    }
	/**
	 * get site configuration 
	 * 
	 * @param key
	 * @param mappingKey
	 * @return
	 */
	public String getSiteConfig(String key, String mappingKey) {
		SiteTO site = _servicesManager.getService(SiteService.class).getSite(mappingKey, key);
		if (site != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("site", site);
			return jsonObject.toString();
		} else {
			return null;
		}
	}

	
    /**
     * Reload site configurations
     */
    public void reloadSiteConfigurations() {
    	this._servicesManager.getService(SiteService.class).reloadSiteConfigurations();
    }

    public void deleteSite(String site) {
        _servicesManager.getService(SiteService.class).deleteSite(site);
    }
}
