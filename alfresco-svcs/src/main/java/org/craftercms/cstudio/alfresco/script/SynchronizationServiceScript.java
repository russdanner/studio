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
package org.craftercms.cstudio.alfresco.script;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.craftercms.cstudio.alfresco.dm.service.api.DmTransactionService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.service.api.SynchronizationService;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * @author Alfonso Vásquez
 */
public class SynchronizationServiceScript extends BaseProcessorExtension {
    
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationServiceScript.class);

    
    protected TaskExecutor taskExecutor;

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    public SynchronizationServiceScript() {
        taskExecutor = new SimpleAsyncTaskExecutor();
        ((SimpleAsyncTaskExecutor) taskExecutor).setConcurrencyLimit(1);
    }

   
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void synchronizeSite(final String site) {
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        final String user = persistenceManagerService.getCurrentUserName();

        
        taskExecutor.execute(new Runnable() {

            @Override
            public void run() {
                AuthenticationUtil.setFullyAuthenticatedUser(user);
                DmTransactionService dmTransactionService = getServicesManager().getService(DmTransactionService.class);
                TransactionHelper txHelper = dmTransactionService.getTransactionHelper();
                try {
                    txHelper.doInTransaction(new RetryingTransactionCallback<Void>() {

                        @Override
                        public Void execute() throws Throwable {
                            SynchronizationService synchronizationService = getServicesManager().getService(SynchronizationService.class);
                            synchronizationService.synchronize(_servicesManager.getService(ServicesConfig.class).getRepositoryRootPath(site));

                            logger.info("Synchronization of site '" + site + "' completed successfully");

                            return null;
                        }

                    }, true);
                } catch (ServiceException e) {
                    logger.error("Site '" + site + "' synchronization failed", e);
                }
            }

        });
    }

}
