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

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DeploymentDispatcherService extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentDispatcherService.class);

    protected String clusterNodeId;
    public void setClusterNodeId(String clusterId) {
        this.clusterNodeId = clusterId;
    }

    protected ServicesManager servicesManager;
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    protected DeploymentDaoService deploymentDaoService;
    public void setDeploymentDaoService(DeploymentDaoService deploymentDaoService) {
        this.deploymentDaoService = deploymentDaoService;
    }

    public void initDispatcher() {
        rollbackTransientQueueBatchStates();
    }

    protected void rollbackTransientQueueBatchStates() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Starting recovery process on node [%s] - rolling back all batches in transient states (queued, deploying)", clusterNodeId));
        }
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        deploymentDaoService.rollbackTransientQueueBatchStates(clusterNodeId);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Finished recovery process on node [%s]", clusterNodeId));
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
