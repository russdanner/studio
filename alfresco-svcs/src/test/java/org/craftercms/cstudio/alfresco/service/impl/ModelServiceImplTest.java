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

import org.craftercms.cstudio.alfresco.service.api.NamespaceService;

/**
 * CStudio ModelService test cases
 * 
 * @author hyanghee
 *
 */
public class ModelServiceImplTest extends BaseVersionStoreTest {

	ModelServiceImpl modelService = null;
	NamespaceService namespaceService = null;
	
	private static final String SITE = "readiness";
	
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        modelService = (ModelServiceImpl) getApplicationContext().getBeanFactory().getBean("cstudioModelService");
        assertNotNull(modelService);
        
        namespaceService = (NamespaceService) getApplicationContext().getBeanFactory().getBean("cstudioNamespaceService");
    }

    /**
     * test getting content assets' metadata
     * 
     * @throws Exception
     *//*
    public void testGetAssetsMetadata() throws Exception {
    	//TODO: create an article 
    	// add attachments and images
    	// call getAssetsMetadata and check if it is generating correct document 
    }*/
    
    /**
     * test getting a sample model data 
     * 
     * @throws Exception
     */
    public void testGetModelData() throws Exception {
    	//1) test getting model data 
//    	List<ModelDataTO> modelData = modelService.getModelData(SITE,
//    			namespaceService.getPrefixedTypeName(CStudioContentModel.TYPE_RDY_AUDIENCE), false, -1, 3);
//    	if (modelData != null) {
//    		assertEquals(modelData.size(), 2);
//    	} else {
//    		fail();
//    	}
    	
    	// 2) change the level = 2 and make sure it gets upto the second level down the tree
    	// 3) set currentOnly = true and get OS hardware type. Make sure GENERIC_OS is not included
    }
    
    /**
     * test getting a model instance by a node reference
     * 
     * @throws Exception
     *//*
    public void testGetModelInstance() throws Exception {
    	//TODO: create an article and get its instance by the nodeRef
    	// parse the document returned by the service
    	// check few properties and list properties
    	// do both for cstudio and readiness
    }*/
    
    /**
     * test getting a model instance by its id
     * 
     * @throws Exception
     *//*
    public void testGetModelInstanceById() throws Exception {
    	//TODO: create an article and get its instance by its id
    	// parse the document returned by the service
    	// check few properties and list properties
    	// do both for cstudio and readiness
    }*/
    
    /**
     * test getting a model template of the readiness article type
     * 
     * @throws Exceptionx
     *//*
    public void testGetModelTemplate() throws Exception {
    	Document document = modelService.getModelTemplate(SITE, "cstudio-rdy:readinessArtice", false, false);
    	if (document != null) {
    		Element root = document.getRootElement();
    		String instanceName = root.valueOf("@name");
    		assertEquals(instanceName, namespaceService.getPrefixedTypeName(CStudioContentModel.TYPE_RDY_ARTICLE));
    	} else {
    		fail();
    	}
    	// repeat above with the download article template and one wcm model template (e.g. downloads-entry)
    }*/
    
}
