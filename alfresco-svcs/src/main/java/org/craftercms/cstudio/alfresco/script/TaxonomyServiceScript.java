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
package org.craftercms.cstudio.alfresco.script;

import java.util.List;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.TaxonomyService;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.TaxonomyTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class of CStudio TaxonomyService to expose the service to Alfresco
 * javascript layer
 * 
 * @author videepkumar1
 * 
 */
public class TaxonomyServiceScript extends BaseProcessorExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyServiceScript.class);

    protected static final String JSON_KEY_TAXONOMIES = "taxonomies";
    protected static final String JSON_KEY_NAME = "name";
    protected static final String JSON_KEY_TYPE = "type";
    protected static final String JSON_KEY_LABEL = "label";
    protected static final String JSON_KEY_ID = "id";
    protected static final String JSON_KEY_NODEREF = "nodeRef";
    // protected static final String JSON_KEY_CREATED = "created";
    // protected static final String JSON_KEY_UPDATED = "updated";
    protected static final String JSON_KEY_DELETED = "deleted";
    protected static final String JSON_KEY_ORDER = "order";
    protected static final String JSON_KEY_IS_CURRENT = "isCurrent";
    protected static final String JSON_KEY_PARENT = "parent";
    protected static final String JSON_KEY_ICON_PATH = "iconPath";
    //protected static final String JSON_KEY_CHILDREN = "children";

    protected ServicesManager _servicesManager;


    /**
     * update taxonomy instances as provided in the request. 
     * The request must contain an array of taxonomy instances as JSON objects.
     * {
     *   "taxonomies": [
     *       {
     *         "name":"", // display name 
     *         "label":"", // label. optional
     *         "type":"", // the prefixed qname of taxonomy type. e.g. cstudio-core:language
     *         "id":"", // id if exists. optional for new 
     *         "noderef":"", // noderef is exists. optional for new
     *         "deleted":"", // is this taxonomy deleted? 
     *         "order":"",  // the order value
     *         "isCurrent":"", // is this taxonomy currently used?
     *         "parent":"", // the immediate parent noderef for create. 
     *                      // if this is empty, it will be the top-level folder of the type given
     *         "iconPath":"", // the path of icon image if exists
     *       },...
     *   ]
     * }
     *
     * @param site
     * @param request
     * @return update result
     */
    public void updateTaxonomyInstances(String site, String request) throws ServiceException {
        JSONObject requestObject = JSONObject.fromObject(request);
        JSONArray items = requestObject.getJSONArray(JSON_KEY_TAXONOMIES);
        if (items != null) {
            List<TaxonomyTO> taxonomies = new FastList<TaxonomyTO>(items.size());
            int max = items.size();
            for (int index = 0; index < max; index++) {
                TaxonomyTO taxonomy = getTaxonomyInstance(items.getJSONObject(index));
                taxonomies.add(taxonomy);
            }
			_servicesManager.getService(TaxonomyService.class).updateTaxonomies(site, taxonomies);
        } else {
			throw new ServiceException("No taxonomy instances provided in the request.");
        }
    }
    
    /**
     * Get TaxonomyTO given a request item
     * 
     * @param items
     * 
     * @return {@link TaxonomyTO}
     */
    protected TaxonomyTO getTaxonomyInstance(JSONObject item) {
        TaxonomyTO taxonomy = new TaxonomyTO();
        taxonomy.setName(item.containsKey(JSON_KEY_NAME) ? item.getString(JSON_KEY_NAME) : null);
        taxonomy.setType(item.containsKey(JSON_KEY_TYPE) ? item.getString(JSON_KEY_TYPE) : null);
        taxonomy.setLabel(item.containsKey(JSON_KEY_LABEL) ? item.getString(JSON_KEY_LABEL) : null);
        taxonomy.setNodeRef(item.containsKey(JSON_KEY_NODEREF) ?  item.getString(JSON_KEY_NODEREF) : null);
        taxonomy.setId(item.containsKey(JSON_KEY_ID) ? item.getLong(JSON_KEY_ID) : -1);
        taxonomy.setDeleted(item.containsKey(JSON_KEY_DELETED) ? item.getBoolean(JSON_KEY_DELETED) : false);
        taxonomy.setCurrent(item.containsKey(JSON_KEY_IS_CURRENT) ? item.getBoolean(JSON_KEY_IS_CURRENT) : true);
        taxonomy.setOrder(item.containsKey(JSON_KEY_ORDER) ? item.getLong(JSON_KEY_ORDER) : -1);
        taxonomy.setParent(item.containsKey(JSON_KEY_PARENT) ? item.getString(JSON_KEY_PARENT) : null);
        taxonomy.setIconPath(item.containsKey(JSON_KEY_ICON_PATH) ? item.getString(JSON_KEY_ICON_PATH) : null);
        return taxonomy;
    }

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}    
    
    /**
     * Update taxonomies for a given site
     * 
     * @param request
     * 
     * @return {@link String}
     *//*
    public String updateTaxonomies(String site, String request) { 
        try {
            JSONObject requestObject = JSONObject.fromObject(request);
            JSONArray items = requestObject.getJSONArray(JSON_KEY_TAXONOMIES);
            if (! items.isEmpty())
            {
                List<TaxonomyTO> taxonomies = new ArrayList<TaxonomyTO>();
                for (int index = 0; index < items.size(); index++) {                    
                    TaxonomyTO taxonomy = getTaxonomy(items.getJSONObject(index));
                    taxonomies.add(taxonomy);
                }
                boolean status = _taxonomyService.updateTaxonomies(site, taxonomies);
                JSONObject jsonObject = new JSONObject();                
                if (status) {
                    jsonObject.put("result", "success");                     
                } else {
                    jsonObject.put("result", "failure");  
                    jsonObject.put("message", "Error in updating the taxonomies.");
                }
                return jsonObject.toString();
            } else {
                JSONObject jsonObject = new JSONObject();                
                jsonObject.put("result", "failure");  
                jsonObject.put("message", "No taxonomies provided for updating.");
                return jsonObject.toString();                
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();                
            jsonObject.put("result", "failure");
            jsonObject.put("message", "Exception occurred: " + e.getMessage() + ": " + e.getCause());
            return jsonObject.toString();
        }
    }*/

    /**
     * Get TaxonomyTO given an item
     * 
     * @param items
     * 
     * @return {@link TaxonomyTO}
     *//*
    protected TaxonomyTO getTaxonomy(JSONObject items) {
        TaxonomyTO taxonomy = new TaxonomyTO();
        
        taxonomy.setName(items.getString(JSON_KEY_NAME));
        taxonomy.setType(items.getString(JSON_KEY_TYPE));
        taxonomy.setNodeRef(items.getString(JSON_KEY_NODEREF));
        taxonomy.setId(items.getLong(JSON_KEY_ID));
        taxonomy.setCreated(items.getBoolean(JSON_KEY_CREATED));
        taxonomy.setUpdated(items.getBoolean(JSON_KEY_UPDATED));
        taxonomy.setDeleted(items.getBoolean(JSON_KEY_DELETED));
        taxonomy.setOrder(items.getLong(JSON_KEY_ORDER));
                
        List<TaxonomyTO> children = new ArrayList<TaxonomyTO>();
        JSONArray itemsChildren = items.getJSONArray(JSON_KEY_CHILDREN);
        if (itemsChildren != null) {
            for (int i = 0; i < itemsChildren.size(); i++) {
                TaxonomyTO child = getTaxonomy(itemsChildren.getJSONObject(i));
                children.add(child);
            }
        }
        taxonomy.setChildren(children);

        return taxonomy;
    }*/
    

   

}
