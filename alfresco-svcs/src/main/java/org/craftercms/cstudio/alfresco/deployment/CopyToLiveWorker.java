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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CopyToLiveWorker extends DeploymentWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyToLiveWorker.class);

    protected static final ReentrantLock singleWorkerLock = new ReentrantLock();

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting *Copy To Live Repo* Worker thread");
        }
        if (singleWorkerLock.tryLock()) {
            try {
                List<CopyLiveLogItem> items = getItems();
                if (items != null && items.size() > 0) {
                    lockItems(items);
                    processItems(items);
                    unlockItems(items);
                }
            } finally {
                singleWorkerLock.unlock();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Stopping *Copy To Live Repo* Worker thread");
        }
    }

    protected void addItemsToDeploymentState(List<CopyLiveLogItem> items) {
        // TODO: add items to "DEPLOYMENT STATE" table
    }

    protected void processItems(List<CopyLiveLogItem> items) {
        for (CopyLiveLogItem item : items) {
            processItem(item);
        }
        addItemsToDeploymentState(items);
    }

    protected List<CopyLiveLogItem> getItems() {
        // TODO: Query "COPY LIVE LOG" table for items
        return null;
    }

    protected void processItem(CopyLiveLogItem item) {
        createNewVersion(item);
        copyToLiveRepo(item);
    }

    protected void createNewVersion(CopyLiveLogItem item) {
        // TODO: create new version
    }

    protected void copyToLiveRepo(CopyLiveLogItem item) {
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        String liveRepoPath = servicesConfig.getLiveRepositoryPath(item.getSite());
        if (!persistenceManagerService.exists(liveRepoPath)) {
            persistenceManagerService.createLiveRepository(item.getSite());
        }
        /*Matcher m = DmConstants.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
        if (m.matches()) {
            String relativePath = m.group(4);
            NodeRef liveNode = persistenceManagerService.getNodeRef(liveRepoRoot, relativePath);
            if (liveNode == null) {
                liveNode = createLiveRepositoryCopy(liveRepoRoot, relativePath, nodeRef);
            } else {
                persistenceManagerService.copy(nodeRef, liveNode);
            }
            Map<QName, Serializable> nodeProps = persistenceManagerService.getProperties(liveNode);
            for (QName propName : DmConstants.SUBMITTED_PROPERTIES) {
                nodeProps.remove(propName);
            }
            persistenceManagerService.setProperties(liveNode, nodeProps);
        }*/
    }

    protected void lockItems(List<CopyLiveLogItem> items) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Locking copy to live items");
        }
        List<String> objectIds = new FastList<String>();
        for (CopyLiveLogItem item : items) {
            objectIds.add(item.getObjectId());
        }
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        persistenceManagerService.setSystemProcessingBulk(objectIds, true);
    }

    protected void unlockItems(List<CopyLiveLogItem> items) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unlocking copy to live items");
        }
        List<String> objectIds = new FastList<String>();
        for (CopyLiveLogItem item : items) {
            objectIds.add(item.getObjectId());
        }
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        persistenceManagerService.setSystemProcessingBulk(objectIds, false);
    }
}
