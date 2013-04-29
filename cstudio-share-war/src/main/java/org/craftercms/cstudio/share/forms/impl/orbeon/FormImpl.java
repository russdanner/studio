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

import org.craftercms.cstudio.share.forms.impl.FormBaseImpl;

/**
 * Form encapsulates the details of the form implementation. Base implementation
 * <p>
 * Chiba Implementation
 * 
 * @author Russ Danner
 */
public class FormImpl extends FormBaseImpl {
	
	private String _formUri;
	
	/**
	 * @return uri for form
	 */
	public String getFormUri() {

		return _formUri;
	}
	
	/**
	 * set form uri
	 * 
	 * @param formUri
	 *            uri of the form
	 */
	public void setFormUri(String formUri) {

		_formUri = formUri;
	}
}
