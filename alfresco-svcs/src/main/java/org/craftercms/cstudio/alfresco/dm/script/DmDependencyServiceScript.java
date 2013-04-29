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

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.craftercms.cstudio.alfresco.constant.CStudioXmlConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.DmDependencyService;
import org.craftercms.cstudio.alfresco.dm.to.DmContentItemTO;
import org.craftercms.cstudio.alfresco.dm.util.DmContentItemComparator;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.util.ContentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;

import java.io.InputStream;
import java.util.List;

/**
 * A wrapper class of WcmDependencyService that exposes the service in Alfresco
 * javascript API
 *
 * @author hyanghee
 * @author Dejan Brkic
 *
 */
public class DmDependencyServiceScript extends BaseProcessorExtension {

    private static final Logger logger = LoggerFactory.getLogger(DmDependencyServiceScript.class);

    protected static final String JSON_KEY_ITEMS = "items";
    protected static final String JSON_KEY_SUBMISSION_COMMENT = "submissionComment";

    protected static final String RESULT_STYLE_JSON = "json";
    protected static final String RESULT_STYLE_STRING = "string";

   protected ServicesManager _servicesManager;
   

    public void setServicesManager(ServicesManager servicesManager) {
	this._servicesManager = servicesManager;
}

	/**
     * get components and assets that must be submitted with the items listed in
     * the request
     *
     * @param site
     * @param content
     * @return a list of dependencies for each item in json string
     * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
     */
    public String getDependencies(String site, Content content) throws ServiceException {
        return getDependencies(site, content,false);
    }

    /**
     * get components and assets that must be submitted with the items listed in
     * the request
     *
     * @param site
     * @param content
     * @param deleteDependencies
     * @return a list of dependencies for each item in json string
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public String getDependencies(String site, Content content, Boolean deleteDependencies) throws ServiceException {

        if(deleteDependencies == null)
            deleteDependencies=false;

        InputStream in = content.getInputStream();
        try {

            Document document = ContentUtils.convertStreamToXml(in);
            Element root = document.getRootElement();
            List<Node> nodes = root.selectNodes("/" + CStudioXmlConstants.DOCUMENT_ELM_ITEMS + "/"
                    + CStudioXmlConstants.DOCUMENT_ELM_ITEM);
            List<DmContentItemTO> items = null;
            if (nodes != null && nodes.size() > 0) {
                List<String> submittedItems = new FastList<String>(nodes.size());
                for (Node node : nodes) {
                    String uri = node.valueOf("@" + CStudioXmlConstants.DOCUMENT_ATTR_URI);
                    submittedItems.add(uri);
                }
                DmContentItemComparator comparator = new DmContentItemComparator(
                        DmContentItemComparator.SORT_BROWSER_URI, true, true, true);
                items = _servicesManager.getService(DmDependencyService.class).getDependencies(site, submittedItems, comparator, false,deleteDependencies);
            } else {
                items = new FastList<DmContentItemTO>(0);
            }
            StringBuilder sb = new StringBuilder();
            for (DmContentItemTO item : items) {
                String comment = item.getSubmissionComment();
                if (StringUtils.isNotEmpty(comment)) {
                    sb.append(comment).append("\n");
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_KEY_ITEMS, items);
            jsonObject.put(JSON_KEY_SUBMISSION_COMMENT, sb.toString());
            return jsonObject.toString();
        } catch (DocumentException e) {
            logger.error("Error getting dependecies",e);
            throw new ServiceException("Error in parsing the submitted content.", e);

        } catch (RuntimeException e){
            logger.error("Error getting dependecies",e);
            throw e;
        }catch (ServiceException e) {
            logger.error("Error getting dependecies",e);
            throw e;
        }
        finally {
            ContentUtils.release(in);
        }
    }

    public String getAllDependencies(String site, String path, String resultStyle) {
        List<String> result = new FastList<String>();
        getAllDependenciesRecursive(site, path, result);
        return parseAllDependencies(result, resultStyle);
    }

    protected void getAllDependenciesRecursive(String site, String path, List<String> dependecyPaths) {
        DmDependencyService dmDependencyService = _servicesManager.getService(DmDependencyService.class);
        List<String> depPaths = dmDependencyService.getDependencyPaths(site, path);
        for (String depPath : depPaths) {
            if (!dependecyPaths.contains(depPath)) {
                dependecyPaths.add(depPath);
                getAllDependenciesRecursive(site, depPath, dependecyPaths);
            }
        }
    }

    protected String parseAllDependencies(List<String> resultset, String resultStyle) {
        String toRet;
        if (resultStyle.equalsIgnoreCase(RESULT_STYLE_JSON)) {
            toRet = generateJSON(resultset);
        } else if (resultStyle.equalsIgnoreCase(RESULT_STYLE_STRING)) {
            toRet = generateString(resultset);
        } else {
            toRet = resultset.toString();
        }
        return toRet;
    }

    protected String generateString(List<String> resultset) {
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for (String path : resultset) {
            sb.append(++index).append(": ").append(path).append("\n");
        }
        return sb.toString();
    }

    protected String generateJSON(List<String> resultset) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(resultset);
        return jsonArray.toString();
    }
}
