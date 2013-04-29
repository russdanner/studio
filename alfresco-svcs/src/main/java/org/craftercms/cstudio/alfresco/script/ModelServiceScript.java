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

import net.sf.json.JSONObject;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.craftercms.cstudio.alfresco.constant.CStudioXmlConstants;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.ModelService;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.ModelDataTO;
import org.craftercms.cstudio.alfresco.to.TaxonomyTypeTO;
import org.craftercms.cstudio.alfresco.util.ContentFormatUtils;
import org.craftercms.cstudio.alfresco.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class of CStudio ModelService to expose the service to Alfresco javascript layer 
 * 
 * @author hyanghee
 * 
 */
public class ModelServiceScript extends BaseProcessorExtension {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceScript.class);

	protected static final String FORMAT_JSON = "json";
	protected static final String FORMAT_XML = "xml";
	
	protected ServicesManager servicesManager;

	/**
	 * get model instance in xml
	 * 
	 * @param site
	 * @param contentType
	 * @param includeDataType
	 * 			populate data type in the model template for each property? (not applicable for WCM models)
	 * @return model instance in XML
	 */
	public String getModelTemplate(String site, String contentType, boolean includeDataType) throws ServiceException {
		Document document = servicesManager.getService(ModelService.class).getModelTemplate(site, contentType, false, true);
		if (document != null) {
			try {
				return XmlUtils.convertDocumentToString(document);
			} catch (IOException e) {
				throw new ServiceException("Failed to get model instance for " + site + ", " + contentType, e);
			}
		} else {
			throw new ServiceException("Failed to get model instance. No document returned.");
		}
	}

	/**
	 * get model data in the specified format
	 * 
	 * @param site
	 * @param modelName
	 * @param currentOnly
	 * 			include current models only? The model must have a 'cstudio-core:isCurrent' property
	 * @param format
	 * 			the format to return model data in
	 * @param elementName (optional) name of model data in response XML 
	 * 			if not specified, it use each model type's local name 
	 * @param startLevel
	 * 			how many levels down from the top taxonomy to read from
	 * @param endLevel
	 * 			how many levels of model instances to read
	 * @return model data in XML
	 * @throws ServiceException 
	 */
	public String getModelData(String site, String modelName, String currentOnly, String format, String elementName, String startLevel, String endLevel) throws ServiceException {
		boolean includeCurrentOnly = (currentOnly != null && currentOnly.equalsIgnoreCase("true")) ? true : false;
		int start = (!StringUtils.isEmpty(startLevel)) ? ContentFormatUtils.getIntValue(startLevel) : -1;
		int end = (!StringUtils.isEmpty(endLevel)) ? ContentFormatUtils.getIntValue(endLevel) : -1;
		List<ModelDataTO> modelData = servicesManager.getService(ModelService.class).getModelData(site, modelName, includeCurrentOnly, start, end);
		if (FORMAT_JSON.equalsIgnoreCase(format)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("modelData", modelData);
			return jsonObject.toString();
		} else if (FORMAT_XML.equalsIgnoreCase(format)) {
			Document document = createXMLDocument(modelData, elementName);
			if (document != null) {
				try {
					return XmlUtils.convertDocumentToString(document);
				} catch (IOException e) {
					throw new ServiceException("Failed to get model data for " + site + ", " + modelName, e);
				}
			} else {
				throw new ServiceException("Failed to get model data. No document returned.");
			}
		} else {
			throw new ServiceException(format + " is not supported.");
		}
	}
	
	/**
	 * create an XML document given the model data
	 * 
	 * @param modelData
	 * @param elementName
	 * 			child element name
	 * @return modelData in XML
	 */
	protected Document createXMLDocument(List<ModelDataTO> modelData, String elementName) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(CStudioXmlConstants.DOCUMENT_MODEL_DATA);
		if (modelData != null) {
			for (ModelDataTO model : modelData) {
				String type = model.getType();
				String localName = (type.contains(":")) ? type.substring(type.indexOf(":") + 1, type.length()) : type;
				String childName = (StringUtils.isEmpty(elementName)) ? localName : elementName;
				Element childElement = root.addElement(childName);
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_NAME, type);
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_ID, String.valueOf(model.getId()));
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_LABEL, model.getLabel());
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_DESCRIPTION, model.getDescription());
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_VALUE, String.valueOf(model.getValue()));
				if (model.getChildren() != null) {
					addChildElements(childElement, model.getChildren(), elementName);
				}
			}
		}
		return document;
	}


	/**
	 * add child elements to the given parent element
	 * 
	 * @param parentElement
	 * 			the parent element to add children to
	 * @param modelData
	 * 			the data to create child elements from
	 * @param elementName
	 * 			the name of child elements
	 */
	protected void addChildElements(Element parentElement, List<ModelDataTO> modelData, String elementName) {
		if (modelData != null) {
			for (ModelDataTO model : modelData) {
				String type = model.getType();
				String localName = type.substring(type.indexOf(":") + 1, type.length());
				String childName = (StringUtils.isEmpty(elementName)) ? localName : elementName;
				Element childElement = parentElement.addElement(childName);
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_NAME, type);
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_ID, model.getId()+"");
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_LABEL, model.getLabel());
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_DESCRIPTION, model.getDescription());
				childElement.addAttribute(CStudioXmlConstants.DOCUMENT_ATTR_VALUE, String.valueOf(model.getValue()));
				if (model.getChildren() != null) {
					addChildElements(childElement, model.getChildren(), elementName);
				}
			}
		}
	}


	/**
	 * get all taxonomies for given site
	 * 
	 * @param site
	 * @return taxonomy data in JSON
	 * @throws ServiceException
	 */
	public String getTaxonomies(String site) throws ServiceException {
		List<TaxonomyTypeTO> taxonomyTypes = servicesManager.getService(ModelService.class).getTaxonomies(site);
		if (taxonomyTypes != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("taxonomies", taxonomyTypes);
			return jsonObject.toString();
		} else {
			throw new ServiceException("Failed to get taxonomies. No document returned.");
		}
	}
		
	/**
	 * get static model data stored as files by the site and the key given
	 * 
	 * @param site
	 * @param key
	 * @return static model data
	 * @throws ServiceException 
	 */
	public String getStaticModelData(String site, String key) throws ServiceException {
		return servicesManager.getService(ModelService.class).getStaticModelData(site, key);
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}
	
}
