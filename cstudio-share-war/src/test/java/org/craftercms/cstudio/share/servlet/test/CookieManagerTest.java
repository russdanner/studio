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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import javax.servlet.http.Cookie;

import org.springframework.extensions.surf.exception.RequestContextException;
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

import org.craftercms.cstudio.share.exception.CStudioException;
import org.craftercms.cstudio.share.servlet.CookieManager;
import org.craftercms.cstudio.share.servlet.ShareLoginServlet;

@Ignore
public class CookieManagerTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private XmlWebApplicationContext appContext;
	private MockServletContext servletConetxt;
	String[] configFiles = new String[]{"alfresco/webscript-framework-application-context.xml","alfresco/web-framework-application-context.xml","alfresco/web-framework-model-context.xml","alfresco/slingshot-application-context.xml","core/crafter-surf-application-context.xml","CStudio-application-context.xml"};
	private String specialCharCookieVal = "!@#$%^&*()|<>~";
	private String cookieValue = "testCookieAdd";
	private String basicCookie = "basicCookie";
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
	
	@Test
	public void testPutCookieValForSpecialChar() throws CStudioException{
		
		cookieManager.putCookieValue(request, response, "cookieKey1", 360,specialCharCookieVal);
		request.setCookies(response.getCookies());
		cookieManager.getCookieValue(request, "cookieKey1");
		Serializable cookieValue = cookieManager.getCookieValue(request, "cookieKey1");
		assertEquals(specialCharCookieVal, cookieValue);
	}
	
	private void createBasicCookie() throws CStudioException{
		cookieManager.putCookieValue(request, response, "basicCookie", 3600,cookieValue);
		request.setCookies(response.getCookies());
	}
	@Test
	public void testCookieManagerBasic() throws CStudioException{
		createBasicCookie();
		Serializable value = cookieManager.getCookieValue(request, basicCookie);
		assertEquals(cookieValue, value);
	}
	
	private Cookie getCoookie(Cookie[] cookies,String cookieName){
		for(Cookie cookie: cookies){
			if(cookie.getName().equals(cookieName)){
				return cookie;
			}
		}
		return null;
	}
	@Test
	public void  testCookieDestroy() throws CStudioException{
		createBasicCookie();
		cookieManager.destroyCookie(request, response, basicCookie);
		Cookie cookie = getCoookie(response.getCookies(), basicCookie);
		int maxAge = cookie.getMaxAge();
		assertEquals(maxAge, 0);
	}
	
	@Test
	public void testCookiePutPath() throws CStudioException{
		cookieManager.putCookieValue(request, response, "testPath/",basicCookie,360,cookieValue);
		Cookie cookie = getCoookie(response.getCookies(),basicCookie);
		String path = cookie.getPath();
		assertEquals(path,"testPath/");
		
	}
}
