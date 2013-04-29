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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.craftercms.cstudio.share.forms.FormService;

/**
 * helper class to register controller params without needing to override spring beans
 */
public class FormControllerParamHandler {

	protected static final Log logger = LogFactory.getLog(FormControllerParamHandler.class);

	private FormService _formService;
	private String _name;
	private Object _object;

	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return _object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		_object = object;
	}

	/**
	 * @return the formService
	 */
	public FormService getFormService() {
		return _formService;
	}

	/**
	 * @param formService the formService to set
	 */
	public void setFormService(FormService formService) {
		_formService = formService;
	}
	
	/**
	 * register object so that it is available in form controllers
	 */
	public void registerControllerObject() {
		if(this.getName() != null && !"".equals(this.getName().trim())) {
			this.getFormService().registerControllerObject(this.getName(), this.getObject());
		}
		else {
			logger.error("unable to register object '"+ this.getObject() +"' as controller object because name property not set");
		}
	}

	
}
