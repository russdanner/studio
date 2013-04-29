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

package org.craftercms.cstudio.share.forms.impl.orbeon;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.craftercms.cstudio.share.app.SeamlessAppContext;

import org.craftercms.cstudio.share.forms.Form;
import org.craftercms.cstudio.share.forms.FormException;
import org.craftercms.cstudio.share.forms.FormRenderParams;
import org.craftercms.cstudio.share.forms.FormSubmissionException;
import org.craftercms.cstudio.share.forms.FormUnavailableException;
import org.craftercms.cstudio.share.forms.ModelContainer;
import org.craftercms.cstudio.share.forms.impl.FormServiceBaseImpl;
import org.craftercms.cstudio.share.servlet.ShareAuthenticationFilter;
import org.craftercms.cstudio.share.util.DomUtils;
import org.craftercms.cstudio.share.util.MD5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * provide basic infrastructure for rendering and processing forms
 * <p/>
 * Use XForms Engine to render the given web form specification
 * 
 * @author Russ Danner
 */
@SuppressWarnings("restriction")
public class FormServiceImpl extends FormServiceBaseImpl {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(FormServiceImpl.class);
	
	public FormServiceImpl() {
		this.setCache(new HashMap<String, Object>());
	}
	
	/**
	 * given an XFORM definition (model included) render the XFORM
	 * 
	 * @parm xform the form definition to be rendered
	 */
	public String renderForm(Form xform, ModelContainer model, FormRenderParams parameters, boolean showForm) {

		String retForm = null;
		boolean isSimpleForm = false;
		FormImpl form = (FormImpl) xform;

		Document xformDocument = form.getFormDefinition();

		if(parameters.getParams().get("simple")!=null && parameters.getParams().get("simple").equals("true")) {
			try {
				DOMSource source = new DOMSource(xformDocument);  
				StringWriter xmlAsWriter = new StringWriter();  
				StreamResult result = new StreamResult(xmlAsWriter);  
				TransformerFactory.newInstance().newTransformer().transform(source, result);  
				ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8")); 
		
				StreamResult transformResult = new StreamResult(new StringWriter());
				TransformerFactory tf = TransformerFactory.newInstance();
				String styleSheetStr = DomUtils.xmlToString(DomUtils.createXmlDocument(getClass().getResourceAsStream("/org/craftercms/cstudio/simpleToXForm.xsl")), "UTF-8");
				StreamSource stylesheetSrc = new StreamSource(new ByteArrayInputStream( styleSheetStr.getBytes("UTF-8") ));

				Transformer t = tf.newTransformer(stylesheetSrc);
				t.transform(new StreamSource(inputStream), transformResult);
				
				String singleComponentFormXml = transformResult.getWriter().toString();
				xformDocument = DomUtils.createXmlDocument(singleComponentFormXml, "UTF-8");
			}
			catch(Exception err) {
				LOGGER.error("error transforming form to single field" , err);
			}			
		}

		this.mergeInstanceDataWithForm(xformDocument, model.getModels(), "xforms");

		if(parameters.getParams().get("mode")!=null && parameters.getParams().get("mode").equals("ice")) {
			try {
				DOMSource source = new DOMSource(xformDocument);  
				StringWriter xmlAsWriter = new StringWriter();  
				StreamResult result = new StreamResult(xmlAsWriter);  
				TransformerFactory.newInstance().newTransformer().transform(source, result);  
				ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8")); 
		
				StreamResult transformResult = new StreamResult(new StringWriter());
				TransformerFactory tf = TransformerFactory.newInstance();
				String styleSheetStr = DomUtils.xmlToString(DomUtils.createXmlDocument(getClass().getResourceAsStream("/org/craftercms/cstudio/singleElementOnly.xsl")), "UTF-8");

				styleSheetStr = styleSheetStr.replace("{fieldId}", parameters.getParams().get("field"));
				StreamSource stylesheetSrc = new StreamSource(new ByteArrayInputStream( styleSheetStr.getBytes("UTF-8") ));

				Transformer t = tf.newTransformer(stylesheetSrc);
				t.transform(new StreamSource(inputStream), transformResult);
				
				String singleComponentFormXml = transformResult.getWriter().toString();
				xformDocument = DomUtils.createXmlDocument(singleComponentFormXml, "UTF-8");
			}
			catch(Exception err) {
				LOGGER.error("error transforming form to single field" , err);
			}
		}
		
		retForm = this.performRender(xformDocument, parameters, showForm);

		return retForm;
	}

	/**
	 * in orbeon we get a major performance boost by caching the
	 * form definitions.  So much so that the default implemenation of this
	 * method which actually merges the dom in should not be used.
	 * <p>
	 * Instead we will add the models to the request and let orbeon forms
	 * include them
	 */
	protected Document mergeInstanceDataWithForm(Document xformDocument, Map<String, Document> model, final String namespace) {

		HttpServletRequest httpRequest = this.getHttpServletRequest();
		
		Set<Entry<String, Document>> modelEntries = model.entrySet();
		
		for(Entry<String, Document> modelEntry : modelEntries) {
			httpRequest.setAttribute(modelEntry.getKey(), modelEntry.getValue());
		}
		
		return xformDocument;
	}

	protected String replaceParameters(String renderedForm, FormRenderParams parameters) {

		HttpServletRequest httpRequest = this.getHttpServletRequest();

		String retRenderedForm = renderedForm;
		Map<String, String> params = parameters.getParams();
		Set<String> keys = params.keySet();

		for (String key : keys) {
			String value = params.get(key);
			httpRequest.setAttribute(key, "<value>"+value+"</value>");   
			retRenderedForm = retRenderedForm.replace("{" + key + "}", params.get(key));
		}

		return retRenderedForm;
	}

	/**
	 * perform render using the engine
	 * 
	 * @param xformDocument
	 * @return an inputstream containing the rendered form
	 */
	protected String performRender(Document xformDocument, FormRenderParams parameters, boolean showForm) {

		String retForm = null;
		
		if (xformDocument == null) {
			return null;
		}
		
		HttpServletRequest httpRequest = this.getHttpServletRequest();
		HttpServletResponse httpResponse = this.getHttpServletResponse();
		ServletContext servletContext = this.getServletContext();
		
		String xformAsString = DomUtils.xmlToString(xformDocument, "UTF-8");//"US-ASCII");
		xformAsString = this.replaceParameters(xformAsString, parameters);

		if(!showForm){
			
			LOGGER.debug("MD5 hash " + MD5.compute(xformAsString));
			
			httpRequest.setAttribute("render.form", xformAsString);   
			httpRequest.setAttribute("orbeon-embeddable", "true");

			try {
				
				/** 
				 * tried using ?orbeon-embeddable=true
				 * doesn't seem to make a difference
				 */
				RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/form-controller/render/");
				
				BufferedResponseWrapper responseWrapper = new BufferedResponseWrapper(httpResponse);
				
				dispatcher.include(httpRequest, responseWrapper);
				
				retForm = responseWrapper.getString();
				//retForm = retForm.replaceAll("~entity~", "&#");
	
				/* temporary solution until it's determined how to properly create an embeded form
			     * testing in FF shows no issues with this approach
				 */
				retForm = retForm.replaceAll("<html>", "<div>");
				retForm = retForm.replaceAll("</html>", "</div>");
				
				retForm = retForm.replaceAll("<head>", "<div>");
				retForm = retForm.replaceAll("</head>", "</div>");
				
				retForm = retForm.replaceAll("<body ", "<div ");
				retForm = retForm.replaceAll("</body>", "</div>");
				retForm = retForm.replaceAll("<!DOCTYPE html", "");
				retForm = retForm.replaceAll("PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict2.dtd\">", "");
	
				// id are being corrupted
				String badIdSeperator = _separateCharToReplace;    // U+00AC (not) and U+03A3 (sigma)
				String goodIdSeparateor =  _separateCharToUse;
				retForm = retForm.replaceAll(badIdSeperator, goodIdSeparateor);
				
			}
			catch (Exception eIncludeFailure) {
				eIncludeFailure.printStackTrace();
			}
		}
		else {
			httpResponse.setHeader("content-type","text/xml");
			retForm = xformAsString;
		}
			
		return retForm;
	}
	
	/**
	 * @return the current http servlet request
	 */
	protected HttpServletRequest getHttpServletRequest() {

		SeamlessAppContext appContext = SeamlessAppContext.currentApplicationContext();
		
		return appContext.getRequest();
	}
	
	/**
	 * @return the current http servlet response
	 */
	protected HttpServletResponse getHttpServletResponse() {

		SeamlessAppContext appContext = SeamlessAppContext.currentApplicationContext();
		
		return appContext.getResponse();
	}
	
	/**
	 * @return the current servlet context
	 */
	protected ServletContext getServletContext() {

		SeamlessAppContext appContext = SeamlessAppContext.currentApplicationContext();
		
		return appContext.getServletContext();
	}
	

	public void processFormSubmission(String formId, InputStream model, String method, String action,
			FormRenderParams parameters) throws FormSubmissionException {

		// TODO Auto-generated method stub
		
	}

	public String getSeparateCharToReplace() {
		return _separateCharToReplace;
	}
	
	public void setSeparateCharToReplace(String value) {
		_separateCharToReplace = value;
	}

	public String GetSeparateCharToUse() {
		return _separateCharToUse;
	}
	
	public void setSeparateCharToUse(String value) {
		_separateCharToUse = value;
	}

	public String _separateCharToReplace;
	public String _separateCharToUse;

}
