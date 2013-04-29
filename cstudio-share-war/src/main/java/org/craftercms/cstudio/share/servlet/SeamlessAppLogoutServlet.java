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
package org.craftercms.cstudio.share.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.cstudio.share.constants.ShareConstants;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.site.servlet.BaseServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * List for call from the web client to log the user out from the current session.
 * 
 * @author Sweta Chalasani
 */
public class SeamlessAppLogoutServlet extends BaseServlet {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CookieManager cookieManager;

	@Override
	    protected void service(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException
	    {
		
			ApplicationContext applContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	  		/* retrieve the cookie manager */
	  		this.cookieManager = (CookieManager) applContext.getBean("rlCookieManager");
	  		
	  		//destroy username and alfresco ticket cookies
	  		cookieManager.destroyCookie(request, response, ShareConstants.COOKIE_ALFRESCO_TICKET);
	  		cookieManager.destroyCookie(request, response, ShareConstants.COOKIE_ALFRESCO_USERNAME);
			
	        AuthenticationUtil.logout(request, response);
	        
	        // redirect to the root of the website
	        response.sendRedirect(request.getContextPath());
	    }
	
	public void setCookieManager(CookieManager cookieManager) {
		this.cookieManager = cookieManager;
	}
}
