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

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.transaction.TransactionService;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import javax.transaction.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeploymentDispatcher implements SmartLifecycle {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeploymentDispatcher.class);

    protected String clusterNodeId;
    public void setClusterNodeId(String clusterId) {
        this.clusterNodeId = clusterId;
    }

    protected ServicesManager servicesManager;
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    protected DeploymentDaoService deploymentDaoService;
    public void setDeploymentDaoService(DeploymentDaoService deploymentDaoService) {
        this.deploymentDaoService = deploymentDaoService;
    }

    protected long sleepPeriodMs;
    public void setSleepPeriodMs(long sleepPeriodMs) {
        this.sleepPeriodMs = sleepPeriodMs;
    }

    protected int threadPoolSize;
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    protected DeploymentCommandFactory commandFactory;
    public void setCommandFactory(DeploymentCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    protected boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected boolean autoStartup = false;

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public void startDispatcher() {
        if (enabled) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Starting Deployment Dispatcher on node [%s]", clusterNodeId));
            }
            rollbackTransientQueueBatchStates();
            BatchProcessor processor = new BatchProcessor(Executors.newFixedThreadPool(threadPoolSize));
            ExecutorService processorThread = Executors.newSingleThreadExecutor();
            processorThread.submit(processor);
        }
    }

    protected void rollbackTransientQueueBatchStates() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Starting recovery process on node [%s] - rolling back all batches in transient states (queued, deploying)", clusterNodeId));
        }
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        deploymentDaoService.rollbackTransientQueueBatchStates(clusterNodeId);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Finished recovery process on node [%s]", clusterNodeId));
        }
    }

    @Override
    public void start() {
        startDispatcher();
    }

    @Override
    public void stop() {
        // TODO: implement stop dispatcher, batch processors and thread pool
    }

    @Override
    public boolean isRunning() {
        // TODO: implement status check service
        return false;
    }

    @Override
    public boolean isAutoStartup() {
        return enabled && autoStartup;
    }

    @Override
    public void stop(Runnable runnable) {
        // TODO: implement stop dispatcher, batch processors and thread pool
    }

    @Override
    public int getPhase() {
        // TODO: implement phased process
        return 0;
    }

    protected void rollbackTransaction(UserTransaction transaction, Exception exc) {
        if (exc instanceof NotSupportedException) {
            LOGGER.error(String.format("Thread is already associated with a transaction and the Transaction Manager does not support nested transactions [node: %s]", clusterNodeId), exc);
        } else if (exc instanceof SystemException) {
            LOGGER.error(String.format("Transaction manager encounters an unexpected error condition when trying to begin transaction [node: %s]", clusterNodeId), exc);
        } else if (exc instanceof HeuristicRollbackException) {
            LOGGER.error(String.format("Heuristic decision was made and all relevant updates have been rolled back [node: %s]", clusterNodeId), exc);
        } else if (exc instanceof RollbackException) {
            LOGGER.error(String.format("Transaction has been rolled back rather than committed [node: %s]", clusterNodeId), exc);
        } else if (exc instanceof HeuristicMixedException) {
            LOGGER.error(String.format("Heuristic decision was made that some relevant updates have been committed while others have been rolled back [node: %s]", clusterNodeId), exc);
        } else {
            LOGGER.error(String.format("Unexpected error in deployment dispatcher [%s]", clusterNodeId), exc);
        }
        try {
            transaction.rollback();
        } catch (SystemException e1) {
            LOGGER.error("Failed to rollback transaction", e1);
        }
    }

    class BatchProcessor implements Runnable {

        protected ExecutorService threadPool;

        protected BatchProcessor(ExecutorService threadPool) {
            this.threadPool = threadPool;
        }

        @Override
        public void run() {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Starting deployment engine's Batch Processor on node [%s]", clusterNodeId));
            }
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
            try {
                runThread();
            } catch (Throwable t) {
                LOGGER.error("Unexpected error inside Batch Processor thread", t);
            }
        }

        protected void runThread() {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Starting infinite processing loop on node [%s] with sleep period of %dms", clusterNodeId, sleepPeriodMs));
            }
            while (true) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Batch processor new iteration on node [%s]", clusterNodeId));
                }
                List<DeploymentBatchDAO> batchQueue = deploymentDaoService.getWorkQueue();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Batch queue size [%d]", batchQueue.size()));
                }
                if (batchQueue != null && !batchQueue.isEmpty()) {
                    for (final DeploymentBatchDAO batch : batchQueue) {
                        processBatch(batch);
                    }
                }
                try {
                    Thread.sleep(sleepPeriodMs);
                } catch (InterruptedException e) {
                }
            }
        }

        public void processBatch(DeploymentBatchDAO batch) {
            if (batch == null) {
                return;
            }
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Start processing batch [%s] on node [%s]", batch.getBatchId(), clusterNodeId));
            }
            TransactionService transactionService = servicesManager.getService(TransactionService.class);
            UserTransaction transaction = transactionService.getUserTransaction();
            try {
                transaction.begin();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Create deployment executor for batch [%s] on node [%s]", batch.getBatchId(), clusterNodeId));
                }
                DeploymentExecutor deploymentExecutor = new DeploymentExecutor(servicesManager, batch.getBatchId(), commandFactory);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Mark batch [%s] as queued on node [%s]", batch.getBatchId(), clusterNodeId));
                }
                deploymentDaoService.markBatchQueued(batch.getBatchId(), clusterNodeId);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Send deployment executor to thread pool for batch [%s] on node [%s]", batch.getBatchId(), clusterNodeId));
                }
                transaction.commit();
                threadPool.submit(deploymentExecutor);
            } catch (Exception e) {
                rollbackTransaction(transaction, batch.getBatchId(), e);
            } finally {
                transaction = null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Finished processing batch [%s] on node [%s]", batch.getBatchId(), clusterNodeId));
            }
        }

        protected void rollbackTransaction(UserTransaction transaction, String batchId, Exception exc) {
            if (exc instanceof NotSupportedException) {
                LOGGER.error(String.format("Thread is already associated with a transaction and the Transaction Manager does not support nested transactions [node: %s]", clusterNodeId), exc);
            } else if (exc instanceof SystemException) {
                LOGGER.error(String.format("Transaction manager encounters an unexpected error condition when trying to begin transaction [node: %s]", clusterNodeId), exc);
            } else if (exc instanceof HeuristicRollbackException) {
                LOGGER.error(String.format("Heuristic decision was made and all relevant updates have been rolled back [node: %s]", clusterNodeId), exc);
            } else if (exc instanceof RollbackException) {
                LOGGER.error(String.format("Transaction has been rolled back rather than committed [node: %s]", clusterNodeId), exc);
            } else if (exc instanceof HeuristicMixedException) {
                LOGGER.error(String.format("Heuristic decision was made that some relevant updates have been committed while others have been rolled back [node: %s]", clusterNodeId), exc);
            } else {
                LOGGER.error(String.format("Unexpected error in deployment dispatcher while processing batch [%s] [node: %s] ", batchId, clusterNodeId), exc);
            }
            try {
                transaction.rollback();
            } catch (SystemException e1) {
                LOGGER.error("Failed to rollback transaction", e1);
            }
        }
    }
}
