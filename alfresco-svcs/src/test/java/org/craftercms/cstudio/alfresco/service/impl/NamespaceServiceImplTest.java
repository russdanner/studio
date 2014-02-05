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

import java.util.List;

//import org.alfresco.repo.version.BaseVersionStoreTest;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;

/**
 * CStudio NamespaceService test cases
 * 
 * @author hyanghee
 *
 */
public class NamespaceServiceImplTest { //} extends BaseVersionStoreTest {

	private NamespaceServiceImpl namespaceService = null;

    /**
     * Called during the transaction setup
     */
    /*
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        namespaceService = (NamespaceServiceImpl) getApplicationContext().getBeanFactory().getBean("cstudioNamespaceService");
    } */

    /**
     * test creating content QName
     */
    public void testCreateContentName() {
//    	// create content name by passing a string name and check if it returns a valid QName
//    	QName contentName1 = namespaceService.createContentName("");
//    	assertNull(contentName1);
//    	QName contentName2 = namespaceService.createContentName("name");
//    	if (contentName2 != null) {
//    		assertEquals(contentName2.toString(), "{http://www.alfresco.org/model/content/1.0}name");
//    	} else {
//    		fail("[name] returned null contentName.");
//    	}
    }

    /**
     * test creating QName from a prefixed string QName
     */
    public void testCreateQName() {
//    	// pass a prefixed QName and check if it returns a valid QName
//    	// empty returns null
//    	QName qname1 = namespaceService.createQName("");
//    	assertNull(qname1);
//    	// wrong format returns null
//    	QName qname2 = namespaceService.createQName("cmname");
//    	assertNull(qname2);
//    	// format check -- alfresco basic
//    	QName qname3 = namespaceService.createQName("cm:name");
//    	if (qname3 != null) {
//    		assertEquals(qname3.toString(), "{http://www.alfresco.org/model/content/1.0}name");
//    	} else {
//    		fail();
//    	}
//    	// format check -- cstudio custom
//    	QName qname4 = namespaceService.createQName("cstudio-core:product");
//    	if (qname4 != null) {
//    		assertEquals(qname4.toString(), "{http://cstudio/assets/core/1.0}product");
//    	} else {
//    		fail();
//    	}
    }

    /**
     * test getting a prefixed string QName from a property QName
     */
    public void testGetPrefixedPropertyName() {
//    	// pass a property QName and check if it returns a valid prefixed QName
//    	String propStr = "cstudio-core:title";
//    	QName qname = namespaceService.createQName(propStr);
//    	String pname = namespaceService.getPrefixedPropertyName(qname);
//    	if (StringUtils.isNotEmpty(pname)) {
//    		assertEquals(pname, propStr);
//    	} else {
//    		fail();
//    	}
    }

    /**
     * test getting a prefixed string QName from a type QName
     */
    public void testGetPrefixedTypeName() {
//    	// pass a type QName and check if it returns a valid prefixed QName
//    	String typeStr = "cstudio-core:product";
//    	QName qname = namespaceService.createQName(typeStr);
//    	String pname = namespaceService.getPrefixedPropertyName(qname);
//    	if (StringUtils.isNotEmpty(pname)) {
//    		assertEquals(pname, typeStr);
//    	} else {
//    		fail();
//    	}
    }

    /**
     * test getting prefixes given a namespace URI
     */
    public void testGetPrefixes() {
//    	// pass a namespace URI (e.g. cstudio-core URI) and check if it returns a prefix (e.g. cstudio-core)
//    	List<String> prefixes = (List<String>)namespaceService.getPrefixes("http://cstudio/assets/core/1.0");
//    	if (prefixes != null) {
//    		// should have one prefix
//    		assertEquals(prefixes.size(), 1);
//    		if (prefixes.size() >= 1) {
//    			assertEquals(prefixes.get(0), "cstudio-core");
//    		} else {
//    			fail("Prefixes list empty");
//    		}
//    	} else {
//    		fail();
//    	}
    }

   
    
}

