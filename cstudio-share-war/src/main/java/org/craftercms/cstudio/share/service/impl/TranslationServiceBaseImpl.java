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
package org.craftercms.cstudio.share.service.impl;

import org.craftercms.cstudio.share.service.api.TranslationException;

/**
 * Provide a connector to a translation service
 * @author russdanner
 */
public class TranslationServiceBaseImpl {
	
	/**
	 * synchronous translation of a string
	 * ? should we send content in and out as readers?
	 */
	public String translate(String content, String fromLang, String toLang, String format) 
	throws TranslationException {
		throw new TranslationException("operation not supported");
	}
	
	// submit an item for translation

	// get status of an in-flight translation	
	
	// cancel an inflight translation
}