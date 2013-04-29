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

public class SiteServiceImplTest extends BaseVersionStoreTest {

	private SiteServiceImpl siteService = null;
	
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        siteService = (SiteServiceImpl) getApplicationContext().getBeanFactory().getBean("cstudioSiteService");
        assertNotNull(siteService);
    }
    
    public void testCreatePreviewUrl() {
    	// TODO: method is not supported. no test case for now.
    }
    
    /**
     * test getting authoring server URL that is used in cstudio
     */
    public void testGetAuthoringServerUrl() {
    	// given a site name (e.g. cstudio) check if this returns the correct authoring URL per environment defined in environment-config.xml
    	// the build environment is defaulted to staging
    }
    
    /**
     * test getting preview server URL that is used in cstudio previewing content
     */
    public void testGetPreviewServerUrl() {
    	// given a site name (e.g. cstudio) check if this returns the correct preview URL per environment defined in environment-config.xml
    	// the build environment is defaulted to staging
    }
    
    /**
     * test getting repository type of a site (wcm or dm)
     */
    public void testGetRepositoryType() {
    	// check if it returns a correct repository type (e.g. wcm) for the given site name (e.g. cstudio)
    	// this configuration is in sites.xml
    }
    
    /**
     * test getting site type of a site (wcm, poc, readiness .. etc)
     */
    public void testGetSiteType() {
    	// check if it returns a correct site type (e.g. wcm) for the given cstudio site key (e.g. cstudio-site-dashboard)
    	// this configuration is in sites.xml
    }
    
    /**
     * test getting site list that is used to create the site selection drop-down in the cstudio site dashboard
     */
    public void testGetSitesMenuItems() {
    	// check if it returns all items listed in sites.xml (sites-menu) 
    }
    
    public void testGetWebProjectForSiteId() {
    	// TODO: method is not supported. no test case for now.
    }
    
}
