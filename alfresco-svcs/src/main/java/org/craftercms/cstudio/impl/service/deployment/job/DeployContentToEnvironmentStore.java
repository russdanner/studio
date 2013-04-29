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
package org.craftercms.cstudio.impl.service.deployment.job;

import org.craftercms.cstudio.api.job.Job;
import org.craftercms.cstudio.api.log.Logger;
import org.craftercms.cstudio.api.log.LoggerFactory;
import org.craftercms.cstudio.api.service.authentication.AuthenticationService;
import org.craftercms.cstudio.api.service.deployment.CopyToEnvironmentItem;
import org.craftercms.cstudio.api.service.transaction.TransactionService;
import org.craftercms.cstudio.impl.service.deployment.PublishingManager;

import javax.transaction.UserTransaction;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class DeployContentToEnvironmentStore implements Job {

	private static final Logger logger = LoggerFactory.getLogger(DeployContentToEnvironmentStore.class);

	public void execute() {
		try {
			Method processJobMethod = this.getClass().getMethod("processJobs", new Class[0]);
			_authenticationService.runAs("admin", this, processJobMethod);
		}
		catch(Exception err) {
			//logger.error("unable to execute job", err);
		}
	}
	
	public void processJobs() {

		try {

			UserTransaction tx = _transactionService.getTransaction();

			try {
				// USE MANAGER TO DO ALL OF THIS, MOST OF THESE ARE PROTECTED MANAGER METHODS!
				tx.begin();

                String environment = "live";

                Set<String> siteNames = _publishingManager.getAllAvailableSites();
                if (siteNames != null && siteNames.size() > 0){
                    for (String site : siteNames) {
                        List<CopyToEnvironmentItem> itemsToDeploy = _publishingManager.getItemsReadyForDeployment(site, environment);
                        if (itemsToDeploy != null && itemsToDeploy.size() > 0) {
                            for (CopyToEnvironmentItem item : itemsToDeploy) {
                                _publishingManager.processItem(item);
                            }
                            _publishingManager.setupItemsForPublishingSync(site, environment, itemsToDeploy);
                        }
                    }
                }
				
				tx.commit();
			} catch(Exception err) {
				tx.rollback();
                err.printStackTrace();
                //logger.error("ERROR: ", err);
			}
		}
		catch(Exception err) {
            err.printStackTrace();
            //logger.error("ERROR: ", err);
		}
	}

	/** getter auth service */
	public AuthenticationService getAuthenticationService() { return _authenticationService; }
	/** setter for auth service */
	public void setAuthenticationService(AuthenticationService service) { _authenticationService = service; }

	/** getter transaction service */
	public TransactionService getTransactionService() { return _transactionService; }
	/** setter for transaction service */
	public void setTransactionService(TransactionService service) { _transactionService = service; }

    public PublishingManager getPublishingManager() { return _publishingManager; }
    public void setPublishingManager(PublishingManager publishingManager) { this._publishingManager = publishingManager; }

    protected TransactionService _transactionService;
	protected AuthenticationService _authenticationService;
    protected PublishingManager _publishingManager;
}
