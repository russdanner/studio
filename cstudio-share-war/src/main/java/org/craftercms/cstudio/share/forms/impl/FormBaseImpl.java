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

import org.craftercms.cstudio.share.forms.Form;
import org.w3c.dom.Document;

/**
 * Form encapsulates the details of the form implementation. Base implementation
 * 
 * @author Russ Danner
 */
public abstract class FormBaseImpl implements Form {
	
	private String formId;
	private Document formDefinition;
	private String _formController;
	
	/**
	 * @return the formDefinition
	 */
	public Document getFormDefinition() {

		return formDefinition;
	}
	
	/**
	 * @param formDefinition
	 *            the formDefinition to set
	 */
	public void setFormDefinition(Document formDefinition) {

		this.formDefinition = formDefinition;
	}
	
	/**
	 * @return return the id of the form
	 */
	public String getFormId() {

		return this.formId;
	}
	
	/**
	 * set form Id
	 * 
	 * @param id
	 *            id to set
	 */
	public void setFormId(String id) {

		this.formId = id;
	}

	/**
	 * setter for form controller
	 * @param script
	 */
	public void setFormController(String script) {
		_formController = script;
	}
	
	/**
	 * script as string for controlling form
	 * @return
	 */
	public String getFormController() {
	
		return _formController;
	}
}
