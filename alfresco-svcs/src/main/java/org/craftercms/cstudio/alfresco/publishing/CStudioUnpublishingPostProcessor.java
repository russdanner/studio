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

import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.to.DmPathTO;
import org.craftercms.cstudio.alfresco.preview.PreviewDeployer;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.CStudioNodeService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;

import java.util.Set;

public class CStudioUnpublishingPostProcessor implements CrafterCMSPublishingProcessor {

    protected CrafterCMSRemoteDeploymentChannelType _channelType;
    public CrafterCMSRemoteDeploymentChannelType getChannelType() {
        return _channelType;
    }
    public void setChannelType(CrafterCMSRemoteDeploymentChannelType channelType) {
        this._channelType = channelType;
    }

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    protected PreviewDeployer previewDeployer;
    public PreviewDeployer getPreviewDeployer() {
        return previewDeployer;
    }
    public void setPreviewDeployer(PreviewDeployer previewDeployer) {
        this.previewDeployer = previewDeployer;
    }

    protected BehaviourFilter behaviourFilter;
    public BehaviourFilter getBehaviourFilter() {
        return behaviourFilter;
    }
    public void setBehaviourFilter(BehaviourFilter publishingBehaviourFilter) {
        this.behaviourFilter = publishingBehaviourFilter;
    }

    @Override
    public void doProcess(Set<NodeRef> publishedNodes, boolean publish) {
        if (!publish) {
            behaviourFilter.enableBehaviour();
            PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
            CStudioNodeService cStudioNodeService = getServicesManager().getService(CStudioNodeService.class);
            for (NodeRef unpublishedNode : publishedNodes) {
                String fullPath = persistenceManagerService.getNodePath(unpublishedNode);
                cStudioNodeService.deleteNode(unpublishedNode);
                DmPathTO dmPathTO = new DmPathTO(fullPath);
                dmPathTO.setAreaName(DmConstants.DM_LIVE_REPO_FOLDER);
                NodeRef liveAreaNodeRef = persistenceManagerService.getNodeRef(dmPathTO.toString());
                cStudioNodeService.deleteNode(liveAreaNodeRef);
            }
            behaviourFilter.disableBehaviour();
        }
    }

    @Override
    public ProcessorType getProcessorType() {
        return ProcessorType.POSTPROCESSOR;
    }

    @Override
    public void register() {
        this._channelType.registerPostProcessor(this);
    }
}
