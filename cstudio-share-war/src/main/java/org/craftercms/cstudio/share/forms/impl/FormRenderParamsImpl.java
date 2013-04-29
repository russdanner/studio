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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.craftercms.cstudio.share.forms.FormRenderParams;
import org.craftercms.cstudio.share.forms.ModelContainer;

/**
 * Model container implementation
 * 
 * @author rdanner
 */
public class FormRenderParamsImpl implements FormRenderParams {
	
	private Map<String, String> _params;
	
	/**
	 * default constructors
	 */
	public FormRenderParamsImpl() {

		this.setParams(new HashMap<String, String>());
	}
	
	/**
	 * add item to container
	 */
	public void addParam(String name, String param) {

		this.getParams().put(name, param);
	}
	
	/**
	 * get a model for a given name
	 */
	public String getParam(String name) {

		return this.getParams().get(name);
	}
	
	/**
	 * @return the models
	 */
	public Map<String, String> getParams() {

		return _params;
	}
	
	/**
	 * @param models
	 *            the models to set
	 */
	public void setParams(Map<String, String> params) {

		_params = params;
	}
}
