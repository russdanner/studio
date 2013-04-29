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
package org.craftercms.cstudio.alfresco.dm.script;

import java.util.List;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmClipboardService;
import org.craftercms.cstudio.alfresco.dm.to.DmPasteItemTO;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.ContentFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmClipboardServiceScript extends BaseProcessorExtension {
    
    protected static final Logger logger = LoggerFactory.getLogger(DmClipboardServiceScript.class);

    protected static String JSON_KEY_CHILDREN = "children";
    protected static String JSON_KEY_DEEP = "deep";
    protected static String JSON_KEY_ITEM = "item";
    protected static String JSON_KEY_URI = "uri";
    
    protected ServicesManager _servicesManager;
   
	/**
     * paste multiple items to the given destination
     *
     * @param site
     * @param request
     * 			request containing paths to paste
     * @param destination
     * @param isCutStr
     * @return a list of new paths
     * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
     */
    public List<String> paste(String site, String request, String destination, String isCutStr) throws ServiceException {
        try {
            boolean cut = ContentFormatUtils.getBooleanValue(isCutStr);
            JSONObject requestObject = JSONObject.fromObject(request);
            JSONArray items = (requestObject.containsKey(JSON_KEY_ITEM)) ? requestObject.getJSONArray(JSON_KEY_ITEM) : null;
            List<DmPasteItemTO> pasteItems = getPasteItems(items, cut);
            if (pasteItems != null) {
                return _servicesManager.getService(DmClipboardService.class).paste(site, pasteItems, destination, cut);
            } else {
                return null;
            }
        } catch (DuplicateChildNodeNameException e) {
            logger.error("Duplicate child name error while pasting", e);
            throw e;
        } catch (ServiceException e){
            logger.error("Service Error while pasting", e);
            throw e;
        }
        catch (RuntimeException e) {
            logger.error("Runtime error Error while pasting", e);
            throw e;
        }
    }

    /**
     * get items to paste from the request object
     *
     * @param items
     * @param cut
     * @return a list of items to paste
     */
    protected List<DmPasteItemTO> getPasteItems(JSONArray items, boolean cut) {
        if (items != null) {
            int length = items.size();
            List<DmPasteItemTO> pasteItems = new FastList<DmPasteItemTO>(length);
            for (int index = 0; index < length; index++) {
                JSONObject item = items.getJSONObject(index);
                String uri = (item.containsKey(JSON_KEY_URI)) ? item.getString(JSON_KEY_URI) : null;
                if (!StringUtils.isEmpty(uri)) {
                    DmPasteItemTO pasteItem = new DmPasteItemTO();
                    pasteItem.setUri(uri);
                    // if cut ==  true, it should be deep 
                    if (cut) {
                        if(uri != null && uri.indexOf(DmConstants.INDEX_FILE) > -1) {
                            uri = uri.replace("/" + DmConstants.INDEX_FILE, "");
                            pasteItem.setUri(uri);
                        }
                        pasteItem.setDeep(true);
                    } else {
                        boolean deep = (item.containsKey(JSON_KEY_DEEP)) ? item.getBoolean(JSON_KEY_DEEP) : false;
                        pasteItem.setDeep(deep);
                    }
                    // only read children when deep = false
                    if (!pasteItem.isDeep()) {
                        JSONArray childItems = (item.containsKey(JSON_KEY_CHILDREN)) ?
                                item.getJSONArray(JSON_KEY_CHILDREN) : null;
                        List<DmPasteItemTO> children = getPasteItems(childItems, cut);
                        pasteItem.setChildren(children);
                    }
                    pasteItems.add(pasteItem);
                }

            }
            return pasteItems;
        }
        return null;
    }

    /**
     * duplicate the content at the given item
     *
     * @param site
     * @param sub
     * @param path
     * @return duplicated content path
     * @throws ServiceException
     */
    public String duplicate(String site, String sub, String path) throws ServiceException {
		return _servicesManager.getService(DmClipboardService.class).duplicate(site, sub, path, path);
        //return _servicesManager.getService(DmClipboardService.class).duplicateToDraft(site, sub, path);
    }

    public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}
}
