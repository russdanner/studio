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

package org.craftercms.cstudio.share.service;

import org.craftercms.cstudio.share.app.SeamlessAppContext;

import  org.craftercms.cstudio.share.util.ServletUtils;

/**
 * Application services provides access to basic application functionality.
 * The ApplicationService is a pojo bean managed and accessible via IoC container.
 * It is also made available to the webscript container
 * 
 * <ul>
 *   <li>ServletContext</li>
 * </ul>
 * 
 * @author rdanner
 */
public class ApplicationService {

	/**
	 * provide a service that returns the current seamless application context
	 * @return current application context
	 */
	public SeamlessAppContext currentApplicationContext()
	{
		return SeamlessAppContext.currentApplicationContext();
	}

	public SeamlessAppContext getApplicationContext()
	{
		return SeamlessAppContext.currentApplicationContext();
	}

	/** 
	 * @return return the key for the current context nav state
	 * <p>
	 * Default implementation returns a key in the following format:<br/>
	 * URIPART-ROLE<br/>
	 * Examples:<br/>
	 * <table>
	 *   <tr><td>/share/page/user/admin/dashboard</td><td>/user/dashboard-admin</td></tr>
	 *   <tr><td>/share/page/site/legal/dashboard</td><td>/site/dashboard-admin</td></tr>
     * </table>
     * 
     * Notice that some parts of the URI are dropped.
     * <ul>
     *  <li>specific user names and site names</li>
     *  <li>context value</li>
     * </ul>
	 */
	public String determineContextualNavKey()
	{
		String retKey = "";
		SeamlessAppContext appContext = this.currentApplicationContext();
		
		String uri = ServletUtils.determineBrowserUri(appContext.getRequest());
		
		/* drop the servlet context */
		String contextPath = appContext.getRequest().getContextPath();
		
		if(!contextPath.equals("/"))
		{
			uri = uri.replace(contextPath, "");
		}
		
		/* drop variables from the uri */
		if(uri.startsWith("/page/user"))
		{
			uri = "/user" + uri.substring(uri.lastIndexOf("/"));
		}
		else if(uri.startsWith("/page/site"))
		{
			uri = "/site" + uri.substring(uri.lastIndexOf("/"));
		}

		// add logic here to determine the right role
		retKey = uri + "|admin";
		
		return retKey;
	}
	
	/**
	 * given the current template ID, determine the site type
	 * <p/>
	 * Since it does not yet appear to be possible to determine a site type 
	 * after creation at this point in time we'll have to come up with a way to do this. 
	 * After looking at several possibilities I've landed on using the templates 
	 * as probably the best choice.  Template IDs can contain the site type in them.  
	 * Because they are XML files they  can all continue to point to the same FTL files.
	 * Other considerations were the theme, this appeared to require duplication of theme 
	 * assets and page, this seemed too low level and would ultimately effect the URL.
	 * <p/>
 	 * Given the above information the site type will be encoded in to the theme id as 
	 * the first word before the first "-"  so in the case of cstudiowcm-dashboard, cstudiowcm is the site type 
	 * @param templateId
	 * @return site type
	 */
	public String determineSiteType(String templateId)
	{
		return templateId.substring(0, templateId.indexOf("-",0)); 
	}
	
	/**
	 * given the site type, determine the path to the contextual navigation configuration
	 * @param siteType
	 * @return path
	 */
	public String determineContextualNavConfigPath(String siteType)
	{
		return  "DEPRICATED";
				//"/company%20home/cstudio/config/apps/" + 
				//siteType + 
				//"/contextual-nav.xml";
	}
}
