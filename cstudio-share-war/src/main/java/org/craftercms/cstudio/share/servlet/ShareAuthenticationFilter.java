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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.constants.ShareConstants;
import org.craftercms.cstudio.share.exception.CStudioException;
import org.craftercms.cstudio.share.user.UserPreferenceManager;

import org.craftercms.cstudio.share.util.ServletUtils;

/**
 * Seamless App Authentication Filter Class for web-tier.
 *
 * @see org.craftercms.SeamlessAppAuthenticationFilter
 * @author Sweta Chalasani
 */
public final class ShareAuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareAuthenticationFilter.class);

    // Various services required by authenticator
    private ConnectorService connectorService;
    private CookieManager cookieManager;
    private UserPreferenceManager userPreferenceManager;
    private ServletContext servletContext;
    private boolean enableRememberLastPage;
    private String defaultLastPage;
    private static final String TIMEZONE = "Timezone";

    /**
     * Run the filter
     *
     * @param request
     *            ServletRequest
     * @param response
     *            ServletResponse
     * @param chain
     *            FilterChain
     * @exception IOException
     * @exception ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        // get ticket and username from cookies
        String ticket = getTicket(httpReq);
        String username = getUsername(httpReq);
        SeamlessAppContext seamlessAppContext = null;
        ConnectorSession connectionSession = null;

        try {
            if (ServletUtils.determineDispatchType(httpReq) == ServletUtils.REQUEST
                    || ServletUtils.determineDispatchType(httpReq) == ServletUtils.FORWARD) {
                seamlessAppContext = new SeamlessAppContext();
                seamlessAppContext.setRequest(httpReq);
                seamlessAppContext.setResponse(httpRes);
                seamlessAppContext.setServletContext(this.servletContext);
                SeamlessAppContext.setApplicationContextForThread(seamlessAppContext);

                if (ticket == null && username == null) {
                    AuthenticationUtil.logout(httpReq, httpRes);
                }

                // Validate the ticket if retrieved from cookie and set it in
                // connection session
                if (ticket != null) {
                    ////////
                    // This is from http://forums1.man.alfresco.com/en/viewtopic.php?f=48&t=27872.
                    // It was added to deal with the fact that FrameworkUtil.getServiceRegistry() returned null.
                    ///////
                    ApplicationContext applContext = WebApplicationContextUtils.getRequiredWebApplicationContext(this.servletContext);
                    // initialize a new request context
                    RequestContext context = FrameworkUtil.getCurrentRequestContext();
                    // This is probably always null in this case.
                    if (context == null) {
                        try {
                            // perform a "silent" init - i.e. no user creation or remote connections
                            context = RequestContextUtil.initRequestContext(applContext, httpReq, true);
                        } catch (RequestContextException ex) {
                            throw new ServletException(ex);
                        }
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Validating alfresco ticket: " + ticket);
                    }
                    connectionSession = validateTicket(request, response, ticket, username);
                }
                if (connectionSession != null)
                    loadLastPages(httpReq, httpRes, username);
            }


            if (connectionSession != null) {
                if (connectionSession.getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET) != null) {
                    seamlessAppContext.setTicket(connectionSession
                            .getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET));
                    seamlessAppContext.setRequest(httpReq);
                    seamlessAppContext.setResponse(httpRes);

                    /* Chain to the next filter */
                    chain.doFilter(httpReq, httpRes);
                }
            } else {
                /* Chain to the next filter */
                chain.doFilter(httpReq, httpRes);
            }
        } finally {
            if (ServletUtils.determineDispatchType(httpReq) == ServletUtils.REQUEST
                    || ServletUtils.determineDispatchType(httpReq) == ServletUtils.FORWARD) {
                SeamlessAppContext.setApplicationContextForThread(null);
            }
        }
    }

    /**
     * get username from cookie
     *
     * @param httpReq
     * @return
     */
    protected String getUsername(HttpServletRequest httpReq) {
        String username = null;
        // retrieve the ticket and username cookies
        if (httpReq.getCookies() != null) {
            try {
                username = (String) cookieManager.getCookieValue(httpReq, ShareConstants.COOKIE_ALFRESCO_USERNAME);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(username + " is already logged in");
                }
            } catch (CStudioException e) {
                // the application will logout if alf ticket or username is null
                LOGGER.error("Failed to load user name from the request.");
            }
        }
        if (StringUtils.isEmpty(username)) {
            username = (String) httpReq.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        }
        if (StringUtils.isEmpty(username)) {
            LOGGER.error("username is not found in cookie or session for request: "
                    + httpReq.getRequestURI() + ". This may cause authentication issues.");
        }
        return username;
    }

    /**
     * get a ticket from cookie or url
     *
     * @return ticket
     */
    protected String getTicket(HttpServletRequest httpReq) {
        String ticket = null;
        // retrieve the ticket and username cookies
        if (httpReq.getCookies() != null) {
            try {
                ticket = (String) cookieManager.getCookieValue(httpReq, ShareConstants.COOKIE_ALFRESCO_TICKET);
            } catch (CStudioException e) {
                // the application will logout if alf ticket or username is null
                LOGGER.error("Failed to load the authentication ticket from the request.");
            }
        }
        /* allow url to override ticket in cookie or in the case of a form submission or flash request
           * provide the ticket via URL (since the request will not contain a cookie
           */
        String ticketOnUrl = httpReq.getParameter(ShareConstants.COOKIE_ALFRESCO_TICKET);
        ticket = (ticketOnUrl != null) ? ticketOnUrl : ticket;
        return ticket;
    }

    /**
     * validate the current user's ticket and set the current session if validated
     *
     * @param httpReq
     * @param httpRes
     * @param connectionSession
     * @param ticket
     * @param username
     */
    protected ConnectorSession validateTicket(ServletRequest request, ServletResponse response, String ticket, String username) {
        ConnectorSession connectionSession = null;
        String endpointUrl = FrameworkUtil.getEndpoint(ShareConstants.ENDPOINT_ALFRESCO).getEndpointUrl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Alfresco Endpoint Url " + endpointUrl);
        }
        RemoteClient remote = new RemoteClient();
        remote.setEndpoint(endpointUrl);
        remote.setTicket(ticket);

        String validateTicketUri = "/api/login/ticket/" + ticket;

        /* validate alfresco ticket */
        Response res = remote.call(validateTicketUri);
        //addTimeZone(request, response, remote);
        if (Status.STATUS_OK == res.getStatus().getCode()) {
            HttpSession session = ((HttpServletRequest)request).getSession();
            addCredentials(session, username, ticket);

            connectionSession = FrameworkUtil.getConnectorSession(((HttpServletRequest)request).getSession(),
                    ShareConstants.ENDPOINT_ALFRESCO);
            connectionSession.setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, ticket);
            Connector connector;
            try {
                session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);

                connector = connectorService.getConnector(ShareConstants.ENDPOINT_ALFRESCO, session);
                connector.setConnectorSession(connectionSession);
                connector.getConnectorSession().setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET,
                        ticket);
            } catch (ConnectorServiceException e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Failed to set connector with ticket: " + ticket + " for " + ShareConstants.ENDPOINT_ALFRESCO, e);
                }
            }
        } else {
            AuthenticationUtil.logout((HttpServletRequest)request, (HttpServletResponse)response);
        }
        return connectionSession;
    }

    /**
     * add credentials to endpoints
     *
     * @param session
     * @param username
     * @param ticket
     */
    protected void addCredentials(HttpSession session, String username, String ticket) {
        // add alfresco endpoint credential to the current session
        CredentialVault vault = FrameworkUtil.getCredentialVault(session, username);
        Credentials alfrescoCredentials = vault.newCredentials(ShareConstants.ENDPOINT_ALFRESCO);
        alfrescoCredentials.setProperty(ShareConstants.COOKIE_ALFRESCO_TICKET, ticket);
        Credentials previewCredentials = vault.newCredentials(ShareConstants.ENDPOINT_PREVIEW);
        previewCredentials.setProperty(ShareConstants.COOKIE_ALFRESCO_TICKET, ticket);
    }

    /**
     * load the last page if it is enabled 
     *
     * @param httpReq
     * @param httpRes
     * @param username
     */
    protected void loadLastPages(HttpServletRequest httpReq, HttpServletResponse httpRes, String username) {
        String pageCookie = userPreferenceManager.getMostRecentDashboard(username, httpReq); // where is this being used?
        String mostRecentUri = httpReq.getRequestURI();
        if(mostRecentUri.startsWith(httpReq.getContextPath())) {
            mostRecentUri = mostRecentUri.substring(httpReq.getContextPath().length());
        }
        if (enableRememberLastPage) {
            /* save the following URIs */
            if(mostRecentUri.startsWith("/service/cstudio/wcm/preview/overlayhook")) {
                String mostRecentPage = httpReq.getParameter("page");
                System.out.println("viewing page:"+mostRecentPage);
                // userPreferenceManager.setMostRecentDashboard(usernameCookie, mostRecentPage, httpReq, httpRes);
            }
            else if(mostRecentUri.matches("/page/site/.*/dashboard")) {
                System.out.println("viewing site dashboard");
                userPreferenceManager.setMostRecentDashboard(username, mostRecentUri, httpReq, httpRes);
            }
            else if(mostRecentUri.matches("/page/user/.*/dashboard")) {

                try {
                    String sendTo = userPreferenceManager.getMostRecentDashboard(username, httpReq);
                    System.out.println("viewing personal dashboard, send to: "+sendTo);

                    if(sendTo != null || !"".equals(sendTo.trim())) {

                        if(!sendTo.startsWith(httpReq.getContextPath())) {
                            sendTo = httpReq.getContextPath() + sendTo;
                        }

                        System.out.println("redirect '"+sendTo+"'");
                        httpRes.sendRedirect(sendTo);
                    }
                }
                catch(Exception noCookie) {
                    // if no cookie is set use will just end up on personal dashboard

                    // if we're going to send to default we have to put something on the
                    // url to determine the number of redirects otherwise we get in to a
                    // loop when the user doesn't have access to the default

                    // also: we should add a parameter to allow a user to pass to the user
                    // dashboard without being redirected
                }
            }
        }
    }


    /**
     * add time zone information to request header 
     *
     * @param request
     * @param response
     * @param remote
     */
    protected void addTimeZone(ServletRequest request, ServletResponse response, RemoteClient remote) {
        try {
            String site = request.getParameter("site");
            if(!StringUtils.isEmpty(site)&&!site.equals("null")) {
                String timeZoneStr = remote.call("/cstudio/wcm/util/timezone?site=" + site).getResponse();
                timeZoneStr = timeZoneStr.trim();
                ((HttpServletResponse) response).setHeader(TIMEZONE, timeZoneStr);
            }
        } catch (Exception e) {
            LOGGER.warn("could not set timezone");
        }
    }

    public void destroy() {

    }

    public void init(FilterConfig filterConfig) {

        this.servletContext = filterConfig.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(this.servletContext);
        /* retrieve the connector service */
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        /* retrieve the cookie manager */
        this.cookieManager = (CookieManager) context.getBean("rlCookieManager");
        /* retrieve the user preference manager */
        this.userPreferenceManager = (UserPreferenceManager) context.getBean("rlUserPreferenceManager");
        this.enableRememberLastPage = new Boolean(filterConfig.getInitParameter("enableRememberLastPage"));
        this.defaultLastPage = filterConfig.getInitParameter("defaultRememberLastPage");

    }

    public void setCookieManager(CookieManager cookieManager) {

        this.cookieManager = cookieManager;
    }

    public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager) {

        this.userPreferenceManager = userPreferenceManager;
    }
}
