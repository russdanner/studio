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
package org.craftercms.cstudio.share.forms.impl.submission;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.ScriptProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.forms.Form;
import org.craftercms.cstudio.share.forms.FormControllerParams;
import org.craftercms.cstudio.share.forms.FormService;
import org.craftercms.cstudio.share.forms.FormSubmissionProcessor;
import org.craftercms.cstudio.share.forms.impl.StringScriptLocation;
import org.craftercms.cstudio.share.forms.impl.orbeon.BufferedResponseWrapper;
import org.craftercms.cstudio.share.forms.impl.FormUtils;

/**
 *execute a script
 * 
 * @author Russ Danner
 */
public class ExecuteControllerScriptSubmissionProcessor implements FormSubmissionProcessor {

	private FormService _formService;
	private ScriptProcessor _scriptProcessor;
		
	/**
	 * @return the scriptProcessor
	 */
	public ScriptProcessor getScriptProcessor() {
		return _scriptProcessor;
	}

	/**
	 * @param scriptProcessor the scriptProcessor to set
	 */
	public void setScriptProcessor(ScriptProcessor scriptProcessor) {
		_scriptProcessor = scriptProcessor;
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
			Map<String, Object> parameters) {

		String response = null;
		FormService formService = this.getFormService();
		ScriptProcessor scriptProcessor = this.getScriptProcessor();

		try {
			Map<String, Object> scriptModel = new HashMap<String, Object>();
			Form form = formService.loadForm(formId);
			String scriptIncludes = "";
			String controllerScriptBody = form.getFormController();

			scriptIncludes = "<import resource=\"classpath:alfresco/site-webscripts/org/craftercms/wcm/lib/content-utils.js\">\r\n"+
                             "<import resource=\"classpath:alfresco/site-webscripts/org/craftercms/wcm/lib/form-utils.js\">\r\n";
			
			String controllerScript = "";
			
			if(controllerScriptBody != null && !"".equals(controllerScriptBody.trim())) {

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
                controllerScript += 
                	"if(controller) { " +
                		"CStudioFormsEngine.dispatch(controller); " +
                	"}";
			}

			FormControllerParams responseContainer = formService.createFormControllerParameters();
			FormControllerParams submissionParameters = formService.createFormControllerParameters();

			if("true".equals((String)parameters.get("xfromModify"))) {
				String xml = this.modelToString(model);
				scriptModel.put("xml", xml);
			}			

			submissionParameters.putAll(parameters);
			
			scriptModel.put("formId", formId);
			scriptModel.put("submissionParameters", submissionParameters);
			scriptModel.put("responseContainer", responseContainer);
			scriptModel.put("formUtils",new FormUtils());
			scriptModel.putAll(parameters);
			
			Set<Entry<String, Object>> registerdControllerObjs = formService.getRegisteredControllerObjects().entrySet();
			
			for(Entry<String, Object> registerdControllerObj : registerdControllerObjs) {
				scriptModel.put(registerdControllerObj.getKey(), registerdControllerObj.getValue());
			}
			
			scriptProcessor.executeScript(new StringScriptLocation(controllerScript), scriptModel);	
			
			response = (String)responseContainer.getParam("response");
		}
		catch(Exception err) {
			System.out.println("exception :" + err);
		}

		return response;
	}
	
	/**
	 * convert the input stream to a string
	 * @param model
	 * @return xml as a string
	 */
	protected String modelToString(InputStream model) {
		
		String xml = "";
	
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(model, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			}
			catch (IOException err) {

				err.printStackTrace();
			}
			finally {
				
				try {
					model.close();
				}
				catch (IOException e) {
				}
			}

			xml = sb.toString();	

		}
		catch(java.io.UnsupportedEncodingException encodingError) {
			encodingError.printStackTrace();	
		}
		
		return xml;
	}	
}
