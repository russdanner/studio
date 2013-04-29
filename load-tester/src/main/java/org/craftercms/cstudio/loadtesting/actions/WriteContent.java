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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class WriteContent extends BaseAction {

	private static String URL_WRITE_CONTENT = "/cstudio/wcm/content/write-content";
	
    private HttpClient httpClient = new HttpClient();
	private String serverUrl;
	private String site;
	private String username;
	private String ticket;
	
	public WriteContent(String serverUrl, String site, String username, String ticket) {
		this.serverUrl = serverUrl;
		this.site = site;
		this.username = username;
		this.ticket = ticket;
	}
	
	
    public void writeContent(String path, String baseFileName, String contentType, String fileName, String internalName) throws Exception {
        long startTime = System.currentTimeMillis();
        TestingUtils.writeLog(username, "WRITE-CONTENT", "started");
        PostMethod method = new PostMethod(serverUrl + URL_WRITE_CONTENT 
        		+ "?createFolders=true&unlock=true&site=" + site + "&contentType=" + contentType
        		+ "&path=" + path + "&fileName=" + fileName + "&user=" + username + "&alf_ticket=" + ticket);
        String requestBody = getFileContent(baseFileName, fileName, internalName);
        method.setRequestEntity(new StringRequestEntity(requestBody, "text/xml", "UTF-8"));
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
	public String getFileContent(String baseFileName, String fileName, String internalName) throws Exception {
		InputStream is = null;
        InputStreamReader isReader = null;
        StringWriter sw  = null;
        XMLWriter writer  = null;
		try {
			is = this.getClass().getResourceAsStream("/"+baseFileName);
            isReader = new InputStreamReader(is, "UTF-8");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(isReader);
			Element root = document.getRootElement();
			Node node = root.selectSingleNode("file-name");
			node.setText(fileName);
			Node node2 = root.selectSingleNode("internal-name");
			node2.setText(internalName);
			sw = new StringWriter();
			writer = new XMLWriter(sw);
			writer.write(document);
			writer.flush();
			return sw.toString();
		} finally {
			if (is != null) {
				is.close();
			}
			if (isReader != null) {
				isReader.close();
			}
			if (sw != null) {
				sw.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}
}
