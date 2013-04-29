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

import net.sf.json.JSONObject;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.ProfileService;
import org.craftercms.cstudio.alfresco.to.UserProfileTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class of CStudio ProfileService to expose the service to Alfresco
 * javascript layer
 * 
 * @author spallam
 * 
 */

public class ProfileServiceScript extends BaseProcessorExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceScript.class);
	
	protected ServicesManager _servicesManager;
	
	/**
	 * assign role to user given user and role
	 * 
	 * @param user
	 * @param role
	 * @return true if the user role is assigned
	 */
	public String assignUserRole(String user, String role, String site) {
		boolean result = _servicesManager.getService(ProfileService.class).assignUserRole(user, role, site);
		return String.valueOf(result);
	}

	/**
	 * remove role assigned to user given user and role
	 * 
	 * @param user
	 * @param role
	 * @return true if the user role is removed
	 */
	public String removeUserRole(String user, String role, String site) {
		boolean result = _servicesManager.getService(ProfileService.class).removeUserRole(user, role, site);
		return String.valueOf(result);
	}

	/**
	 * get user profile given user id
	 * 
	 * @param user
	 * @return user profile in JSON
	 */
	public String getUserProfile(String user, String site) {

		UserProfileTO userProfile = _servicesManager.getService(ProfileService.class).getUserProfile(user, site, true);
		JSONObject userObject = new JSONObject();
		if (userProfile != null) {
			try {
				if (userProfile.getProfile() != null) {
					userObject.putAll(userProfile.getProfile());
				}
				if (userProfile.getUserRoles() != null) {
					userObject.putAll(userProfile.getUserRoles());
				}
				userObject.put("contextual", userProfile.getContextual());
			} catch (Exception e) {
				LOGGER.error("Failed to get user profile ");
			}
		}
		return userObject.toString();
	}

	/**
	 * Check is user has that particular role
	 * 
	 * @param user
	 * @return true if the user has the role
	 */
	public boolean checkUserRole(String user, String site, String role) {
		return _servicesManager.getService(ProfileService.class).checkUserRole(user, site, role);
	}

	public void setServicesManager(ServicesManager serviceManager) {
		this._servicesManager = serviceManager;
	}

}
