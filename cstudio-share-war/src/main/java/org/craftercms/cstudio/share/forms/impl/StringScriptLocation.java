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
package org.craftercms.cstudio.share.forms.impl;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.extensions.webscripts.ScriptContent;

public class StringScriptLocation implements ScriptContent {

	private final String _script;

	public StringScriptLocation(String script) {
		_script = script;
	}

	public InputStream getInputStream() {

		InputStream retStream = null;

		try {
			
			retStream = new ByteArrayInputStream(_script.getBytes("UTF-8"));
		}
		catch(UnsupportedEncodingException errEncoding) {
			
		}

		return retStream;
	}

	public Reader getReader() {
		
		Reader retReader = null;
		
		try {
			retReader = new InputStreamReader(getInputStream(), "UTF-8");
		}
		catch(UnsupportedEncodingException errEncoding) {
			
		}

		return retReader;
	}

	public String getPath() {
		return _script;
	}

	public String getPathDescription() {
		return _script;
	}

	public boolean isCachable() {
		return true;
	}

	public boolean isSecure() {
		return true;
	}

	public String toString() {
		return getPathDescription();
	}
}
