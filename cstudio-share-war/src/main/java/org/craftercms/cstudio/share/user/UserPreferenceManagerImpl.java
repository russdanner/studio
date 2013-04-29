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
package org.craftercms.cstudio.share.user;

import javax.servlet.http.HttpServletRequest;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.exception.CStudioException;
import org.craftercms.cstudio.share.servlet.CookieManagerImpl;
import org.craftercms.cstudio.share.servlet.ShareAuthenticationFilter;

/**
 * @author Sweta Chalasani
 * 
 */
public class UserPreferenceManagerImpl implements UserPreferenceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShareAuthenticationFilter.class);
	private CookieManagerImpl cookieManager;
	private final int COOKIE_AGE = 24 * 60 * 60;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.cstudio.share.user.UserPreferenceManager#getMostRecentDashboard
	 * (java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public String getMostRecentDashboard(String pUserId, HttpServletRequest httpReq) {
		try {
			return (String) cookieManager.getCookieValue(httpReq, pUserId);
		} catch (CStudioException e) {
			LOGGER.error("Failed to read the page cookie from the request for user: " + pUserId, e);
			// TODO: return UserPreferenceManager.WIDGET_WINDOW_STATE_NOMRAL (check with Sweta)
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.cstudio.share.user.UserPreferenceManager#getWidgetWindowState
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getWidgetWindowState(String pUserId, String pDashboardName, String pWidgetId) {
		SeamlessAppContext context = SeamlessAppContext.currentApplicationContext();
		String key = pUserId + pDashboardName + pWidgetId;
		try {
			return (String) cookieManager.getCookieValue(context.getRequest(), key);
		} catch (CStudioException e) {
			LOGGER.error("Failed to read the widget state for user: " + pUserId + " dashboard: " + pDashboardName
					+ " widget: " + pWidgetId, e);
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.cstudio.share.user.UserPreferenceManager#setMostRecentDashboard
	 * (java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public void setMostRecentDashboard(String pUserId, String pDashboardName, HttpServletRequest httpReq,
			HttpServletResponse httpRes) {
		try {
			// protection from unexpected null pointer, hence 500 error
			// expecting no harm if Most Recent Dashboard cookie is empty
			if ( !StringUtils.isEmpty(pUserId) && (httpReq != null) && !StringUtils.isEmpty(httpReq.getRequestURI())) { 
				cookieManager.putCookieValue(httpReq, httpRes, pUserId, this.COOKIE_AGE, httpReq.getRequestURI());
			}
		} catch (CStudioException e) {
			LOGGER.error("Failed to set the most recent dashboard for user: " + pUserId + " dashboard: " + pDashboardName,
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.cstudio.share.user.UserPreferenceManager#setWidgetWindowState
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setWidgetWindowState(String pUserId, String pDashboardName, String pWidgetId, String pState) {
		SeamlessAppContext context = SeamlessAppContext.currentApplicationContext();
		String key = pUserId + pDashboardName + pWidgetId;
		try {
			// protection from unexpected null pointer, hence 500 error
			// expecting no harm if Widget window state cookie is empty
			if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(pState)) {
				cookieManager.putCookieValue(context.getRequest(), context.getResponse(), key, this.COOKIE_AGE, pState);
			}
		} catch (CStudioException e) {
			LOGGER.error("Failed to set the widget state for user: " + pUserId + " dashboard: " + pDashboardName
					+ " widget: " + pWidgetId + ", state: " + pState, e);
		}
	}

	public void setCookieManager(CookieManagerImpl cookieManager) {
		this.cookieManager = cookieManager;
	}

}
