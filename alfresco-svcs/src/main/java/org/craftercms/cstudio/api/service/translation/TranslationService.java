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
package org.craftercms.cstudio.api.service.translation;

import java.io.InputStream;
import java.util.Set;
import java.util.List;

/**
 * Provide support for facilitating translations with an external translation provider
 * @author rdanner
 *
 */
public interface TranslationService {

	/**
	 * given a source site, a set of paths and a target site calcualate the files in the srcPaths set that need to be 
	 * translated because they exist in the target site.
     *
	 * If the target site and the source site are the same (perhaps you are managing translations as branches in the same site)
	 * The intersaction is the same.
	 * @param srcSite the id of source site
	 * @param srcPaths the paths of the content updated in the source site
	 * @param targetSite the id of the target site
	 * @return a list of file paths that represent the set
	 */
	List<String> calculateTargetTranslationSet(String srcSite, List<String> srcPaths, String targetSite);

	/**
	 * given a site, a source language, a target language and a path submit the item for translation
	 * @param the site where content is house
	 * @param the source language for the content
	 * @param the target language for the translation
	 * @param the path to the content
	 */
	void translate(String site, String sourceLanguage, String targetLanguage, String path);

	/**
	 * get a percent complete status update on in flight translation
	 * @param path to content 
	 * -- note it's clear this method will require more information
	 */
	int getTranslationStatusForItem(String path);


	/**
	 * return the translated version of the content for a given item
	 * @param path to content 
	 * -- note it's clear this method will require more information
	 */
	InputStream getTranslatedContentForItem(String path);

	/**
	 * update site content with the translated content.  
	 * Service will help make associations to source content?
	 * @param path to content 
	 * -- note it's clear this method will require more information
	 */
	void updateSiteWithTranslatedContent(String site, String path, InputStream content);
}
