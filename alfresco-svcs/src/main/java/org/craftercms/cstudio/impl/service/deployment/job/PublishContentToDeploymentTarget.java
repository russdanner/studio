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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.UserTransaction;

import org.craftercms.cstudio.api.job.Job;
import org.craftercms.cstudio.api.log.Logger;
import org.craftercms.cstudio.api.log.LoggerFactory;
import org.craftercms.cstudio.api.service.authentication.AuthenticationService;
import org.craftercms.cstudio.api.service.deployment.PublishingSyncItem;
import org.craftercms.cstudio.api.service.deployment.PublishingTargetItem;
import org.craftercms.cstudio.api.service.transaction.TransactionService;
import org.craftercms.cstudio.impl.service.deployment.PublishingManager;

public class PublishContentToDeploymentTarget implements Job {

	private static final Logger logger = LoggerFactory.getLogger(PublishContentToDeploymentTarget.class);

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
				tx.begin();
                Set<String> siteNames = _publishingManager.getAllAvailableSites();
                if (siteNames != null && siteNames.size() > 0){
                    for (String site : siteNames) {
                        Set<PublishingTargetItem> targets = _publishingManager.getAllTargetsForSite(site);
                        for (PublishingTargetItem target : targets) {
                            if (_publishingManager.checkConnection(target)) {
								long targetVersion = _publishingManager.getTargetVersion(target, site);
                                
                                if(targetVersion != -1) {
                                	List<PublishingSyncItem> syncItems = _publishingManager.getItemsToSync(site, targetVersion);
	                                if (syncItems != null && syncItems.size() > 0) {
	                                	logger.info("publishing \"{0}\" item(s) to \"{1}\" for site \"{2}\"", syncItems.size(), target.getName(), site);
	                                	
	                                	List<PublishingSyncItem> filteredItems = filterItems(syncItems, target);
	                                    if (filteredItems != null && filteredItems.size() > 0) {
	                                        _publishingManager.deployItemsToTarget(site, filteredItems, target);
	                                    }
	                                    
	                                    long newVersion = getDeployedVersion(syncItems);
										_publishingManager.setTargetVersion(target, newVersion, site);
	                                    _publishingManager.insertDeploymentHistory(target, filteredItems, new Date());
	                                }
                                }
                                else {
                                	// we can talk to the agent but there is something wrong
                                	// for example the features we need are not supported
                                	logger.error("cannot negotiate a version for deployment agent \"{0}\" for site \"{1}\"", target.getName(), site);
                                }
                                
                            } 
                            else {
                                // TODO: update target status
                            	logger.warn("cannot connect to deployment agent \"{0}\" for site \"{1}\"", target.getName(), site);
                            }
                        }
                    }
                }
				tx.commit();
			}
			catch(Exception err) {
				logger.error("error while processing items to be published", err);
				tx.rollback();
			}
		}
		catch(Exception err) {
			logger.error("error while processing items to be published", err);
		}
	}

    protected long getDeployedVersion(List<PublishingSyncItem> syncItems) {
        Collections.sort(syncItems, new VersionComparator());
        PublishingSyncItem item = syncItems.get(0);
        return item.getTimestampVersion();
    }

    class VersionComparator implements Comparator<PublishingSyncItem> {

        @Override
        public int compare(PublishingSyncItem publishingSyncItem, PublishingSyncItem publishingSyncItem2) {
            long result = publishingSyncItem.getTimestampVersion() - publishingSyncItem2.getTimestampVersion();
            if (result > 0) {
                return -1;
            } else if (result < 0) {
                return 1;
            }
            return 0;
        }
    }

    protected List<PublishingSyncItem> filterItems(List<PublishingSyncItem> syncItems, PublishingTargetItem target) {
        List<String> includePaths = target.getIncludePattern();
        List<String> excludePaths = target.getExcludePattern();
        List<PublishingSyncItem> filteredItems = new ArrayList<PublishingSyncItem>();
        for (PublishingSyncItem item : syncItems) {
            boolean exclude = false;
            Pattern regexPattern;
            if (includePaths != null) {
                for (String includePath : includePaths) {
                    regexPattern = Pattern.compile(includePath);
                    Matcher m = regexPattern.matcher(item.getPath());
                    if (m.matches()) {
                        exclude = false;
                    }
                }
            }
            if (excludePaths != null) {
                for (String excludePath : excludePaths) {
                    regexPattern = Pattern.compile(excludePath);
                    Matcher m = regexPattern.matcher(item.getPath());
                    if (m.matches()) {
                        exclude = true;
                    }
                }
            }
            if (!exclude) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    /** getter auth service */
	public AuthenticationService getAuthenticationService() { return _authenticationService; }
	/** setter for auth service */
	public void setAuthenticationService(AuthenticationService service) { _authenticationService = service; }

	/** getter transaction service */
	public TransactionService getTransactionService() { return _transactionService; }
	/** setter for transaction service */
	public void setTransactionService(TransactionService service) { _transactionService = service; }

    public PublishingManager getPublishingManager() { return this._publishingManager; }
    public void setPublishingManager(PublishingManager publishingManager) { this._publishingManager = publishingManager; }

	protected TransactionService _transactionService;
	protected AuthenticationService _authenticationService;
    protected PublishingManager _publishingManager;
}
