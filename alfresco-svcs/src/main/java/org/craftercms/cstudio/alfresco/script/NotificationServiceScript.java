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

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.NotificationService;
import org.craftercms.cstudio.alfresco.to.MessageTO;

/**
 * A wrapper class of CStudio NotificationService to expose the service to Alfresco javascript layer 
 * 
 * @author hyanghee
 *
 */
public class NotificationServiceScript extends BaseProcessorExtension {
	
	protected ServicesManager _servicesManager;

	/**
	 * get canned rejection reasons given a site name 
	 * 
	 * @param site
	 * @return messages
	 */
	public List<MessageTO> getCannedRejectionReasons(String site) {
		return _servicesManager.getService(NotificationService.class).getCannedRejectionReasons(site);
	}

	/**
	 * get a general message by the given key
	 * 
	 * @param site
	 * @param key
	 * @return message
	 */
	public String getGeneralMessage(String site, String key) {
		return _servicesManager.getService(NotificationService.class).getGeneralMessage(site, key);
	}
	
	public void sendApprovalNotification(String site,String to,String url)
	{
		_servicesManager.getService(NotificationService.class).sendApprovalNotification(site, to,url, AuthenticationUtil.getAdminUserName());
	}
	
	/**
	 * send notice upon the action given?
	 * 
	 * @param site
	 * @param action
	 * @return
	 */
	public boolean sendNotice(String site, String action) {
		return this._servicesManager.getService(NotificationService.class).sendNotice(site, action);
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}
	
	
}
