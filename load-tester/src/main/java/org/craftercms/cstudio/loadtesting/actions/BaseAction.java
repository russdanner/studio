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
package org.craftercms.cstudio.loadtesting.actions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.params.HttpClientParams;

public class BaseAction {

	public int runAction(HttpClient httpClient, HttpMethodBase method, String username) throws Exception {
		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(0);
		httpClient.setParams(params);
		if (username != null) {	
			method.addRequestHeader("cookie", "username=" + username);
		}
		return httpClient.executeMethod(method);
	}
	
}
