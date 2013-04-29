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
package org.craftercms.cstudio.share.servlet;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.cstudio.share.exception.CStudioException;

/**
 * This interface is used for setting and getting serialized objects in cookies
 * 
 * @author Chander Shankar
 * 
 */
public interface CookieManager {

	/**
	 * read a value by the given key from http cookie
	 * @param request
	 * @param key
	 * @return Serializable if found
	 * @throws CStudioException if cookie value exists but cannot be retrieved 
	 */
	public Serializable getCookieValue(HttpServletRequest request, String key) throws CStudioException;
	
	/**
	 * put a value with the given key to http cookie
	 * @param response
	 * @param key 
	 * @param age time to live in seconds
	 * @param value 
	 * @exception CStudioException if cookie value is provided but cannot be saved
	 */
	public void putCookieValue(HttpServletRequest request, HttpServletResponse response, String key, int age,
			Serializable value) throws CStudioException;
	
	/**
	 * put a value with the given key to http cookie
	 * @param response
	 * @param path
	 * @param key
	 * @param age
	 * @param value
	 * @exception CStudioException if cookie value is provided but cannot be saved
	 */
	public void putCookieValue(HttpServletRequest request, HttpServletResponse response, String path, 
			String key, int age, Serializable value) throws CStudioException;
		
	/**
	 * removes the cookie named 'key' in the 'request' from the 'response'
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @param key
	 */
	public void destroyCookie(HttpServletRequest request,
			HttpServletResponse response, String key, String path);
	
	/**
	 * removes the cookie named 'key' in the 'request' from the 'response'
	 * @param request
	 * @param response
	 * @param key
	 */
	public void destroyCookie(HttpServletRequest request,
			HttpServletResponse response, String key);
	
}
