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
import java.util.Set;

import org.w3c.dom.Document;

import org.craftercms.cstudio.share.forms.ModelContainer;

/**
 * Model container implementation
 * 
 * @author rdanner
 */
public class ModelContainerImpl implements ModelContainer {
	
	private Map<String, Document> _models;
	
	/**
	 * default constructors
	 */
	public ModelContainerImpl() {

		this.setModels(new HashMap<String, Document>());
	}
	
	/**
	 * add item to container
	 */
	public void addModel(String name, Document model) {

		this.getModels().put(name, model);
	}
	
	/**
	 * get a model for a given name
	 */
	public Document getModel(String name) {

		return this.getModels().get(name);
	}
	
	/**
	 * @return the names of all the models in the container
	 */
	public Set<String> getModelNames() {
		return _models.keySet();
	}

	/**
	 * @return the models
	 */
	public Map<String, Document> getModels() {

		return _models;
	}
	
	/**
	 * @param models
	 *            the models to set
	 */
	public void setModels(Map<String, Document> models) {

		_models = models;
	}
}
