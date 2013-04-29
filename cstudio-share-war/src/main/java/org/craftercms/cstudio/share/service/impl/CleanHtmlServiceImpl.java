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
package org.craftercms.cstudio.share.service.impl;

import org.craftercms.cstudio.share.service.api.CleanHtmlService;
import org.craftercms.cstudio.share.service.api.CleanMarkupConfig;

import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * Clean inbound HTML up and respond with valid markup
 */
public class CleanHtmlServiceImpl implements CleanHtmlService {
	
	/**
	 * @return an empty properties map
	 */
	public CleanMarkupConfig createConfiguration() {
		return new MarkupConfig();
	}
	
	/**
	 * clean up inbound markup
	 * @param dirtyMarkup
	 */
	public String cleanMarkup(String dirtyMarkup) {
		return this.cleanMarkup(dirtyMarkup, null);
	}
	
	/**
	 * clean up inbound markup
	 * @param dirtyMarkup
	 * @return
	 */
	public String cleanMarkup(String dirtyMarkup, CleanMarkupConfig config) {
		
		String retCleanMarkup = dirtyMarkup;
		
		try {
			Tidy tidy = new Tidy();
			
			if(config != null) {
				Properties props = config.getProps();
				tidy.setConfigurationFromProps(props);
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(dirtyMarkup.getBytes("UTF-8"));
			
			tidy.parse(in, out);
		
			retCleanMarkup = out.toString("UTF-8");
		}
		catch(Exception err) {
			System.out.println("error while cleaning html markup "+err);
		}
		
		return retCleanMarkup;
	}

	private class MarkupConfig implements CleanMarkupConfig {
		
		private Properties _props;
		
		public MarkupConfig() {
			_props = new Properties(); 
		}
		
		public Properties getProps() {
			return _props;
		}
		
		public void addParam(String key, String value) {
			_props.put(key, value);
		}
	}
}
