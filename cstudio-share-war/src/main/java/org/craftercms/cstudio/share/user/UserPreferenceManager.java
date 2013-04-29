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

/**
 * @author Sweta Chalasani
 * 
 */
public interface UserPreferenceManager {
	
	public static int WIDGET_WINDOW_STATE_NOMRAL = 0;
	public static int WIDGET_WINDOW_STATE_MINIMIZED = 1;
	public static int WIDGET_WINDOW_STATE_MAXIMIZED = 2;

	/**
	 * for a given dashboard and widget id combo return the window state
	 * 
	 * @param pUserId
	 *            user Id
	 * @param pDashboardName
	 *            page id of dashboard screen
	 * @param pWidgetId
	 *            id of widget
	 * @return method returns recorded state if know or
	 *         WIDGET_WINDOW_STATE_NOMRAL if unknown.
	 */
	public String getWidgetWindowState(String pUserId, String pDashboardName, String pWidgetId);

	/**
	 * set a given dashboard and widget id combo
	 * 
	 * @param pUserId
	 *            user Id
	 * @param pDashboardName
	 *            page id of dashboard screen
	 * @param pWidgetId
	 *            id of widget
	 * @param pState
	 * 			widget state
	 */
	public void setWidgetWindowState(String pUserId, String pDashboardName, String pWidgetId, String pState);

	/**
	 * get most recent dashboad for the current user
	 * 
	 * @param pUserId
	 *            user Id
	 * @return returns the most recent dashboard for the given user
	 */
	public String getMostRecentDashboard(String pUser, HttpServletRequest httpReq);

	/**
	 * get most recent dashboad for the current user
	 * 
	 * @param pUserId
	 *            user Id
	 * @param pDashboardName
	 *            page id of dashboard screen
	 * @return returns the most recent dashboard for the given user
	 */
	public void setMostRecentDashboard(String pUserId, String pDashboardName, HttpServletRequest httpReq,
			HttpServletResponse httpRes);

}
