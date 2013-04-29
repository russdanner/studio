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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.craftercms.cstudio.share.forms.FormSubmissionProcessor;

/**
 * respond with the xml result
 *  
 * @author Russ Danner
 */
public class EchoSubmissionProcessor
implements FormSubmissionProcessor
{
	
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
	public String processFormSubmission(String formId, InputStream model, String method, String action, Map<String,Object> parameters) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(model));
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n\r");
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
		
		String xml = sb.toString();
		
		System.out.println(xml);
		
		return null;
	}	
}
