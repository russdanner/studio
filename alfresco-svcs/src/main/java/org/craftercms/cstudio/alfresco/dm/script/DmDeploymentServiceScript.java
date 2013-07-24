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
package org.craftercms.cstudio.alfresco.dm.script;

import net.sf.json.JSONObject;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.constant.CStudioConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmDeploymentService;
import org.craftercms.cstudio.alfresco.dm.to.DmDeploymentTaskTO;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.ContentFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DmDeploymentServiceScript extends BaseProcessorExtension {

    private static final Logger logger = LoggerFactory.getLogger(DmDeploymentServiceScript.class);

    protected ServicesManager _servicesManager;
    
	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}


	/**
     * get page items made live within the given date range
     *
     * if no days and no num specified, get deployment items made live on the
     * current date
     *
     * when both specified and there are more items than the specified number,
     * return only the number of items specified. e.g. days = 7, num = 20. If 30
     * items made live within 7 days, return only last 20 items items must be
     * categorized by their deployment date and will be sorted by their URIs
     * within each category
     *
     * @param site
     *            ID of the site to submit items in
     * @param daysFromToday
     *            how many number of days to get deployment history from (e.g. 7
     *            days means get all deployment items within 7 days since today)
     *            If this number is not specified, get the number of deployment
     *            items as specified in num paramter regardless of their data
     *            range
     * @param numberOfItems
     *            how many number of deployment items to get.
     * @param sort
     * 			sorty key to sort the items within each deployed date
     * @param ascending
     * 			sort in ascending order?
     * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
     *
     */
    public String getDeploymentHistory(String site,
                                       String daysFromToday, String numberOfItems, String sort, boolean ascending,String filterType) throws ServiceException {
        int days = 7;
        if (!StringUtils.isEmpty(daysFromToday))
            days = ContentFormatUtils.getIntValue(daysFromToday);

        int number = 20;
        if (! StringUtils.isEmpty(numberOfItems))
            number = ContentFormatUtils.getIntValue(numberOfItems);
        
        List<DmDeploymentTaskTO> deployedTasks = null;
        if(deploymentEngine){
        	deployedTasks=_servicesManager.getService(DmDeploymentService.class).getDeploymentHistoryDeploymentEngine(site, days, number, sort, ascending,filterType);
        }else{
        	deployedTasks=_servicesManager.getService(DmDeploymentService.class).getDeploymentHistory(site, days, number, sort, ascending,filterType);
        }
        JSONObject jsonObject = new JSONObject();
        if (deployedTasks != null) {
            int count = 0;
            for (DmDeploymentTaskTO task : deployedTasks) {
                count += task.getNumOfChildren();
            }
            jsonObject.put(CStudioConstants.PROPERTY_TOTAL, count);
            jsonObject.put(CStudioConstants.PROPERTY_DOCUMENTS, deployedTasks);
            return jsonObject.toString();
        } else {
            jsonObject.put(CStudioConstants.PROPERTY_TOTAL, 0);
            jsonObject.put(CStudioConstants.PROPERTY_DOCUMENTS, null);
        }
        return jsonObject.toString();
    }
}
