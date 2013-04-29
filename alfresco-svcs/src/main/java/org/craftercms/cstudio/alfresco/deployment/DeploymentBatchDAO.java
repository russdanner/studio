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

import java.util.Date;

public class DeploymentBatchDAO {

    protected long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    protected String siteId;
    public String getSite() {
        return siteId;
    }
    public void setSite(String site) {
        this.siteId = site;
    }

    protected String batchId;
    public String getBatchId() {
        return batchId;
    }
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    protected int batchSize;
    public int getBatchSize() {
        return batchSize;
    }
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    protected int readyFlag;
    public int getReady() {
        return readyFlag;
    }
    public void setReady(int readyFlag) {
        this.readyFlag = readyFlag;
    }

    protected String stateFlag;
    public String getState() {
        return stateFlag;
    }
    public void setState(String stateFlag) {
        this.stateFlag = stateFlag;
    }

    protected Date submitDatetime;
    public Date getSubmitDatetime() {
        return submitDatetime;
    }
    public void setSubmitDatetime(Date submitDatetime) {
        this.submitDatetime = submitDatetime;
    }

    protected Date goliveDatetime;
    public Date getGoliveDatetime() {
        return goliveDatetime;
    }
    public void setGoliveDatetime(Date goliveDatetime) {
        this.goliveDatetime = goliveDatetime;
    }

    protected Date lastAttemptTime;
    public Date getLastAttemptTime() {
        return lastAttemptTime;
    }
    public void setLastAttemptTime(Date lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }

    protected int numberOfRetries;
    public int getNumberOfRetries() {
        return numberOfRetries;
    }
    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    protected String clusterNodeId;
    public String getClusterNodeId() {
        return clusterNodeId;
    }
    public void setClusterNodeId(String clusterNodeId) {
        this.clusterNodeId = clusterNodeId;
    }

    protected String submissionComment;
    public String getSubmissionComment() {
        return submissionComment;
    }
    public void setSubmissionComment(String submissionComment) {
        this.submissionComment = submissionComment;
    }
}
