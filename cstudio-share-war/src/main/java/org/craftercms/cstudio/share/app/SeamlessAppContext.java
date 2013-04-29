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

package org.craftercms.cstudio.share.app;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * provide access throught the application to salient information
 * <ul>
 * <li>current request</li>
 * <li>current response</li>
 * <li>current serlvet context</li>
 * <li>current ticket</li>
 * <ul>
 * 
 * @author Sweta Chalasani
 */
public class SeamlessAppContext {
	
	private static ThreadContainer mThreadContainer = new ThreadContainer();
	private String ticket;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;

	public ServletContext getServletContext() {

		return servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {

		this.servletContext = servletContext;
	}
	
	/**
	 * set the current component container for the thread
	 * @param pContainer - container to set for thread 
	 */
	public static void setApplicationContextForThread(SeamlessAppContext pContainer)
	{

		if (mThreadContainer != null) {
			mThreadContainer.set(pContainer);
		}
	}
	
	/**
	 * return the current application context, null if no context is available
	 */
	public static SeamlessAppContext currentApplicationContext() {

		SeamlessAppContext vRetContainer = null;
		
		if (mThreadContainer != null) {
			vRetContainer = (SeamlessAppContext) mThreadContainer.get();
		}
		
		return vRetContainer;
	}
	
	protected static class ThreadContainer extends ThreadLocal<SeamlessAppContext> {
		
		public SeamlessAppContext initialValue() {

			return null;
		}
	}

	public String getTicket() {

		return ticket;
	}
	
	public void setTicket(String ticket) {

		this.ticket = ticket;
	}
	
	public HttpServletRequest getRequest() {

		return request;
	}
	
	public void setRequest(HttpServletRequest request) {

		this.request = request;
	}
	
	public HttpServletResponse getResponse() {

		return response;
	}
	
	public void setResponse(HttpServletResponse response) {

		this.response = response;
	}
	
}
