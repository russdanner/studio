<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
model.previewServerBaseUrl = CStudioAuthoring.Service.determinePreviewServer(siteId);
model.widgetId = "MyRecentActivity";
model.widgetName = "My Recent Activity";
model.widgetVisible = true;
model.showTotalRecordCount = true;
model.enableSetResultLimit = true;
model.collapsible=false;
