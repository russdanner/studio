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

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

public class TaxonomyServiceTest {

    String userName = "admin";
    String site = "cstudio";

    boolean checkin = true;

    /** Local Testing **/
    // String publishTaxonomiesFileName =
    // "src/test/resources/services/publish-taxonomies.xml";
    String serverUrl = "http://localhost:8080/alfresco/s";
    String updateTaxonomiesFileName = "src/test/resources/services/update-taxonomies.xml";

    /** Jupiter Testing **/
    String publishTaxonomiesFileName = "src/test/resources/services/get-taxonomies.xml";
    // String serverUrl = "http://jupiter:7070/alfresco/s";

    String loginUrl = "/api/login?u=" + userName + "&pw=" + userName;
    String publishTaxonomyUrl = "/cstudio/wcm/content/publish-taxonomies?site="
            + site + "&alf_ticket=";
    String updateTaxonomyUrl = "/cstudio/taxonomy/update-taxonomy?site="
            + site + "&alf_ticket=";

    
    /*
    @Test
    public void testPublishTaxonomies() throws Exception {
        String ticket = getTicket();
        File file = new File(publishTaxonomiesFileName);
        PostMethod postMethod = new PostMethod(serverUrl + publishTaxonomyUrl
                + ticket);
        postMethod.setRequestEntity(new InputStreamRequestEntity(
                new FileInputStream(file), file.length()));
        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod);
        System.out.println(statusCode);
        System.out.println(postMethod.getResponseBodyAsString());
    }
    */    
    

    @Test
    public void testUpdateTaxonomies() throws Exception {
        String ticket = getTicket();
        File file = new File(updateTaxonomiesFileName);
        PostMethod postMethod = new PostMethod(serverUrl + updateTaxonomyUrl
                + ticket);
        postMethod.setRequestEntity(new InputStreamRequestEntity(
                new FileInputStream(file), file.length()));
        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod);
        System.out.println(statusCode);
        System.out.println(postMethod.getResponseBodyAsString());
    }    
    
    
    private String getTicket() throws Exception {
        GetMethod getMethod = new GetMethod(serverUrl + loginUrl);
        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(getMethod);
        if (statusCode == 200) {
            String xml = getMethod.getResponseBodyAsString();
            int beginIndex = xml.indexOf("<ticket>");
            int endIndex = xml.indexOf("</ticket>");
            return xml.substring(beginIndex + "<ticket>".length(), endIndex);
        }
        throw new Exception("cannot get a ticket");
    }
}
