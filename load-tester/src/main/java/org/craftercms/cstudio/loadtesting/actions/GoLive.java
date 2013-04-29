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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class GoLive extends BaseAction {

	private static String URL_GET_DEPENDENCIES = "/cstudio/wcm/dependency/get-dependencies";
	private static String URL_GO_LIVE = "/cstudio/wcm/workflow/go-live";
	
	private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String site;
	private String username;
	private String ticket;
	private String publishChannel;
	
	public GoLive(String serverUrl, String site, String username, String ticket, String publishChannel) {
		this.serverUrl = serverUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
		this.publishChannel = publishChannel;
	}

	public String getDependencies(String fileName) throws Exception {
		ArrayList<String> content = new ArrayList<String>();
		DataInputStream in = null;
		try {
			System.out.println("[" + username + "] GET-DEPENDENCIES: reading " + fileName);
			FileInputStream fstream = new FileInputStream(fileName);
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String item;
			while ((item = br.readLine()) != null) {
				content.add(item);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return getDependencies(content);
	}

	public String getDependencies(List<String> contents) throws Exception {
		long startTime = System.currentTimeMillis();
		TestingUtils.writeLog(username, "GET-DEPENDENCIES", "started");
		PostMethod method = new PostMethod(serverUrl + URL_GET_DEPENDENCIES + "?site=" + site + "&alf_ticket=" + ticket);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<items>");
		for (String item : contents) {
			buffer.append("<item uri=\"" + item +  "\">" + "</item>");
			System.out.println("\t\t\t\t" + item);
		}
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

	public String goLive(String items) throws Exception {
		String publishingInfo = ",\"publishChannel\":{\"index\":\"0\",\"name\":\"" + publishChannel + "\"},\"status\":{\"channels\":[],\"message\":\"\"},\"now\":\"true\",\"scheduledDate\":\"\"}";
		String requestBody = items.substring(0, items.lastIndexOf("}")) + publishingInfo;
		long startTime = System.currentTimeMillis();
		TestingUtils.writeLog(username, "GO-LIVE", "started");
		PostMethod method = new PostMethod(serverUrl + URL_GO_LIVE + "?site=" + site + "&alf_ticket=" + ticket);
		method.setRequestEntity(new StringRequestEntity(requestBody, "text/xml", "UTF-8"));
		int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
			TestingUtils.writeLog(username, "GO-LIVE", "end:success", (System.currentTimeMillis() - startTime));
		} else {
			TestingUtils.writeLog(username, "GO-LIVE", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
		String result = method.getResponseBodyAsString();
		return result;
	}

}
