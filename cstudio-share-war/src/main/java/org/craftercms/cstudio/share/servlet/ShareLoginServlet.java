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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.craftercms.cstudio.share.constants.ShareConstants;
import org.craftercms.cstudio.share.user.UserPreferenceManager;
import org.craftercms.cstudio.share.user.UserPreferenceManagerImpl;


/**
 * Responds to Login POSTs to allow the user to authenticate to the web site.
 * 
 * @author Sweta Chalasani
 */
public class ShareLoginServlet extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int COOKIE_AGE = 24 * 60 * 60;
	private CookieManager cookieManager;
	private UserPreferenceManager userPreferenceManager;
	private static final Logger LOGGER = LoggerFactory.getLogger(ShareAuthenticationFilter.class);

	/* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = (String) request.getParameter("username");
        String password = (String) request.getParameter("password");
        
        if (LOGGER.isDebugEnabled())
        {
        	LOGGER.debug("Share Login Username " + username);
        	LOGGER.debug("Share Login Password " + password);
        }
        
        String successPage = (String) request.getParameter("success");
        String failurePage = (String) request.getParameter("failure");
        
        ApplicationContext applContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
  		/* retrieve the cookie manager */
  		this.cookieManager = (CookieManager) applContext.getBean("rlCookieManager");
  		/* retrieve the user preference manager */
  		this.userPreferenceManager = (UserPreferenceManager) applContext.getBean("rlUserPreferenceManager");
        
        // See if we can load the user with this identity
        boolean success = false;
        try
        {
        	////////
        	// This is from http://forums1.man.alfresco.com/en/viewtopic.php?f=48&t=27872.
        	// It was added to deal with the fact that FrameworkUtil.getServiceRegistry() returned null.
        	///////
        	// initialize a new request context
            RequestContext context = FrameworkUtil.getCurrentRequestContext();
            // This is probably always null in this case.
            if(context == null){
               try{
                   // perform a "silent" init - i.e. no user creation or remote connections
                   context = RequestContextUtil.initRequestContext(applContext, request, true);
               }
               catch (RequestContextException ex)
               {
                   throw new ServletException(ex);
               }
            }
        	////////
        	
        	WebFrameworkServiceRegistry serviceRegistry = context.getServiceRegistry();
        	UserFactory userFactory = serviceRegistry.getUserFactory();
            
            // see if we can authenticate the user
            boolean authenticated = userFactory.authenticate(request, username, password);
            if (LOGGER.isDebugEnabled())
            {
            	LOGGER.debug("Is authenticated " + authenticated);
            }
            if (authenticated)
            {
                // this will fully reset all connector sessions
                AuthenticationUtil.login(request, response, username);
                
                ConnectorSession cs = FrameworkUtil.getConnectorSession(request.getSession(), ShareConstants.ENDPOINT_ALFRESCO);
                String alf_ticket = cs.getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
                
                //protecting any unexpected situation, when ticket/username is null
                if ( (StringUtils.isEmpty(alf_ticket)) || (StringUtils.isEmpty(username)) ) {
                    success = false;
                } else { 
                	//set cookies when authenticated 
                	cookieManager.putCookieValue(request, response, ShareConstants.COOKIE_ALFRESCO_TICKET, COOKIE_AGE, alf_ticket);
                	cookieManager.putCookieValue(request, response, ShareConstants.COOKIE_ALFRESCO_USERNAME, COOKIE_AGE, username);
                  
                	// mark the fact that we succeeded
                	success = true;
                }
            }
        }
        catch (Throwable err)
        {
            throw new ServletException(err);
            // instead of throwing exception, we should have forwarded to login page; but later!
        }
        
        // If they succeeded in logging in, redirect to the success page
        // Otherwise, redirect to the failure page
        if (success)
        {
            if (successPage != null)
            {
            	//check for recent dashboard and set the page to it
            	Cookie cookies [] = request.getCookies ();
            	String pageCookie = null;
//            	if (cookies != null) {
//            		pageCookie = userPreferenceManager.getMostRecentDashboard(username, request);
//	        	}
//            	if(pageCookie != null) {
//    		        if(successPage.contains("/page") && ! successPage.contains("/site-index")) {
//    		        	response.sendRedirect(successPage);
//    		        } else {
//    		        	response.sendRedirect(pageCookie);
//    		        }
//            	} else {
            		 response.sendRedirect(successPage);
//            	}
            }
            else
            {
                response.sendRedirect(request.getContextPath());
            }
        }
        else
        {
            if (failurePage != null)
            {
                response.sendRedirect(failurePage);
            }
            else
            {
                response.sendRedirect(request.getContextPath());
            }
        }        
    }
    
	public void setCookieManager(CookieManager cookieManager) {
		this.cookieManager = cookieManager;
	}

	public void setUserPreferenceManager(
			UserPreferenceManager userPreferenceManager) {
		this.userPreferenceManager = userPreferenceManager;
	}
}
