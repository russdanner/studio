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

package org.craftercms.cstudio.share.forms;

import java.util.Map;

import org.w3c.dom.Document;

/**
 * params container contains multiple named parameters that need to be
 * included / merged in to the form
 * 
 * @author Russ Danner
 */
public interface FormRenderParams {
	
	/**
	 * add model to container
	 * 
	 * @param name
	 *            name of a model
	 * @param param
	 *            model
	 */
	public void addParam(String name, String param);
	
	/**
	 * given a name return the model
	 * 
	 * @param name
	 *            name of the param to return
	 * @return the param
	 */
	public String getParam(String name);
	
	/**
	 * @return a map of the name-model pairs contained within the container.
	 */
	public Map<String, String> getParams();
}
