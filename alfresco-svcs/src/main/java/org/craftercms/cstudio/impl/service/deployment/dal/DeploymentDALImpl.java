/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.impl.service.deployment.dal;

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.craftercms.cstudio.api.log.Logger;
import org.craftercms.cstudio.api.log.LoggerFactory;
import org.craftercms.cstudio.api.repository.ContentRepository;
import org.craftercms.cstudio.api.service.authentication.AuthenticationService;
import org.craftercms.cstudio.api.service.deployment.CopyToEnvironmentItem;
import org.craftercms.cstudio.api.service.deployment.DeploymentSyncHistoryItem;
import org.craftercms.cstudio.api.service.deployment.PublishingSyncItem;
import org.craftercms.cstudio.api.service.deployment.PublishingTargetItem;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DeploymentDALImpl implements DeploymentDAL {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentDALImpl.class);

    private static final int HISTORY_ALL_LIMIT = 9999999;
    private final static String CONTENT_TYPE_ALL= "all";

    private static Map<String, CopyToEnvironmentItem> COPY_TO_ENV_DATA = new HashMap<String, CopyToEnvironmentItem>();
    private static int CTED_AUTOINCREMENT = 0;

    private static Map<String, PublishingSyncItem> PUBLISHING_SYNC_DATA = new HashMap<String, PublishingSyncItem>();
    private static int PSD_AUTOINCREMENT = 0;

    private static final String STATEMENT_CHECK_CTE_TABLE_EXISTS = "deploymentWorkers.checkTableExistsCTE";
    private static final String STATEMENT_CHECK_COLUMN_CTECS_EXISTS = "deploymentWorkers.checkIfCTESCExists";
    private static final String STATEMENT_GET_ITEMS_READY_FOR_DEPLOYMENT = "deploymentWorkers.getItemsReadyForDeployment";
    private static final String STATEMENT_INSERT_ITEMS_FOR_DEPLOYMENT = "deploymentWorkers.insertItemForDeployment";
    private static final String STATEMENT_SETUP_ITEMS_DEPLOYMENT_STATE = "deploymentWorkers.setupItemsDeploymentState";
    private static final String STATEMENT_GET_SCHEDULED_ITEMS = "deploymentWorkers.getScheduledItems";

    private static final String STATEMENT_CHECK_PTT_TABLE_EXISTS = "deploymentWorkers.checkTableExistsPTT";
    private static final String STATEMENT_INSERT_ITEMS_FOR_TARGETS_SYNC = "deploymentWorkers.insertItemForTargetSync";
    private static final String STATEMENT_GET_ITEMS_READY_FOR_TARGET_SYNC = "deploymentWorkers.getItemsReadyForTargetSync";

    private static final String STATEMENT_CHECK_DSH_TABLE_EXISTS = "deploymentWorkers.checkTableExistsDSH";
    private static final String STATEMENT_INSERT_DEPLOYMENT_HISTORY = "deploymentWorkers.insertDeploymentSyncHistoryItem";
    private static final String STATEMENT_GET_DEPLOYMENT_HISTORY = "deploymentWorkers.getDeploymentHistory";

    public void initTable() {
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
            List<HashMap> checkTableCTE = _sqlMapClient.queryForList(STATEMENT_CHECK_CTE_TABLE_EXISTS);
            List<HashMap> checkTablePTT = _sqlMapClient.queryForList(STATEMENT_CHECK_PTT_TABLE_EXISTS);

            if (checkTableCTE == null || checkTableCTE.size() < 1 || checkTablePTT == null || checkTablePTT.size() < 1) {
                scriptRunner.runScript(Resources.getResourceAsReader(_initializeWorkerTablesScriptPath));
            }

            List<HashMap> checkColumnCTECS = _sqlMapClient.queryForList(STATEMENT_CHECK_COLUMN_CTECS_EXISTS);
            if (checkColumnCTECS == null || checkColumnCTECS.isEmpty()  ) {
                scriptRunner.runScript(Resources.getResourceAsReader(_addSubmissionCommentCTEScriptPath));
            }

            List<HashMap> checkTableDSH = _sqlMapClient.queryForList(STATEMENT_CHECK_DSH_TABLE_EXISTS);
            if (checkTableDSH == null || checkTableDSH.size() < 1 ) {
                scriptRunner.runScript(Resources.getResourceAsReader(_initializeHistoryTableScriptPath));
            }

            connection.commit();
            if (oldval != -1) {
                connection.setTransactionIsolation(oldval);
            }

        } catch (SQLException e) {
            logger.error("Error while initializing \"Copy To Environment\" table", e);
        } catch (IOException e) {
            logger.error("Error while initializing \"Copy To Environment\" table", e);
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
    public List<CopyToEnvironmentItem> getItemsReadyForDeployment(String site, String environment) {
        List<CopyToEnvironmentItem> retQueue = null;

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site", site);
            params.put("state", CopyToEnvironmentItem.State.READY_FOR_LIVE);
            params.put("now", new Date());

            retQueue = (List<CopyToEnvironmentItem>) _sqlMapClient.queryForList(STATEMENT_GET_ITEMS_READY_FOR_DEPLOYMENT, params);

            if (retQueue != null) {
                if(retQueue.size() > 0) {
                    params = new HashMap<String, Object>();
                    params.put("state", CopyToEnvironmentItem.State.PROCESSING);
                    params.put("items", retQueue);
                    _sqlMapClient.update(STATEMENT_SETUP_ITEMS_DEPLOYMENT_STATE, params);
                }
                else {
                    logger.info("Deployment queue is empty.");
                    retQueue = null; // since other paths return null as empty (consider changing this)
                }
            } else {
                logger.info("Deployment queue is empty.");
            }
        } catch (SQLException e) {
            logger.error("Error while getting deployment work queue", e);
        }

        return retQueue;
    }

    @Override
    public void setupItemsToDeploy(String site, String environment, Map<CopyToEnvironmentItem.Action, List<String>> paths, Date scheduledDate, String approver, String submissionComment) {
        List<CopyToEnvironmentItem> items = createItems(site, environment, paths, scheduledDate, approver, submissionComment);
        try {
            _sqlMapClient.startBatch();
            for (CopyToEnvironmentItem item : items) {
                _sqlMapClient.insert(STATEMENT_INSERT_ITEMS_FOR_DEPLOYMENT, item);
            }
            int numberOfRowsInserted = _sqlMapClient.executeBatch();
        } catch (SQLException e) {
            logger.error("Error while inserting items for deploy", e);
        }
    }

    private List<CopyToEnvironmentItem> createItems(String site, String environment, Map<CopyToEnvironmentItem.Action, List<String>> paths, Date scheduledDate, String approver, String submissionComment) {
        List<CopyToEnvironmentItem> newItems = new ArrayList<CopyToEnvironmentItem>(paths.size());
        for (CopyToEnvironmentItem.Action action : paths.keySet()) {
            for (String path : paths.get(action)) {
                CopyToEnvironmentItem item = new CopyToEnvironmentItem();
                item.setId(Integer.toString(++CTED_AUTOINCREMENT));
                item.setSite(site);
                item.setEnvironment(environment);
                item.setPath(path);
                item.setScheduledDate(scheduledDate);
                item.setState(CopyToEnvironmentItem.State.READY_FOR_LIVE);
                item.setAction(action);
                if (_contentRepository.isRenamed(site, path)) {
                    String oldPath = _contentRepository.getOldPath(site, item.getPath());
                    item.setOldPath(oldPath);
                }
                String contentTypeClass = _contentRepository.getContentTypeClass(site, path);
                item.setContentTypeClass(contentTypeClass);
                item.setUser(approver);
                item.setSubmissionComment(submissionComment);
                newItems.add(item);
            }
        }
        return newItems;
    }

    @Override
    public List<PublishingSyncItem> getItemsReadyForTargetSync(String site, long version) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site", site);
            params.put("version", version);
            List<PublishingSyncItem> queue = (List<PublishingSyncItem>) _sqlMapClient.queryForList(STATEMENT_GET_ITEMS_READY_FOR_TARGET_SYNC, params);
            if (queue != null) {
                return queue;
            } else {
                logger.info("Deployment queue is empty.");
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error while getting deployment work queue", e);
            return null;
        }
    }

    @Override
    public void setupItemsToDelete(String site, String environment, List<String> paths, String approver, Date scheduledDate) {
        List<CopyToEnvironmentItem> items = createDeleteItems(site, environment, paths, approver, scheduledDate);
        try {
            _sqlMapClient.startBatch();
            for (CopyToEnvironmentItem item : items) {
                _sqlMapClient.insert(STATEMENT_INSERT_ITEMS_FOR_DEPLOYMENT, item);
            }
            int numberOfRowsInserted = _sqlMapClient.executeBatch();
        } catch (SQLException e) {
            logger.error("Error while inserting items for deploy", e);
        }
    }

    private List<CopyToEnvironmentItem> createDeleteItems(String site, String environment, List<String> paths, String approver, Date scheduledDate) {
        List<CopyToEnvironmentItem> newItems = new ArrayList<CopyToEnvironmentItem>(paths.size());
        for (String path : paths) {
            CopyToEnvironmentItem item = new CopyToEnvironmentItem();
            item.setId(Integer.toString(++CTED_AUTOINCREMENT));
            item.setSite(site);
            item.setEnvironment(environment);
            item.setPath(path);
            item.setScheduledDate(scheduledDate);
            item.setState(CopyToEnvironmentItem.State.READY_FOR_LIVE);
            item.setAction(CopyToEnvironmentItem.Action.DELETE);
            if (_contentRepository.isRenamed(site, path)) {
                String oldPath = _contentRepository.getOldPath(site, item.getPath());
                item.setOldPath(oldPath);
            }
            String contentTypeClass = _contentRepository.getContentTypeClass(site, path);
            item.setContentTypeClass(contentTypeClass);
            item.setUser(approver);
            newItems.add(item);
        }
        return newItems;
    }

    @Override
    public void setupItemsForPublishingSync(String site, String environment, List<CopyToEnvironmentItem> itemsToDeploy) {
        List<PublishingSyncItem> items = createItems(site, environment, itemsToDeploy);

        try {
            _sqlMapClient.startBatch();
            for (PublishingSyncItem item : items) {
                _sqlMapClient.insert(STATEMENT_INSERT_ITEMS_FOR_TARGETS_SYNC, item);
            }
            int numberOfRowsInserted = _sqlMapClient.executeBatch();
        } catch (SQLException e) {
            logger.error("Error while inserting items for target sync", e);
        }
    }

    private List<PublishingSyncItem> createItems(String site, String environment, List<CopyToEnvironmentItem> itemsToDeploy) {
        Calendar cal = Calendar.getInstance();
        long currentTimestamp = cal.getTimeInMillis();
        List<PublishingSyncItem> newItems = new ArrayList<PublishingSyncItem>(itemsToDeploy.size());
        for (CopyToEnvironmentItem itemToDeploy : itemsToDeploy) {
            PublishingSyncItem item = new PublishingSyncItem();
            item.setId(Integer.toString(++PSD_AUTOINCREMENT));
            item.setSite(site);
            item.setEnvironment(environment);
            item.setPath(itemToDeploy.getPath());
            item.setUser(itemToDeploy.getUser());
            item.setTimestampVersion(currentTimestamp);
            if (itemToDeploy.getAction() == CopyToEnvironmentItem.Action.NEW) {
                item.setAction(PublishingSyncItem.Action.NEW);
            } else if (itemToDeploy.getAction() == CopyToEnvironmentItem.Action.MOVE) {
                item.setAction(PublishingSyncItem.Action.MOVE);
                item.setOldPath(itemToDeploy.getOldPath());
            } else if (itemToDeploy.getAction() == CopyToEnvironmentItem.Action.DELETE) {
                item.setAction(PublishingSyncItem.Action.DELETE);
                item.setOldPath(itemToDeploy.getOldPath());
            } else {
                item.setAction(PublishingSyncItem.Action.UPDATE);
            }
            item.setContentTypeClass(itemToDeploy.getContentTypeClass());
            newItems.add(item);
        }
        return newItems;
    }

    @Override
    public void insertDeploymentHistory(PublishingTargetItem target, List<PublishingSyncItem> publishedItems, Date publishingDate) {
        List<DeploymentSyncHistoryItem> items = createItems(target, publishedItems, publishingDate);
        try {
            _sqlMapClient.startBatch();
            for (DeploymentSyncHistoryItem item : items) {
                _sqlMapClient.insert(STATEMENT_INSERT_DEPLOYMENT_HISTORY, item);
            }
            int numberOfRowsInserted = _sqlMapClient.executeBatch();
        } catch (SQLException e) {
            logger.error("Error while inserting items for target sync", e);
        }
    }

    private List<DeploymentSyncHistoryItem> createItems(PublishingTargetItem target, List<PublishingSyncItem> publishedItems, Date publishingDate) {
        List<DeploymentSyncHistoryItem> items = new ArrayList<DeploymentSyncHistoryItem>(publishedItems.size());

        for (PublishingSyncItem item : publishedItems) {
            DeploymentSyncHistoryItem historyItem = new DeploymentSyncHistoryItem();
            historyItem.setSite(item.getSite());
            historyItem.setPath(item.getPath());
            historyItem.setEnvironment(item.getEnvironment());
            historyItem.setSyncDate(publishingDate);
            historyItem.setTarget(target.getName());
            historyItem.setUser(item.getUser());
            historyItem.setContentTypeClass(item.getContentTypeClass());
            items.add(historyItem);
        }

        return items;
    }

    @Override
    public List<DeploymentSyncHistoryItem> getDeploymentHistory(String site, Date fromDate, Date toDate, String filterType, int numberOfItems) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site", site);
            params.put("from_date", fromDate);
            params.put("to_date", toDate);
            if (numberOfItems <= 0) {
                params.put("limit", HISTORY_ALL_LIMIT);
            } else {
                params.put("limit", numberOfItems);
            }
            List<DeploymentSyncHistoryItem> deploymentHistory = null;
            if (!filterType.equalsIgnoreCase(CONTENT_TYPE_ALL)) {
                params.put("filter", filterType);
            }
            deploymentHistory = (List<DeploymentSyncHistoryItem>) _sqlMapClient.queryForList(STATEMENT_GET_DEPLOYMENT_HISTORY, params);
            if (deploymentHistory != null) {
                return deploymentHistory;
            } else {
                logger.info("Deployment queue is empty.");
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error while getting deployment work queue", e);
            return null;
        }
    }

    @Override
    public List<CopyToEnvironmentItem> getScheduledItems(String site) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("site", site);
            params.put("state", CopyToEnvironmentItem.State.READY_FOR_LIVE);
            params.put("now", new Date());
            List<CopyToEnvironmentItem> scheduledItems = (List<CopyToEnvironmentItem>) _sqlMapClient.queryForList(STATEMENT_GET_SCHEDULED_ITEMS, params);
            if (scheduledItems != null) {
                return scheduledItems;
            } else {
                logger.info("No scheduled items.");
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error while getting scheduled items ", e);
            return null;
        }
    }

    public ContentRepository getContentRepository() { return _contentRepository; }
    public void setContentRepository(ContentRepository contentRepository) { this._contentRepository = contentRepository; }

    public SqlMapClient getSqlMapClient() { return _sqlMapClient; }
    public void setSqlMapClient(SqlMapClient sqlMapClient) { this._sqlMapClient = sqlMapClient; }

    public String getInitializeWorkerTablesScriptPath() { return _initializeWorkerTablesScriptPath; }
    public void setInitializeWorkerTablesScriptPath(String initializeScriptPath) { this._initializeWorkerTablesScriptPath = initializeScriptPath; }

    public String getInitializeHistoryTableScriptPath() { return _initializeHistoryTableScriptPath; }
    public void setInitializeHistoryTableScriptPath(String initializeScriptPath) { this._initializeHistoryTableScriptPath = initializeScriptPath; }

    public String getAddSubmissionCommentCTEScriptPath() { return _addSubmissionCommentCTEScriptPath; }
    public void setAddSubmissionCommentCTEScriptPath(String addSubmissionCommentCTEScriptPath) { this._addSubmissionCommentCTEScriptPath = addSubmissionCommentCTEScriptPath; }

    protected ContentRepository _contentRepository;
    protected SqlMapClient _sqlMapClient;
    protected String _initializeWorkerTablesScriptPath;
    protected String _initializeHistoryTableScriptPath;
    protected String _addSubmissionCommentCTEScriptPath;
}
