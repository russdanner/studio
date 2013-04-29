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
package  org.craftercms.cstudio.share.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provide common methods for dealing with URLs
 * <p>
 * 
 * @author Russ Danner
 */
public class ServletUtils {
	
	/**
	 * @return the approperiate URI for the give dispatch cycle
	 */
	public static String determineUri(HttpServletRequest pHttpRequest) {

		String rRetUri = pHttpRequest.getRequestURI();
		
		if (pHttpRequest.getAttribute("javax.servlet.include.request_uri") != null) {
			rRetUri = (String) pHttpRequest.getAttribute("javax.servlet.include.request_uri");
		}
		// else
		// if(pHttpRequest.getAttribute("javax.servlet.forward.request_uri") !=
		// null)
		// {
		// Forward URI is in servlet, no action is needed here. Code below
		// returns the original url
		// rRetUri =
		// (String)pHttpRequest.getAttribute("javax.servlet.forward.request_uri");
		// }
		
		return rRetUri;
	}

	/**
	 * @return the appropriate URL for the give dispatch cycle
	 *
	 * Question: Do we need to deal with forward attributes?
	 */
	public static String determineBrowserUrlWithQueryString(HttpServletRequest pHttpRequest) {
		StringBuffer urlBuffer = pHttpRequest.getRequestURL();

		String queryString = pHttpRequest.getQueryString();
		if (queryString != null &&
		    queryString.trim().length() > 0)
			urlBuffer.append("?" + pHttpRequest.getQueryString());
		String result = urlBuffer.toString();		
		return result;
	}
	
	/**
	 * @return the approperiate URI for the give dispatch cycle
	 */
	public static String determineBrowserUri(HttpServletRequest pHttpRequest) {

		String rRetUri = pHttpRequest.getRequestURI();
		
		if (pHttpRequest.getAttribute("javax.servlet.forward.request_uri") != null) {
			rRetUri = (String) pHttpRequest.getAttribute("javax.servlet.forward.request_uri");
		}
		
		return rRetUri;
	}
	
	/**
	 * @return the approperiate URI for the give dispatch cycle
	 */
	public static String determineBrowserQueryString(HttpServletRequest pHttpRequest) {

		String rRetQueryString = pHttpRequest.getQueryString();
		
		if (pHttpRequest.getAttribute("javax.servlet.forward.request_uri") != null) {
			rRetQueryString = (String) pHttpRequest.getAttribute("javax.servlet.forward.query_string");
		}
		
		return rRetQueryString;
	}
	
	/**
	 * determin the dispatch type for the given request
	 * 
	 * @param pHttpServletRequest
	 *            servlet request to examine
	 * @return String REQUEST | INCLUDE | FORWARD
	 */
	public static int determineDispatchType(HttpServletRequest pHttpServletRequest) {

		int rRetDispatchType = REQUEST;
		
		if (pHttpServletRequest.getAttribute("javax.servlet.include.request_uri") != null) {
			rRetDispatchType = INCLUDE;
		}
		else if (pHttpServletRequest.getAttribute("javax.servlet.forward.request_uri") != null) {
			rRetDispatchType = FORWARD;
		}
		
		return rRetDispatchType;
	}
	
	/**
	 * given a query string from a url.getQuery, return a map containing the key
	 * value pairs
	 * 
	 * @param query
	 *            query string
	 * @return map containing key value pairs
	 */
	public static Map<String, String> getQueryParamMap(String query) {

		Map<String, String> retMap = new HashMap<String, String>();
		
		if (query != null) {
			String[] params = query.split("&");
			
			for (String param : params) {
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				retMap.put(name, value);
			}
		}
		
		return retMap;
	}
	
	public static final int REQUEST = 0;
	public static final int INCLUDE = 1;
	public static final int FORWARD = 2;
	
}
