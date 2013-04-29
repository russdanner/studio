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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class AlfrescoWriteContent extends BaseAction {

	private static String URL_WRITE_CONTENT = "/cstudio/test/content/write-content";
	
    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String username;
	private String versionable;
	private String ticket;
	private String fileContent = null;
	
	public AlfrescoWriteContent(String serverUrl, String username, String versionable, String ticket) {
		this.serverUrl = serverUrl;
		this.username = username;
		this.versionable = versionable;
		this.ticket = ticket;
	}
	
	
    public void writeContent(String path, String fileName, String baseFileName) throws Exception {
        long startTime = System.currentTimeMillis();
        TestingUtils.writeLog(username, "WRITE-CONTENT", "started");
        PostMethod method = new PostMethod(serverUrl + URL_WRITE_CONTENT 
        		+ "?path=" + path + "&fileName=" + fileName 
        		+ "&user=" + username + "&versionable=" + versionable  + "&alf_ticket=" + ticket);
        if (fileContent == null) {
        	fileContent = getFileContent(baseFileName);
        }
        method.setRequestEntity(new StringRequestEntity(fileContent, "text/xml", "UTF-8"));
        int statusCode = this.runAction(httpClient, method, username);
		if (statusCode == 200) {
            TestingUtils.writeLog(username, "WRITE-CONTENT", "end:success", (System.currentTimeMillis() - startTime));
		} else {
            TestingUtils.writeLog(username, "WRITE-CONTENT", "end:failure", (System.currentTimeMillis() - startTime));
			throw new Exception(method.getResponseBodyAsString());
		}
	}
	

	/**
	 * convert InputStream to string
	 * @param internalName 
	 * 
	 * @param is
	 * @return string
	 */
	public String getFileContent(String baseFileName) throws Exception {
		InputStream is = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputReader = null;
		try {
			is = this.getClass().getResourceAsStream("/"+baseFileName);
			StringBuilder sb = new StringBuilder();
			String line = null;
			inputReader = new InputStreamReader(is);
			bufferedReader = new BufferedReader(inputReader);
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
        } finally {
        	is.close();
        	inputReader.close();
        	bufferedReader.close();
        }
	}

}
