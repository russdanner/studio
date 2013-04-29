<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
var role = CStudioAuthoring.Service.determineAuthoringRoleForUser(siteId, user.id);

model.previewServerBaseUrl = CStudioAuthoring.Service.determinePreviewServer(siteId);
model.widgetId = "recentlyMadeLive";
model.widgetName = "Recently Made Live";
model.widgetVisible = true;
model.showTotalRecordCount = false;
model.enableSetResultLimit = true;
model.collapsible=true;
