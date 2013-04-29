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

import java.util.List;
import java.util.Set;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PermissionService;

/**
 * A wrapper of PermissionService to expose the service to Alfresco JS layer 
 * 
 * @author Sandra O'Keeffe
 *
 */
public class PermissionServiceScript extends BaseProcessorExtension {

	protected ServicesManager _servicesManager;
	

	/**
	 * Get the user's permissions based on site, user, groups 
	 * for a give path.
	 * 
	 * @param site
	 * @param path
	 * @param user
	 * @param groups
	 * @return
	 */
	public Set<String> getUserPermissions(String site, String path,
			String user, List<String> groups) {
		return _servicesManager.getService(PermissionService.class).getUserPermissions(site, path, user, groups);
	}
	
	/**
	 * get user roles based on site and user
	 * 
	 * @param site
	 * @param user
	 * @return list of user roles
	 */
	public Set<String> getUserRoles(String site, String user) {
		return _servicesManager.getService(PermissionService.class).getUserRoles(site, user);
	}

	/**
	 * get all roles from the site 
	 * 
	 * @param site
	 * @return
	 */
	public Set<String> getRoles(String site) {
		return _servicesManager.getService(PermissionService.class).getRoles(site);
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}
	
	
	
}
