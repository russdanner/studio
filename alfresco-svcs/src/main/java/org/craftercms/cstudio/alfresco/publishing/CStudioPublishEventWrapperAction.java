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

import javolution.util.FastList;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.publishing.PublishingEventProcessor;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmWorkflowService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class CStudioPublishEventWrapperAction extends ActionExecuterAbstractBase {

    protected static final Logger logger = LoggerFactory.getLogger(CStudioPublishEventWrapperAction.class);

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    protected PublishingEventProcessor _publishingEventProcessor;
    public PublishingEventProcessor getPublishingEventProcessor() {
        return _publishingEventProcessor;
    }
    public void setPublishingEventProcessor(PublishingEventProcessor publishingEventProcessor) {
        this._publishingEventProcessor = publishingEventProcessor;
    }

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        final PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        TransactionService transactionService = getServicesManager().getService(TransactionService.class);
        persistenceManagerService.setSystemProcessing(actionedUponNodeRef, true);
        DmWorkflowService dmWorkflowService = getServicesManager().getService(DmWorkflowService.class);
        Collection<String> nodesToPublish = (Collection<String>)persistenceManagerService.getProperty(actionedUponNodeRef, PublishingModel.PROP_PUBLISHING_EVENT_NODES_TO_PUBLISH);
        for (final String nodeRef : nodesToPublish) {
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    persistenceManagerService.setSystemProcessing(new NodeRef(nodeRef), true);
                    return null;
                }
            }, false, true);
        }
        dmWorkflowService.prePublish(actionedUponNodeRef);
        if (logger.isDebugEnabled()) {
        	logger.debug("[WORKFLOW] processing publishing event: " + actionedUponNodeRef);
        	List<NodeRef> nodes = (List<NodeRef>) persistenceManagerService.getProperty(actionedUponNodeRef, PublishingModel.PROP_PUBLISHING_EVENT_NODES_TO_PUBLISH);
        	logger.debug("[WORKFLOW] nodes to publish: " + nodes);
        }
        _publishingEventProcessor.processEventNode(actionedUponNodeRef);
        if (logger.isDebugEnabled()) {
        	logger.debug("[WORKFLOW] done processing publishing event.");
        }
        for (final String nodeRef : nodesToPublish) {
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    persistenceManagerService.setSystemProcessing(new NodeRef(nodeRef), false);
                    return null;
                }
            }, false, true);
        }

        persistenceManagerService.setSystemProcessing(actionedUponNodeRef, false);
        //_dmWorkflowService.postPublish(actionedUponNodeRef);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
