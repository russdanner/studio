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

package org.craftercms.cstudio.share.forms.impl.store;

import java.io.InputStream;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.craftercms.cstudio.share.forms.Form;
import org.craftercms.cstudio.share.forms.FormStore;
import org.craftercms.cstudio.share.forms.impl.orbeon.FormImpl;

/**
 * Load xform definitions from class path
 * 
 * @author rdanner
 * 
 * CODEREV
 * - exceptions
 * - use of logging package
 * - constants
 * - javadoc
 */
public class ClassPathFormStoreImpl implements FormStore {
	
	public Form loadForm(String formId) {

		FormImpl retForm = null;
		InputStream formAsInputStream = null;
		ClassLoader classloader = this.getClassLoader();
		
		try {
			
			formAsInputStream = classloader.getResourceAsStream("cstudio/cstudio/config/forms/" + formId + "/xform.xml");
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			Document xformDefinitionDoc = docBuilder.parse(formAsInputStream);
			
			retForm = new FormImpl();
			retForm.setFormId(formId);
			retForm.setFormDefinition(xformDefinitionDoc);
		}
		catch (Exception err) {
			
			err.printStackTrace();
		}
		
		return retForm;
	}

	/**
	 * @return true if form can be found in store
	 */
	public boolean isFormInStore(String formId) {
		
		// lie
		return true;
	}	

	/**
	 * @return the cloassloader used to retrieve the forms
	 */
	protected ClassLoader getClassLoader() {

		return this.getClass().getClassLoader();
	}

	public String invokeLoadForm(String formId) throws Exception {
		
		// lie
		return null;
	}
}
