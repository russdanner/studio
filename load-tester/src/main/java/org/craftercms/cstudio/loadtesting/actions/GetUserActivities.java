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

public class GetUserActivities extends BaseAction {

	private static String URL_GET_ACTIVITY = "/cstudio/wcm/activity/get-user-activities?sort=eventDate&ascending=false&num=10&filterType=pages&excludeLive=false";
	
    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String shareUrl; 
	private String site;
	private String username;
	private String ticket;
	
	public GetUserActivities(String serverUrl, String shareUrl, String site, String username, String ticket) {
		this.serverUrl = serverUrl;
		this.shareUrl = shareUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
	}

	public void getWidget() throws Exception {
		String getUserActivityUrl = URL_GET_ACTIVITY + "&site=" + site + "&user=" + username + "&alf_ticket=" + ticket;
		loadWidget(serverUrl, getUserActivityUrl, "GET-USER-ACTIVITIES");
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
