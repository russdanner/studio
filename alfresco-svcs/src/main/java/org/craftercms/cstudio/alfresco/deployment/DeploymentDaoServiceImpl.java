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

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

import javolution.util.FastList;

import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DeploymentDaoServiceImpl implements DeploymentDaoService {

    private static final int HISTORY_ALL_LIMIT = 9999999;

	private static final Logger LOGGER = LoggerFactory
            .getLogger(DeploymentDaoServiceImpl.class);

    private static final String STATEMENT_GET_DEPLOYMENT_BATCH = "deployment.getDeploymentBatch";
    private static final String STATEMENT_START_NEW_DEPLOYMENT_ATTEMPT = "deployment.startNewDeploymentAttempt";
    private static final String STATEMENT_GET_DEPLOYMENT_BATCH_ITEMS = "deployment.getDeploymentBatchItems";
    private static final String STATEMENT_GET_DEPLOYMENT_BATCH_DELETE_ITEMS = "deployment.getDeploymentBatchDeleteItems";
    private static final String STATEMENT_INSERT_DEPLOYMENT_BATCH = "deployment.insertDeploymentBatch";
    private static final String STATEMENT_INSERT_DEPLOYMENT_BATCH_ITEM = "deployment.insertDeploymentBatchItem";
    private static final String STATEMENT_INSERT_DEPLOYMENT_BATCH_DELETE_ITEM = "deployment.insertDeploymentBatchDeleteItem";
    private static final String STATEMENT_GET_DEPLOYMENT_WORK_QUEUE = "deployment.getDeploymentWorkQueue";
    private static final String STATEMENT_MARK_BATCH_QUEUED = "deployment.markBatchQueued";
    private static final String STATEMENT_MARK_BATCH_READY = "deployment.markBatchReady";
    private static final String STATEMENT_UPDATE_BATCH_STATUS = "deployment.updateBatchStatus";
    private static final String STATEMENT_UPDATE_ITEM_DEPLOYED_DATE = "deployment.updateItemDeployedDate";
    private static final String STATEMENT_UPDATE_DELETE_ITEM_DEPLOYED_DATE = "deployment.updateDeleteItemDeployedDate";
    private static final String STATEMENT_GET_AFFECTED_PATHS = "deployment.getAffectedPaths";
    private static final String STATEMENT_GET_BATCH_ENDPOINTS = "deployment.getBatchEndpoints";
    private static final String STATEMENT_ROLLBACK_IN_PROGRESS_STATES = "deployment.rollbackInProgressStates";
    private static final String STATEMENT_GET_SCHEDULED_ITEMS = "deployment.getScheduledItems";
    private static final String STATEMENT_GET_BATCH_SIZE = "deployment.getBatchSize";
    private static final String STATEMENT_UPDATE_BATCH_SIZE_QUEUE = "deployment.updateBatchSizeQueue";
    private static final String STATEMENT_UPDATE_BATCH_SIZE_DEPLOYMENT_ITEMS = "deployment.updateBatchSizeDeploymentItems";
    private static final String STATEMENT_UPDATE_BATCH_SIZE_DELETE_ITEMS = "deployment.updateBatchSizeDeleteItems";
    private static final String STATEMENT_CANCEL_SCHEDULED_BATCHES_FOR_PATH = "deployment.cancelScheduledBatchesForPath";
    private static final String STATEMENT_GET_CANCELED_BATCHES_FOR_PATH = "deployment.getCanceledBatchesForPath";
    private static final String STATEMENT_DELETE_BATCH = "deployment.deleteBatch";
    private static final String STATEMENT_DELETE_BATCH_ITEMS = "deployment.deleteBatchItems";
    private static final String STATEMENT_DELETE_BATCH_DELETE_ITEMS = "deployment.deleteBatchDeleteItems";

    /**
     * table check and creation *
     */
    private static final String STATEMENT_CREATE_QUEUE_TABLE = "deployment.createTableWQ";
    private static final String STATEMENT_CHECK_QUEUE_TABLE_EXISTS = "deployment.checkTableExistsWQ";
    private static final String STATEMENT_CREATE_ITEM_TABLE = "deployment.createTableWI";
    private static final String STATEMENT_CHECK_ITEM_TABLE_EXISTS = "deployment.checkTableExistsWI";
    private static final String STATEMENT_CREATE_DELETE_ITEM_TABLE = "deployment.createTableWDI";
    private static final String STATEMENT_CHECK_DELETE_ITEM_TABLE_EXISTS = "deployment.checkTableExistsWDI";
	private static final String STATEMENT_CHECK_ITEM_TABLE_COL_TYPE_EXISTS = "deployment.checkIfWITExists";
    private static final String STATEMENT_CHECK_QUEUE_TABLE_COL_COMMENT_EXISTS = "deployment.checkIfWQSCExists";
    /**
     * table indexes *
     */
    private static final String STATEMENT_ADD_WQ_BATCH_IDX = "deployment.addWQBatchIndex";
    private static final String STATEMENT_CHECK_WQ_BATCH_IDX = "deployment.checkWQSiteIndex";
    private static final String STATEMENT_ADD_WQ_SITE_IDX = "deployment.addWQSiteIndex";
    private static final String STATEMENT_CHECK_WQ_SITE_IDX = "deployment.checkWQSiteIndex";
    private static final String STATEMENT_ADD_WQ_READY_IDX = "deployment.addWQReadyIndex";
    private static final String STATEMENT_CHECK_WQ_READY_IDX = "deployment.checkWQReadyIndex";
    private static final String STATEMENT_ADD_WQ_STATE_IDX = "deployment.addWQStateIndex";
    private static final String STATEMENT_CHECK_WQ_STATE_IDX = "deployment.checkWQStateIndex";

    private static final String STATEMENT_ADD_WI_SITE_IDX = "deployment.addWISiteIndex";
    private static final String STATEMENT_CHECK_WI_SITE_IDX = "deployment.checkWISiteIndex";
    private static final String STATEMENT_ADD_WI_SITEPATH_IDX = "deployment.addWISitePathIndex";
    private static final String STATEMENT_CHECK_WI_SITEPATH_IDX = "deployment.checkWISitePathIndex";
    private static final String STATEMENT_ADD_WI_ENDPOINT_IDX = "deployment.addWIEndpointIndex";
    private static final String STATEMENT_CHECK_WI_ENDPOINT_IDX = "deployment.checkWIEndpointIndex";
    private static final String STATEMENT_ADD_WI_USER_IDX = "deployment.addWIUserIndex";
    private static final String STATEMENT_CHECK_WI_USER_IDX = "deployment.checkWIUserIndex";
    private static final String STATEMENT_ADD_WI_BATCH_IDX = "deployment.addWIBatchIndex";
    private static final String STATEMENT_CHECK_WI_BATCH_IDX = "deployment.checkWIBatchIndex";

    private static final String STATEMENT_ADD_WDI_SITE_IDX = "deployment.addWDISiteIndex";
    private static final String STATEMENT_CHECK_WDI_SITE_IDX = "deployment.checkWDISiteIndex";
    private static final String STATEMENT_ADD_WDI_SITEPATH_IDX = "deployment.addWDISitePathIndex";
    private static final String STATEMENT_CHECK_WDI_SITEPATH_IDX = "deployment.checkWDISitePathIndex";
    private static final String STATEMENT_ADD_WDI_ENDPOINT_IDX = "deployment.addWDIEndpointIndex";
    private static final String STATEMENT_CHECK_WDI_ENDPOINT_IDX = "deployment.checkWDIEndpointIndex";
    private static final String STATEMENT_ADD_WDI_USER_IDX = "deployment.addWDIUserIndex";
    private static final String STATEMENT_CHECK_WDI_USER_IDX = "deployment.checkWDIUserIndex";
    private static final String STATEMENT_ADD_WDI_BATCH_IDX = "deployment.addWDIBatchIndex";
    private static final String STATEMENT_CHECK_WDI_BATCH_IDX = "deployment.checkWDIBatchIndex";

    private static final String STATEMENT_GET_DEPLOYMENT_HISTORY_ALL = "deployment.getDeploymentHistoryALL";
    private static final String STATEMENT_GET_DEPLOYMENT_HISTORY_FILTER = "deployment.getDeploymentHistory";


    protected SqlMapClient _sqlMapClient;

    public SqlMapClient getSqlMapClient() {
        return _sqlMapClient;
    }

    public void setSqlMapClient(SqlMapClient sqlMapClient) {
        this._sqlMapClient = sqlMapClient;
    }

    protected String initializeScriptPath;

    public String getInitializeScriptPath() {
        return initializeScriptPath;
    }

    public void setInitializeScriptPath(String initializeScriptPath) {
        this.initializeScriptPath = initializeScriptPath;
    }

    @Override
    public void initIndexes() {
        DataSource dataSource = _sqlMapClient.getDataSource();
        Connection connection = null;
        int oldval = -1;
        try {
            connection = dataSource.getConnection();
            oldval = connection.getTransactionIsolation();
            if (oldval != Connection.TRANSACTION_READ_COMMITTED) {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
            List<HashMap> checkTableWQ = _sqlMapClient.queryForList(STATEMENT_CHECK_QUEUE_TABLE_EXISTS);
            List<HashMap> checkTableWI = _sqlMapClient.queryForList(STATEMENT_CHECK_ITEM_TABLE_EXISTS);
            List<HashMap> checkTableWDI = _sqlMapClient.queryForList(STATEMENT_CHECK_DELETE_ITEM_TABLE_EXISTS);
            
            if (checkTableWQ == null || checkTableWQ.size() < 1 || checkTableWI == null || checkTableWI.size() < 0) {
                scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath));
            } else {
                if (checkTableWDI == null || checkTableWDI.size() < 1) {
                    scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath.replace("initialize", "initialize_wdi")));
                }
            }
            checkTableWDI = _sqlMapClient.queryForList(STATEMENT_CHECK_DELETE_ITEM_TABLE_EXISTS);
            if (checkTableWDI == null || checkTableWDI.size() < 1) {
                scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath.replace("initialize", "initialize_wdi")));
            }

            List<HashMap> checkIfWDIT = _sqlMapClient.queryForList(STATEMENT_CHECK_ITEM_TABLE_COL_TYPE_EXISTS);
            if (checkIfWDIT == null || checkIfWDIT.isEmpty()) {
                scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath.replace("initialize", "add_type_to_wdi")));
            }

            List<HashMap> checkIfWQSC = _sqlMapClient.queryForList(STATEMENT_CHECK_QUEUE_TABLE_COL_COMMENT_EXISTS);
            if (checkIfWQSC == null || checkIfWQSC.isEmpty()) {
                scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath.replace("initialize", "add_comment_to_wq")));
            }
            
            connection.commit();

            List<HashMap> indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WQ_BATCH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WQ_BATCH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WQ_SITE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WQ_SITE_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WQ_READY_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WQ_READY_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WQ_STATE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WQ_STATE_IDX);
            }

            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WI_SITE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WI_SITE_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WI_SITEPATH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WI_SITEPATH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WI_USER_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WI_USER_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WI_BATCH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WI_BATCH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WI_ENDPOINT_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WI_ENDPOINT_IDX);
            }

            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WDI_SITE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WDI_SITE_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WDI_SITEPATH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WDI_SITEPATH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WDI_USER_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WDI_USER_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WDI_BATCH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WDI_BATCH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_WDI_ENDPOINT_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_WDI_ENDPOINT_IDX);
            }
            
            

            connection.commit();
            if (oldval != -1) {
                connection.setTransactionIsolation(oldval);
            }

        } catch (SQLException e) {
            LOGGER.error("Error while initializing Deployment History DB indexes.", e);
        } catch (IOException e) {
            LOGGER.error("Error while initializing Sequence table DB indexes.", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
                connection = null;
            }
        }
    }

    @Override
    public DeploymentBatchDAO getDeploymentBatch(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            DeploymentBatchDAO entity = (DeploymentBatchDAO) _sqlMapClient.queryForObject(STATEMENT_GET_DEPLOYMENT_BATCH, params);
            if (entity != null) {
                return entity;
            } else {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Deployment Batch is not found for batch id: " + batchId + ".");
                }
                return null;
            }
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting a Deployment Batch for " + batchId + ".", e);
            }
            return null;
        }
    }

    @Override
    public DeploymentBatchDAO startNewDeploymentAttempt(String batchId) {

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            params.put("state_flag", DeploymentBatchStatus.DEPLOYING);
            _sqlMapClient.update(STATEMENT_START_NEW_DEPLOYMENT_ATTEMPT, params);
            DeploymentBatchDAO result = getDeploymentBatch(batchId);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while updating Deployment Batch with new deployment attempt for batch "  + batchId + ".", e);
            }
            return null;
        }
    }

    @Override
    public List<DeploymentItemDAO> getDeploymentBatchItems(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            List<DeploymentItemDAO> result = (List<DeploymentItemDAO>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_BATCH_ITEMS, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting a Deployment Batch Items for batch " + batchId + ".", e);
            }
            return null;
        }
    }

    @Override
    public List<DeploymentItemDAO> getDeploymentBatchDeleteItems(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            List<DeploymentItemDAO> result = (List<DeploymentItemDAO>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_BATCH_DELETE_ITEMS, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting a Deployment Batch Delete Items for batch " + batchId + ".", e);
            }
            return null;
        }
    }

    @Override
    public String createDeploymentBatch(String site, Date launchDate, String username) {
        return createDeploymentBatch(site, launchDate, username, null);
    }

    @Override
    public String createDeploymentBatch(String site, Date launchDate, String username, String submissionComment) {
        try {
            String batchId = UUID.randomUUID().toString();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", site);
            params.put("batch_id", batchId);
            params.put("batch_size", 0);
            if(launchDate==null)
            	launchDate=new Date();
            params.put("golive_datetime", launchDate);
            params.put("state_flag", DeploymentBatchStatus.UNPROCESSED);
            params.put("submission_comment", submissionComment);
            _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH, params);
            return batchId;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while creating new Deployment Batch.", e);
            }
            return null;
        }
    }

    @Override
    public void addBatchDeploymentItems(String batchId, String site, String endpoint, String username, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete) {
        try {
            Map<String, Object> params = null;
            int batchSize = ((Integer)_sqlMapClient.queryForObject(STATEMENT_GET_BATCH_SIZE, batchId)).intValue();
            for (DeploymentItemPathDescriptor path : pathsToDeploy) {
                params = new HashMap();
                params.put("site_id", site);
                params.put("path", path.getPath());
                params.put("endpoint", endpoint);
                params.put("username", username);
                params.put("batch_id", batchId);
                params.put("batch_order", batchSize++);
                params.put("batch_size", 0);
                params.put("type", path.getType());
                _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH_ITEM, params);
            }
            for (String path : pathsToDelete) {
                params = new HashMap();
                params.put("site_id", site);
                params.put("path", path);
                params.put("endpoint", endpoint);
                params.put("username", username);
                params.put("batch_id", batchId);
                params.put("batch_order", batchSize++);
                params.put("batch_size", 0);
                _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH_DELETE_ITEM, params);
            }
            params = new HashMap();
            params.put("batch_id", batchId);
            params.put("batch_size", batchSize);
            _sqlMapClient.update(STATEMENT_UPDATE_BATCH_SIZE_QUEUE, params);
            _sqlMapClient.update(STATEMENT_UPDATE_BATCH_SIZE_DEPLOYMENT_ITEMS, params);
            _sqlMapClient.update(STATEMENT_UPDATE_BATCH_SIZE_DELETE_ITEMS, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while adding new items to deployment batch [ID: " + batchId + "]", e);
            }
        }
    }

    @Override
    public String createDeploymentBatch(String site, List<DeploymentItemPathDescriptor> pathsToDeploy, List<String> pathsToDelete, String endpoint, Date launchDate, String username) {
        try {
            String batchId = UUID.randomUUID().toString();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", site);
            params.put("batch_id", batchId);
            params.put("batch_size", pathsToDeploy.size() + pathsToDelete.size());
            params.put("golive_datetime", launchDate);
            params.put("state_flag", DeploymentBatchStatus.UNPROCESSED);
            _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH, params);
            int order = 0;
            for (DeploymentItemPathDescriptor deploymentItemPathDescriptor : pathsToDeploy) {
                params = new HashMap();
                params.put("site_id", site);
                params.put("path", deploymentItemPathDescriptor.getType());
                params.put("endpoint", endpoint);
                params.put("username", username);
                params.put("batch_id", batchId);
                params.put("batch_order", order++);
                params.put("batch_size", pathsToDeploy.size());
                params.put("type", deploymentItemPathDescriptor.getType());
                _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH_ITEM, params);
            }
            for (String path : pathsToDelete) {
                params = new HashMap();
                params.put("site_id", site);
                params.put("path", path);
                params.put("endpoint", endpoint);
                params.put("username", username);
                params.put("batch_id", batchId);
                params.put("batch_order", order++);
                params.put("batch_size", pathsToDeploy.size());
                _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_BATCH_DELETE_ITEM, params);
            }
            return batchId;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while creating new Deployment Batch.", e);
            }
            return null;
        }
    }

    @Override
    public List<DeploymentBatchDAO> getWorkQueue() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("undeployed_states", DeploymentBatchStatus.UNDEPLOYED_STATES_STR);
            params.put("in_progress_states", DeploymentBatchStatus.IN_PROGRESS_STATES_STR);
            List<DeploymentBatchDAO> queue = (List<DeploymentBatchDAO>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_WORK_QUEUE, params);
            if (queue != null) {
                return queue;
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Deployment work queue is empty.");
                }
                return null;
            }
        } catch (SQLException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Error while getting deployment work queue", e);
            } else {
                LOGGER.error(String.format("Error while getting deployment work queue: %s", e.getMessage()));
            }
            return null;
        }
    }

    @Override
    public void markDeploymentSuccess(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            _sqlMapClient.update(STATEMENT_UPDATE_ITEM_DEPLOYED_DATE, params);
            _sqlMapClient.update(STATEMENT_UPDATE_DELETE_ITEM_DEPLOYED_DATE, params);
            params.put("state_flag", DeploymentBatchStatus.DEPLOYED.toString());
            _sqlMapClient.update(STATEMENT_UPDATE_BATCH_STATUS, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while marking successful deployment of batch " + batchId, e);
            }
        }
    }

    @Override
    public void markDeploymentFailure(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            params.put("state_flag", DeploymentBatchStatus.NOT_DEPLOYED.toString());

            _sqlMapClient.update(STATEMENT_UPDATE_BATCH_STATUS, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while marking deployment failure for batch " + batchId, e);
            }
        }
    }

    @Override
    public void markBatchQueued(String batchId, String clusterNodeId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            params.put("state_flag", DeploymentBatchStatus.QUEUED.toString());
            params.put("cluster_node_id", clusterNodeId);
            _sqlMapClient.update(STATEMENT_MARK_BATCH_QUEUED, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while marking deployment failure for batch " + batchId, e);
            }
        }
    }

    @Override
    public void rollbackTransientQueueBatchStates(String clusterNodeId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("cluster_node_id", clusterNodeId);
            params.put("states", DeploymentBatchStatus.IN_PROGRESS_STATES_STR);
            params.put("state_flag", DeploymentBatchStatus.NOT_DEPLOYED.toString());
            _sqlMapClient.update(STATEMENT_ROLLBACK_IN_PROGRESS_STATES, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while rolling back transient batches ", e);
            }
        }
    }

    @Override
    public void markBatchReady(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            _sqlMapClient.update(STATEMENT_MARK_BATCH_READY, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while marking batch ready for deployment " + batchId, e);
            }
        }
    }

    @Override
    public List<String> getAffectedPaths(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            List<String> result = _sqlMapClient.queryForList(STATEMENT_GET_AFFECTED_PATHS, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting affected paths for " + batchId, e);
            }
            return new FastList<String>();
        }
    }

    @Override
    public List<String> getDeploymentBatchEndpoints(String batchId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("batch_id", batchId);
            List<String> result = (List<String>) _sqlMapClient.queryForList(STATEMENT_GET_BATCH_ENDPOINTS, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting endpoints for " + batchId, e);
            }
            return new FastList<String>();
        }
    }

    @Override
    public List<DeploymentItemDAO> getScheduledItems(String siteId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", siteId);
            params.put("undeployed_states", DeploymentBatchStatus.UNDEPLOYED_STATES_STR);
            List<DeploymentItemDAO> result = (List<DeploymentItemDAO>) _sqlMapClient.queryForList(STATEMENT_GET_SCHEDULED_ITEMS, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting scheduled Items for " + siteId, e);
            }
            return new FastList<DeploymentItemDAO>();
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<DeploymentItemDAO> getDeploymentHistory(String site, Date fromDate, Date toDate,String filter,int limit){
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", site);
            params.put("deployed_states", DeploymentBatchStatus.DEPLOYED.toString());
            params.put("from_date", fromDate);
            params.put("to_date", toDate);
            
            if(limit<=0)
            	params.put("limit",HISTORY_ALL_LIMIT);
            else
            	params.put("limit",limit);
            	
            List<DeploymentItemDAO> result=null;
            
            if(filter.equalsIgnoreCase(DmConstants.CONTENT_TYPE_ALL))
            	result = (List<DeploymentItemDAO>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_HISTORY_ALL, params);
            else{
            	params.put("filter", filter);
            	result = (List<DeploymentItemDAO>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_HISTORY_FILTER, params);
            }
            
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting Deployment " + site, e);
            }
            return new FastList<DeploymentItemDAO>();
        }
    }

    @Override
    public void cancelScheduledBatchesForPath(String site, String path) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", site);
            params.put("path", path);
            params.put("state_flag", DeploymentBatchStatus.UNPROCESSED);
            _sqlMapClient.update(STATEMENT_CANCEL_SCHEDULED_BATCHES_FOR_PATH, params);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("Error while canceling workflow for site [%s] path [%s]", site, path), e);
            }
        }
    }

    @Override
    public List<DeploymentBatchDAO> getCanceledBatchesForPath(String site, String path) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site_id", site);
            params.put("path", path);
            params.put("state_flag", DeploymentBatchStatus.UNPROCESSED);
            List<DeploymentBatchDAO> result = _sqlMapClient.queryForList(STATEMENT_GET_CANCELED_BATCHES_FOR_PATH, params);
            return result;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("Error while getting canceled batches for site [%s] path [%s]", site, path), e);
            }
            return new FastList<DeploymentBatchDAO>();
        }
    }

    @Override
    public void deleteBatch(String batchId) {
        try {
            _sqlMapClient.delete(STATEMENT_DELETE_BATCH_ITEMS, batchId);
            _sqlMapClient.delete(STATEMENT_DELETE_BATCH_DELETE_ITEMS, batchId);
            _sqlMapClient.delete(STATEMENT_DELETE_BATCH, batchId);
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("Error while deleting batch [%s]", batchId), e);
            }
        }
    }
}
