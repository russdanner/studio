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
package org.craftercms.cstudio.share.service.impl;

import org.craftercms.cstudio.share.app.SeamlessAppContext;
import org.craftercms.cstudio.share.forms.impl.submission.endpoint.WebScriptEndpoint;
import org.craftercms.cstudio.share.service.api.ClipboardKeyGenerator;
import org.craftercms.cstudio.share.service.api.ClipboardService;
import org.craftercms.cstudio.share.service.api.ServiceResponse;
import org.craftercms.cstudio.share.to.ClipboardItemTO;
import net.sf.json.JSONObject;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import org.springframework.extensions.webscripts.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Clipboard Service.  List works in LIFO style.
 *
 * @author tanveer
 */
public class ClipboardServiceImpl implements ClipboardService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClipboardServiceImpl.class);

    // Collection & top needs to be unique based on Site, so Session key = clipboard-name:site
    protected static final String CLIPBOARD_SESSION_COLLECTION = "clipboard_collection";
    protected static final String CLIPBOARD_SESSION_COLLECTION_TOP = "clipboard_collection_top";
    protected static final int CLIPBOARD_COLLECTION_MAX = 100;
    protected static final String CLIPBOARD_PROXY = "clipboardProxy";

    protected WebScriptEndpoint alfrescoWebscriptEndpoint;

    /** clipboard key generator **/
    protected ClipboardKeyGenerator generator;
    
    protected Map<String,ClipboardItemTO> clipItemMap = new HashMap<String,ClipboardItemTO>();

    public ServiceResponse copy(String site, String itemTree, boolean depth) {

        boolean cut = false;
        return _clip(site, itemTree, cut);
    }

    protected ServiceResponse _clip(String site, String itemTree, boolean cut) {
        HttpSession session = SeamlessAppContext.currentApplicationContext().getRequest().getSession();
        if (!validateJSON(itemTree)) {
            return new ServiceResponse(false, "Invalid input json [" + itemTree + "]");
        }
        ClipboardItemTO item = new ClipboardItemTO();
        item.setCutFlag(cut);
        item.setDeep(true);
        item.setBody(itemTree);
        String siteKey = topKey(site) + "_copied";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Copied----------------------------------------");
            LOGGER.debug(itemTree);
        }
        //not removed session to keep old behavior
        session.setAttribute(siteKey, item);
        String key = getClipboardKey();
        clipItemMap.put(siteKey + key,item);
        return new ServiceResponse(true, "Successfully Copied ");
    }

    protected boolean validateJSON(String itemTree) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(itemTree);
        } catch (Exception e) {
            LOGGER.error("Invalid json [" + itemTree + "]", e);
            return false;
        }
        return true;
    }

    public ServiceResponse cut(String site, String itemTree) throws Exception {
        return _clip(site, itemTree, true);
    }

    public ServiceResponse paste(String site, String pasteToFolder) {
        HttpSession session = SeamlessAppContext.currentApplicationContext().getRequest().getSession();
        String siteKey = topKey(site) + "_copied";
        ClipboardItemTO item = (ClipboardItemTO) session.getAttribute(siteKey);
        String key = getClipboardKey();
        // no item then try to find in stored Map
        if (item == null) {
            item = clipItemMap.get(siteKey + key);
            if(item == null) {
                return new ServiceResponse(false, "");
            }
        }
        String pasteUrl = "/cstudio/wcm/clipboard/paste?site=" + site + "&destination=" + pasteToFolder + "&cut="+item.isCutFlag();
        String body = item.getBody();
        Map map = new HashMap();
        Response response = alfrescoWebscriptEndpoint.post(pasteUrl, body, map);
        ResponseStatus status = response.getStatus();
        if (Status.STATUS_OK == status.getCode()) {
        	
        	BufferedReader br = new BufferedReader(new InputStreamReader(response.getResponseStream()));
    		
        	StringBuilder responseString=new StringBuilder();
    		String readLine;
        	try {
				while(((readLine = br.readLine()) != null)) {
					responseString.append(readLine);
				}
			} catch (IOException e) {	
				LOGGER.error("IOException while accessing response");
			}
        	
            //remove from both session and Map
            session.removeAttribute(siteKey);
            clipItemMap.remove(siteKey + key);

            if(responseString.toString().contains("DESTINATION_NODE_EXIST")){
            	return new ServiceResponse(false, responseString.toString());
            }
            return new ServiceResponse(true, responseString.toString());
        }
        if (item.isCutFlag()) {
            // TODO: if item was for cut, so remove it from Nodes.
        }        
        return new ServiceResponse(false, "");
    }

    public boolean pasteAll(String site, String parentPath) throws Exception {
        return true;
    }

    public List<ClipboardItemTO> getItems(String site) throws Exception {
        HttpSession session = SeamlessAppContext.currentApplicationContext().getRequest().getSession();
        String siteKey = topKey(site) + "_copied";
        ClipboardItemTO item = (ClipboardItemTO) session.getAttribute(siteKey);
        //it null then try to get from Map.
        if(item == null) {
            String key = getClipboardKey();
            item = clipItemMap.get(siteKey + key);
        }
        if (null != item) {
            return Collections.singletonList(item);
        }
        return Collections.emptyList();
        //return this.getClipboardCollection(session, site);
    }

    public ClipboardItemTO removeItem(String site) throws Exception {
        ClipboardItemTO item = null;
        HttpSession session = SeamlessAppContext.currentApplicationContext().getRequest().getSession();
        item = this.pop(session, site);

        return item;
    }

    public void clear(String site) throws Exception {

        HttpSession session = SeamlessAppContext.currentApplicationContext().getRequest().getSession();

        List<ClipboardItemTO> items = this.getClipboardCollection(session, site);

        items = null;

        this.setClipboardCollection(session, site, items);

        this.setClipboardTop(session, site, 0);
    }

    protected ClipboardItemTO pop(HttpSession session, String site) {
        int top = this.getClipboardTop(session, site);
        if (top == 0) {
            LOGGER.debug("Clipboard Collection empty; but trying to get item");
            return null;
        }
        List<ClipboardItemTO> items = this.getClipboardCollection(session, site);

        top--;
        // top item
        ClipboardItemTO item = items.get(top);
        // remove top
        items.remove(top);
        // update session data
        this.setClipboardCollection(session, site, items);
        this.setClipboardTop(session, site, top);

        return item;
    }

    @SuppressWarnings("unchecked")
    protected List<ClipboardItemTO> getClipboardCollection(HttpSession session, String site) {
        List<ClipboardItemTO> items = null;

        items = (List<ClipboardItemTO>) session.getAttribute(this.collectionKey(site));

        if (items == null) {
            return new ArrayList<ClipboardItemTO>();
        }

        return items;
    }

    protected void setClipboardCollection(HttpSession session, String site, List<ClipboardItemTO> items) {
        session.setAttribute(this.collectionKey(site), items);
    }

    protected int getClipboardTop(HttpSession session, String site) {
        Integer itop = (Integer) session.getAttribute(this.topKey(site));

        if (itop == null) {
            return 0;
        }
        return itop.intValue();
    }

    protected void setClipboardTop(HttpSession session, String site, int top) {
        session.setAttribute(this.topKey(site), new Integer(top));
    }

    protected String collectionKey(String site) {
        return CLIPBOARD_SESSION_COLLECTION + ":" + site;
    }

    protected String topKey(String site) {
        return CLIPBOARD_SESSION_COLLECTION_TOP + ":" + site;
    }

    public WebScriptEndpoint getAlfrescoWebscriptEndpoint() {
        return alfrescoWebscriptEndpoint;
    }

    public void setAlfrescoWebscriptEndpoint(WebScriptEndpoint alfrescoWebscriptEndpoint) {
        this.alfrescoWebscriptEndpoint = alfrescoWebscriptEndpoint;
    }

    protected String getClipboardKey() {
        HttpServletRequest httpReq = SeamlessAppContext.currentApplicationContext().getRequest();
        return this.generator.generateKey(httpReq);
    }

	/**
	 * @return the generator
	 */
	public ClipboardKeyGenerator getGenerator() {
		return generator;
	}

	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(ClipboardKeyGenerator generator) {
		this.generator = generator;
	}
	
}
