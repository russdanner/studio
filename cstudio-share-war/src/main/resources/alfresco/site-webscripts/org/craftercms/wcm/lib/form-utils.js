CStudioFormsEngine = function() {		

	/* ==================================== */
	// BASE Controller
	/* ==================================== */

	/**
	 * base class constructor
	 */
	var controller = function() {
		this.init();
	};

	/**
	 * initialize controller
	 */
	controller.prototype.init = function(config) {
		this.config = config || {};
	}; 

	/**
	 * on load method is called when form is loaded
	 */
	controller.prototype.onLoad = function() {

	};
	
	/**
	 * on before save method is called right before form is saved.
	 * this can be used to modify the dom prior to saving
	 */	
	controller.prototype.onBeforeSave = function() {
	};
	
	/**
	 * the onSave method is called at save time 
	 * this method is entrusted with the action of persisting the xml content
	 */
	controller.prototype.onSave = function() {
	};
	
	/**
	 * dispatch is used to invoke the proper controller method for the active phases
	 */
	controller.prototype.dispatch = function(controller) {
		controller.dispatch();
	};

	/**
	 * Do necessary cleanup. Cleanup all draft content.
	 */
	controller.prototype.onCancel = function(){};

	controller.prototype.handleExcessNamespaces = function(xml) {
		
		xml = xml.replace(/xmlns?:xxforms=?"http?:\/\/orbeon.org\/oxf\/xml\/xforms?"/g, "");
		xml = xml.replace(/xmlns?:xxi=?"http?:\/\/orbeon.org\/oxf\/xml\/xinclude?"/g, "");
		xml = xml.replace(/xmlns?:ev=?"http?:\/\/www.w3.org\/2001\/xml-events?"/g, "");
		xml = xml.replace(/xmlns?:xi=?"http?:\/\/www.w3.org\/2001\/XInclude?"/g, "");
		xml = xml.replace(/xmlns?:xs=?"http?:\/\/www.w3.org\/2001\/XMLSchema?"/g, "");
		xml = xml.replace(/xmlns?:xbl=?"http?:\/\/www.w3.org\/ns\/xbl?"/g, "");
		xml = xml.replace(/xmlns?:xsi=?"http?:\/\/www.w3.org\/2001\/XMLSchema-instance?"/g, "");
		xml = xml.replace(/xmlns?:fr=?"http?:\/\/orbeon.org\/oxf\/xml\/form-runner?"/g, "");
		xml = xml.replace(/xmlns?:xdt=?"http?:\/\/www.w3.org\/2005\/xpath-datatypes?"/g, "");
		xml = xml.replace(/xmlns?:xhtml=?"http?:\/\/www.w3.org\/1999\/xhtml?"/g, "");
		xml = xml.replace(/xmlns?:widget=?"http?:\/\/orbeon.org\/oxf\/xml\/widget?"/g, "");
		xml = xml.replace(/xmlns?:f=?"http?:\/\/orbeon.org\/oxf\/xml\/formatting?"/g, "");			
		xml = xml.replace(/xmlns?:xforms=?"http?:\/\/www.w3.org\/2002\/xforms?"/g, "");		
		xml = xml.replace(/xmlns?:cs=?"http?:\/\/cstudio?"/g, "");		
		xml = xml.replace(/xmlns?:cstudio=?"http?:\/\/cstudio?"/g, "");		

		return xml;		          
	};
	
	/**
	 * convert script and style tags that were "escaped" by the RTE
	 */
	controller.prototype.handledRteEscapedElements = function(xml) {
		var modifiedXml = removeXmlDocType(xml);

 		return modifiedXml;
	};

	/* ==================================== */
	// AVM Controller
	/* ==================================== */	

	WcmController.prototype = new controller;
	WcmController.prototype.constructor = WcmController;

	/**
	 * WCM constructor
	 */
	function WcmController(config){ 
		this._self = this;
		this.parent = this.prototype;
		this.init(config);
		this.type = "WCM";
	};

	/**
	 * initialize controller
	 */
	WcmController.prototype.init = function(config) {
		this.config = config || {};
		this.enableDeployToTestServer = (this.config.enableDeployToTestServer) ? this.config.enableDeployToTestServer : false;
		this.auditTrailEnabled = this.config.enableAuditTrail || true;
		
		//config.allPagesAsIndex = (config.allPagesAsIndex) ? config.allPagesAsIndex : false;
	}; 

	/**
	 * default implementation for the WCM onLoad method
	 * load a single XML model based on the content type
	 */
	WcmController.prototype.onLoad = function() {

		CStudioFormsUtils.executeBasicWcmOnLoadFormController(
			this.config.contentType, 
			this.config.instanceName);
	};

	/**
	 * default implementation for onSave
	 * 1) post the content to be saved to the AVM
	 * 2) if configured, initiate a deployment to the test server
	 */
	WcmController.prototype.onSave = function(xml) {
		var indexFile = "index.xml";
		var xmlExtension = ".xml";
		var fileName = "" + xml['file-name'];	
		var contentAsFolder = false;
		var params = submissionParameters.getParams();
		var saveContentAtPath = path;
        var toDraft = (draft == 'true' && duplicate!='true' && changeTemplate!='true');
        var create = !oldContentPath.match(".xml$");
        
		// make call to repository to find out if this content should be captured as a folder/index.xml
		var typeInfoJson = alfrescoRemote.get("/cstudio/wcm/contenttype/get-content-type" +
				"?site=" + site + "&type=" + contentType,  params);
		
		if(typeInfoJson != null) {
			var typeInfo = eval('(' + typeInfoJson + ')');
			contentAsFolder = typeInfo.contentAsFolder;
		}
		
		var oldFileName = "";
        if (create) {
	        // clean up the path by adding '/' at the end if doesn't end with it 
			var lastChar = oldContentPath.charAt(oldContentPath.length - 1);
        	if (lastChar != '/') {
				consoleLogger.debug("[ONSAVE] adding / to the oldContentPath :" + oldContentPath);
				oldContentPath = oldContentPath + "/";
        	}
        	if (contentAsFolder == 'false') {
				consoleLogger.debug("[ONSAVE] setting path to be the old content path :" + oldContentPath);
				path = oldContentPath;
			}
        } else {
        	// get the old file name
			var oldFileName = oldContentPath.substring(oldContentPath.lastIndexOf("/") + 1, oldContentPath.length);
        }
		consoleLogger.debug("[ONSAVE] oldFileName : " + oldFileName);
        
		// for file duplicate, clean up path and file name 
        if (duplicate && oldFileName != indexFile) {
			consoleLogger.debug("[ONSAVE] cleaning up path and file name for file duplicate.");
			path = path.replace(".html", "");
			fileName = fileName.replace(".html", "");
			xml["file-name"] = fileName;
        }
        
		consoleLogger.debug("[ONSAVE] path :" + path);
		consoleLogger.debug("[ONSAVE] oldContentPath :" + oldContentPath);
		consoleLogger.debug("[ONSAVE] fileName :" + fileName);
		consoleLogger.debug("[ONSAVE] duplicate : " + duplicate);
		consoleLogger.debug("[ONSAVE] draft : " + draft);
		consoleLogger.debug("[ONSAVE] changeTemplate : " + changeTemplate);
		consoleLogger.debug("[ONSAVE] toDraft : " + toDraft);
		consoleLogger.debug("[ONSAVE] create : " + create);
		consoleLogger.debug("[ONSAVE] contentAsFolder : " + contentAsFolder);
        
		var rename = false;
		// if it's not create and duplicate and the old filen name is different from the new one 
		if (!create && !duplicate && oldFileName != fileName) {
			// if the content is folder (meaning that the oldContentPath ends with index.xml)
			// compare the file name without extension and the last folder in the old content path
			if (oldFileName == indexFile) {
				consoleLogger.debug("[ONSAVE] checking if this is renaming a folder.");
				var oldContentFolderPath = oldContentPath.substring(0, oldContentPath.lastIndexOf("/"));
				var oldFolderName = oldContentFolderPath.substring(oldContentFolderPath.lastIndexOf("/") + 1, oldContentFolderPath.length);
				var newFolderName = fileName.substring(0, fileName.lastIndexOf("."));
				consoleLogger.debug("[ONSAVE] newFolderName : " + newFolderName);
				consoleLogger.debug("[ONSAVE] oldContentFolderPath : " + oldContentFolderPath);
				consoleLogger.debug("[ONSAVE] oldFolderName : " + oldFolderName);
				if (newFolderName != oldFolderName) {
					consoleLogger.debug("[ONSAVE] new folder name and old folder name do not match. this is a folder rename.");
					rename = true;
				}
			} else {
				consoleLogger.debug("[ONSAVE] old file name is not empty and does not match with new file name.");
				rename = true;
			}
		} 
		
		consoleLogger.debug("[ONSAVE] rename : " + rename);
		consoleLogger.debug("[ONSAVE] unlock : " + unlock);

		var saveContentAtPath = path;
		// calculate new folder path for any folder name change
		if (oldFileName == indexFile && fileName != indexFile) {
			consoleLogger.debug("[ONSAVE] changing path for folder duplication.");
			// remove the last file and folder
			var newFolderPathWithoutFile = saveContentAtPath.substring(0, saveContentAtPath.lastIndexOf("/"));
			consoleLogger.debug("[ONSAVE] saveContentAtPath after removing the last file : " + newFolderPathWithoutFile);
			var newFolderPath = newFolderPathWithoutFile.substring(0, newFolderPathWithoutFile.lastIndexOf("/"));
			consoleLogger.debug("[ONSAVE] saveContentAtPath after removing the last folder : " + newFolderPath);
			var newFolderName = fileName.substring(0, fileName.lastIndexOf("."));
			consoleLogger.debug("[ONSAVE] newFolderName : " + newFolderName);
			saveContentAtPath = newFolderPath + "/" + newFolderName + "/" + indexFile;
			consoleLogger.debug("[ONSAVE] new save path : " + saveContentAtPath);
			xml["file-name"] = indexFile;
			consoleLogger.debug("[ONSAVE] setting file name to be " + indexFile);
		} else if (duplicate && contentAsFolder == 'false') {
			consoleLogger.debug("[ONSAVE] correcting path for file duplicate.");
			// if duplicate and content as file, remove file name from the path and fix file name 
			saveContentAtPath = saveContentAtPath.substring(0, saveContentAtPath.lastIndexOf("/"));
		} else if (create && contentAsFolder == 'true') {
			consoleLogger.debug("[ONSAVE] setting file name to be " + indexFile);
			// reset the file name to be index.xml for folders
			xml["file-name"] = indexFile;
		} 

		consoleLogger.debug("[ONSAVE] posting to write content: path (" +  saveContentAtPath + ")");
		
        if (!toDraft) {
            if(duplicate =='true') {saveContentAtPath += "/"+fileName};
            writeServiceUrl = "/cstudio/wcm/content/write-content" +
                    "?site=" + site +
                    "&path=" + saveContentAtPath +
                    "&user=" + userId +
                    "&unlock=" + unlock +
                    "&contentType=" + contentType;
        } else{
           writeServiceUrl = "/cstudio/wcm/preview/write-content" +
                    "?site=" + site +
                    "&path=" + saveContentAtPath +
                    "&fileName="+fileName+
                    "&user=" + userId +
                    "&unlock=" + unlock +
                    "&contentType=" + contentType;
        }
        
        
		if (rename) {
			writeServiceUrl += "&oldContentPath="+oldContentPath;
		}

        consoleLogger.error("[ONSAVE]: posting url (" +  writeServiceUrl + ")");
        							
		alfrescoRemote.post(writeServiceUrl, 
							xml.toString(), 
							params);
		
		consoleLogger.debug("on save: enableDeployToTestServer = " + this.enableDeployToTestServer);
		if(this.enableDeployToTestServer == 'true') {
			consoleLogger.debug("on save: deploying to test server");
			alfrescoRemote.get("/cstudio/wcm/deployment/deploy-to-preview?site="+site, submissionParameters.getParams());
		}
	};
	
	WcmController.prototype.setAuditTrailProperties = function(xml) {
	    if(this.auditTrailEnabled) {
    		xml.lastModifiedBy = user_id;
    	    xml.lastModifiedDate = _getUTCDateString(new Date());
	    }
		return xml;
	};

	WcmController.prototype.onCancel = function() {
	   if(edit == 'true') {
	       consoleLogger.debug("Cancelling changes");
	       alfrescoRemote.get("/cstudio/wcm/content/cancel-editing?site="+site+"&path="+path, submissionParameters.getParams());
	   }
	};
	
	/**
	 * WCM Dispatcher
	 */
	WcmController.prototype.dispatch = function() {
		if(phase) {
            consoleLogger.debug("Phase: "+phase);
			switch(phase) {
				case "beforeSave":
					// orbeon removes all the formatting whitepace in an xml document.
					// to bypass this we're encoding right before going to orbeon (submit/index.jsp) and unecoding right after (here)
					xml = new java.lang.String(""+ xml.toString());
			        xml = xml.replaceAll("~SN~","\n");
			        xml = xml.replaceAll("~SR~","\r");
			        xml = xml.replaceAll("~ST~","\t");	
			        
					var xmlAsE4x = new XML(this.handledRteEscapedElements(xml));
					xmlAsE4x = this.setAuditTrailProperties(xmlAsE4x);
					var onBeforeSaveResult = this.onBeforeSave(xmlAsE4x, submissionParameters) || xmlAsE4x;

					if(onBeforeSaveResult) {
						responseContainer.addParam("response", onBeforeSaveResult.toString());
					}					

					break;

				case "onSave":
					// orbeon removes all the formatting whitepace in an xml document.
					// to bypass this we're encoding right before going to orbeon (submit/index.jsp) and unecoding right after (here)
					xml = new java.lang.String(""+ xml.toString());
			        xml = xml.replaceAll("~SN~","\n");
			        xml = xml.replaceAll("~SR~","\r");
			        xml = xml.replaceAll("~ST~","\t");	
			        
					consoleLogger.debug("onSave: converting doc to E4x");
					var cleanXml = removeXmlDocType(xml);
					cleanXml = this.handleExcessNamespaces(cleanXml);
					var xmlAsE4x = new XML(cleanXml);
					
					consoleLogger.debug("call onSave");
					this.onSave(xmlAsE4x);
					break;

				case "onCancel" :
				    this.onCancel();
				    break;
					
				case "onLoad":
					this.onLoad();

					var dynamicInsertCode = 
					   CStudioFormsUtils.generateInsertsForDynamicInstances(
					   modelContainer);
					
					// Generate xforms:insert with unique values for any page
					consoleLogger.debug("Perform edit " + performEditFlag);
					
					dynamicInsertCode += CStudioFormsUtils.generateUniquePageId(this.config.instanceName);
					
					renderParameters.addParam(
					   "z_dynamicInsertModelCode", 
					   dynamicInsertCode);
					break;
				default:
					throw("unknown phase '" + phase + "'");
			}
		}
		else {
			throw("phase variable is not available'");
		}
	}; 

	/* ==================================== */
	// Controller Factories
	/* ==================================== */

	this.ControllerFactory = {
		
		newWcmController: function(config) {

			function newController(config) {
				WcmController.call(this, config);
			};
			
			newController.prototype = new WcmController;
			newController.prototype.constructor = newController;

			var instance = new newController(config);
			instance.parent = WcmController.prototype;
			instance.config = config;
			instance.parent.config = config;

			return instance;
		}
	};

	/* ==================================== */
	// public API
	/* ==================================== */

	var obj = {};
	obj.WcmController = WcmController;
	obj.ControllerFactory = ControllerFactory;
	obj.dispatch = controller.prototype.dispatch;
	
	return obj;
}();

/**
 * library of javascript based capabilities for the forms system
 */
CStudioFormsUtils = new Object();

/**
 * basic controller for AVM based content
 */
CStudioFormsUtils.executeBasicWcmOnLoadFormController = function(contentType, primaryInstanceName) {
		var instanceName = (primaryInstanceName) ? primaryInstanceName : "instance";
		
		// load content
		var isEdit = CStudioFormsUtils.hasWritePermission(siteId, contentId, contentPath, currentUserId);
		var primaryContentInst = loadWcmAvmContentInstance(siteId, contentId, contentPath, contentType, performEditFlag,draft,changeTemplate, isEdit);

		var submissionHandlers = null;
		// get wcm standard submission handlers
		submissionHandlers = CStudioFormsUtils.getStandardAvmRepositorySubmissionHanders(
			formId,  
			instanceName, 
			siteId,
			currentUserId,
			primaryContentInst.type,
			primaryContentInst.path,
			currentAlfTicket,
            draft, performEditFlag);


		var loadHandlers = CStudioFormsUtils.getStandardAvmRepositoryLoadHandlers(
			formId,  
			instanceName, 
			siteId,
			currentUserId,
			primaryContentInst.type,
			primaryContentInst.path,
			currentAlfTicket);
		
		// add models
		modelContainer.addModel(instanceName, primaryContentInst.dom);
        
		renderParameters.addParam("contentPath", primaryContentInst.path);
		renderParameters.addParam("contentType", primaryContentInst.type);
		renderParameters.addParam("site", siteId);
		renderParameters.addParam("edit", isEdit);
        renderParameters.addParam("readonly", !isEdit);
		renderParameters.addParam("standardRepoSubmissionHanders11", submissionHandlers);
		renderParameters.addParam("standardRepoLoadHandlers", loadHandlers);
		
};

/**
* check if the content should be retrieved as read only mode. (no write permission)
* added in Crafter EXT 1.5.0
*/
CStudioFormsUtils.hasWritePermission = function(siteId, contentId, contentPath, userId) {
	var isLocked = false;

	if(contentId && contentId != null) { 
		var getContentUrl = "/cstudio/wcm/content/get-item?" +
							"&path=" + contentId + 
							"&site=" + siteId;
		
		var result = alfrescoRemote.get(getContentUrl);
		var obj = eval("(" + result + ")");

		if(obj) {
			if(obj.item) {
				if(obj.item.lockOwner) {
					if(obj.item.lockOwner != "" && obj.item.lockOwner != userId) {
						isLocked = true;
					}					
				}
			}
		}
	}
		
	if(isLocked == false) {
		var loadPermissionsUrl = "/cstudio/permission/get-user-permissions?" +
			"&path=" + contentPath + "&site=" + siteId + "&user=" + userId;
		result = alfrescoRemote.get(loadPermissionsUrl);
		consoleLogger.debug("Got user Permission: ");
		consoleLogger.debug(result);
		
		obj = eval("(" + result + ")");
		var permissions = obj.permissions;
		var isWrite = false;
		var isNotAllowed = false;
		for (var i = 0; i < permissions.length; i++) {
			if (permissions[i].permission == "not allowed") {
				isNotAllowed = true;
			} 
			if(permissions[i].permission == "write") {
				isWrite = true;
			} 
		}
		return isWrite && !isNotAllowed;
	}
	return false;
};

/**
 * return insert statements for models in the model container
 */
CStudioFormsUtils.generateInsertsForDynamicInstances = function(modelContainer) {
	var code = "";
	var modelKeySetItr = modelContainer.getModelNames().iterator();
	
	while(modelKeySetItr.hasNext()) {
		var modelKey = modelKeySetItr.next();

		code += "<xforms:insert ev:event='xforms-ready' " +
	              "nodeset=\"instance('" + modelKey + "')\" " +
	              "origin=\"xxforms:get-request-attribute('" + modelKey + "')\" />";
	}
	
	return code;
};

/**
 * Generate unique page id by quering the sequence service
 * TODO: Query sequence service only if we need to. Check xml if pageId node exists already                           .;-=
 */
CStudioFormsUtils.generateUniquePageId = function(instanceName, namespace) {
    var _namespace = namespace || "crafter-object";
	//var pageId = alfrescoRemote.get("/cstudio/sequence/next?namespace=" + _namespace + "&create=true");
    var ids = alfrescoRemote.get("/cstudio/sequence/contentitemid/next");
    var obj = eval("(" + ids + ")");
    var pageId = obj.pageId;
	var pageIdGroup = obj.pageGroupId;
	renderParameters.addParam("pageId", pageId);
    renderParameters.addParam("pageIdGroup", pageIdGroup);
    var codeFirst = renderParameters.getParam("standardRepoSubmissionHanders11");
    var code2 = renderParameters.getParam("standardRepoSubmissionHandersCode2");
    codeFirst = 
    	codeFirst +
    	"<pageId>" + pageId + "</pageId>" +
		"<pageIdGroup> " + pageIdGroup +  "</pageIdGroup>"+
		code2;
	renderParameters.addParam("standardRepoSubmissionHanders",codeFirst);
    var code =      "<xforms:action ev:event=\"xforms-ready\" if=\"not(exists(instance('" + instanceName + "')/pageId))\" >";
        code += 	"<xforms:insert nodeset=\"instance('" + instanceName + "')/*[1]\" origin=\"xxforms:element('pageId')\" />";
    	code += 	"<xforms:insert nodeset=\"instance('" + instanceName + "')/*[1]\" origin=\"xxforms:element('pageIdGroup')\" />";
        code +=     "<xforms:setvalue ref=\"instance('" + instanceName + "')/pageId\" value=\"instance('cs-formcontrol')/pageId\" />";
        code +=     "<xforms:setvalue ref=\"instance('" + instanceName + "')/pageIdGroup\" value=\"instance('cs-formcontrol')/pageIdGroup\" />";
        code +=     "</xforms:action>";
             
    return code;
};


/**
 * generate html for the event and the submission handler
 */
CStudioFormsUtils.getStandardRepositorySubmissionHanderHTML = function(instanceName, serviceUrl, action, validate, replace,successJs, failJs) {
	return "" +
			"<xforms:submission " +
 				"id='"+action+"-submission' " +
 				"ref='instance(\"" +  instanceName + "\")' " + 
				"validate='" + validate + "' " +
				"action=\""+ serviceUrl +"\" " +
				"method='post' " +
				"replace='"+replace+"'>" +
				
				"<xforms:action ev:event='xforms-submit-done'> " + 
					"<xxforms:script ev:event='DOMActivate'> " +
						successJs +
					"</xxforms:script> " +
				"</xforms:action> " +

				 "<xforms:action ev:event='xforms-submit-error'> " +
					 "<xxforms:script ev:event='DOMActivate'> " +
						failJs +
					"</xxforms:script> " +
				"</xforms:action> " +
			"</xforms:submission> "; 
};

/**
 * generate html for the event and the submission handler
 */
CStudioFormsUtils.getStandardRepositoryRetrievalHanderHTML = function(instanceName, resourceUrl, action, replace) {
	return "" +
			"<xforms:submission " +
 				"id='"+action+"-submission' " +
 				"resource=\""+ resourceUrl +"\" " +
				"method='get' " +
				"instance=\""+ instanceName +"\" " +
				"replace='"+replace+"'>" +
				
				
			"</xforms:submission> "; 
};


/**
 * get standard repo load handlers
 */
CStudioFormsUtils.getStandardAvmRepositoryLoadHandlers = function(formId, instanceName, site, user, contentType, contentPath, ticket) {
	
	var loadPermissionsUrl = "/proxy/alfresco/cstudio/permission/get-user-permissions?" +
	"path={xxforms:get-request-attribute('contentPath')}" +
	"&amp;site=" + site +
	"&amp;user={xxforms:get-request-attribute('user')}" +
	"&amp;alf_ticket={xxforms:get-request-attribute('alf_ticket')}" +
	"&amp;format=xml";



	var loadPermissionsCode = CStudioFormsUtils.getStandardRepositoryRetrievalHanderHTML(
			"permissions", 
			loadPermissionsUrl, 
			"load-permissions", 
			"instance");
	
	
	
	var code =	"" + "<xforms:instance id='permissions'>" +
				"<permissions><permission>read</permission></permissions>" +
			
			     "</xforms:instance>" +
			
			"<xforms:send ev:event='xforms-model-construct-done' " +
					"submission='load-permissions-submission'/>" +
			
			loadPermissionsCode;
			
	return code;
	
};

/**
 * get standard WCM submission handlers
 */
CStudioFormsUtils.getStandardAvmRepositorySubmissionHanders = function(formId, instanceName, site, user, contentType, contentPath, ticket,draft,isEdit) {
	
	var loadModelData = "";
	if (isEdit) {
		loadModelData = "<xforms:submission id='load-instance' method='get' serialization='none' action=\"/cstudio-form/load-content&#63;site={xxforms:get-request-parameter('site')}&amp;path={xxforms:get-request-parameter('path')}&amp;draft={xxforms:get-request-parameter('draft')}&amp;changeTemplate={xxforms:get-request-parameter('changeTemplate')}&amp;edit={xxforms:get-request-parameter('edit')}&amp;refererHost={instance('cs-formcontrol')/refererHost}&amp;alf_ticket={xxforms:get-request-parameter('alf_ticket')}\" replace='instance' xxforms:instance='instance'>" +
		  	"<xforms:dispatch ev:event='xforms-submit-error' target='load-content-error-dialog' name='fr-show'/>" +
    		"</xforms:submission>"
	} else {
		loadModelData = "<xforms:submission id='load-instance' method='get' serialization='none' action=\"/cstudio-form/load-instance&#63;site={xxforms:get-request-parameter('site')}&amp;contentType={xxforms:get-request-parameter('contentType')}&amp;edit={xxforms:get-request-parameter('edit')}&amp;refererHost={instance('cs-formcontrol')/refererHost}&amp;alf_ticket={xxforms:get-request-parameter('alf_ticket')}\" replace='instance' xxforms:instance='instance'>" +
		  	"<xforms:dispatch ev:event='xforms-submit-error' target='load-content-error-dialog' name='fr-show'/>" +
    		"</xforms:submission>";
	}

	var preSaveSubmissionUrl = "{instance('cs-formcontrol')/refererHost}" + "/form-controller/submit/&#63;action=execute-controller" +
		"&amp;phase=beforeSave" +
		"&amp;draft={instance('cs-formcontrol')/renderParams/draft}"+		
		"&amp;duplicate={instance('cs-formcontrol')/renderParams/duplicate}"+
		"&amp;changeTemplate={instance('cs-formcontrol')/renderParams/changeTemplate}"+
		"&amp;user_id={instance('cs-formcontrol')/userId}" +
		"&amp;formId="+ formId +
		"&amp;xfromModify=true" +
		"&amp;path={instance('cs-formcontrol')/contentPath}" + 
		"&amp;oldContentPath={instance('cs-formcontrol')/oldContentPath}" + 				
		"&amp;contentType=" + contentType +
		"&amp;site=" + site +
		"&amp;siteId=" + site +
		"&amp;alf_ticket={instance('cs-formcontrol')/alfTicket}";

	var saveSubmissionUrl =  "{instance('cs-formcontrol')/refererHost}" + "/form-controller/submit/&#63;action=execute-controller" +
		"&amp;phase=onSave" +
		"&amp;draft={instance('cs-formcontrol')/renderParams/draft}"+
        "&amp;duplicate={instance('cs-formcontrol')/renderParams/duplicate}"+
        "&amp;changeTemplate={instance('cs-formcontrol')/renderParams/changeTemplate}"+
		"&amp;formId="+ formId +
		"&amp;path={instance('cs-formcontrol')/contentPath}" +
		"&amp;oldContentPath={instance('cs-formcontrol')/oldContentPath}" + 
		"&amp;contentType=" + contentType + 
		"&amp;xfromModify=true" +
		"&amp;unlock=true" +
		"&amp;site=" + site +
		"&amp;siteId=" + site +
		"&amp;userId={instance('cs-formcontrol')/userId}" +
		"&amp;alf_ticket={instance('cs-formcontrol')/alfTicket}";
				
	var previewSubmissionUrl = "{instance('cs-formcontrol')/refererHost}" + "/form-controller/submit/&#63;action=execute-controller" +
		"&amp;phase=onSave" +
		"&amp;draft={instance('cs-formcontrol')/renderParams/draft}"+
        "&amp;duplicate={instance('cs-formcontrol')/renderParams/duplicate}"+
        "&amp;changeTemplate={instance('cs-formcontrol')/renderParams/changeTemplate}"+
		"&amp;formId="+ formId +
		"&amp;path={instance('cs-formcontrol')/contentPath}" +
		"&amp;oldContentPath={instance('cs-formcontrol')/oldContentPath}" + 
		"&amp;contentType=" + contentType + 
		"&amp;xfromModify=true" +
		"&amp;unlock=false" +
		"&amp;site=" + site +
		"&amp;siteId=" + site +
		"&amp;userId={instance('cs-formcontrol')/userId}" +
		"&amp;alf_ticket={instance('cs-formcontrol')/alfTicket}";

	var cancelSubmissionUrl =  "{instance('cs-formcontrol')/refererHost}" + "/form-controller/submit/&#63;action=execute-controller" +
		"&amp;site=" + site +
		"&amp;draft={instance('cs-formcontrol')/renderParams/draft}"+
		"&amp;edit={instance('cs-formcontrol')/renderParams/edit}"+
		"&amp;formId="+ formId +
		"&amp;contentType=" + contentType +
		"&amp;path={instance('cs-formcontrol')/contentPath}" +
		"&amp;oldContentPath={instance('cs-formcontrol')/oldContentPath}" +
		"&amp;site=" + site +
		"&amp;siteId=" + site +
		"&amp;user_id={instance('cs-formcontrol')/userId}" +
		"&amp;alf_ticket={instance('cs-formcontrol')/alfTicket}" +
		"&amp;phase=onCancel";
		
	var preSaveSubmissionCode = CStudioFormsUtils.getStandardRepositorySubmissionHanderHTML(
		instanceName, 
		preSaveSubmissionUrl, 
		"pre-save", 
		true,
		"instance",
		"EntityAvmHack.updateEntity();",
		"");
		
	var saveSubmissionCode = CStudioFormsUtils.getStandardRepositorySubmissionHanderHTML(
		instanceName, 
		saveSubmissionUrl, 
		"save", 
		true,
		"instance",
		"CStudioForms.activeFormControl.saveSuccess();",
		"CStudioForms.activeFormControl.saveFailure();");

	var previewSubmissionCode = CStudioFormsUtils.getStandardRepositorySubmissionHanderHTML(
		instanceName, 
		previewSubmissionUrl, 
		"preview", 
		true,
		"instance",
		"CStudioForms.activeFormControl.previewSuccess();",
		"CStudioForms.activeFormControl.previewFailure();");

	var cancelSubmissionCode = CStudioFormsUtils.getStandardRepositorySubmissionHanderHTML(
		instanceName, 
		cancelSubmissionUrl, 
		"cancel", 
		false,
		"none",
		"CStudioForms.activeFormControl.cancelSuccess();",
		"CStudioForms.activeFormControl.cancelFailure();");
	var code =
	    "<xforms:instance id='cs-formcontrol'>" +
	       "<control>" +
	         "<contentPath></contentPath>"+
	         "<oldContentPath></oldContentPath>"+
	         "<alfTicket></alfTicket>"+
	         "<userId></userId>"+
	         "<refererHost></refererHost>"+
	         "<renderParams>" +
	         	"<site></site>" +
	         	"<path></path>" +
	         	"<id></id>" +
				"<readonly></readonly>" +
	         	"<draft></draft>"+
	         	"<duplicate></duplicate>"+
	         	"<changeTemplate></changeTemplate>"+
	         	"<edit></edit>"+
	         "</renderParams>";
	var code2 =         
	       "</control>" +
	    "</xforms:instance>"+
	    
		"<xforms:bind ref=\"instance('cs-formcontrol')/contentPath\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('contentPath')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/oldContentPath\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('contentPath')\"/>"+   
		"<xforms:bind ref=\"instance('cs-formcontrol')/alfTicket\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('alf_ticket')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/refererHost\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('baseAuthorUri')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/userId\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('user')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/site\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('site')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/path\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('contentPath')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/id\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('id')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/readonly\" type=\"xs:boolean\" " +
           "xxforms:default=\"xxforms:get-request-parameter('readonly')\"/>"+   
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/draft\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('draft')\"/>"+
		"<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/edit\" " + 
		   "xxforms:default=\"xxforms:get-request-parameter('edit')\"/>"+
        "<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/duplicate\" " +
		   "xxforms:default=\"xxforms:get-request-parameter('duplicate')\"/>"+
         "<xforms:bind ref=\"instance('cs-formcontrol')/renderParams/changeTemplate\" " +
		   "xxforms:default=\"xxforms:get-request-parameter('changeTemplate')\"/>"+
		
		"<xforms:action ev:event='submit-save'>" +
			"<xxforms:script>" +
			" CStudioForms.inSave = true; " +
			" CStudioForms.FormEvent.fireOnSaveCallbacks();"+
			"</xxforms:script>" +
			"<xforms:send submission='pre-save-submission' />" +
			"<xxforms:script>" +
			" CStudioForms.FormEvent.fireOnSaveCallbacks();"+
			"</xxforms:script>" +
			"<xforms:send submission='save-submission' />" +
		"</xforms:action>" +


		"<xforms:action ev:event='submit-preview'>" +
			"<xxforms:script>" +
			" CStudioForms.FormEvent.fireOnSaveCallbacks();"+
			"</xxforms:script>" +
			"<xforms:send submission='pre-save-submission' />" +

			"<xxforms:script>" +
			" CStudioForms.FormEvent.fireOnSaveCallbacks();"+
			"</xxforms:script>" +
			"<xforms:send submission='preview-submission' />" +
		"</xforms:action>" +	

		"<xforms:action ev:event='submit-cancel'>" +
			"<xforms:send submission='cancel-submission' />" +
		"</xforms:action>" +
		
		preSaveSubmissionCode +	
		saveSubmissionCode +
		previewSubmissionCode +
		cancelSubmissionCode +
		loadModelData;

		code2 += "{z_dynamicInsertModelCode}";
		
		renderParameters.addParam("standardRepoSubmissionHandersCode2", code2);
  
	return code;
};


//TODO: Refactor this into a separate js file
var dateFormat = function () {
	var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
		timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
		timezoneClip = /[^-+\dA-Z]/g,
		pad = function (val, len) {
			val = String(val);
			len = len || 2;
			while (val.length < len) val = "0" + val;
			return val;
		};

	// Regexes and supporting functions are cached through closure
	return function (date, mask, utc) {
		var dF = dateFormat;

		// You can't provide utc if you skip other args (use the "UTC:" mask prefix)
		if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
			mask = date;
			date = undefined;
		}

		// Passing date through Date applies Date.parse, if necessary
		date = date ? new Date(date) : new Date;
		if (isNaN(date)) throw SyntaxError("invalid date");

		mask = String(dF.masks[mask] || mask || dF.masks["default"]);

		// Allow setting the utc argument via the mask
		if (mask.slice(0, 4) == "UTC:") {
			mask = mask.slice(4);
			utc = true;
		}

		var	_ = utc ? "getUTC" : "get",
			d = date[_ + "Date"](),
			D = date[_ + "Day"](),
			m = date[_ + "Month"](),
			y = date[_ + "FullYear"](),
			H = date[_ + "Hours"](),
			M = date[_ + "Minutes"](),
			s = date[_ + "Seconds"](),
			L = date[_ + "Milliseconds"](),
			o = utc ? 0 : date.getTimezoneOffset(),
			flags = {
				d:    d,
				dd:   pad(d),
				ddd:  dF.i18n.dayNames[D],
				dddd: dF.i18n.dayNames[D + 7],
				m:    m + 1,
				mm:   pad(m + 1),
				mmm:  dF.i18n.monthNames[m],
				mmmm: dF.i18n.monthNames[m + 12],
				yy:   String(y).slice(2),
				yyyy: y,
				h:    H % 12 || 12,
				hh:   pad(H % 12 || 12),
				H:    H,
				HH:   pad(H),
				M:    M,
				MM:   pad(M),
				s:    s,
				ss:   pad(s),
				l:    pad(L, 3),
				L:    pad(L > 99 ? Math.round(L / 10) : L),
				t:    H < 12 ? "a"  : "p",
				tt:   H < 12 ? "am" : "pm",
				T:    H < 12 ? "A"  : "P",
				TT:   H < 12 ? "AM" : "PM",
				Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
				o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
				S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
			};

		return mask.replace(token, function ($0) {
			return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
		});
	};
}();

// Some common format strings
dateFormat.masks = {
	"default":      "ddd mmm dd yyyy HH:MM:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "mmm d, yyyy",
	longDate:       "mmmm d, yyyy",
	fullDate:       "dddd, mmmm d, yyyy",
	shortTime:      "h:MM TT",
	mediumTime:     "h:MM:ss TT",
	longTime:       "h:MM:ss TT Z",
	isoDate:        "yyyy-mm-dd",
	isoTime:        "HH:MM:ss",
	isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
	isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

// Internationalization strings
dateFormat.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	]
};

// For convenience...
_dateformat = function (mask, utc) {
	return dateFormat(this, mask, utc); 
};

_getUTCDateString = function(date) {
	if(!date) {
		date = new Date();
	}
	
	return date.getUTCFullYear() + '-' + _padding(date.getUTCMonth()+1) + '-' + _padding(date.getUTCDate()) + 'T' + _padding(date.getUTCHours()) + ':' + _padding(date.getUTCMinutes()) + ':' + _padding(date.getUTCSeconds()) + '-00:00';
};

_padding = function (str) {
	var format = "00";
	str = str + "";
	var prefix = format.substr(0, (2 - str.length));
	return prefix + str;
};