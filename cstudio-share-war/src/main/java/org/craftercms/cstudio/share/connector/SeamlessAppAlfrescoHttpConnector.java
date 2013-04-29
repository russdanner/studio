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
package org.craftercms.cstudio.share.connector;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.connector.AlfrescoConnector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;

import org.craftercms.cstudio.share.app.SeamlessAppContext;

/**
 * Overriding an implementation of an Alfresco Connector that can be used to connect
 * to an Alfresco Repository and issue URL invokes against it.
 * 
 * The Alfresco Connector extends the Http Connector and provides the
 * additional functionality of stamping a ticket onto the outgoing request.
 * 
 * The ticket is retrieved from the SeamlessApp Context and set to the remote client.
 * 
 * @author Sweta Chalasani
 */
public class SeamlessAppAlfrescoHttpConnector extends AlfrescoConnector {

	/**
     * Instantiates a new SeamlessApp alfresco connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public SeamlessAppAlfrescoHttpConnector(ConnectorDescriptor descriptor,
			String endpoint) {
		super(descriptor, endpoint);
	}

    public Response call(String uri, ConnectorContext context)
    {
        Response response;
        RemoteClient remoteClient = initRemoteClient(context);

        remoteClient = remoteClientTicket(remoteClient);
        
        // call client and process response
        response = remoteClient.call(uri);
        processResponse(remoteClient, response);
        return response;
    }

    public Response call(String uri, ConnectorContext context, InputStream in)
    {
        Response response;
        RemoteClient remoteClient = initRemoteClient(context);
        
        remoteClient = remoteClientTicket(remoteClient);
        
        // call client and process response
        response = remoteClient.call(uri, in);
        processResponse(remoteClient, response);
        return response;
    }
    
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        Response response;
        RemoteClient remoteClient = initRemoteClient(context);
        
        remoteClient = remoteClientTicket(remoteClient);
        
        // call client and process response
        response = remoteClient.call(uri, in, out);
        processResponse(remoteClient, response);
        return response;
    }
    
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
	    Response response;
	    RemoteClient remoteClient = initRemoteClient(context);
	    
	    remoteClient = remoteClientTicket(remoteClient);
	            
	    // call client and process response
	    response = remoteClient.call(uri, req, res);
	    processResponse(remoteClient, response);    
	    return response;
    }
    
    private RemoteClient remoteClientTicket(RemoteClient remoteClient) {
    	SeamlessAppContext context = SeamlessAppContext.currentApplicationContext();
    	if(context != null) {   
    		String ticket = context.getTicket();
    		remoteClient.setTicket(ticket);
    	}
    	return remoteClient;
    }
}
