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
import javolution.util.FastMap;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.constant.CStudioContentModel;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.to.DmPathTO;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class DeploymentExecutor implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeploymentExecutor.class);

    protected ServicesManager servicesManager;
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    protected String batchId;
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    protected DeploymentCommandFactory commandFactory;
    public void setCommandFactory(DeploymentCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public DeploymentExecutor(ServicesManager servicesManager, String batchId, DeploymentCommandFactory commandFactory) {
        this.servicesManager = servicesManager;
        this.batchId = batchId;
        this.commandFactory = commandFactory;
    }

    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Starting deployment executor for batch [%s]", batchId));
        }
        executeDeployment();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Finished deployment executor for batch [%s]", batchId));
        }
    }

    public void executeDeployment() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Start deployment for batch [%s]", batchId));
        }
        DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
        SiteService siteService = servicesManager.getService(SiteService.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        DeploymentBatchDAO batch = deploymentService.getDeploymentBatch(batchId);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Mark new deployment attempt for batch [%s]", batchId));
            }
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
            TransactionService transactionService = servicesManager.getService(TransactionService.class);
            UserTransaction transaction = transactionService.getUserTransaction();
            try {
                transaction.begin();
                batch = deploymentService.startNewDeploymentAttempt(batchId);
                startSystemProcessing(batch);
                transaction.commit();
            } catch (Exception e) {
                rollbackTransaction(transaction, e);
                signalFailure(batch);
                return;
            } finally {
                transaction = null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Get all endpoints for batch [%s]", batchId));
            }
            List<String> endpoints = deploymentService.getDeploymentBatchEndpoints(batchId);
            for (String endpoint : endpoints) {
                transaction = transactionService.getUserTransaction();
                try {
                    transaction.begin();
                    DeploymentEndpointConfigTO endpointConfig = siteService.getDeploymentEndpoint(batch.getSite(), endpoint);
                    if (endpointConfig != null) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(String.format("Create deployment command for batch [%s] endpoint [%s]", batchId, endpoint));
                        }
                        AbstractDeploymentCommand command = commandFactory.createDeploymentCommand(endpointConfig.getType());
                        if (command != null) {
                            command.setBatchId(batchId);
                            command.setEndpoint(endpointConfig);
                            command.setServicesManager(servicesManager);

                            command.deploy();
                        } else {
                            LOGGER.error(String.format("Failed to create deployment command for endpoint [%s], command type [%s]. Skipping deployment on this endpoint", endpoint, endpointConfig.getType()));
                        }
                    } else {
                        LOGGER.error(String.format("Configuration for endpoint [%s] does not existing. Skipping deployment on this endpoint", endpoint));
                    }
                    transaction.commit();
                } catch (Exception e) {
                    rollbackTransaction(transaction, e);
                    signalFailure(batch);
                    return;
                } finally {
                    transaction = null;
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Remove deleted items from repository for batch [%s]", batchId));
            }
            transaction = transactionService.getUserTransaction();
            try {
                transaction.begin();
                List<DeploymentItemDAO> batchDeleteItems = deploymentService.getDeploymentBatchDeleteItems(batchId);
                if (batchDeleteItems.size() > 0) {
                    List<String> deletePaths = new FastList<String>();
                    for (DeploymentItemDAO deletedItem : batchDeleteItems) {
                        deletePaths.add(deletedItem.getPath());
                    }
                    DmContentService dmContentService = servicesManager.getService(DmContentService.class);
                    dmContentService.deleteContents(batch.getSite(), deletePaths, true, batchDeleteItems.get(0).getUser());
                    persistenceManagerService.deleteObjectStateForPaths(batch.getSite(), deletePaths);
                }
                transaction.commit();
            } catch (NotSupportedException e) {
                rollbackTransaction(transaction, e);
                signalFailure(batch);
                return;
            } finally {
                transaction = null;
            }

            signalSuccess(batch);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Deployment successful for batch [%s]", batchId));
            }
        } catch (Exception exc) {
            LOGGER.error(String.format("Deployment failed for batch [%s]", batchId), exc);
            signalFailure(batch);
        }
    }

    protected void startSystemProcessing(DeploymentBatchDAO batch) {
        DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        String repoRootPath = servicesConfig.getRepositoryRootPath(batch.getSite());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Get all affected paths for batch [%s]", batch.getBatchId()));
        }
        List<String> affectedPaths = deploymentService.getAffectedPaths(batch.getBatchId());
        List<String> nodeRefs = new FastList<String>();
        for (String path : affectedPaths) {
            String fullPath = repoRootPath + path;
            NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
            if (nodeRef != null) {
                nodeRefs.add(nodeRef.getId());
            }
        }
        persistenceManagerService.setSystemProcessingBulk(nodeRefs, true);
    }

    protected void signalFailure(DeploymentBatchDAO batch) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Signal failure for batch [%s]", batchId));
        }
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        TransactionService transactionService = servicesManager.getService(TransactionService.class);
        UserTransaction transaction = transactionService.getUserTransaction();
        try {
            transaction.begin();
            DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
            deploymentService.markDeploymentFailure(batch.getBatchId());
            resetSystemProcessing(batch, true);
            deploymentService.markBatchReady(batchId);
            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction, e);
        } finally {
            transaction = null;
        }
    }

    protected void signalSuccess(DeploymentBatchDAO batch) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Signal success for batch [%s]", batchId));
        }
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        TransactionService transactionService = servicesManager.getService(TransactionService.class);
        UserTransaction transaction = transactionService.getUserTransaction();
        try {
            transaction.begin();
            DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
            deploymentService.markDeploymentSuccess(batch.getBatchId());
            resetSystemProcessing(batch, false);
            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction, e);
        } finally {
            transaction = null;
        }
    }

    protected void resetSystemProcessing(DeploymentBatchDAO batch, boolean failed) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Reset system processing for batch [%s]", batchId));
		}
		ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
		PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
		DeploymentService deploymentService = servicesManager.getService(DeploymentService.class);
		String repoRootPath = servicesConfig.getRepositoryRootPath(batch.getSite());
		List<String> affectedPaths = deploymentService.getAffectedPaths(batch.getBatchId());
		List<String> nodeRefs = new FastList<String>(affectedPaths.size());
		DmVersionService dmVersionService = servicesManager.getService(DmVersionService.class);
		for (String path : affectedPaths) {
			String fullPath = repoRootPath + path;
			NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
			if (nodeRef != null) {
				nodeRefs.add(nodeRef.getId());
				if (!failed) {
					copyToLiveRepo(batch.getSite(), nodeRef);

					if (persistenceManagerService.hasAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED)) {
						String oldPath = DefaultTypeConverter.INSTANCE.convert(String.class,
								persistenceManagerService.getProperty(nodeRef, CStudioContentModel.PROP_RENAMED_OLD_URL));
						if (!StringUtils.equalsIgnoreCase(path, oldPath)) {
							oldPath = oldPath.replace("/" + DmConstants.INDEX_FILE, "");
							String renamedPath = servicesConfig.getRepositoryRootPath(batch.getSite()) + oldPath;
							DmPathTO dmPathTO = new DmPathTO(renamedPath);
							dmPathTO.setAreaName(DmConstants.DM_LIVE_REPO_FOLDER);
							NodeRef nodeToDelete = persistenceManagerService.getNodeRef(dmPathTO.toString());
							if (nodeToDelete != null) {
								persistenceManagerService.deleteNode(dmPathTO.toString());
							}
						}
						persistenceManagerService.removeAspect(nodeRef, CStudioContentModel.ASPECT_RENAMED);
					}
					dmVersionService.createNextMajorVersion(batch.getSite(), path, batch.getSubmissionComment());
				}
			}
		}
		if (failed) {
			persistenceManagerService.transitionBulk(nodeRefs, ObjectStateService.TransitionEvent.DEPLOYMENT_FAILED,
					ObjectStateService.State.NEW_PUBLISHING_FAILED);
		} else {
			persistenceManagerService.transitionBulk(nodeRefs, ObjectStateService.TransitionEvent.DEPLOYMENT,
					ObjectStateService.State.EXISTING_UNEDITED_UNLOCKED);
		}
		persistenceManagerService.setSystemProcessingBulk(nodeRefs, false);
    }

    protected void rollbackTransaction(UserTransaction transaction, Exception exc) {
        if (exc instanceof NotSupportedException) {
            LOGGER.error(String.format("Thread is already associated with a transaction and the Transaction Manager does not support nested transactions for batch [%s]", batchId), exc);
        } else if (exc instanceof SystemException) {
            LOGGER.error(String.format("Transaction manager encounters an unexpected error condition when trying to begin transaction for batch [%s]", batchId), exc);
        } else if (exc instanceof HeuristicRollbackException) {
            LOGGER.error(String.format("Heuristic decision was made and all relevant updates have been rolled back for batch [%s]", batchId), exc);
        } else if (exc instanceof RollbackException) {
            LOGGER.error(String.format("Transaction has been rolled back rather than committed for batch [%s]", batchId), exc);
        } else if (exc instanceof HeuristicMixedException) {
            LOGGER.error(String.format("Heuristic decision was made that some relevant updates have been committed while others have been rolled back for batch [%s]", batchId), exc);
        } else if (exc instanceof DeploymentException) {
            LOGGER.error(String.format("Error happened during deployment process for batch [%s]", batchId), exc);
        } else {
            LOGGER.error(String.format("Unexpected error deploying batch [%s]", batchId), exc);
        }
        try {
            transaction.rollback();
        } catch (SystemException e1) {
            LOGGER.error("Failed to rollback transaction", e1);
        }
    }

    protected void copyToLiveRepo(String site, NodeRef nodeRef) {
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        String liveRepoPath = servicesConfig.getLiveRepositoryPath(site);
        NodeRef liveRepoRoot = persistenceManagerService.getNodeRef(liveRepoPath);
        if (liveRepoRoot == null)
            liveRepoRoot = createLiveRepository(site, DmConstants.DM_LIVE_REPO_FOLDER);
        String fullPath = persistenceManagerService.getNodePath(nodeRef);
        Matcher m = DmConstants.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
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
        }
    }

    protected NodeRef createLiveRepository(String site, String liveRepoName) {
        ServicesConfig servicesConfig = servicesManager.getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        String siteRepoPath = servicesConfig.getRepositoryRootPath(site);
        NodeRef siteRepoRoot = persistenceManagerService.getNodeRef(siteRepoPath);
        NodeRef result = persistenceManagerService.createNewFolder(siteRepoRoot, liveRepoName);
        return result;
    }

    protected NodeRef createLiveRepositoryCopy(NodeRef liveRepoRoot, String relativePath, NodeRef nodeRef) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[PUBLISHING POST PROCESSOR] creating live repository copy of " + relativePath);
        }
        PersistenceManagerService persistenceManagerService = servicesManager.getService(PersistenceManagerService.class);
        NodeRef result = null;

        String[] pathSegments = relativePath.split("/");
        NodeRef helperNode = liveRepoRoot;
        NodeRef parent = null;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if (!"".equals(pathSegments[i])) {
                parent = helperNode;
                helperNode = persistenceManagerService.getChildByName(helperNode, ContentModel.ASSOC_CONTAINS, pathSegments[i]);
                if (helperNode == null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("[WORKFLOW] creating a node with name: " + pathSegments[i]);
                    }
                    Map<QName, Serializable> properties = new FastMap<QName, Serializable>();
                    properties.put(ContentModel.PROP_NAME, pathSegments[i]);
                    helperNode = persistenceManagerService.createNewFolder(parent, pathSegments[i], properties);
                }
            }
        }
        String nodeName = (String) persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName assocQName = QName.createQName(ContentModel.TYPE_CONTENT.getNamespaceURI(), QName.createValidLocalName(nodeName));
        result = persistenceManagerService.copy(nodeRef, helperNode, ContentModel.ASSOC_CONTAINS, assocQName);
        persistenceManagerService.setProperty(result, ContentModel.PROP_NAME, nodeName);
        return result;
    }
}
