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
/**
 * 
 */
package org.craftercms.cstudio.share.service.impl;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.service.api.WcmWorkflowService;
import org.craftercms.cstudio.share.servlet.CookieManager;
import org.craftercms.cstudio.share.servlet.ShareAuthenticationFilter;



/**
 * @author videepkumar1
 *
 */
public class WcmWorkflowServiceImpl implements WcmWorkflowService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WcmWorkflowServiceImpl.class);
	
	private final String COOKIE_ALFRESCO_TICKET = "alf_ticket";	
	
	private CookieManager cookieManager;
	
	private String workflowServiceUri;
	
	
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.cstudio.share.service.api.WcmWorkflowService#submitToWorkflow(java.lang.String, java.util.ArrayList, java.lang.String, java.util.HashMap)
	 */
	public void submitToWorkflow(String storeName, ArrayList<String> assets,
								 String workflowName, HashMap<String, String> workflowParameters) throws Exception {
		// 1. Construct the XML String
		StringBuffer xmlString = new StringBuffer();
		xmlString.append("<wcm-submit>")
				 .append("<store>").append(storeName).append("</store>")
				 .append("<assets>");
		
		for (String asset: assets) {
			xmlString.append("<asset>").append(asset).append("</asset>");
		}
				 
		xmlString.append("</assets>")
				 .append("<workflow>")
				 .append("<name>").append(workflowName).append("</name>")
				 .append("<parameters>");
		
		Iterator<String> keyIterator = workflowParameters.keySet().iterator();
		Iterator<String> valueIterator = workflowParameters.values().iterator();
		while (keyIterator.hasNext()) {
			String parameterName = (String)keyIterator.next();
			String parameterValue = (String)valueIterator.next();
			xmlString.append("<parameter>").append("<parameter-name>").append(parameterName).append("</parameter-name>").append("<parameter-value>").append(parameterValue).append("</parameter-value>").append("</parameter>");	
		}			
		
		xmlString.append("</parameters>")
				 .append("<message>").append(workflowName).append("</message>")		 
				 .append("<comment>").append(workflowName).append("</comment>")
				 .append("</workflow>")
				 .append("</wcm-submit>");
						
		// 2. Invoke Alfresco Services to submit the assets to the specified workflow		
		try {
			String response = callRESTService(workflowServiceUri, xmlString.toString());
		} catch (Exception e) {
			LOGGER.error("Error during invoke of alfresco services: " + e.getMessage(), e);
			throw e;
		}
	}	
	
	/*
	 * This will invoke the REST api at the alfresco repository
	 */
	private String callRESTService(final String targetURI, final String xmlString) throws Exception {
		String xmlResponseString = "";
		int result = -1;
		
		//TODO: Use Alfresco Endpoint instead of httpclient
		
	    PostMethod postMethod = null;
	    BufferedReader br = null;
    	HttpClient httpClient = new HttpClient();
    	
	    // PRECONDITIONS
	    assert targetURI != null && targetURI.trim().length() > 0 : "path must not be null, empty or blank.";
	
	    // Body
	    postMethod = new PostMethod(targetURI);
	    postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
	    	    	    
	    // Get the Alfresco Ticket	    
	    SeamlessAppContext context = SeamlessAppContext.currentApplicationContext();
		String alfTicketCookie = (String) cookieManager.getCookieValue(context.getRequest(), this.COOKIE_ALFRESCO_TICKET);

		// Set the Parameter
	    //getMethod.setQueryString(new NameValuePair[] {new NameValuePair("formId", formId)});
		postMethod.setQueryString("?xml=" + xmlString + "&" + COOKIE_ALFRESCO_TICKET + "=" + alfTicketCookie);
	    	    
	    try{
	    	result = httpClient.executeMethod(postMethod);

	        if(result == HttpStatus.SC_NOT_IMPLEMENTED) {	        	
	        	if (LOGGER.isErrorEnabled()) {
	        		LOGGER.error("The POST method is not implemented by this URI");
	        		throw new Exception("The POST method is not implemented by this URI");
	        	}
	        	// still consume the response body
	        	xmlResponseString = postMethod.getResponseBodyAsString();
	        } else {
	        	if (result == HttpStatus.SC_OK) {
	        		br = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream()));
	        		
	        		String readLine;
		        	while(((readLine = br.readLine()) != null)) {
		        		xmlResponseString = xmlString + readLine;
		        	}
	        	} else {	        		
	        		if (LOGGER.isErrorEnabled()) {
	        			LOGGER.error("Push to Alfresco Service Failed: " + HttpStatus.getStatusText(result));
	        			throw new Exception("Push to Alfresco Service Failed: " + HttpStatus.getStatusText(result));
	        		}
	        	}	           	
	        }
	    } catch (HttpException he) {	    	
    		if (LOGGER.isErrorEnabled()) {
    			LOGGER.error("Push to Alfresco Service Failed due to HttpException: " + he.getMessage(), he);
    			throw he;
    		}
	    }  catch (IOException ie) {	    	
    		if (LOGGER.isErrorEnabled()) {
    			LOGGER.error("Push to Alfresco Service Failed due to IOException: " + ie.getMessage(), ie);
    		}
	    } finally {
	    	postMethod.releaseConnection();
	    	if(br != null) try { br.close(); } catch (Exception fe) {}
	    }
	    
	    return(xmlResponseString);
	}
	

	/**
	 * @param cookieManager the cookieManager to set
	 */
	public void setCookieManager(CookieManager cookieManager) {
		this.cookieManager = cookieManager;
	}


	/**
	 * @param workflowServiceUri the workflowServiceUri to set
	 */
	public void setWorkflowServiceUri(String workflowServiceUri) {
		this.workflowServiceUri = workflowServiceUri;
	}

}
