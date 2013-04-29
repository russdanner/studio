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

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.extensions.webscripts.ScriptProcessor;

import org.craftercms.cstudio.share.forms.Form;
import org.craftercms.cstudio.share.forms.FormControllerParams;
import org.craftercms.cstudio.share.forms.FormException;
import org.craftercms.cstudio.share.forms.FormRenderParams;
import org.craftercms.cstudio.share.forms.FormSubmissionProcessor;
import org.craftercms.cstudio.share.forms.FormService;
import org.craftercms.cstudio.share.forms.FormStore;
import org.craftercms.cstudio.share.forms.FormUnavailableException;
import org.craftercms.cstudio.share.forms.ModelContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

// import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.apache.xml.dtm.ref.DTMNodeList;

import org.craftercms.cstudio.share.util.DomUtils;

import org.craftercms.cstudio.share.forms.ContentTransform;

import java.io.BufferedInputStream;

/**
 * provide basic infrastructure for rendering and processing forms
 * <p/>
 * Base implementation provides the basic functionality for loading forms and
 * processing submissions but does not address rendering. This can be left up to
 * implementations that address specific xform engines.
 * 
 * @author Russ Danner code review - remove system.out
 */
@SuppressWarnings("restriction")
public abstract class FormServiceBaseImpl implements FormService {

	protected static final Log logger = LogFactory.getLog(FormServiceBaseImpl.class);

	private FormStore formStore;
	private Map<String, FormSubmissionProcessor> formSubmissionProcessorMap;
	private Map<String, Object> _formControllerScriptObjectMap;
	private List<ContentTransform> inboundContentTransformers;
	private List<ContentTransform> outboundContentTransformers;
	private ScriptProcessor _scriptProcessor;
	private Map<String, Object> _cache;
	private boolean _cacheEnabled;

	/**
	 * @return the formControllerScriptObjectMap
	 */
	public Map<String, Object> getFormControllerScriptObjectMap() {
		return _formControllerScriptObjectMap;
	}

	/**
	 * @param formControllerScriptObjectMap
	 *            the formControllerScriptObjectMap to set
	 */
	public void setFormControllerScriptObjectMap(Map<String, Object> formControllerScriptObjectMap) {
		_formControllerScriptObjectMap = formControllerScriptObjectMap;
	}

	/**
	 * @return the cache
	 */
	public Map<String, Object> getCache() {
		return _cache;
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(Map<String, Object> cache) {
		_cache = cache;
	}

	/**
	 * @return the cacheEnabled
	 */
	public boolean getCacheEnabled() {
		return _cacheEnabled;
	}

	/**
	 * allow cache clear
	 */
	public void flushCache() {
		_cache.clear();
	};

	/**
	 * @param cacheEnabled
	 *            the cacheEnabled to set
	 */
	public void setCacheEnabled(boolean cacheEnabled) {
		_cacheEnabled = cacheEnabled;
	}

	/**
	 * @return the formStore
	 */
	public FormStore getFormStore() {

		return formStore;
	}

	/**
	 * @return the formSubmissionProcessorMap
	 */
	public Map<String, FormSubmissionProcessor> getFormSubmissionProcessorMap() {

		return formSubmissionProcessorMap;
	}

	/**
	 * @param formSubmissionProcessorMap
	 *            the formSubmissionProcessorMap to set
	 */
	public void setFormSubmissionProcessorMap(Map<String, FormSubmissionProcessor> formSubmissionProcessorMap) {

		this.formSubmissionProcessorMap = formSubmissionProcessorMap;
	}

	/**
	 * @param formStore
	 *            the formStore to set
	 */
	public void setFormStore(FormStore formStore) {

		this.formStore = formStore;
	}

	/**
	 * @param contentTransformers
	 *            Content Transformers collection
	 */
	public void setInboundContentTransformers(List<ContentTransform> inboundContentTransformers) {

		this.inboundContentTransformers = inboundContentTransformers;
	}

	/**
	 * @param contentTransformers
	 *            Content Transformers collection
	 */
	public List<ContentTransform> getInboundContentTransformers() {

		return this.inboundContentTransformers;
	}

	/**
	 * get script processor bean
	 * 
	 * @return
	 */
	public ScriptProcessor getScriptProcessor() {
		return _scriptProcessor;
	}

	/**
	 * set script processor
	 * 
	 * @param processor
	 */
	public void setScriptProcessor(ScriptProcessor processor) {
		_scriptProcessor = processor;
	}

	/**
	 * default constructor
	 */
	public FormServiceBaseImpl() {

		this.setFormStore(null);
		this.setFormSubmissionProcessorMap(new HashMap<String, FormSubmissionProcessor>());
		this.setFormControllerScriptObjectMap(new HashMap<String, Object>());
	}

	/**
	 * generate form
	 */
	public Form loadForm(String formId) throws FormUnavailableException, FormException {

		Form retForm = (Form) this.lookupCachedFormObject(formId + "-formDefinition");

		if (retForm == null) {
			retForm = getFormStore().loadForm(formId);

			if (retForm != null) {
				this.cacheFormObject(formId + "-formDefinition", retForm);
			}
		}

		return retForm;
	}

	/**
	 * process form submission
	 */
	public String processFormSubmission(String formId, InputStream model, String method, String action, Map<String, Object> parameters) {

		String response = null;
		InputStream activeModel = null;

		try {

			activeModel = model;

			FormSubmissionProcessor processor = this.getFormSubmissionProcessorMap().get(action);

			BufferedInputStream bufferedActiveModel = new BufferedInputStream(activeModel);

			response = processor.processFormSubmission(formId, bufferedActiveModel, method, action, parameters);

		}
		catch (Exception err) {
			logger.error("error processing for submission", err);
		}

		return response;
	}

	/**
	 * merge the instance data with the document
	 * 
	 * @param xformDocument
	 * @param model
	 * @return
	 */
	protected Document mergeInstanceDataWithForm(Document xformDocument, Map<String, Document> model, final String namespace) {

		Document retDocument = xformDocument;

		if (model != null) {

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			try {

				NamespaceContext cstudio = new NamespaceContext() {

					public String getNamespaceURI(String prefix) {

						String uri = "";
						if (prefix.equals(namespace))
							uri = "http://www.w3.org/2002/xforms";

						return uri;
					}

					@SuppressWarnings("unchecked")
					public Iterator getPrefixes(String val) {

						return null;
					}

					public String getPrefix(String uri) {

						return null;
					}

				};

				xPath.setNamespaceContext(cstudio);
				XPathExpression xPathExpression = xPath.compile("//" + namespace + ":instance");

				// DOMSource source = new DOMSource(xformDocument);
				// StringWriter xmlAsWriter = new StringWriter();
				// StreamResult result = new StreamResult(xmlAsWriter);
				// TransformerFactory.newInstance().newTransformer().transform(source,
				// result);
				StringReader xmlReader = new StringReader(DomUtils.xmlToString(xformDocument));
				InputSource documentAsInputSource = new InputSource(xmlReader);

				// using implementation here, see article
				// http://onjava.com/pub/a/onjava/2005/01/12/xpath.html
				DTMNodeList nodes = (DTMNodeList) xPathExpression.evaluate(documentAsInputSource, XPathConstants.NODESET);

				int nodeCount = nodes.getLength();

				for (int i = 0; i < nodeCount; i++) {
					Element currentNode = (Element) nodes.item(i);
					String modelId = currentNode.getAttribute("id");

					Document documentToInsert = model.get(modelId);

					if (documentToInsert != null) {

						Element importedModelRoot = documentToInsert.getDocumentElement();

						retDocument = currentNode.getOwnerDocument();
						Node importedModel = retDocument.importNode(importedModelRoot, true);

						currentNode.appendChild(importedModel);
					}
				}
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}

		return retDocument;
	}

	/**
	 * make it easy to render a form through webscripts
	 * 
	 * @param formId
	 *            the id of the form to load
	 * @param modelContainer
	 *            a map containing named model DOM objects
	 * @return an HTML string representation of the rendered form
	 */
	public String renderForm(String formId, ModelContainer modelContainer, FormRenderParams renderParameters, FormControllerParams controllerParams, boolean showFormDef) throws FormException {

		String retRenderedForm = "";
		StringBuilder sb = new StringBuilder();

		if (modelContainer == null) {
			modelContainer = new ModelContainerImpl();
		}

		Form form = this.loadForm(formId);

		this.executeFormController(form, modelContainer, renderParameters, controllerParams);

		retRenderedForm = this.renderForm(form, modelContainer, renderParameters, showFormDef);

		return retRenderedForm;
	}

	/**
	 * replace parameters in the xform definition with values in parameter map
	 * 
	 * @param renderedForm
	 * @param parameters
	 * @return
	 */
	protected String replaceParameters(String renderedForm, FormRenderParams parameters) {

		String retRenderedForm = renderedForm;
		Map<String, String> params = parameters.getParams();
		Set<String> keys = params.keySet();

		for (String key : keys) {
			retRenderedForm = retRenderedForm.replace("{" + key + "}", params.get(key));
		}

		return retRenderedForm;
	}

	/**
	 * create form controller parameters
	 * 
	 * @return
	 */
	public FormControllerParams createFormControllerParameters() {

		return new FormControllerParamsImpl();
	}

	/**
	 * @return a new name, value mapping for model objects
	 */
	public ModelContainer createModelContainer() {

		ModelContainer retContainer = new ModelContainerImpl();

		return retContainer;
	}

	/**
	 * given a string containing an xml document create a dom
	 * 
	 * @param xmlAsString
	 * @return
	 */
	public Document createXmlDocument(String xmlAsString) {

		return DomUtils.createXmlDocument(xmlAsString);
	}

	/**
	 * @return a map for use in passing parameters during form submission
	 */
	public Map<String, Object> createFormSubmissionParameterMap() {

		return new HashMap<String, Object>();
	}

	public FormRenderParams createFormRenderParameters() {
		return new FormRenderParamsImpl();
	}

	/**
	 * execute the form controller
	 * 
	 * @param formId
	 * @param modelContainer
	 * @param renderParams
	 * @param scriptModel
	 */
	public void executeFormController(Form form, ModelContainer modelContainer, FormRenderParams renderParams, FormControllerParams controllerParams) {

		Map<String, Object> model = new HashMap<String, Object>();

		String controllerScript = "";
		String controllerScriptBody = form.getFormController();
		String scriptIncludes = "";

		scriptIncludes = "<import resource=\"classpath:alfresco/site-webscripts/org/craftercms/wcm/lib/content-utils.js\">\r\n"+
						 "<import resource=\"classpath:alfresco/site-webscripts/org/craftercms/wcm/lib/form-utils.js\">\r\n";

		if (controllerScriptBody != null && !"".equals(controllerScriptBody.trim())) {

			if (controllerScriptBody != null && !"".equals(controllerScriptBody.trim())) {

				/* get includes from the controller */
				Pattern p = Pattern.compile("<import resource=(.*)>");
				Matcher m = p.matcher(controllerScriptBody);
				
				while(m.find()) {
					scriptIncludes += "<import resource="+m.group(1)+">\r\n";
				}
				
				/* now that we've captured them from the controller, remove them so we can build our own preamble */
				controllerScriptBody = controllerScriptBody.replaceAll("<import resource=(.*)>", "");

				/* script starts with includes */
				controllerScript += scriptIncludes;

				/* then the body */
				controllerScript += controllerScriptBody + "\r\n";

				/* then the controller execution */
				controllerScript += "if(controller) { " +
				                        "CStudioFormsEngine.dispatch(controller); " +
			 		                "}";
			}
			
			// spring supplied objects	
			for (String controllerObjectName : getFormControllerScriptObjectMap().keySet()) {

				model.put(controllerObjectName, getFormControllerScriptObjectMap().get(controllerObjectName));
			}

			// web script supplied objects
			for (String controllerObjectName : controllerParams.getParams().keySet()) {

				model.put(controllerObjectName, controllerParams.getParams().get(controllerObjectName));
			}

			_scriptProcessor.executeScript(new StringScriptLocation(controllerScript), model);
		}
	}

	/**
	 * provide cache capability to form consumers;
	 * 
	 * @param key
	 * @param value
	 */
	public void cacheFormObject(String key, Object value) {

		if (_cacheEnabled) {
			_cache.put(key, value);
		}
	}

	/**
	 * lookup a cached form object
	 * 
	 * @param key
	 * @return
	 */
	public Object lookupCachedFormObject(String key) {
		Object value = _cache.get(key);

		return value;
	}

	/**
	 * register a controller object so it can be added to all form controllers
	 * @param name
	 * @param obj
	 */
	public void registerControllerObject(String name, Object obj) {

		if(name != null && !"".equals(name.trim())) {
			this.getFormControllerScriptObjectMap().put(name, obj);
		}
	}

	/**
	 * get registered controller objects
	 */
	public Map<String, Object> getRegisteredControllerObjects() {
		return this.getFormControllerScriptObjectMap();
	}
}
