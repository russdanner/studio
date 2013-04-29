if (typeof CStudioAuthoring == "undefined" || !CStudioAuthoring) {
	var CStudioAuthoring = {};
};

/** 
 * utils
 */
CStudioAuthoring.Utils = CStudioAuthoring.Utils || {

	/**
	 * return true if value is undefined, null or has no value
	 */
	isEmpty: function(value) {
		return (!value || value == null || value == "");
	},
	
	/**
	 * generate a unique id
	 */
	generateUUID: function() {
		return 	(((1+Math.random())*0x10000)|0).toString(16).substring(1) + "-" +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) + "-" +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) + "-" +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) + "-" +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) + "-" +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1) +
				(((1+Math.random())*0x10000)|0).toString(16).substring(1);
	}
}

/**
 * Load content from the AVM
 */
function loadWcmAvmContentInstance(siteId, id, contentPath, contentType, editFlag,draft,changeTemplate, isEdit) {

	var modelInstanceXml = null;
	var error = "";
	try {
       consoleLogger.debug("editFlag "+editFlag);
       consoleLogger.debug("draft "+draft);
		if(editFlag == undefined || editFlag == null || editFlag == false) {
			
			if(contentPath == undefined || contentPath == null) {
			    throwException("path parameter is required to create WCM content");
			}
			
			var newContentUri = "/cstudio/model/get-model-template" +
				"?site=" + siteId + 
				"&contentType=" + contentType;
			
			modelInstanceXml = cstudioFormService.lookupCachedFormObject(newContentUri);
			
			if(modelInstanceXml == undefined || modelInstanceXml == null) {
				modelInstanceXml = remote.call(newContentUri);
				cstudioFormService.cacheFormObject(newContentUri, modelInstanceXml);
			}
		}
		else {	
	
			var existingContentUri = "/cstudio/wcm/content/get-content" +
		 	"?site=" + siteId +
		 	"&path=" + id+
            "&draft=" + draft+
            "&changeTemplate=" + changeTemplate + 
			"&edit=" + isEdit;        

			modelInstanceXml = remote.call(existingContentUri);
		}


		var modelInstanceDom = cstudioFormService.createXmlDocument(modelInstanceXml);

		if(isEmptyValue(id)) {
			id=contentPath;
		}
	}
	catch(err) {
		error = "" + err + "(" + modelInstanceXml + ")";
		throwException(error);
	}
	
	return { 	id: id,
				path: contentPath, 
				type: contentType,
				xml: modelInstanceXml, 
				dom: modelInstanceDom,
				error: error
		   };	
}

/**
 * given a site ID and a content Type lookup information about the content type
 */
function lookupWcmContentTypeInfo(siteId, contentType) {
	
	var contentInfoJson = remote.call("/cstudio/wcm/contenttype/get-content-type?site=" + siteId + "&type=" + contentType);
	var contentInfo = eval('(' + contentInfoJson + ')');
	
	return contentInfo;
}

/**
 * given a site ID and a content Type lookup information about the content type
 */
function lookupWcmContentProperties(siteId, contentPath) {
	
	var contentPropertiesJson = remote.call("/cstudio/wcm/content/get-content-properties?site=" + siteId + "&path=" + contentPath);
	var contentPropertiesObj = eval('(' + contentPropertiesJson + ')');
	
	return contentPropertiesObj.properties;
}

/** 
 * load content from 
 */
function loadWcmDmContentInstance(site, currentUser, articleId, nodeRef, contentType) {
	var serviceUrl = "/cstudio/content/edit-content" +
	    "?site=" + site + 
	 	"&user=" + currentUser +
	 	"&contentType="+contentType;

	var modelInstanceXml = null;
	var modelInstanceDom = null;

	if(isEmptyValue(siteId)) throwException("Site ID cannot be null");
	if(isEmptyValue(currentUser)) throwException("User ID cannot be null");
	if(isEmptyValue(contentType)) throwException("Content ID cannot be null");

	if(isEmptyValue(articleId) && isEmptyValue(nodeRef)) {
		// content will be loaded from prototype	
		modelInstanceXml = remote.call(serviceUrl);	
		modelInstanceDom = cstudioFormService.createXmlDocument(modelInstanceXml);
	} 
	else {

		if(isEmptyValue(articleId)) {
			serviceUrl += "&nodeRef=" + nodeRef; 		
		}
		else {
			serviceUrl += "&id=" + articleId; 
		}

		modelInstanceXml = remote.call(serviceUrl);
		modelInstanceDom = cstudioFormService.createXmlDocument(modelInstanceXml);
	}


	if(isEmptyValue(articleId)) {
		articleId = contentIdFromXml(modelInstanceXml);
	}
	
	if(isEmptyValue(nodeRef)) {
		nodeRef = lookupNoderefForContent(site, articleId, contentType);
	}

	return { 	id: articleId, 
				type: contentType,
				noderef: nodeRef, 
				xml: modelInstanceXml, 
				dom: modelInstanceDom 
		   };
}


/**
 * given a taxonomy name and site id, load the requested taxonomy
 */
function loadTaxonomyInstance(site, modelName, isCurrentOnly, elementName, endLevel, startLevel) {

	var serviceUrl = "/cstudio/model/get-model-data" +
	                 "?site=" + site + 
	                 "&modelName=" + modelName;
	
	if(isCurrentOnly != undefined && isCurrentOnly != null && isCurrentOnly != "") {
		serviceUrl += "&currentOnly=" + isCurrentOnly;
	}
	
	if(elementName != undefined && elementName != null && elementName != "") {
		serviceUrl += "&elementName=" + elementName;
	}
	
	if(startLevel != undefined && startLevel != null && startLevel != "") {
		serviceUrl += "&startLevel=" + startLevel;
	}

	if(endLevel != undefined && endLevel != null && endLevel != "") {
		serviceUrl += "&endLevel=" + endLevel;
	}

	return getTaxonomyInstance(serviceUrl, modelName);
}

/**
 * given a site id and a key, load the requested static model data
 */
function loadStaticModelData(site, key) {

	var serviceUrl = "/cstudio/model/get-static-model-data" +
	                 "?site=" + site + 
	                 "&key=" + key;
	
	return getTaxonomyInstance(serviceUrl, key);
}

/**
 * given a service URL, get the requested taxonomy
 */
function getTaxonomyInstance(serviceUrl, modelName) {

	try {
		var taxonomyXml = cstudioFormService.lookupCachedFormObject(serviceUrl);
	
		if(isEmptyValue(taxonomyXml)) {
			var remoteResponse = alfrescoServiceGet(serviceUrl);
			taxonomyXml = remoteResponse.response.trim();
			
			cstudioFormService.cacheFormObject(serviceUrl, taxonomyXml);
		}
	
		var taxonomyDom = cstudioFormService.createXmlDocument(taxonomyXml);
	} 
	catch(error) {
		throwException(error);
	}
	
	return { 	modelName: modelName,
				xml: taxonomyXml, 
				dom: taxonomyDom 
		   };
}

/**
 * lookup the noderef of a content item given the site,articleId and contentType
 */
function lookupNoderefForContent(site, articleId, contentType) {

	// node ref did not come in on URL so look it up
	var nodeMetadataJson = remote.call("/cstudio/content/get-content-properties" +
			"?site=" + site + 
			"&id=" + articleId + 
			"&contentType=" + contentType);

	var nodeProperties = eval("("+nodeMetadataJson+")");

	if(!nodeProperties || nodeProperties.properties == null || (nodeProperties.code && nodeProperties.code == 500)) {
throwException("error getting NodeRef for content site: '"+site+"'"+
                  "article id: '"+articleId+"'"+
                  "content type:"+contentType+
                  "message: '"+nodeMetadataJson+"'");		
	}

	return nodeProperties.properties["nodeRef"];
}

/**
 * given an xml document as a string, extract the articleId
 */
function contentIdFromXml(domAsString) {

	var retId = null;
	var str = new String(domAsString);

	//Javascript E4X module has problems with XML header
	if ( str.substr(0,5).indexOf("?xml") != -1 ) {
		positionRootElement = str.indexOf("<", 10);//get first real tag
		str = str.substr( positionRootElement, str.length - 1 ); 
	}
	
	var e4x = new XML(str);	
	for each (prop in e4x.*::prop) {		
		if((prop.@name.toString()).equals("cstudio-core:articleId")) {		
			retId = prop.toString();
		}
	}
	
	return retId;
}

function removeXmlDocType(xml) {
	var str = new String(xml);

	//Javascript E4X module has problems with XML header
	if ( str.substr(0,5).indexOf("?xml") != -1 ) {
		positionRootElement = str.indexOf("<", 10);//get first real tag
		str = str.substr( positionRootElement, str.length - 1 ); 
	}
	
	return str;
}


/**
 * return true if an item has no value
 */
function isEmptyValue(value) {
	return (value == undefined || value == null);// || trim(value) == "");
}

function trim(value) {
	return value.replace(/^s+/g,'').replace(/s+$/g,'');
}

/**
 * stop the code and send error to browser
 */
function throwException(message) {
	if(!status) {
	     status.code = 400;
	     status.message = message;
	     status.redirect = true;
	}
	else {
		consoleLogger.debug(message);
	}
}

/**
 * given a site ID and a content Type lookup information about the content type
 */
function getSequenceNextValue(siteId, contentType) {
	var contentInfo = lookupWcmContentTypeInfo(siteId, contentType);
	var namespace = contentInfo.namespace;
	var nextVal = remote.call("/cstudio/sequence/next?namespace="+namespace+"&create=true");
	return nextVal;
}

/**
 * for some reason remote does not get injected to all scripts, just webscripts
 * since this library is used by both webscripts and the form service we must work
 * around this issue until we can fix it.
 */
function alfrescoServiceGet(uri) {
	var response = "";
	//if(remote) {
	//	response = contentInfoJson = remote.call(uri);
	//}
	//else {
	//	response = contentInfoJson = alfrescoRemote.get(uri);
	//}
	
	// alfresco remote object is not available, hence this hack
	try {
		response = contentInfoJson = remote.call(uri);
	}
	
	catch (remoteNoExist){
		response = contentInfoJson = alfrescoRemote.get(uri);
	}
	return response;
}
