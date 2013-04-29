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

/**
 * Abstract the provisioning of a given form
 * 
 * @author Russ Danner
 */
public interface FormStore {

	/**
	 * load form object form store
	 * @param formId
	 *            The ID of the form in question
	 * @return Form
	 * @throws FormException, FormUnavailableException 
	 */
	public Form loadForm(String formId) throws FormException, FormUnavailableException;
	
	/**
	 * This is a utility method for the webscript to do testing
	 * 
	 * @param formId
	 * @return
	 * @throws Exception
	 */
	public String invokeLoadForm(String formId) throws Exception;

	/**
	 * determine if form is available in store
	 *
	 * @param formId
	 *            The ID of the form in question
	 * @return true if found false if not
	 */
	public boolean isFormInStore(String formId);

}
