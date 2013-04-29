<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
var user = CStudioAuthoring.Service.determineCurrentUser();
var role = CStudioAuthoring.Service.determineAuthoringRoleForUser(siteId, user.id);
var site = CStudioAuthoring.Service.getSite(siteId);

var authoringServer = "";
var previewServer = "";
var collabSandbox = "";
var webprojId = "";
var cookieDomain = "";
var openSiteDropdown = "false";
var formServerUrl = "";

if (site != undefined) {
	authoringServer = site.authoringUrl;
	previewServer = site.previewUrl;
	collabSandbox = site.collabSandbox;
	webprojId = site.webProject;
	cookieDomain = site.cookieDomain;
	openSiteDropdown = "" + site.openSiteDropdown;
	formServerUrl =  site.formServerUrl;
} 


model.userId = user.id;
model.authRole = role;
model.siteId = siteId;
model.collabSandbox = collabSandbox;
model.authoringServer = authoringServer;
model.previewServer = previewServer;
model.cookieDomain = cookieDomain;
model.openSiteDropdown = openSiteDropdown;
model.formServerUrl = formServerUrl;