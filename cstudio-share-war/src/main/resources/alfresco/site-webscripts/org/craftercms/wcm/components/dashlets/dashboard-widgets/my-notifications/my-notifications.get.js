<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
model.previewServerBaseUrl = CStudioAuthoring.Service.determinePreviewServer(siteId);
model.widgetId = args.regionId;
model.widgetName = "My Notifications";
//model.widgetVisible = true;
model.widgetVisible = false; // disabled for initial launch
model.showTotalRecordCount = true;
model.enableSetResultLimit = true;
model.collapsible=false;
