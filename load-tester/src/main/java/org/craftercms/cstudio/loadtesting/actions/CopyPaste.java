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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class CopyPaste extends BaseAction {

	private static String URL_PASTE = "/cstudio/wcm/clipboard/paste";
    private static String URL_GET_ITEMS = "/cstudio/wcm/content/get-pages";
    
    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String username;
	private String ticket;
	private String site;
	
	public CopyPaste(String serverUrl, String site, String username, String ticket) {
		this.serverUrl = serverUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
	}
	
    public void pasteContent(String fileName, String source, String destination) throws Exception {
        long startTime = System.currentTimeMillis();
		String items = getItems(source);
		String pasteUrl = serverUrl + URL_PASTE + "?site=" + site + "&destination=" + destination + "&alf_ticket=" + ticket;
        PostMethod method = new PostMethod(pasteUrl);
        method.setRequestEntity(new StringRequestEntity(items, "text/plain", "UTF-8"));
        TestingUtils.writeLog(username, "PASTE", "started");
        int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, "PASTE", "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, "PASTE", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
		String response = method.getResponseBodyAsString();
        JSONObject responseObj = JSONObject.fromObject(response);
        JSONArray paths = responseObj.getJSONArray("paths");
        StringBuffer buffer = new StringBuffer();
        for (int index = 0; index < paths.size(); index++) {
        	buffer.append(paths.get(index) + "\n");
        }

        TestingUtils.writeLog(username, "PASTE", "writing items to " + fileName);
        BufferedWriter out = null;
        try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			FileWriter fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(buffer.toString());
			out.close();
        } catch (Exception e) { 
            TestingUtils.writeLog(username, "PASTE", "generating " + fileName + " failed. items are:");
    		System.out.println(buffer.toString());
        } finally {
        	if (out != null) {
        		out.close();
        	}
        }
    }	
    
    public String getItems(String target) throws Exception {
        long startTime = System.currentTimeMillis();
    	String getItemsUrl = URL_GET_ITEMS + "?site=" + site + "&depth=-1&path=" + target + "&alf_ticket=" + ticket;
        GetMethod getMethod = new GetMethod(serverUrl + getItemsUrl);
        TestingUtils.writeLog(username, "GET-ITEMS", "started");
        int statusCode = this.runAction(httpClient, getMethod, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, "GET-ITEMS", "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, "GET-ITEMS", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(getMethod.getResponseBodyAsString());
		}
        String response = getMethod.getResponseBodyAsString();
        JSONObject pasteObj = new JSONObject();
        JSONObject responseObj = JSONObject.fromObject(response);
        JSONObject itemObj = responseObj.getJSONObject("item");
        List items = new ArrayList<JSONObject>(1);
        items.add(itemObj);
        pasteObj.put("item", items);
        return pasteObj.toString();
    }
    
}
