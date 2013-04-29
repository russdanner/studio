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
package org.craftercms.cstudio.loadtesting.actions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class GetSiteNav extends BaseAction {
	
	private static String URL_GET_CONFIGURATION_NAV = "/cstudio/site/get-configuration?path=/context-nav/contextual-nav.xml";
	private static String URL_GET_CONFIGURATION_DROPDOWN = "/cstudio/site/get-configuration?path=/context-nav/site-dropdown.xml";
	private static String URL_GET_SITES = "/api/people/";
	
	private static String URL_GET_CONTEXT = "/service/cstudio/wcm/preview/overlay/";
	private static String URL_GET_USER_ROLES = "/cstudio/permission/get-user-roles?";
	
    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String shareUrl; 
	private String site;
	private String username;
	private String ticket;
	
	public GetSiteNav(String serverUrl, String shareUrl, String site, String username, String ticket) {
		this.serverUrl = serverUrl;
		this.shareUrl = shareUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
	}

	public void getWidget() throws Exception {
		String getNavConfigUrl = URL_GET_CONFIGURATION_NAV + "&site=" + site + "&alf_ticket=" + ticket;
		loadWidget(serverUrl, getNavConfigUrl, "GET-NAV-CONFIG");
		String getDropdownConfigUrl = URL_GET_CONFIGURATION_DROPDOWN + "&site=" + site + "&alf_ticket=" + ticket;
		loadWidget(serverUrl, getDropdownConfigUrl, "GET-DROPDOWN-CONFIG");
		String getUserRolesUrl = URL_GET_USER_ROLES + "&site=" + site + "&user=" + username + "&alf_ticket=" + ticket;
		loadWidget(serverUrl, getUserRolesUrl, "GET-USER-ROLES");
		String getUserSitesUrl = URL_GET_SITES + username + "/sites" + "?alf_ticket=" + ticket;
		loadWidget(serverUrl, getUserSitesUrl, "GET-USER-SITES");
	
		String getContextURL = URL_GET_CONTEXT + site + "?context=default&alf_ticket=" + ticket;
		loadWidget(shareUrl, getContextURL, "GET-SITE-CONTEXT");
	}
	
	private void loadWidget(String serverUrl, String url, String widgetName) throws Exception {
        GetMethod method = new GetMethod(serverUrl + url);
        long startTime = System.currentTimeMillis();
        int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, widgetName, "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, widgetName, "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
	}
}
