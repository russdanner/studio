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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizeToTargetWorker extends DeploymentWorker {

    protected static final ReentrantLock singleWorkerLock = new ReentrantLock();

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (singleWorkerLock.tryLock()) {
            try {
                List<Object> agents = getAgents();
                if (agents != null && agents.size() > 0) {
                    Object newTimestamp = getCurrentTimestamp();
                    for (Object agent : agents) {
                        checkAgentStatus();
                        Object timestamp = getAgentTimestamp(agent);
                        List<Object> items = getItems(timestamp);
                        processItems(agent, items);
                        setAgentTimestamp(agent, newTimestamp);
                    }
                }
            } finally {
                singleWorkerLock.unlock();
            }
        }
    }

    protected void setAgentTimestamp(Object agent, Object newTimestamp) {
        // TODO: implement set new timestamp for agent
    }

    protected Object getCurrentTimestamp() {
        // TODO: implement get current timestamp
        return null;
    }

    protected List<Object> getItems(Object timestamp) {
        // TODO: implement get items from deployment state table for timestamp
        return null;
    }

    protected Object getAgentTimestamp(Object agent) {
        // TODO: implement get agent timestamp
        return null;
    }

    protected void checkAgentStatus() {
        // TODO: implement check agent status
    }

    protected void processItems(Object agent, List<Object> items) {
        List<Object> filteredItems = filterItems(agent);
        for (Object item : filteredItems) {
            deployItem(item, agent);
            addDeploymentHistoryEntry(item , agent);
        }
    }

    protected void addDeploymentHistoryEntry(Object item, Object agent) {
        // TODO: implement adding deployment history entry
    }

    protected void deployItem(Object item, Object agent) {
        // TODO: implement item deployment on agent
    }

    protected List<Object> filterItems(Object agent) {
        // TODO: implement items filtering for agent
        return null;
    }

    protected List<Object> getAgents() {
        // TODO: implement get agents method
        return null;
    }
}
