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

import java.io.IOException;
import java.util.List;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.craftercms.cstudio.alfresco.constant.CStudioSearchConstants;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.SearchService;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.FilterTO;
import org.craftercms.cstudio.alfresco.to.SearchColumnTO;
import org.craftercms.cstudio.alfresco.to.SearchCriteriaTO;
import org.craftercms.cstudio.alfresco.to.SearchResultTO;
import org.craftercms.cstudio.alfresco.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class of CStudio SearchService to expose the service to Alfresco
 * javascript layer
 * 
 * @author hyanghee
 * 
 */
public class SearchServiceScript extends BaseProcessorExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceScript.class);

	protected ServicesManager _servicesManager;
	/**
	 * content search by the given keyword and the type
	 * 
	 * @param site
	 * @param jsonString
	 *            JSON String contains all param in JSON format
	 * 		{
	 * 			contentTypes: content types, 
	 * 			includeAspects: aspects to include, 
	 * 			excludeAspects: aspects to exclude, 
	 * 			keywords: search text, 
	 * 			page: which page, 
	 * 			pageSize: items in a page, 
	 * 			sortBy: [Relevance or any title from query column, 
	 * 			sortAscending: (true/false),
	 * 			filters: <Array>
	 * 				qname: 
	 * 				value: 
	 * 			columns: <Array>
	 * 				title: front-end title 
	 * 				qname:
	 * 				searchable: (true/false) indicates if this column would participate in Text search
	 * 		}
	 *            
	 * @return search results 
	 * @throws ServiceException 
	 */
	public Object search(String site, String jsonString) throws ServiceException {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		// get columns to display in the search result
		JSONArray columnsObj = jsonObject.getJSONArray(CStudioSearchConstants.SEARCH_JSON_COLOUMNS);
		List<SearchColumnTO> columns = getSearchColumns(columnsObj);
		// content types
		List<String> contentTypes = getStringList(jsonObject.getJSONArray(CStudioSearchConstants.SEARCH_JSON_CONTENT_TYPES));
		List<String> includeAspects = getStringList(jsonObject.getJSONArray(CStudioSearchConstants.SEARCH_JSON_INCLUDE_ASPECTS));
		List<String> excludeAspects = getStringList(jsonObject.getJSONArray(CStudioSearchConstants.SEARCH_JSON_EXCLUDE_ASPECTS));
		// Keyword			
		String keyword = (jsonObject.getString(CStudioSearchConstants.SEARCH_JSON_KEYWORD)).trim();
		// filters
		List<FilterTO> filters = getFilters(jsonObject);
		// sort the result
		boolean ascending = true;
		String sort = jsonObject.getString(CStudioSearchConstants.SEARCH_JSON_SORT);
		//sort = (StringUtils.isEmpty(sort)) ? CStudioSearchConstants.SEARCH_DEFAULT_SORT : sort;
		String ascStr = jsonObject.getString(CStudioSearchConstants.SEARCH_JSON_SORT_ASCENDING);
		if (StringUtils.isEmpty(ascStr) || ascStr.equalsIgnoreCase("false")) {
			ascending = false;
		}
		// generate the search output
		int page = jsonObject.getInt(CStudioSearchConstants.SEARCH_JSON_PAGE);
		int pageSize = jsonObject.getInt(CStudioSearchConstants.SEARCH_JSON_PAGESIZE);
		// perform search
		SearchCriteriaTO criteria = new SearchCriteriaTO(site, contentTypes, keyword, filters, columns, 
				includeAspects, excludeAspects, true, true, sort, ascending);
		SearchResultTO searchResult = _servicesManager.getService(SearchService.class).search(criteria, page, pageSize);
		return generateResponseObject(searchResult);
	}

	/**
	 * get a list of strings from json array
	 * 
	 * @param jsonArray
	 * @return a list of strings
	 */
	protected List<String> getStringList(JSONArray jsonArray) {
		List<String> values = null;
		if (jsonArray != null && jsonArray.size() > 0) {
			int len = jsonArray.size();
			values = new FastList<String>(len);
			for (int i = 0; i < len; i++) {
				values.add(jsonArray.getString(i));
			}
		} else {
			values = new FastList<String>(0);
		}
		return values;
	}


	/**
	 * get search filters from the request object
	 *  
	 * @param jsonObject
	 * @return a list of filters
	 */
	protected List<FilterTO> getFilters(JSONObject jsonObject) {
		//filters
		JSONArray filterArray = jsonObject.getJSONArray(CStudioSearchConstants.SEARCH_JSON_FILTERS);
		if (filterArray != null && filterArray.size() > 0) {
			List<FilterTO> filters = new FastList<FilterTO>(filterArray.size());
			int len = filterArray.size();
			for (int i = 0; i < len; i++) {
				JSONObject filterObj = filterArray.getJSONObject(i);
				FilterTO filter = new FilterTO();
				filter.setKey(filterObj.getString(CStudioSearchConstants.SEARCH_JSON_FILTERS_QNAME));
				filter.setValue(filterObj.getString(CStudioSearchConstants.SEARCH_JSON_FILTERS_VALUE));
				if (filterObj.containsKey(CStudioSearchConstants.SEARCH_JSON_FILTERS_START_DATE)) {
					filter.setStartDate(filterObj.getString(CStudioSearchConstants.SEARCH_JSON_FILTERS_START_DATE));
				}
				if (filterObj.containsKey(CStudioSearchConstants.SEARCH_JSON_FILTERS_END_DATE)) {
					filter.setEndDate(filterObj.getString(CStudioSearchConstants.SEARCH_JSON_FILTERS_END_DATE));
				}
				if (filterObj.containsKey(CStudioSearchConstants.SEARCH_JSON_FILTERS_USE_WILD_CARD)) {
					filter.setUseWildcard(filterObj.getBoolean(CStudioSearchConstants.SEARCH_JSON_FILTERS_USE_WILD_CARD));
				}
				filters.add(filter);
			}
			return filters;
		}
		return null;
	}
	
	/**
	 * generate response object
	 * 
	 * @param searchResult
	 * @return response object
	 */
	protected Object generateResponseObject(SearchResultTO searchResult) {
		// for DM search, just return the original search result 
		return searchResult;
	}

	/**
	 * get a list of search columns from the request object
	 * 
	 * @param columnsObj
	 * @return  search columns
	 */
	protected List<SearchColumnTO> getSearchColumns(JSONArray columnsObj) {
		List<SearchColumnTO> columns = new FastList<SearchColumnTO>();
		int len = columnsObj.size();
		for (int i = 0; i < len; i++) {
			JSONObject columnObj = columnsObj.getJSONObject(i);
			SearchColumnTO column = new SearchColumnTO();
			// FIXME: change QName to name
			column.setName(columnObj.getString(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_QNAME));
			column.setTitle(columnObj.getString(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_TITLE));
			if (columnObj.containsKey(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_SEARCHABLE)) {
				String searchable = columnObj.getString(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_SEARCHABLE);
				if (!StringUtils.isEmpty(searchable) && searchable.equalsIgnoreCase(CStudioSearchConstants.SEARCH_JSON_TRUE)) {
					column.setSearchable(true);
				} else {
					column.setSearchable(false);
				}
			}
			if (columnObj.containsKey(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_USE_WILD_CARD)) {
				String useWildCard = columnObj.getString(CStudioSearchConstants.SEARCH_JSON_COLOUMNS_USE_WILD_CARD);
				if (!StringUtils.isEmpty(useWildCard) && useWildCard.equalsIgnoreCase(CStudioSearchConstants.SEARCH_JSON_TRUE)) {
					column.setUseWildCard(true);
				} else {
					column.setUseWildCard(false);
				}
			}
			columns.add(column);
		}
		return columns;
	}

	/**
	 * get search template in XML
	 * 
	 * @param site
	 *            site 
	 * @return search template in XML
	 */
	public String getSearchTemplate(String site) throws ServiceException {
		Document document = _servicesManager.getService(SearchService.class).getSearchTemplate(site);
		if (document != null) {
			try {
				return XmlUtils.convertDocumentToString(document);
			} catch (IOException e) {
				throw new ServiceException("Failed to convert search template document for " + site, e);
			}
		} else {
			throw new ServiceException("Failed to generate a search template for " + site);
		}
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this._servicesManager = servicesManager;
	}

	

}
