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
package org.craftercms.cstudio.impl.service.translation.provider.smartling;

import java.io.InputStream;
import java.util.List;
import org.craftercms.cstudio.api.service.translation.*;
import org.craftercms.cstudio.api.service.workflow.*;
import org.craftercms.cstudio.impl.service.translation.*;

/**
 * Translation provider for Smartling translation API
 * @author rdanner
 */
public class SmartlingTranslationProvider implements TranslationProvider {

	/**
	 * translate or submit content for translation
	 * @param sourceLanguage the ISO code for the source content's language
	 * @param targetLanguage the ISO country code for the target translation's language
	 * @param the file name of the content to translate (path)
	 * @param the raw bits of the content to translate
	 */
	public void translate(String sourceLanguage, String targetLanguage, String filename, InputStream content) {
		System.out.println("submitting job for translation");
	}

	/**
	 * return a percentage complete from the translation provider
	 * @param filename the file to get status on
	 */
	public int getTranslationStatusForItem(String path) {
		return 0;
	}

	/**
	 * retrieve the translated content from the system
	 * @param filename the path of the content (filename)
	 */
	public InputStream getTranslatedContentForItem(String path) {
		return null;
	}
}
