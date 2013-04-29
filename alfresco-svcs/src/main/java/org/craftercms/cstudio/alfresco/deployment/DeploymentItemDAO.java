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

public class DeploymentItemDAO {

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

    protected String path;
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    protected String endpoint;
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    protected String username;
    public String getUser() {
        return username;
    }
    public void setUser(String username) {
        this.username = username;
    }

    protected Date deployedDate;
    public Date getDeployedDate() {
        return deployedDate;
    }
    public void setDeployedDate(Date deployedDate) {
        this.deployedDate = deployedDate;
    }

    protected String batchId;
    public String getBatchId() {
        return batchId;
    }
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    protected int batchOrder;
    public int getBatchOrder() {
        return batchOrder;
    }
    public void setBatchOrder(int batchOrder) {
        this.batchOrder = batchOrder;
    }

    protected int batchSize;
    public int getBatchSize() {
        return batchSize;
    }
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    protected Date goLiveDate;
	public Date getGoLiveDate() {
		return goLiveDate;
	}
	public void setGoLiveDate(Date goliveDate) {
		this.goLiveDate = goliveDate;
	}
    
	protected String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
