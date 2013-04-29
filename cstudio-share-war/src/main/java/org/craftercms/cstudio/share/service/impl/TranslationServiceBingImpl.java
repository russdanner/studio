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
package org.craftercms.cstudio.share.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import java.util.*;
import java.io.IOException;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.craftercms.cstudio.share.util.DomUtils;
import org.craftercms.cstudio.share.service.api.*;

/**
 * Provide a connector to a translation service
 * @author russdanner
 */
public class TranslationServiceBingImpl implements TranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranslationServiceBingImpl.class);

	private String _apiKey;
	private String _translateServiceUrl;

	/**
	 * synchronous translation of a string
	 * ? should we send content in and out as readers?
	 */
	public String translate(String content, String fromLang, String toLang, String format) 
	throws TranslationException {
		String translatedContent = "";
		
		if(format != null && format.toLowerCase().equals("xml")) {
			BingXml bingXml = xmlToBingXml(content);
			bingXml.translatedBingText = fireBingService(bingXml.bingText, fromLang, toLang);
			bingXml = bingXmlToXml(bingXml);
		}
		else {
			translatedContent = fireBingService(content, fromLang, toLang);
		}
		
		return translatedContent;
	}

	/**
	 * fire the bing service
	 */
	protected String fireBingService(String content, String fromLang, String toLang) {
		String translatedContent = content;
		
		HttpClient client = new HttpClient();
		String url = getTranslateServiceUrl();
		url = url.replace("{appId}", getApiKey());
		url = url.replace("{fl}", fromLang);
		url = url.replace("{tl}", toLang);
		url = url.replace("{text}", content);
		
		GetMethod method = null;

	    try {
		    method = new GetMethod(url);
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

	    	int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        LOGGER.error("Method failed: " + method.getStatusLine());
	      }
	      else {
		      byte[] responseBody = method.getResponseBody();
		      translatedContent =  new String(responseBody);
		      translatedContent = translatedContent.replaceAll("\\/", "/");
		      translatedContent = translatedContent.substring(1, translatedContent.length()-1);
		      
	      }
	      
	    } catch (HttpException e) {
	      LOGGER.error("Fatal protocol violation: " + e.getMessage(), e);
	    } catch (IOException e) {
	      LOGGER.error("Fatal transport error: " + e.getMessage(), e);
	    } finally {
	      // Release the connection.
	      method.releaseConnection();
	    }  		
		return translatedContent;
	}
	
	/**
	 * bing doesn't like XML, can we convert the XML to something bing will translate
	 */
	protected BingXml xmlToBingXml(String content){
		BingXml bingXml = new BingXml();
		bingXml.originalXml = content;
		bingXml.bingText = content;
		
		Map<String, String> tagMap = new HashMap<String, String>(); 
		Document xmlDocument = DomUtils.createXmlDocument(content);
		
		buildTagMap(xmlDocument, tagMap);
		bingXml.tagMap = tagMap;
		
		int id = 0;
		for(String key : tagMap.keySet()) {
			id++;
			tagMap.put(key, ""+id);
	        bingXml.bingText = bingXml.bingText.replaceAll(key, ""+id);			
		}

		return bingXml;
	}
	
	/**
	 * build a map of all tags
	 */
	protected void buildTagMap(Node node, Map<String, String> tagMap) {
		
		if(node instanceof Element) {
			String tagName = ((Element)node).getTagName();
			tagMap.put(tagName, "not initialized");
		}
		
		NodeList children = node.getChildNodes();

		int len = children.getLength();
		for(int i=0; i<len; i++) {
			buildTagMap(children.item(i), tagMap);
		}
	}
	
	/**
	 * take a bing translated text and convert it back to XML
	 */
	protected BingXml bingXmlToXml(BingXml bingXml) {
		bingXml.translated = true;

		for(String key : bingXml.tagMap.keySet()) {
			String id = bingXml.tagMap.get(key);
	        bingXml.translatedBingText = bingXml.translatedBingText.replaceAll(id, key);			
		}

		return bingXml;
	}
	
	/**
     * data structure for managing an in-flight bing translation
	 */
	protected class BingXml {
		public String originalXml;
		public String xmlMap;
		public String bingText;
		public String translatedBingText;
		public String translatedXml;
		public Map<String, String> tagMap;
		public boolean translated = false;
		
		public void dump() {
			LOGGER.info("original xml\r\n==============" + originalXml);
			LOGGER.info("\r\n\r\n\r\n\r\n\r\n");
			LOGGER.info("xml map\r\n==============" + xmlMap);
			LOGGER.info("\r\n\r\n\r\n\r\n\r\n");
			LOGGER.info("bing text\r\n==============" + bingText);
			LOGGER.info("\r\n\r\n\r\n\r\n\r\n");
			LOGGER.info("translated bing text\r\n==============" + translatedBingText);
			LOGGER.info("\r\n\r\n\r\n\r\n\r\n");
			LOGGER.info("translated xml\r\n==============" + translatedXml);
		}
	}
	
	/** setter for api key */
	public void setApiKey(String value) {
		_apiKey = value;
	}

	/** setter for api key */
	public String getApiKey() {
		return _apiKey;
	}

	/** setter for api service url */
	public void setTranslateServiceUrl(String value) {
		_translateServiceUrl = value;
	}

	/** getter for api service url */
	public String getTranslateServiceUrl() {
		return _translateServiceUrl;
	}
	
	
}