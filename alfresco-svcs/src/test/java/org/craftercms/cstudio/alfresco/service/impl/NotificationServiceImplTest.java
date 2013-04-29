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
package org.craftercms.cstudio.alfresco.service.impl;

import org.alfresco.repo.version.BaseVersionStoreTest;

/**
 * CStudio NotificationService test case
 * 
 * @author hyanghee
 *
 */
public class NotificationServiceImplTest extends BaseVersionStoreTest {

	private NotificationServiceImpl notificationService = null;
	
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        notificationService = (NotificationServiceImpl) getApplicationContext().getBeanFactory().getBean("cstudioNotificationService");
        assertNotNull(notificationService);
    }
    
    /**
     * test getting complete messages that are used in workflow action pop-ups (e.g. submit to go live pop-up)
     */
    public void testGetCompleteMessage() {
    	// get complete message specified in notification-config.xml using one of keys (e.g. submit-to-go-live) and check it is not empty
    }

    /**
     * test getting general messages that are used in workflow action pop-ups  (e.g. scheduling policy)
     */
    public void testGetGeneralMessage() {
    	// get general message specified in notification-config.xml using one of keys (e.g. scheduling-policy) and check it is not empty
    }
   
    /**
     * test getting canned rejection reasons that are used in the rejection pop-up
     */
    public void testGetCannedRejectionReasons() {
    	// get canned rejection reasons specified in notifcation-config.xml and check it is returning at least one rejection release to select
    }
   

}
