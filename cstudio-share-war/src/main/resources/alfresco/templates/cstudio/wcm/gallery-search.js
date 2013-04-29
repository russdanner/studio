<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var site = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);

model.searchResultTemplates = CStudioAuthoring.Service.retrieveAvailableGalleryResultTemplates(site);

model.searchFilterTemplates = CStudioAuthoring.Service.retrieveAvailableGalleryFilterTemplates(site);

var mode = page.url.args.mode;
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
model.mode = (mode) ? mode : "";
model.cookieDomain = cookieDomain;

// prepare keywords
var keywords  = page.url.args.keywords;
if(keywords == undefined || keywords == ''){
	model.keywords = '';		
} 
else{
	keywords = decodeURIComponent(keywords); 
	keywords = CStudioAuthoring.Service.encodeHTMLForSearch(keywords)
	model.keywords = keywords;
}

//prepare sortBy
var sortBy  = page.url.args.sortBy;
if(sortBy == undefined || sortBy == ''){
	model.sortBy = '';		
} 
else{
	model.sortBy = sortBy;
}
