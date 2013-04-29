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

import org.alfresco.repo.publishing.PublishingEventHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmStateManager;
import org.craftercms.cstudio.alfresco.dm.to.DmContentItemTO;
import org.craftercms.cstudio.alfresco.dm.to.DmPathTO;
import org.craftercms.cstudio.alfresco.dm.util.DmUtils;
import org.craftercms.cstudio.alfresco.dm.workflow.WorkflowProcessor;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.*;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class CStudioStatusUpdatePublishingPostProcessor implements CrafterCMSPublishingProcessor {

    protected ProcessorType _type = ProcessorType.POSTPROCESSOR;

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    protected CrafterCMSRemoteDeploymentChannelType _channelType;
    public CrafterCMSRemoteDeploymentChannelType getChannelType() {
        return _channelType;
    }
    public void setChannelType(CrafterCMSRemoteDeploymentChannelType channelType) {
        this._channelType = channelType;
    }

    protected WorkflowProcessor _workflowProcessor;
    public WorkflowProcessor getWorkflowProcessor() {
        return _workflowProcessor;
    }
    public void setWorkflowProcessor(WorkflowProcessor workflowProcessor) {
        this._workflowProcessor = workflowProcessor;
    }

    @Override
    public void register() {
        this._channelType.registerPostProcessor(this);
    }

    @Override
    public void doProcess(Set<NodeRef> publishedNodes, boolean publish) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        DmVersionService dmVersionService = getServicesManager().getService(DmVersionService.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String fullPath;
        String relativePath;
        String site;
        if (publish) {
            for (NodeRef nodeRef : publishedNodes) {
                fullPath = persistenceManagerService.getNodePath(nodeRef);
                Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
                if (m.matches()) {
                    site = m.group(2).length() != 0 ? m.group(2) : "";
                    relativePath = m.group(4).length() != 0 ? m.group(4) : "/";
                } else {
                    site = "";
                    relativePath = "/";
                }
                if (persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED)) {
                    String oldPath = DefaultTypeConverter.INSTANCE.convert(String.class, persistenceManagerService.getProperty(nodeRef, CStudioContentModel.PROP_RENAMED_OLD_URL));
                    oldPath = oldPath.replace("/" + DmConstants.INDEX_FILE, "");
                    String renamedPath = servicesConfig.getRepositoryRootPath(site) + oldPath;
                    DmPathTO dmPathTO = new DmPathTO(renamedPath);
                    dmPathTO.setAreaName(DmConstants.DM_LIVE_REPO_FOLDER);
                    NodeRef nodeToDelete = persistenceManagerService.getNodeRef(dmPathTO.toString());
                    if (nodeToDelete != null) {
                        persistenceManagerService.deleteNode(dmPathTO.toString());
                    }
                    persistenceManagerService.removeAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED);
                }
                persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.DEPLOYMENT);
                //dmVersionService.createNextMajorVersion(site, relativePath);
                this._workflowProcessor.removeInFilghtItem(fullPath);
            }
        }
    }

    @Override
    public ProcessorType getProcessorType() {
        return this._type;
    }
}
