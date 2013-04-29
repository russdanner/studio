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
package org.craftercms.cstudio.alfresco.deployment;

import javolution.util.FastList;
import org.alfresco.service.cmr.publishing.PublishingDetails;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.craftercms.cstudio.alfresco.service.api.DeploymentService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AlfrescoChannelDeploymentCommand extends AbstractDeploymentCommand {

    private final static Logger LOGGER = LoggerFactory.getLogger(AlfrescoChannelDeploymentCommand.class);

    @Override
    public void deploy() {
        LOGGER.info("Executing deployment");
        //GeneralLockService generalLockService = servicesManager.getService(GeneralLockService.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        DeploymentBatchDAO batch = deploymentService.getDeploymentBatch(batchId);
        //batch = deploymentService.startNewDeploymentAttempt(batchId);
        List<DeploymentItemDAO> batchItems = deploymentService.getDeploymentBatchItems(batchId);
        String rootPath = servicesConfig.getRepositoryRootPath(batch.getSite());
        List<NodeRef> nodesToPublish = new FastList<NodeRef>();
        for (DeploymentItemDAO batchItem : batchItems) {
            String fullPath = rootPath + batchItem.getPath();
            NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
            nodesToPublish.add(nodeRef);
        }
        Channel channel = persistenceManagerService.getChannelByName(endpoint.getName());
        PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
        publishingDetails.setSchedule(null);
        publishingDetails.addNodesToPublish(nodesToPublish);
        publishingDetails.setPublishChannelId(channel.getId());
        String eventId = persistenceManagerService.scheduleNewEvent(publishingDetails);
    }
}
