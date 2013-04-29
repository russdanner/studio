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
package org.craftercms.cstudio.share.servlet.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.service.ApplicationService;
import org.craftercms.cstudio.share.servlet.CookieManager;
import org.craftercms.cstudio.share.servlet.ShareLoginServlet;

public class ApplicationServiceTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private XmlWebApplicationContext appContext;
	private MockServletContext servletConetxt;
	String[] configFiles = new String[]{"alfresco/webscript-framework-application-context.xml","alfresco/web-framework-application-context.xml","alfresco/web-framework-model-context.xml","alfresco/slingshot-application-context.xml","core/crafter-surf-application-context.xml","cstudio-application-context.xml"};
	CookieManager cookieManager = null;
	
	@After
	public void tearDown(){
		appContext.close();
	}
	
	@Before
	public void setUp() throws Exception {
		
			request = new MockHttpServletRequest();
			response = new MockHttpServletResponse();
			
			servletConetxt = new MockServletContext();
			appContext = new XmlWebApplicationContext();
			appContext.setConfigLocations(configFiles);
			appContext.setServletContext(servletConetxt);
			appContext.refresh();
			
			servletConetxt.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,appContext);
			MockServletConfig serveltConfig = new MockServletConfig(servletConetxt);
			cookieManager = (CookieManager) appContext.getBean("rlCookieManager");
	}
	
	private void createSeamAppContext(String requestURI){
		SeamlessAppContext seamAppContext = new SeamlessAppContext();
		request.setContextPath("");
		request.setRequestURI(requestURI);
		seamAppContext.setRequest(request);
		seamAppContext.setResponse(response);
		seamAppContext.setServletContext(servletConetxt);
		seamAppContext.setApplicationContextForThread(seamAppContext);
	}
	@Test
	public void testContextPathRoot(){
		createSeamAppContext("");
		ApplicationService  appService = new ApplicationService();
		String determineContextualNavKey = appService.determineContextualNavKey();
		Assert.assertTrue((determineContextualNavKey.indexOf("admin") != -1));
	}
	
	@Test
	public void testContextPathSas(){
		createSeamAppContext("/sas-auth/testThis");
		request.setContextPath("/sas-auth/");
		ApplicationService  appService = new ApplicationService();
		String determineContextualNavKey = appService.determineContextualNavKey();
		Assert.assertTrue((determineContextualNavKey.indexOf("sas-auth") != 1));
	}
	
	@Test
	public void testRequestURIUser(){
		createSeamAppContext("/page/user/index");
		ApplicationService  appService = new ApplicationService();
		String determineContextualNavKey = appService.determineContextualNavKey();
		Assert.assertTrue((determineContextualNavKey.indexOf("user") != -1));
	}
	@Test
	public void testRequestURISite(){
		createSeamAppContext("/page/site/index");
		ApplicationService  appService = new ApplicationService();
		String determineContextualNavKey = appService.determineContextualNavKey();
		Assert.assertTrue((determineContextualNavKey.indexOf("site") != -1));
	}
}