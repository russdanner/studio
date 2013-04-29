<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var site = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);


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
var liveServer = "";

if (site != undefined) {
	authoringServer = site.authoringUrl;
	formServerUrl = site.formServerUrl;
	previewServer = site.previewUrl;
	liveServer = site.liveUrl;
	collabSandbox = site.collabSandbox;
	webprojId = site.webProject;
	cookieDomain = site.cookieDomain;
} 

model.userId = user.id;
model.authRole = role;
model.siteId = siteId;
model.authoringServer = authoringServer;
model.formServerUrl = formServerUrl;
model.previewServer = previewServer;
model.liveServer = liveServer;
model.cookieDomain = cookieDomain;