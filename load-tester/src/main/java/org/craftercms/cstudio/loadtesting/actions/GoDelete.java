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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class GoDelete extends BaseAction {
	private static String URL_DELETE = "/cstudio/wcm/workflow/go-delete";
	private static String URL_GET_DEPENDENCIES = "/cstudio/wcm/dependency/get-dependencies";

    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String username;
	private String ticket;
	private String site;
	
	public GoDelete(String serverUrl, String site, String username, String ticket) {
		this.serverUrl = serverUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
	}
	
    public void deleteContent(String requestBody) throws Exception {
        long startTime = System.currentTimeMillis();
        TestingUtils.writeLog(username, "DELETE", "started");
        PostMethod method = new PostMethod(serverUrl + URL_DELETE + "?deletedep=true&site=" + site + "&alf_ticket=" + ticket);
        method.setRequestEntity(new StringRequestEntity(requestBody, "text/xml", "UTF-8"));
        int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, "DELETE", "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, "DELETE", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
    }	
    
    public String getDependencies(String target) throws Exception {
        long startTime = System.currentTimeMillis();
        TestingUtils.writeLog(username, "GET-DEPENDENCIES", "started");
		target = TestingConstants.ROOT_PATH + target;
		if (!target.endsWith(".xml")) {
			target = target + "/index.xml";
		}
        PostMethod method = new PostMethod(serverUrl + URL_GET_DEPENDENCIES + "?site=" + site + "&alf_ticket=" + ticket);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<items>");
	    buffer.append("<item uri=\"" + target +  "\">" + "</item>");
	    System.out.println("\t\t\t\t" + target);
        buffer.append("</items>");
        
        String requestBody = buffer.toString();
        
        method.setRequestEntity(new StringRequestEntity(requestBody, "text/xml", "UTF-8"));
        int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, "GET-DEPENDENCIES", "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, "GET-DEPENDENCIES", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
		String result = method.getResponseBodyAsString();
		return result;
	}
}
