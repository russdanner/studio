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
package org.craftercms.cstudio.impl.service.translation.workflow.handler;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.Node;

import org.craftercms.cstudio.api.log.*;

import org.craftercms.cstudio.api.service.site.*;
import org.craftercms.cstudio.api.service.workflow.*;
import org.craftercms.cstudio.api.service.translation.*;
import org.craftercms.cstudio.impl.service.workflow.*;
import org.craftercms.cstudio.impl.service.translation.workflow.*;

/**
 * given a workflow job, create a new job to submit and monitor the translation
 * @author rdanner
 */
public class KickoffTranslationWorkflowForWaterfallItemsHandler implements JobStateHandler {

	protected static final String MSG_ERROR_CREATE_NEW_TRANSLATE_JOB = "err_create_new_translate_job";
	
	private static final Logger logger = LoggerFactory.getLogger(KickoffTranslationWorkflowForWaterfallItemsHandler.class);
	
	/**
	 * given a job, perform an action and return the next state
	 * @param job the job to operate on
	 * @return the next state
	 */
	public String handleState(WorkflowJob job) {
		// load the configuration for child sites from site config
		String retState = job.getCurrentStatus();
		String site = job.getSite();
		
		try {
			// construct a list of file
			List<String> paths = new ArrayList<String>();
			
			for(WorkflowItem item : job.getItems()) {
				paths.add(item.getPath());
			}

			// load the translation for the site
			Document siteConfigEl = _siteService.getSiteConfiguration(site);
			String sourceLanguage = siteConfigEl.valueOf("/site-config/translation/sourceLanguage");
			
			List<Node> targetEls = siteConfigEl.selectNodes("/site-config/translation/targetSites//targetSite");

			if(paths.size() > 0 && targetEls.size() > 0) {
				// for each configuration
				for(Node targetEl : targetEls) {
					String targetSiteId = targetEl.valueOf("id");
					String basePath = targetEl.valueOf("basePath");
					String targetLanguage = targetEl.valueOf("targetLanguage");
					
					// keep existing properties and add new ones
					Map<String, String> properties = job.getProperties();
					properties.put("sourceSite", site);
					properties.put("sourceLanguage", sourceLanguage);
					properties.put("targetSite", targetSiteId);
					properties.put("basePath", basePath);
					properties.put("targetLanguage", targetLanguage);
					
					// calculate the intersection
					List<String> targetPaths = _translationService.calculateTargetTranslationSet(site, paths, targetSiteId);
					
				    // submit job
					for(String path : targetPaths) {
						List<String> submitAsSingleItemList = new ArrayList<String>();
						submitAsSingleItemList.add(path);
						
						_workflowService.createJob(targetSiteId, submitAsSingleItemList,  "translate", properties);
					}
				}
			}			
			
			retState = WorkflowService.STATE_ENDED;
		}
		catch(Exception err) {
			logger.error(MSG_ERROR_CREATE_NEW_TRANSLATE_JOB, err, job);
		}
		
		return retState;
	}

	/** getter site config property */
	public SiteService getSiteService() { return _siteService; }
	/** setter for site config property */
	public void setSiteService(SiteService service) { _siteService = service; }

	/** getter translation service property */
	public TranslationService getTranslationService() { return _translationService; }
	/** setter for translation service property */
	public void setTranslationService(TranslationService service) { _translationService = service; }

	/** getter workflow service property */
	public WorkflowService getWorkflowService() { return _workflowService; }
	/** setter for workflow service property */
	public void setWorkflowService(WorkflowService service) { _workflowService = service; }
	
	protected SiteService _siteService;
	protected TranslationService _translationService;
	protected WorkflowService _workflowService;
}
