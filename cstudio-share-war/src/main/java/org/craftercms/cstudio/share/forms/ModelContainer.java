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
import java.util.Set;

import org.w3c.dom.Document;

/**
 * model container contains multiple named model documents that need to be
 * included / merged in to the form
 * 
 * @author Russ Danner
 */
public interface ModelContainer {
	
	/**
	 * add model to container
	 * 
	 * @param name
	 *            name of a model
	 * @param model
	 *            model
	 */
	public void addModel(String name, Document model);
	
	/**
	 * given a name return the model
	 * 
	 * @param name
	 *            name of the model to return
	 * @return the model
	 */
	public Document getModel(String name);
	
	/**
	 * @return the names of all the models in the container
	 */
	public Set<String> getModelNames();
	
	/**
	 * @return a map of the name-model pairs contained within the container.
	 */
	public Map<String, Document> getModels();
}
