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
package org.craftercms.cstudio.share.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Concatenation Servlet
 * This servlet may be used to concatenate multiple resources into
 * a single response.  It is intended to be used to load multiple
 * javascript or css files, but may be used for any content of the 
 * same mime type that can be meaningfully concatenated.
 * <p>
 * The servlet uses {@link RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
 * to combine the requested content, so dynamically generated content
 * may be combined (Eg engine.js for DWR).
 * <p>
 * The servlet uses parameter names of the query string as resource names
 * relative to the context root.  So these script tags:
 * <pre>
 *  &lt;script type="text/javascript" src="../js/behaviour.js"&gt;&lt;/script&gt;
 *  &lt;script type="text/javascript" src="../js/ajax.js&/chat/chat.js"&gt;&lt;/script&gt;
 *  &lt;script type="text/javascript" src="../chat/chat.js"&gt;&lt;/script&gt;
 * </pre> can be replaced with the single tag (with the ConcatServlet mapped to /concat):
 * <pre>
 *  &lt;script type="text/javascript" src="../concat?/js/behaviour.js&/js/ajax.js&/chat/chat.js"&gt;&lt;/script&gt;
 * </pre>
 * The {@link ServletContext#getMimeType(String)} method is used to determine the 
 * mime type of each resource.  If the types of all resources do not match, then a 415 
 * UNSUPPORTED_MEDIA_TYPE error is returned.
 * <p>
 * If the init parameter "development" is set to "true" then the servlet will run in
 * development mode and the content will be concatenated on every request. Otherwise
 * the init time of the servlet is used as the lastModifiedTime of the combined content
 * and If-Modified-Since requests are handled with 206 NOT Modified responses if 
 * appropriate. This means that when not in development mode, the servlet must be 
 * restarted before changed content will be served.
 * 
 * @author gregw
 *
 */
public class ConcatResourceServlet extends HttpServlet
{
    boolean _development;
    long _lastModified;
    ServletContext _context;

    
    public void init() throws ServletException
    {
        _lastModified=System.currentTimeMillis();
        _context=getServletContext();   
        _development="true".equals(getInitParameter("development"));
    }

    
    /* 
     * @return The start time of the servlet unless in development mode, in which case -1 is returned.
     */
    protected long getLastModified(HttpServletRequest req)
    {
        return _development?-1:_lastModified;
    }
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String q=req.getQueryString();
        if (q==null)
        {
            resp.sendError(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        
        String[] parts = q.split("\\&");
        String type=null;
        for (int i=0;i<parts.length;i++)
        {
            String t = _context.getMimeType(parts[i]);
            if (t!=null)
            {
                if (type==null)
                    type=t;
                else if (!type.equals(t))
                {
                    resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    return;
                }
            }   
        }

        if (type!=null)
            resp.setContentType(type);

        for (int i=0;i<parts.length;i++)
        {
        	try {
	            RequestDispatcher dispatcher=_context.getRequestDispatcher(parts[i]);
	            if (dispatcher!=null) {
	            	dispatcher.include(req,resp);
	            	resp.getOutputStream().println("\r\n \r\n");
	            }
        	}
        	catch(Exception err) {
        		System.out.println(err);
        	}
        }
    }
}
