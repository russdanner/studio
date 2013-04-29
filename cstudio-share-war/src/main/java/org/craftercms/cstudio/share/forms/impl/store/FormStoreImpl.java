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

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.IdentityType;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.forms.impl.orbeon.BufferedResponseWrapper;

import org.craftercms.cstudio.share.forms.Form;
import org.craftercms.cstudio.share.forms.FormException;
import org.craftercms.cstudio.share.forms.FormStore;
import org.craftercms.cstudio.share.forms.FormUnavailableException;
import org.craftercms.cstudio.share.forms.impl.orbeon.FormImpl;

/**
 * repository based form store
 */
public class FormStoreImpl implements FormStore {

	private static final Logger logger = LoggerFactory.getLogger(FormStoreImpl.class);

	private ConnectorService _connectorService;
	private static final String PARAM_ALF_TICKET = "alf_ticket";
	private String formServiceUri;
	private String _formComponentServiceUri;

	/**
	 * @return the connectorService
	 */
	public ConnectorService getConnectorService() {

		return _connectorService;
	}

	/**
	 * @param connectorService
	 *            the connectorService to set
	 */
	public void setConnectorService(ConnectorService connectorService) {

		_connectorService = connectorService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.craftercms.cstudio.share.forms.FormStore#loadForm(java.lang.String)
	 */
	public Form loadForm(String formId) throws FormException, FormUnavailableException {

		FormImpl retForm = null;
		String formString = "";
		String controllerString = "";

		// Convert xml string to w3c document
		try {
			// Get the form definition as a string from Alfresco DM Form Service
			formString = invokeLoadForm(formId);

			if (formString == null || formString.equals("")) {
				throw new FormUnavailableException("No form definition specified for the given form id: " + formId);
			}

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document xformDefinitionDoc = docBuilder.parse(new InputSource(new StringReader(formString)));

			controllerString = invokeLoadControllerScript(formId);

			// Create the FormImpl instance
			retForm = new FormImpl();
			retForm.setFormId(formId);
			retForm.setFormDefinition(xformDefinitionDoc);
			retForm.setFormController(controllerString);
			
		} catch (Exception err) {
			if (logger.isErrorEnabled()) {
				logger.error("Loading form '"+formId+"'definition failed: " + err.getMessage(), err);
			}
			throw new FormException("Error creating Form "+formId+" object from the form string: " + err.getMessage() + "\nForm:\n" + formString);
		}

		return retForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.craftercms.cstudio.share.forms.FormStore#invokeLoadForm(java.lang.String)
	 */
	public String invokeLoadForm(String formId) throws Exception {
		String formString = null;

		formString = callRESTService(formServiceUri, formId, "xform.xml");

		return formString;
	}

	/**
	 * load the controller script from the repository
	 * 
	 * @param formId
	 * @return
	 * @throws Exception
	 */
	public String invokeLoadControllerScript(String formId) throws Exception {

		String controllerString = null;

		controllerString = callRESTService(_formComponentServiceUri, formId, "controller.js");
		
		if("".equals(controllerString.trim())) {
			controllerString = null;
		}

		return controllerString;
	}

	/*
	 * This will invoke the REST api at the alfresco repository
	 */
	private String callRESTService(final String targetURI, final String formId, String component) throws Exception {

		String retXmlString = "";
		int result = -1;

		String endpointId = "alfresco";

		SeamlessAppContext appContext = SeamlessAppContext.currentApplicationContext();
		HttpServletRequest req = appContext.getRequest();
		HttpServletResponse res = appContext.getResponse();

		res = new BufferedResponseWrapper(res);

		/* dont need to do this every time, can cache config */
		ApplicationContext alfAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(appContext
				.getServletContext());
		ConfigService configService = (ConfigService) alfAppContext.getBean("web.config");
		RemoteConfigElement config = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");

		try {
			// retrieve the endpoint descriptor - do not allow proxy access to
			// unsecure endpoints
			EndpointDescriptor descriptor = config.getEndpointDescriptor(endpointId);

			if (descriptor == null || descriptor.getUnsecure()) {
				// throw an exception if endpoint ID is does not exist or
				// invalid
				throw new AlfrescoRuntimeException("Invalid EndPoint Id: " + endpointId);
			}

			String ticket = req.getParameter(PARAM_ALF_TICKET);

			if (ticket == null) {
				ticket = appContext.getTicket();
			}

			// user id from session NOTE: @see org.alfresco.web.site.UserFactory
			Connector connector = null;

			String userId = (String) req.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);

			if (userId != null) {
				// build an authenticated connector - as we have a userId
				connector = _connectorService.getConnector(endpointId, userId, req.getSession());
			} else if (ticket != null || descriptor.getIdentity() == IdentityType.NONE
					|| descriptor.getIdentity() == IdentityType.DECLARED || descriptor.getExternalAuth()) {

				connector = _connectorService.getConnector(endpointId, req.getSession());
			} else if (descriptor.getBasicAuth()) {

				String authorization = req.getHeader("Authorization");
				if (authorization == null || authorization.length() == 0) {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
							"No user id found in session and requested endpoint requires authentication.");
					res.setHeader("WWW-Authenticate", "Basic realm=\"Alfresco\"");
				} else {

					String[] authParts = authorization.split(" ");
					if (!authParts[0].equalsIgnoreCase("basic")) {
						throw new AlfrescoRuntimeException("Authorization '" + authParts[0] + "' not supported.");
					}

					String[] values = new String(Base64.decode(authParts[1])).split(":");
					if (values.length == 2) {
						if (logger.isDebugEnabled())
							logger.debug("Authenticating (BASIC HTTP) user " + values[0]);

						connector = _connectorService.getConnector(endpointId, values[0], req.getSession());
						Credentials credentials = new CredentialsImpl(endpointId);
						credentials.setProperty(Credentials.CREDENTIAL_USERNAME, values[0]);
						credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, values[1]);
						connector.setCredentials(credentials);
					} else {

						throw new AlfrescoRuntimeException("Authorization request did not provide user/pass.");
					}
				}
			} else {
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
						"No user id found in session and requested endpoint requires authentication.");
			}

			ConnectorContext context;

			if (ticket == null) {
				context = new ConnectorContext();
			} else {
				// special case for some Flash apps - see above
				Map<String, String> params = new HashMap<String, String>(1, 1.0f);
				params.put("formId", formId);
				params.put(PARAM_ALF_TICKET, ticket);

				context = new ConnectorContext(params, null);
			}

			// context.setContentType("text/xml");
			context.setMethod(HttpMethod.valueOf("GET"));

			String url = targetURI + "?formId=" + formId + "&componentName=" + component + "&" + PARAM_ALF_TICKET +"="+ticket;

			if (logger.isDebugEnabled()) {
				System.out.println("EndPointProxyServlet preparing to proxy:");
				System.out.println(" - endpointId: " + endpointId);
				System.out.println(" - userId: " + userId);
				System.out.println(" - connector: " + connector);
				System.out.println(" - method: " + context.getMethod());
				System.out.println(" - url: " + url);
				System.out.println(" - params: " + context);
			}

			Response response = connector.call(url);// , context, req, res);

			retXmlString = response.getResponse();
			String statusVal = "" + response.getStatus().getCode();

			
			if("400".equals(statusVal) || "500".equals(statusVal) || "503".equals(statusVal)) {
				throw new Exception(response.getStatus().getMessage() + "\n"
						+ response.getStatus().getException().toString());
			}
			else if("401".equals(statusVal)) {
				throw new Exception("unable to authenticate at remote endpoint");
			}
			else if("404".equals(statusVal)) {
				throw new Exception("unable to reach remote endpoint (404)");
			}
		} catch (Throwable err) {

			throw new AlfrescoRuntimeException("Error while aquiring form asset '"+component+"' for ID '"+formId+"': " + err.getMessage(), err);

		}

		return retXmlString;
	}

	/**
	 * @return true if form can be found in store
	 */
	public boolean isFormInStore(String formId) {

		// lie
		return true;
	}

	public String getFormServiceUri() {
		return formServiceUri;
	}

	public void setFormServiceUri(String formServiceUri) {
		this.formServiceUri = formServiceUri;
	}

	public String getFormComponentServiceUri() {
		return _formComponentServiceUri;
	}

	public void setFormComponentServiceUri(String formComponentServiceUri) {
		_formComponentServiceUri = formComponentServiceUri;
	}

}
