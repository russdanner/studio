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

//import org.alfresco.repo.version.BaseVersionStoreTest;

public class FormServiceImplTest { // extends BaseVersionStoreTest {

	private FormServiceImpl formService = null;
	
    /**
     * Called during the transaction setup
     */
    /*
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        formService = (FormServiceImpl) getApplicationContext().getBeanFactory().getBean("cstudioFormService");
        assertNotNull(formService);
    } */
    
    public void testGetWidgetConfiguration() {
    	// check on widget-config.xml 
    	// make calls with different site type (e.g. wcm), form id and widget id 
    	// and check configuration based on widget-config.xml
    }

    public void testLoadComponentAsString() {
    	// given a form id and a component name, 
    	// check if it loads the corresponding component xform definition as string
    	// component name should be the file name that exists under the form folder
    }

    public void testLoadForm() {
    	// given a form id, check if it loads the corresponding xform definition as a document
    }


    public void testLoadFormAsString() {
    	// given a form id, check if it loads the corresponding xform definition as string
    }

}
