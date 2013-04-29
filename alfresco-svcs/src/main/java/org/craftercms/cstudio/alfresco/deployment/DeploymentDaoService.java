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

import java.util.Date;
import java.util.List;

public interface DeploymentDaoService {

    public void initIndexes();

    public DeploymentBatchDAO getDeploymentBatch(String batchId);

    public DeploymentBatchDAO startNewDeploymentAttempt(String batchId);

    public List<DeploymentItemDAO> getDeploymentBatchItems(String batchId);

    public String createDeploymentBatch(String site, Date launchDate, String username);

    public String createDeploymentBatch(String site, Date launchDate, String username, String submissionComment);

    public void addBatchDeploymentItems(String batchId, String site, String endpoint, String username, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete);

    public String createDeploymentBatch(String site, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete, String endpoint, Date launchDate, String username);

    public List<DeploymentBatchDAO> getWorkQueue();

    public void markDeploymentSuccess(String batchId);

    public void markDeploymentFailure(String batchId);

    public void markBatchQueued(String batchId, String clusterNodeId);

    public void rollbackTransientQueueBatchStates(String clusterNodeId);

    public void markBatchReady(String batchId);

    public List<String> getAffectedPaths(String batchId);

    public List<String> getDeploymentBatchEndpoints(String batchId);
    
    public List<DeploymentItemDAO> getScheduledItems(String siteId);

    public List<DeploymentItemDAO> getDeploymentHistory(String site, Date fromDate, Date toDate,String filter,int limit);

    public List<DeploymentItemDAO> getDeploymentBatchDeleteItems(String batchId);

    public void cancelScheduledBatchesForPath(String site, String path);

    public List<DeploymentBatchDAO> getCanceledBatchesForPath(String site, String path);

    public void deleteBatch(String batchId);
}
