/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.cstudio.alfresco.dm.script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.craftercms.cstudio.alfresco.action.DmImportActionExecutor;
import org.craftercms.cstudio.alfresco.action.DmPublishActionExecutor;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.util.ContentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmImportScript extends BaseProcessorExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(DmImportScript.class);

    /**
     * Alfresco ActionService
     */
    protected ActionService _actionService;
    
	/**
	 * ServicesConfig
	 */
	protected ServicesConfig _servicesConfig = null;

	/** service manager **/
    protected ServicesManager _servicesManager;
    
    /** PersistenceManager **/
	protected PersistenceManagerService _persistenceManagerService = null;
	
    
    /**
     * import site contents
     * 
     * @param configLocation
     */
	@SuppressWarnings("unchecked")
	public void importSite(String configLocation) {
		Document document = loadConfiguration(configLocation);
		if (document != null) {
			Element root = document.getRootElement();
			List<Node> siteNodes = root.selectNodes("site");
			if (siteNodes != null) {
				for (Node siteNode : siteNodes) {
					String name = siteNode.valueOf("name");
					String buildDataLocation = siteNode.valueOf("build-data-location");
					String publishingChannelGroup = siteNode.valueOf("publish-channel-group");
					String publish = siteNode.valueOf("publish");
					String publishSize = siteNode.valueOf("publish-chunk-size");
					int chunkSize = (!StringUtils.isEmpty(publishSize) && StringUtils.isNumeric(publishSize)) 
										? Integer.valueOf(publishSize) : -1;
					Node foldersNode = siteNode.selectSingleNode("folders");
					String targetRoot = this.getServicesConfig().getRepositoryRootPath(name);
					NodeRef targetRef = this.findContent(targetRoot);
					String sourceLocation = buildDataLocation + "/" + name;
					String delayIntervalStr = siteNode.valueOf("delay-interval");
					int delayInterval = (!StringUtils.isEmpty(delayIntervalStr) && StringUtils.isNumeric(delayIntervalStr)) 
											? Integer.valueOf(delayIntervalStr) : -1;
					String delayLengthStr = siteNode.valueOf("delay-length");
					int delayLength = (!StringUtils.isEmpty(delayLengthStr) && StringUtils.isNumeric(delayLengthStr)) 
											? Integer.valueOf(delayLengthStr) : -1;
					
					// trigger an action
			        Map<String, Serializable> args = new FastMap<String, Serializable>();
			        args.put(DmImportActionExecutor.PARAM_SITE, name);
			        args.put(DmImportActionExecutor.PARAM_SOURCE_LOCATION, sourceLocation);
			        args.put(DmImportActionExecutor.PARAM_TARGET_LOCATION, targetRoot);
			        args.put(DmImportActionExecutor.PARAM_PUBLISH, publish);
			        args.put(DmImportActionExecutor.PARAM_CHUNK_SIZE, chunkSize);
			        args.put(DmImportActionExecutor.PARAM_DELAY_INTERVAL, delayInterval);
			        args.put(DmImportActionExecutor.PARAM_DELAY_LENGTH, delayLength);
			        args.put(DmImportActionExecutor.PARAM_PUBLISH_CHANNEL_GROUP, publishingChannelGroup);
			        args.put(DmImportActionExecutor.PARAM_CONFIG_NODE, (Serializable) foldersNode);
			        Action action = _actionService.createAction(DmImportActionExecutor.NAME, args);
			        _actionService.executeAction(action, targetRef, false, true);
				}
			}
		}
	}
	
	/**
     * publish site contents imported
	 * 
	 * @param site
	 * @param publishingChannelGroup
	 * @param path
	 * 			path to start publishing from
	 * @param publishSize
	 */
	public void publishSite(String site, String publishingChannelGroup, String path, String publishSize) {
		String targetRoot = this.getServicesConfig().getRepositoryRootPath(site);
		NodeRef targetRef = this.findContent(targetRoot + path);
		int chunkSize = (!StringUtils.isEmpty(publishSize) && StringUtils.isNumeric(publishSize)) 
							? Integer.valueOf(publishSize) : -1;
					
		// trigger an action
        Map<String, Serializable> args = new FastMap<String, Serializable>();
        args.put(DmPublishActionExecutor.PARAM_SITE, site);
        args.put(DmPublishActionExecutor.PARAM_TARGET_LOCATION, targetRoot);
        args.put(DmPublishActionExecutor.PARAM_START_PATH, path);
        args.put(DmPublishActionExecutor.PARAM_CHUNK_SIZE, chunkSize);
        args.put(DmPublishActionExecutor.PARAM_PUBLISH_CHANNEL_GROUP, publishingChannelGroup);
        Action action = _actionService.createAction(DmPublishActionExecutor.NAME, args);
        _actionService.executeAction(action, targetRef, false, true);
	}
	
	/**
	 * load import configuration e.g.
		<import-config>
			<site>
				<name>acme</name>
				<build-data-location>/sites/data/builddata</build-data-location>
				<folders>
					<folder name="site" import-all="true" over-write="true"/>
					<folder name="static-assets" import-all="true" over-write="true"/>
					<folder name="templates" import-all="true" over-write="true" />
				</folders>
				<publish>true</publish>
			</site>
		</import-config>
	 * 
	 * 
	 * @param configLocation
	 *            configuration file location in class path
	 */
	protected Document loadConfiguration(String configLocation) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[IMPORT] loading " + configLocation);
		}
		InputStream in = null;
		try {
			in = new FileInputStream(configLocation);
			if (in != null) {
				return ContentUtils.convertStreamToXml(in);
			}
		} catch (FileNotFoundException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("[IMPORT] failed to load configuration.", e);
			}
		} catch (DocumentException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("[IMPORT] failed to load configuration.", e);
			}
		} finally {
			ContentUtils.release(in);
		}
		return null;
	}
	
	/**
	 * find a content noderef at the given path
	 * 
	 * @param fullPath
	 * @return
	 */
	private NodeRef findContent(String fullPath) {
        return getPersistenceManager().getNodeRef(fullPath);
	}

	/**
	 * get PersistenceManagerService
	 * @return
	 */
    private PersistenceManagerService getPersistenceManager() {
    	if (this._persistenceManagerService == null) {
    		this._persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
    	}
    	return this._persistenceManagerService;
	}

    /**
     * get ServicesManager
     * @return
     */
	public ServicesManager getServicesManager() {
		return this._servicesManager;
	}

	/**
	 * 
	 * @param serviceManager
	 */
	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

	/**
	 * @return the actionService
	 */
	public ActionService getActionService() {
		return _actionService;
	}

	/**
	 * @param actionService the actionService to set
	 */
	public void setActionService(ActionService actionService) {
		this._actionService = actionService;
	}

	/**
	 * @return the servicesConfig
	 */
	public ServicesConfig getServicesConfig() {
		if (this._servicesConfig == null) {
			this._servicesConfig = getServicesManager().getService(ServicesConfig.class);
		}
		return this._servicesConfig;
	}

	/**
	 * @param servicesConfig
	 *            the servicesConfig to set
	 */
	public void setServicesConfig(ServicesConfig servicesConfig) {
		this._servicesConfig = servicesConfig;
	}
}
