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

import java.io.InputStream;
import java.util.Map;

import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.craftercms.cstudio.share.forms.FormSubmissionProcessor;
import org.craftercms.cstudio.share.forms.impl.submission.endpoint.WebScriptEndpoint;

/**
 * take submission and invoke at a given endpoint
 * 
 * @author Russ Danner
 */
public class EndpointGetSubmissionProcessor implements FormSubmissionProcessor {

	private WebScriptEndpoint _webscriptEndpoint;

	private static Log logger = LogFactory.getLog(EndpointGetSubmissionProcessor.class);

	/**
	 * @return the webscriptEndpoint
	 */
	public WebScriptEndpoint getWebscriptEndpoint() {
		return _webscriptEndpoint;
	}

	/**
	 * @param webscriptEndpoint
	 *            the webscriptEndpoint to set
	 */
	public void setWebscriptEndpoint(WebScriptEndpoint webscriptEndpoint) {
		_webscriptEndpoint = webscriptEndpoint;
	}

	public String processFormSubmission(String formId, InputStream model, String method, String action, Map<String, Object> parameters) {
		String url = (String) parameters.get("url");
		return _webscriptEndpoint.get(url, parameters).getResponse();
	}
}
