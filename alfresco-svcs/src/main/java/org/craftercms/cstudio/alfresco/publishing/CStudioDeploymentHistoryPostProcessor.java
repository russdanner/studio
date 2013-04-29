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

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Set;

public class CStudioDeploymentHistoryPostProcessor implements CrafterCMSPublishingProcessor {

    protected ProcessorType _type = ProcessorType.POSTPROCESSOR;

    protected CrafterCMSRemoteDeploymentChannelType _channelType;
    public CrafterCMSRemoteDeploymentChannelType getChannelType() {
        return _channelType;
    }
    public void setChannelType(CrafterCMSRemoteDeploymentChannelType channelType) {
        this._channelType = channelType;
    }

    @Override
    public void doProcess(Set<NodeRef> publishedNodes, boolean publish) {
        if (publish) {

        }
    }

    @Override
    public ProcessorType getProcessorType() {
        return this._type;
    }

    @Override
    public void register() {
        this._channelType.registerPostProcessor(this);
    }
}
