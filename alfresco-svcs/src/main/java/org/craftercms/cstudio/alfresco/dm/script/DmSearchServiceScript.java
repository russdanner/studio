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

import net.sf.json.JSONObject;

import org.craftercms.cstudio.alfresco.constant.CStudioSearchConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmSearchService;
import org.craftercms.cstudio.alfresco.script.SearchServiceScript;
import org.craftercms.cstudio.alfresco.service.api.SearchService;
import org.craftercms.cstudio.alfresco.to.SearchResultTO;

public class DmSearchServiceScript extends SearchServiceScript {


    /**
     * Is indexing required for the given site?
     *
     * @param site
     * @return true if the last index time is older than the index life time
     */
    public boolean isIndexingRequired(String site) {
        return ((DmSearchService)_servicesManager.getService(SearchService.class)).isIndexingRequired(site);
    }

    /**
     * synchronously index the user sandbox of the given store
     *
     * @param site
     */
    public void indexSite(String site) {
        ((DmSearchService)_servicesManager.getService(SearchService.class)).indexSite(site);
    }

    /*
      * (non-Javadoc)
      * @see org.craftercms.cstudio.alfresco.script.SearchServiceScript#generateResponseObject(org.craftercms.cstudio.alfresco.to.SearchResultTO)
      */
    protected Object generateResponseObject(SearchResultTO searchResult) {
        // generate JSON string here
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_COUNT, searchResult.getTotal());
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_PAGE_TOTAL, searchResult.getTotalPages());
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_PER_PAGE, searchResult.getNumOfItems());
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_SEARCH_FAILED, searchResult.isSearchFailed());
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_FAIL_CAUSE, searchResult.getFailCause());
        jsonObject.put(CStudioSearchConstants.SEARCH_RESULT_OBJECT_LIST, searchResult.getItems());
        return jsonObject.toString();
    }
}
