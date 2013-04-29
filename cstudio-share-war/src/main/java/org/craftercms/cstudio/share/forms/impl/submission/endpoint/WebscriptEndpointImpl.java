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
package org.craftercms.cstudio.share.forms.impl.submission.endpoint;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.IdentityType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.forms.impl.orbeon.BufferedResponseWrapper;

public class WebscriptEndpointImpl implements WebScriptEndpoint {

	private static final String PARAM_ALF_TICKET = "alf_ticket";
    private static final String POST_REQUEST_DEFAULT_CONTENT_TYPE = "application/octet-stream";

	private static Log logger = LogFactory.getLog(WebscriptEndpointImpl.class);

	private ConnectorService _connectorService;

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

	/**
	 * get content from url
	 */
	public Response get(String url) {
		return this.get(url, new HashMap<String, Object>());
	}
	
	/**
	 * get content from url
	 */
	public Response get(String url, Map<String, Object> parameters) {
		InputStream is = null;
		
		try {
			is = new java.io.ByteArrayInputStream("".getBytes("UTF-8"));
		}
		catch(Exception encodingErr) {
			/* wont happen */
		}
		
		return this.call("GET", url, is, parameters);
	}

	/**
	 * post content to url
	 */
	public Response post(String url, InputStream model, Map<String, Object> parameters) {
		return this.call("POST", url, model, parameters);
	}
	
	/**
	 * post content to url
	 */
	protected Response call(String op, String url, InputStream model, Map<String, Object> parameters) {

		Response response = null;
		
		try {

			String endpointId = "alfresco";

			SeamlessAppContext appContext = SeamlessAppContext.currentApplicationContext();
			HttpServletRequest req = appContext.getRequest();
			HttpServletResponse res = appContext.getResponse();

			res = new BufferedResponseWrapper(res);

			/* dont need to do this every time, can cache config */
			ApplicationContext alfAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(appContext.getServletContext());
			ConfigService configService = (ConfigService) alfAppContext.getBean("web.config");
			RemoteConfigElement config = (RemoteConfigElement) configService.getConfig("Remote").getConfigElement("remote");

			try {
				// retrieve the endpoint descriptor - do not allow proxy access
				// to
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

				// user id from session NOTE: @see
				// org.alfresco.web.site.UserFactory
				Connector connector = null;

				String userId = (String) req.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);

				if (userId != null) {
					// build an authenticated connector - as we have a userId
					connector = _connectorService.getConnector(endpointId, userId, req.getSession());
				}
				else if (ticket != null || descriptor.getIdentity() == IdentityType.NONE || descriptor.getIdentity() == IdentityType.DECLARED || descriptor.getExternalAuth()) {

					connector = _connectorService.getConnector(endpointId, req.getSession());
				}
				else if (descriptor.getBasicAuth()) {

					String authorization = req.getHeader("Authorization");
					if (authorization == null || authorization.length() == 0) {
						res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "No user id found in session and requested endpoint requires authentication.");
						res.setHeader("WWW-Authenticate", "Basic realm=\"Alfresco\"");
					}
					else {

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
						}
						else {

							throw new AlfrescoRuntimeException("Authorization request did not provide user/pass.");
						}
					}
				}
				else {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, "No user id found in session and requested endpoint requires authentication.");
				}

				ConnectorContext context;

				if (ticket == null) {
					context = new ConnectorContext();
				}
				else {
					// special case for some Flash apps - see above
					Map<String, String> params = new HashMap<String, String>(1, 1.0f);
					params.put(PARAM_ALF_TICKET, ticket);
					context = new ConnectorContext(params, null);
				}
				context.setContentType(req.getContentType());
				
				HttpMethod httpMethod = HttpMethod.valueOf(op);
				context.setMethod(httpMethod);

				if(url.indexOf("?") != -1) {
					url += "&" + PARAM_ALF_TICKET  + "=" + ticket;
				}
				else {
					url += "?" + PARAM_ALF_TICKET  + "=" + ticket;
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("EndPointProxyServlet preparing to proxy:");
					logger.debug(" - endpointId: " + endpointId);
					logger.debug(" - userId: " + userId);
					logger.debug(" - connector: " + connector);
					logger.debug(" - method: " + context.getMethod());
					logger.debug(" - url: " + url);
				}

				if(model != null && "POST".equals(op)) {
					// set default content type for post if not provided
					if (StringUtils.isEmpty(context.getContentType())) {
						context.setContentType(POST_REQUEST_DEFAULT_CONTENT_TYPE);
					}
					response = connector.call(url, context, model);
				}
				else {
					response = connector.call(url, context);
				}
				
				String statusCode = "" + response.getStatus().getCode();
				int statusCodeAsInt = Integer.parseInt(statusCode);

				if (statusCodeAsInt >= 400) {
					System.out.println("Submission handler: End point Return code: '" + statusCode + "'");
					System.out.println(" - message: " + response.getStatus().getMessage());
					System.out.println(" - message: " + response.getStatus().getException());
					System.out.println(((BufferedResponseWrapper) res).getString());
					System.out.println(" - endpointId: " + endpointId);
					System.out.println(" - userId: " + userId);
					System.out.println(" - connector: " + connector);
					System.out.println(" - method: " + context.getMethod());
					System.out.println(" - url: " + url);

				}
			}
			catch (Throwable err) {
				System.out.println("error during proxy:" + err);
				throw new AlfrescoRuntimeException("Error during endpoint proxy processing: " + err.getMessage(), err);
			}
		}
		catch (Throwable err) {
			System.out.println("error during proxy:" + err);
			err.printStackTrace();
			throw new AlfrescoRuntimeException("Error during endpoint proxy processing: " + err.getMessage(), err);
		}

		return response;
	}
	
	/**
	 * post, allow user to pass string based model
	 */
	public Response post(String url, String model, Map<String, Object> parameters) {
		InputStream is = null;
		
		try {
			is = new java.io.ByteArrayInputStream(model.getBytes("UTF-8"));
		}
		catch(Exception encodingErr) {
			/* wont happen */
		}
		
		return this.post(url, is, parameters);
	}
}
