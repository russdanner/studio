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

import java.io.InputStream;
import java.util.Map;

/**
 * provide basic infrastructure for rendering and processing forms
 * 
 * @author Russ Danner CODE REVIEW oct 28 - add common exceptions as
 *         declared exceptions - factor createXMLDOm method as new service in
 *         webscript framework - create base exception class and package - drop
 *         use of map in favor of model contain
 */
public interface FormService {
	
	/**
	 * given a model and form ID return an xform definition that is ready to
	 * render
	 * 
	 * @param formId
	 *            the form id to load
	 * @param model
	 *            the model include
	 */
	public Form loadForm(String formId) throws FormUnavailableException, FormException;
	
	/**
	 * make it easy to render a form through webscripts
	 * 
	 * @param formId
	 *            the id of the form to load
	 * @param modelContainer
	 *            a map containing named model DOM objects
	 * @return an HTML string representation of the rendered form
	 */
	public String renderForm(String formId, ModelContainer modelContainer, FormRenderParams renderParameters,  FormControllerParams formControllerParams, boolean showFormDef) throws FormException;
	
	/**
	 * make it easy to render a form through webscripts
	 * 
	 * @param form
	 *            the to render
	 * @param modelContainer
	 *            a map containing named model DOM objects
	 * @return an HTML string representation of the rendered form
	 */
	public String renderForm(Form form, ModelContainer modelContainer, FormRenderParams renderParameters, boolean showFormDef) throws FormException;
	
	/**
	 * given a form submission process the result
	 * 
	 * @param formId
	 *            the id of the form to process
	 * @param model
	 *            the submitted model
	 * @param method
	 *            the submit method
	 * @param action
	 *            the submit action
	 * @param parameters
	 *            parameters to use in the processing
	 */
	public String processFormSubmission(String formId, InputStream model, String method, String action,
			Map<String, Object> parameters) throws FormSubmissionException;
	
	/**
	 * @return a new name, value mapping for model objects
	 */
	public ModelContainer createModelContainer();

	/**
	 * create form controller parameters
	 * @return
	 */
	public FormControllerParams createFormControllerParameters();

	/**
	 * @return a map for use in passing parameters during form submission
	 */
	public Map<String, Object> createFormSubmissionParameterMap();

	/**
	 * @return a map for parameters
	 */
	public FormRenderParams createFormRenderParameters();

	/**
	 * provide cache capability to form consumers;
	 * @param key
	 * @param value
	 */
	public void cacheFormObject(String key, Object value);
	
	/**
	 * lookup a cached form object
	 * @param key
	 * @return
	 */
	public Object lookupCachedFormObject(String key);
	
	/**
	 * register a controller object so it can be added to all form controllers
	 * @param name
	 * @param obj
	 */
	public void registerControllerObject(String name, Object obj);
	
	/**
	 * return registered controller objects
	 */
	public Map<String, Object> getRegisteredControllerObjects();
}
