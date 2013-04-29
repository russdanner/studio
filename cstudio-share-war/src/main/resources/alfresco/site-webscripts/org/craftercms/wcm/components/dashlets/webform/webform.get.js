<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
var user = CStudioAuthoring.Service.determineCurrentUser();
var role = CStudioAuthoring.Service.determineAuthoringRoleForUser(siteId, user.id);
var site = CStudioAuthoring.Service.getSite(siteId);

var authoringServer = "";
var formServerUrl = "";
var previewServer = "";
var collabSandbox = "";
var webprojId = "";
var cookieDomain = "";

if (site != undefined) {
	authoringServer = site.authoringUrl;
	formServerUrl = site.formServerUrl;
	previewServer = site.previewUrl;
	collabSandbox = site.collabSandbox;
	webprojId = site.webProject;
	cookieDomain = site.cookieDomain;
} 

model.userId = user.id;
model.authRole = role;
model.siteId = siteId;
model.collabSandbox = collabSandbox;
model.previewServer = previewServer;
model.authoringServer = authoringServer;
model.formServerUrl = formServerUrl;
model.cookieDomain = cookieDomain;

/* acquire url and environment values*/
var id = page.url.args.id;
var currentUser=context.user.id;

var formId = (page.url.args.form) ? page.url.args.form : null;
var editParam = (page.url.args.edit) ? page.url.args.edit : false;
var draft = (page.url.args.draft) ? page.url.args.draft : false;
var duplicate = (page.url.args.duplicate) ? page.url.args.duplicate : false;
var changeTemplate = (page.url.args.changeTemplate) ? page.url.args.changeTemplate : false;
var nodeRef = (page.url.args.noderef) ? page.url.args.noderef : null;
var contentPath = (page.url.args.path) ? page.url.args.path : null;
var contentType = (page.url.args.contentType) ? page.url.args.contentType : null;
var showFormDef = (page.url.args.showFormDef) ? true : false;
var showXmlWidgetFlag = (page.url.args.debugXml) ? true : false;
model.baseDir = (page.url.context) ? page.url.context : "";

model.showFormDef = showFormDef; 
model.webFormRendition = "<h1>Form Not Rendered</h1><br/>";  
 
var error = false;

/* error handling */
if(!formId) {
	error = true;
	model.webFormRendition += "Form ID parameter 'form' is required<br/>";
}

if(editParam) {
	if(!id && !nodeRef) {
		error = true;
		model.webFormRendition += "content id parameter ('id' | 'noderef') must be provided when edit is true <br/>";
	}
	
}

if(!error) {

	// make it easy to debug XML on the fly
	var xmlWidgetCode = '';
	if(showXmlWidgetFlag == true) {
		xmlWidgetCode = '<widget:xforms-instance-inspector xmlns:widget="http://orbeon.org/oxf/xml/widget"/>';
	}

	/* create render containers */ 
	var modelContainer = cstudioFormService.createModelContainer();
	var controllerParameters = cstudioFormService.createFormControllerParameters();
	var renderParameters = cstudioFormService.createFormRenderParameters();
	var alfTicket = cstudioApplicationService.currentApplicationContext().getTicket();
	
	/* set common render parameters */
	renderParameters.addParam("title", "Web Form");
	renderParameters.addParam("site", siteId);
	renderParameters.addParam("user", currentUser);
	renderParameters.addParam("action", "create");
	renderParameters.addParam("alf_ticket", alfTicket); 
	renderParameters.addParam("xml-inspector-widget", xmlWidgetCode);

	for(var key in page.url.args) {
		renderParameters.addParam(key, page.url.args[key]);
	}
	
	/* set up controller parameters */  
	controllerParameters.addParam("phase", "onLoad");
	controllerParameters.addParam("remote", remote);
	controllerParameters.addParam("formId", formId);
	controllerParameters.addParam("contentId", id);
	controllerParameters.addParam("siteId", siteId);
	controllerParameters.addParam("contentPath", contentPath);
	controllerParameters.addParam("contentType", contentType);
	controllerParameters.addParam("contentNodeRef", nodeRef);
	controllerParameters.addParam("performEditFlag", editParam);
	controllerParameters.addParam("draft", draft);
	controllerParameters.addParam("duplicate", duplicate);
	controllerParameters.addParam("changeTemplate", changeTemplate);
	controllerParameters.addParam("currentUserId", currentUser);
	controllerParameters.addParam("currentAlfTicket", alfTicket);
	controllerParameters.addParam("cstudioFormService", cstudioFormService);
	controllerParameters.addParam("modelContainer", modelContainer);
	controllerParameters.addParam("renderParameters", renderParameters);
	controllerParameters.addParam("status", status);
	controllerParameters.addParam("ctxJsUser",model.userId);
	controllerParameters.addParam("ctxJsAuthRole",model.authRole);
	controllerParameters.addParam("ctxJsSiteId",model.siteId);
	controllerParameters.addParam("ctxJsCollabSandbox",model.collabSandbox);
	controllerParameters.addParam("ctxJsPreviewServer",model.previewServer);
	controllerParameters.addParam("ctxJsAuthoringServer",model.authoringServer);
	controllerParameters.addParam("ctxJsFormServer",model.formServerUrl);
	controllerParameters.addParam("ctxJsCookierDomain",model.cookieDomain);
    controllerParameters.addParam("ctxJsBaseDir",model.baseDir);
	try {
		/**
		 * create rendition
		 */
		model.webFormRendition = ""+cstudioFormService.renderForm(formId, modelContainer, renderParameters, controllerParameters, showFormDef);
	}
	catch(err) {
		model.webFormRendition += err;
	}
}
