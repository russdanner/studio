//<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

//var siteId = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
//var adminEmail =  CStudioAuthoring.Service.determineAdminEmailAddress(siteId);
// instead, setting from properties.  
//model.adminEmail = adminEmail;
model.currentYear = new Date().getFullYear().toString();
