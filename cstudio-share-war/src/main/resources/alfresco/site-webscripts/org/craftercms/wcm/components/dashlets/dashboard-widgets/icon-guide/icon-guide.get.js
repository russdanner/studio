<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
model.previewServerBaseUrl = CStudioAuthoring.Service.determinePreviewServer(siteId);
model.widgetId = "icon-guide";
model.widgetName = "Icon Guide";
model.widgetVisible = true;
model.showTotalRecordCount = false;
model.enableSetResultLimit = false;
model.collapsible=false;
