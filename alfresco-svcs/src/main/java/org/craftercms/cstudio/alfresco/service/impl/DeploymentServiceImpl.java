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
package org.craftercms.cstudio.alfresco.service.impl;

import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.deployment.DeploymentBatchDAO;
import org.craftercms.cstudio.alfresco.deployment.DeploymentDaoService;
import org.craftercms.cstudio.alfresco.deployment.DeploymentItemDAO;
import org.craftercms.cstudio.alfresco.deployment.DeploymentItemPathDescriptor;
import org.craftercms.cstudio.alfresco.service.AbstractRegistrableService;
import org.craftercms.cstudio.alfresco.service.api.DeploymentService;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;

import java.util.Date;
import java.util.List;

public class DeploymentServiceImpl extends AbstractRegistrableService implements DeploymentService {

    DeploymentDaoService deploymentDaoService;
    public void setDeploymentDaoService(DeploymentDaoService deploymentDaoService) {
        this.deploymentDaoService = deploymentDaoService;
    }

    @Override
    public void register() {
        _servicesManager.registerService(DeploymentService.class, this);
    }

    @Override
    public DeploymentBatchDAO getDeploymentBatch(String batchId) {
        if (StringUtils.isNotEmpty(batchId)) {
            return deploymentDaoService.getDeploymentBatch(batchId);
        } else {
            return null;
        }
    }

    @Override
    public DeploymentBatchDAO startNewDeploymentAttempt(String batchId) {
        if (StringUtils.isNotEmpty(batchId)) {
            return deploymentDaoService.startNewDeploymentAttempt(batchId);
        } else {
            return null;
        }
    }

    @Override
    public List<DeploymentItemDAO> getDeploymentBatchItems(String batchId) {
        if (StringUtils.isNotEmpty(batchId)) {
            return deploymentDaoService.getDeploymentBatchItems(batchId);
        } else {
            return null;
        }
    }

    @Override
    public List<DeploymentItemDAO> getDeploymentBatchDeleteItems(String batchId) {
        if (StringUtils.isNotEmpty(batchId)) {
            return deploymentDaoService.getDeploymentBatchDeleteItems(batchId);
        } else {
            return null;
        }
    }

    @Override
    public String createDeploymentBatch(String site, Date launchDate, String username) {
        return deploymentDaoService.createDeploymentBatch(site, launchDate, username);
    }

    @Override
    public String createDeploymentBatch(String site, Date launchDate, String username, String submissionComment) {
        return deploymentDaoService.createDeploymentBatch(site, launchDate, username, submissionComment);
    }

    @Override
    public void addBatchDeploymentItems(String batchId, String site, String endpoint, String username, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete) {
        deploymentDaoService.addBatchDeploymentItems(batchId, site, endpoint, username, pathsToDeploy, pathsToDelete);
    }

    @Override
    public String createDeploymentBatch(String site, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete, String endpointName, Date launchDate, String username) {
        return deploymentDaoService.createDeploymentBatch(site, pathsToDeploy, pathsToDelete, endpointName, launchDate, username);
    }

    @Override
    public void markDeploymentSuccess(String batchId) {
        deploymentDaoService.markDeploymentSuccess(batchId);
    }

    @Override
    public void markDeploymentFailure(String batchId) {
        deploymentDaoService.markDeploymentFailure(batchId);
    }

    @Override
    public void markBatchReady(String batchId) {
        deploymentDaoService.markBatchReady(batchId);
    }

    @Override
    public List<String> getAffectedPaths(String batchId) {
        return deploymentDaoService.getAffectedPaths(batchId);
    }

    @Override
    public List<String> getDeploymentBatchEndpoints(String batchId) {
        return deploymentDaoService.getDeploymentBatchEndpoints(batchId);
    }

	@Override
	public List<DeploymentItemDAO> getDeploymentHistory(String site,
			Date fromDate, Date toDate,String filter,int limit) {
		return deploymentDaoService.getDeploymentHistory(site,fromDate,toDate,filter,limit);
	}

    @Override
    public void cancelWorkflow(String site, String path) {
        deploymentDaoService.cancelScheduledBatchesForPath(site, path);
        List<DeploymentBatchDAO> cancelBatchList = deploymentDaoService.getCanceledBatchesForPath(site, path);
        ServicesConfig servicesConfig = getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        for (DeploymentBatchDAO batchDAO : cancelBatchList) {
            String repoRootPath = servicesConfig.getRepositoryRootPath(batchDAO.getSite());
            List<String> affectedPaths = deploymentDaoService.getAffectedPaths(batchDAO.getBatchId());
            for (String batchPath : affectedPaths) {
                String fullPath = repoRootPath + batchPath;
                persistenceManagerService.transition(fullPath, ObjectStateService.TransitionEvent.REJECT);
            }
            deploymentDaoService.deleteBatch(batchDAO.getBatchId());
        }
    }

    @Override
    public List<String> getWorkflowAffectedPaths(String site, String path) {
        List<String> toReturn = new FastList<String>();
        List<String> allAffectedPaths = new FastList<String>();
        List<DeploymentBatchDAO> cancelBatchList = deploymentDaoService.getCanceledBatchesForPath(site, path);
        for (DeploymentBatchDAO batchDAO : cancelBatchList) {
            List<String> affectedPaths = deploymentDaoService.getAffectedPaths(batchDAO.getBatchId());
            allAffectedPaths.addAll(affectedPaths);
        }
        for (String affectedPath : allAffectedPaths) {
            if (!toReturn.contains(affectedPath)) {
                toReturn.add(affectedPath);
            }
        }
        return toReturn;
    }
}
