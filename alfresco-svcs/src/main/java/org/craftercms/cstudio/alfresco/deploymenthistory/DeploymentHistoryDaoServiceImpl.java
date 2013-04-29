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
package org.craftercms.cstudio.alfresco.deploymenthistory;

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.craftercms.cstudio.alfresco.to.TableIndexCheckTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DeploymentHistoryDaoServiceImpl implements DeploymentHistoryDaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentHistoryDaoServiceImpl.class);

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

    /** statements **/
    private static final String STATEMENT_GET_HISTORY_FOR_SITE = "deploymentHistory.getDeploymentHistoryForSite";
    private static final String STATEMENT_INSERT_ENTRY = "deploymentHistory.insertEntry";
    private static final String STATEMENT_DELETE_DEPLOYMENT_HISTORY_FOR_SITE = "deploymentHistory.deleteDeploymentHistoryForSite";

    /** table check and creation **/
    private static final String STATEMENT_CREATE_TABLE = "deploymentHistory.createTable";
    private static final String STATEMENT_CHECK_TABLE_EXISTS = "deploymentHistory.checkTableExists";

    /** table indexes **/
    private static final String STATEMENT_ADD_SITE_IDX = "deploymentHistory.addSiteIndex";
    private static final String STATEMENT_CHECK_SITE_IDX = "deploymentHistory.checkSiteIndex";
    private static final String STATEMENT_ADD_SITEPATH_IDX = "deploymentHistory.addSitePathIndex";
    private static final String STATEMENT_CHECK_SITEPATH_IDX = "deploymentHistory.checkSitePathIndex";
    private static final String STATEMENT_ADD_USER_IDX = "deploymentHistory.addUserIndex";
    private static final String STATEMENT_CHECK_USER_IDX = "deploymentHistory.checkUserIndex";
    private static final String STATEMENT_ADD_CHANNEL_IDX = "deploymentHistory.addChannelIndex";
    private static final String STATEMENT_CHECK_CHANNEL_IDX = "deploymentHistory.checkChannelIndex";
    private static final String STATEMENT_ADD_DEPLOYMENTDATE_IDX = "deploymentHistory.addDeploymentDateIndex";
    private static final String STATEMENT_CHECK_DEPLOYMENTDATE_IDX = "deploymentHistory.checkDeploymentDateIndex";

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
            List<HashMap> checkTable = _sqlMapClient.queryForList(STATEMENT_CHECK_TABLE_EXISTS);
            if (checkTable == null || checkTable.size() < 1) {
                ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
                scriptRunner.runScript(Resources.getResourceAsReader(initializeScriptPath));
            }
            connection.commit();
            List<TableIndexCheckTO> indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_SITE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_SITE_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_SITEPATH_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_SITEPATH_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_USER_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_USER_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_CHANNEL_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_CHANNEL_IDX);
            }
            indexCheckResult = _sqlMapClient.queryForList(STATEMENT_CHECK_DEPLOYMENTDATE_IDX);
            if (indexCheckResult == null || indexCheckResult.size() < 1) {
                _sqlMapClient.insert(STATEMENT_ADD_DEPLOYMENTDATE_IDX);
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
    public List<DeploymentHistoryDAO> getDeploymentHistoryForSite(String site) {
        try {
            List<DeploymentHistoryDAO> entities = (List<DeploymentHistoryDAO>)_sqlMapClient.queryForList(STATEMENT_GET_HISTORY_FOR_SITE, site);
            if (entities == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Deployment history does not exists for site " + site);
                }
            }
            return entities;
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while getting deployment history for " + site + ".", e);
            }
            return null;
        }
    }

    @Override
    public long insertEntry(DeploymentHistoryDAO entry) {
        SqlMapClient sqlClient = getSqlMapClient();
        Long id = null;
        try {
            Object result = sqlClient.insert(STATEMENT_INSERT_ENTRY, entry);
            if (result != null) {
                id = Long.parseLong(result.toString());
            }
        } catch (SQLException e) {
            LOGGER.error("Error while adding new deployment history entry", e);
        }
        return (id != null ? id : -1);
    }

    @Override
    public int insertEntries(List<DeploymentHistoryDAO> entries) {
        int numRows = 0;
        for (DeploymentHistoryDAO entry : entries) {
            long rowId = insertEntry(entry);
            if (rowId != -1) numRows++;
        }
        return numRows;
    }

    @Override
    public void deleteDeploymentHistoryForSite(String site) {
        try {
            LOGGER.info("Deleting deployment history for site " + site);
            _sqlMapClient.delete(STATEMENT_DELETE_DEPLOYMENT_HISTORY_FOR_SITE, site);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting deployment history for site " + site);
        }
    }
}
