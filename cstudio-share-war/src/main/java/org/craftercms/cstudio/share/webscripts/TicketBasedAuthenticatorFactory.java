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
package org.craftercms.cstudio.share.webscripts;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.share.constants.ShareConstants;
import org.craftercms.cstudio.share.servlet.ShareAuthenticationFilter;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.BasicHttpAuthenticatorFactory;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;

/**
 * Authenticator factory that is using alf_ticket  for authentication
 * 
 * @author hyanghee
 *
 */
public class TicketBasedAuthenticatorFactory extends BasicHttpAuthenticatorFactory {

	@Override
	public Authenticator create(WebScriptServletRequest req, WebScriptServletResponse res) {
		return new TicketBasedAuthenticator(req, res);
	}

	/**
	 * HTTP Basic Authentication
	 */
	public class TicketBasedAuthenticator extends BasicHttpAuthenticator {

        private WebScriptServletRequest servletReq;
        private WebScriptServletResponse servletRes;
        
		/**
		 * constructor
		 * 
		 * @param req
		 * @param res
		 */
		public TicketBasedAuthenticator(WebScriptServletRequest req, WebScriptServletResponse res) {
			super(req, res);
            this.servletReq = req;
            this.servletRes = res;
		}

		@Override
		public boolean authenticate(RequiredAuthentication required, boolean isGuest) {
            HttpServletRequest httpReq = servletReq.getHttpServletRequest();
            // find alf_ticket from cookies
            Cookie [] cookies = httpReq.getCookies();
            String ticket = null;
            if (cookies != null) {
            	for (Cookie cookie : cookies) {
            		if (cookie.getName().equalsIgnoreCase(ShareConstants.COOKIE_ALFRESCO_TICKET)) {
            			ticket = cookie.getValue();
            		}
            	}
            }
            // if not found in cookie, check in the request parameters
            if (StringUtils.isEmpty(ticket)) {
            	ticket = httpReq.getParameter(ShareConstants.COOKIE_ALFRESCO_TICKET);
            }
            boolean authenticated = !StringUtils.isEmpty(ticket);
            if (!authenticated) {
            	// if fails the authentication, call basic http authentication
            	return super.authenticate(required, isGuest);
            } else {
            	return authenticated;
            }
		}

		/**
		 * @return the servletRequest
		 */
		public WebScriptServletRequest getServletRequest() {
			return servletReq;
		}

		/**
		 * @param servletRequest the servletRequest to set
		 */
		public void setServletRequest(WebScriptServletRequest servletRequest) {
			this.servletReq = servletRequest;
		}

		/**
		 * @return the servletResponse
		 */
		public WebScriptServletResponse getServletResponse() {
			return servletRes;
		}

		/**
		 * @param servletRes the servletResponse to set
		 */
		public void setServletResponse(WebScriptServletResponse servletResponse) {
			this.servletRes = servletResponse;
		}
	}

}
