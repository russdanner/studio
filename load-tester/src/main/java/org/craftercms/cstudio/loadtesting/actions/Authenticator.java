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
import org.apache.commons.httpclient.params.HttpClientParams;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class Authenticator extends BaseAction {

    private static String URL_LOGIN = "/api/login";

    private String serverUrl;
    private HttpClient httpClient = new HttpClient();

    
    public Authenticator(String serverUrl) {
    	this.serverUrl = serverUrl;
    }
    
    public String getTicket(String username, String password) throws Exception {
    	String loginUrl = URL_LOGIN + "?u=" + username + "&pw=" + password;
        GetMethod getMethod = new GetMethod(serverUrl + loginUrl);
        long startTime = System.currentTimeMillis();
        TestingUtils.writeLog(username, "GET-TICKET", "started");
        int statusCode = this.runAction(httpClient, getMethod, null);
        if (statusCode == 200) {
            TestingUtils.writeLog(username, "GET-TICKET", "end:success", (System.currentTimeMillis() - startTime));
            String xml = getMethod.getResponseBodyAsString();
            int beginIndex = xml.indexOf("<ticket>");
            int endIndex = xml.indexOf("</ticket>");
            return xml.substring(beginIndex + "<ticket>".length(), endIndex);
        } else {
            TestingUtils.writeLog(username, "GET-TICKET", "end:failure", (System.currentTimeMillis() - startTime));
        }
        throw new Exception("cannot get a ticket");
    }
    
    
	
}
