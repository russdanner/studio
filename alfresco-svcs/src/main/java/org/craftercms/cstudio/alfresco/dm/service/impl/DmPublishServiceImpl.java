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
package org.craftercms.cstudio.alfresco.dm.service.impl;

import javolution.util.FastList;
import org.alfresco.model.WCMWorkflowModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.craftercms.cstudio.alfresco.dm.filter.DmFilterWrapper;
import org.craftercms.cstudio.alfresco.dm.service.api.DmPublishService;
import org.craftercms.cstudio.alfresco.dm.to.DmPathTO;
import org.craftercms.cstudio.alfresco.dm.workflow.MultiChannelPublishingContext;
import org.craftercms.cstudio.alfresco.service.AbstractRegistrableService;
import org.craftercms.cstudio.alfresco.service.api.*;
import org.craftercms.cstudio.alfresco.to.PublishingChannelConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelGroupConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelTO;
import org.craftercms.cstudio.api.service.deployment.DeploymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DmPublishServiceImpl extends AbstractRegistrableService implements DmPublishService {

    private static final Logger logger = LoggerFactory.getLogger(DmPublishServiceImpl.class);

    protected DmFilterWrapper dmFilterWrapper;
    protected org.craftercms.cstudio.api.service.deployment.DeploymentService deploymentService;
    
    public void setDmFilterWrapper(DmFilterWrapper dmFilterWrapper) {
		this.dmFilterWrapper = dmFilterWrapper;
	}

    public void setDeploymentService(org.craftercms.cstudio.api.service.deployment.DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Override
    public void register() {
        this._servicesManager.registerService(DmPublishService.class, this);
    }

    @Override
    public void publish(String site, List<String> paths, Date launchDate,
                        MultiChannelPublishingContext mcpContext) {
        List<String> pathsToPublish = new FastList<String>();
        for (String p : paths) {
            DmPathTO dmPathTO = new DmPathTO(p);
            pathsToPublish.add(dmPathTO.getRelativePath());
        }
        if (launchDate == null) {
            launchDate = new Date();
        }
        String approver = AuthenticationUtil.getFullyAuthenticatedUser();

        try {
            deploymentService.deploy(site, "live", pathsToPublish, launchDate, approver, mcpContext.getSubmissionComment());
        } catch (DeploymentException ex) {
            logger.error("Unable to publish files due a error ", ex);
        }
    }

    @Override
    public void unpublish(String site, List<String> paths, String approver) {
        unpublish(site, paths, approver, null);
    }

    @Override
    public void unpublish(String site, List<String> paths,  String approver, Date scheduleDate) {
        if (scheduleDate == null) {
            scheduleDate = new Date();
        }
        try {
            deploymentService.delete(site, "live", paths, approver, scheduleDate);
        } catch (DeploymentException ex) {
            logger.error("Unable to delete files due a error ",ex);
        }
    }

    @Override
    public void cancelScheduledItem(String site, String path) {
        try {
            deploymentService.cancelWorkflow(site, path);
            ServicesConfig servicesConfig = getService(ServicesConfig.class);
            PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
            String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
            NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
            persistenceManagerService.removeProperty(nodeRef, WCMWorkflowModel.PROP_LAUNCH_DATE);
        } catch (DeploymentException e) {
            logger.error(String.format("Error while canceling workflow for content at %s, site %s", path, site), e);
        }
    }

    @Override
    public List<PublishingChannelTO> getAvailablePublishingChannelGroups(String site, String path) {
        List<PublishingChannelTO> channelTOs = new FastList<PublishingChannelTO>();
        List<String> channels = getPublishingChannels(site);
        for (String ch : channels) {
            PublishingChannelTO chTO = new PublishingChannelTO();
            chTO.setName(ch);
            chTO.setPublish(true);
            chTO.setUpdateStatus(false);
            channelTOs.add(chTO);
        }
        return channelTOs;
    }

    protected List<String> getPublishingChannels(String site) {
        SiteService siteService = getService(SiteService.class);
        List<String> channels = new FastList<String>();
        Map<String, PublishingChannelGroupConfigTO> channelGroupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        for (PublishingChannelGroupConfigTO configTO : channelGroupConfigTOs.values()) {
            channels.add(configTO.getName());
        }
        return channels;
    }
    
    /**
     * Checks if there are any publishing channels configure
     * @return true if there is at least one publishing channel config
     */
    @Override
	public boolean hasChannelsConfigure(String site, MultiChannelPublishingContext mcpContext){
    	SiteService siteService = getService(SiteService.class);
    	boolean toReturn = false;
        if (mcpContext != null) {
            Map<String, PublishingChannelGroupConfigTO> publishingChannelGroupConfigs = siteService.getPublishingChannelGroupConfigs(site);
            PublishingChannelGroupConfigTO configTO = publishingChannelGroupConfigs.get(mcpContext.getPublishingChannelGroup());
            if (configTO != null) {
                for (PublishingChannelConfigTO channelConfigTO : configTO.getChannels()) {
                    if (channelConfigTO != null) {
                        if (siteService.getDeploymentEndpoint(site, channelConfigTO.getName()) != null) {
                            toReturn = true;
                        }
                    }
                }
            }
        }
    	return toReturn;
    }
}
