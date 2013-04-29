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
package org.craftercms.cstudio.alfresco.publishing;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.publishing.PublishingEventHelper;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.publishing.NodeSnapshot;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.alfresco.repo.publishing.PublishingModel.*;
import static org.alfresco.repo.publishing.PublishingModel.PROP_PUBLISHING_EVENT_PAYLOAD;

public class CStudioPublishingEventHelper extends PublishingEventHelper {
    
    protected String _workflowDefinitionName = WORKFLOW_DEFINITION_NAME;
    public String getWorkflowDefinitionName() {
        return _workflowDefinitionName;
    }
    public void setWorkflowDefinitionName(String workflowDefinitionName) {
        this._workflowDefinitionName = workflowDefinitionName;
    }
    
    protected String _workflowEngineId;
    public String getWorkflowEngineId() {
        return _workflowEngineId;
    }
    public void setWorkflowEngineId(String workflowEngineId) {
        this._workflowEngineId = workflowEngineId;
    }

    protected ServicesManager servicesManager;
    public ServicesManager getServicesManager() {
        return servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public String startPublishingWorkflow(NodeRef eventNode, Calendar scheduledTime) {
        //Set parameters
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        Map<QName, Serializable> parameters = new HashMap<QName, Serializable>();
        parameters.put(PROP_WF_PUBLISHING_EVENT, eventNode);
        parameters.put(WorkflowModel.ASSOC_PACKAGE, persistenceManagerService.createPackage(null));
        parameters.put(PROP_WF_SCHEDULED_PUBLISH_DATE, scheduledTime);

        //Start workflow
        WorkflowPath path = persistenceManagerService.startWorkflow(getPublshingWorkflowDefinitionId(), parameters);
        String instanceId = path.getInstance().getId();

        //Set the Workflow Id on the event node.
        persistenceManagerService.setProperty(eventNode, PROP_PUBLISHING_EVENT_WORKFLOW_ID, instanceId);

        //End the start task.
        //TODO Replace with endStartTask() call after merge to HEAD.
        WorkflowTask startTask = persistenceManagerService.getStartTask(instanceId);
        persistenceManagerService.endTask(startTask.getId(), null);
        return instanceId;
    }

    private String getPublshingWorkflowDefinitionId() {
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String definitionName = _workflowEngineId + "$" + _workflowDefinitionName;
        WorkflowDefinition definition = persistenceManagerService.getDefinitionByName(definitionName);
        if (definition == null)
        {
            String msg = "The Web publishing workflow definition does not exist! Definition name: " + definitionName;
            throw new AlfrescoRuntimeException(msg);
        }
        return definition.getId();
    }

}
