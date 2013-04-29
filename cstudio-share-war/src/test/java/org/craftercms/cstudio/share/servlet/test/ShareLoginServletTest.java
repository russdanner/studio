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
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import junit.framework.TestCase;

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

import org.craftercms.cstudio.share.exception.CStudioException;
import org.craftercms.cstudio.share.servlet.CookieManager;
import org.craftercms.cstudio.share.servlet.ShareLoginServlet;
import static org.junit.Assert.assertEquals;

@Ignore
public class ShareLoginServletTest {
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private XmlWebApplicationContext appContext;
	private MockServletContext servletConetxt;
	private String successParam = "loginSuccess";
	private String failureParam = "loginFailed";
	String[] configFiles = new String[]{"alfresco/webscript-framework-application-context.xml","alfresco/web-framework-application-context.xml","alfresco/web-framework-model-context.xml","alfresco/slingshot-application-context.xml","core/crafter-surf-application-context.xml","CStudio-application-context.xml"};
	ShareLoginServlet loginServlet = null;
	
	@After
	public void tearDown(){
		appContext.close();
	}
	
	@Before
	public void setUp() throws Exception {
		
			request = new MockHttpServletRequest();
			response = new MockHttpServletResponse();
			request.addParameter("success",successParam);
			request.addParameter("failure",failureParam);
			
			servletConetxt = new MockServletContext();
			appContext = new XmlWebApplicationContext();
			appContext.setConfigLocations(configFiles);
			appContext.setServletContext(servletConetxt);
			appContext.refresh();
			servletConetxt.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,appContext);
			MockServletConfig serveltConfig = new MockServletConfig(servletConetxt);
			loginServlet = new ShareLoginServlet();
			loginServlet.init(serveltConfig);
	
	}

	@Test
	public void testLoginServletSuccess() throws ServletException, IOException, Exception {
		request.addParameter("username", "admin");
		request.addParameter("password", "admin");
		
		loginServlet.service(request,response);
		String redirectedUrl = response.getRedirectedUrl();
		assertEquals(successParam, redirectedUrl);
		request.setCookies(response.getCookies());
		CookieManager cookieManager = (CookieManager) appContext.getBean("rlCookieManager");
		Serializable cookieValue = cookieManager.getCookieValue(request, "alf_ticket");
		
	}
	
	@Test
	public void testCookieManagerValues() throws CStudioException{
		CookieManager cookieManager = (CookieManager) appContext.getBean("rlCookieManager");
		Serializable cookieValue = cookieManager.getCookieValue(request, "alf_ticket");
	}
	
	@Test
	public void testLoginServletFailure() throws ServletException, IOException{
		request.addParameter("username", "failure");
		request.addParameter("password", "failure");	
		loginServlet.service(request,response);
		String redirectedUrl = response.getRedirectedUrl();
		assertEquals(failureParam, redirectedUrl);
	}

}
