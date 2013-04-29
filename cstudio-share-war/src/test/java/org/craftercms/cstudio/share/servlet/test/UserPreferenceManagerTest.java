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

import javax.servlet.http.Cookie;

import org.springframework.extensions.surf.exception.RequestContextException;
import org.junit.After;
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
import org.craftercms.cstudio.share.servlet.CookieManager;
import org.craftercms.cstudio.share.servlet.ShareLoginServlet;
import org.craftercms.cstudio.share.user.UserPreferenceManager;
import static org.junit.Assert.*;

public class UserPreferenceManagerTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private XmlWebApplicationContext appContext;
	private MockServletContext servletConetxt;
	String[] configFiles = new String[]{"alfresco/webscript-framework-application-context.xml","alfresco/web-framework-application-context.xml","alfresco/web-framework-model-context.xml","alfresco/slingshot-application-context.xml","core/crafter-surf-application-context.xml","cstudio-application-context.xml"};
	UserPreferenceManager  userPreferenceManager = null;
	SeamlessAppContext seamAppContext = null;
	
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
			seamAppContext = new SeamlessAppContext();
			userPreferenceManager = (UserPreferenceManager) appContext.getBean("rlUserPreferenceManager");
			seamAppContext = new SeamlessAppContext();
			seamAppContext.setRequest(request);
			seamAppContext.setResponse(response);
			seamAppContext.setServletContext(servletConetxt);
			seamAppContext.setApplicationContextForThread(seamAppContext);
	}
	String pUserId = "admin";
	String pDashboardName ="testCreateDashboard";
	String pId = "001";
	String pWidgetValue = "Created a sample widget";
	
	@Test
	public void testCreateWidgetState(){
		userPreferenceManager.setWidgetWindowState(pUserId, pDashboardName, pId,pWidgetValue);
		Cookie cookie = response.getCookie(pUserId + pDashboardName + pId);
		assertNotNull(cookie);
		
	}
	
	@Test
	public void testgetWidgetState(){
		userPreferenceManager.setWidgetWindowState(pUserId, pDashboardName, pId,pWidgetValue);
		request.setCookies(response.getCookies());
		String widgetWindowState = userPreferenceManager.getWidgetWindowState(pUserId, pDashboardName, pId);
		assertEquals(pWidgetValue,widgetWindowState);
	}

	@Test
	public void testDashboard(){
		request.setRequestURI("/dashbord1/admin");
		//Not sure why dashBorad name is taken as itys not being used...!!!!!!!!!!!!!!!!  
		userPreferenceManager.setMostRecentDashboard(pUserId, pDashboardName, request, response);
		request.setCookies(response.getCookies());
		String mostRecentDashboard = userPreferenceManager.getMostRecentDashboard(pUserId, request);
		assertEquals("/dashbord1/admin", mostRecentDashboard);
		
	}
	
}