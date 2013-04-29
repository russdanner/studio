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

import org.craftercms.cstudio.share.forms.FormControllerParams;

/**
 * Model container implementation
 * 
 * @author rdanner
 */
public class FormControllerParamsImpl implements FormControllerParams {
	
	private Map<String, Object> _params;
	
	/**
	 * default constructors
	 */
	public FormControllerParamsImpl() {

		this.setParams(new HashMap<String, Object>());
	}
	
	/**
	 * add item to container
	 */
	public void addParam(String name, Object param) {

		this.getParams().put(name, param);
	}
	
	/**
	 * merge a map in to this map
	 * @param mapToMerge
	 */
	public void putAll(Map <String, Object> mapToMerge) {
		this.getParams().putAll(mapToMerge);
	}
	
	/**
	 * get a model for a given name
	 */
	public Object getParam(String name) {

		return this.getParams().get(name);
	}
	
	/**
	 * @return the models
	 */
	public Map<String, Object> getParams() {

		return _params;
	}
	
	/**
	 * @param models
	 *            the models to set
	 */
	public void setParams(Map<String, Object> params) {

		_params = params;
	}
}
