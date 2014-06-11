<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var site = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
var siteObj = CStudioAuthoring.Service.getSite(site);
if(siteObj!=undefined){
    model.cookieDomain=siteObj.cookieDomain;
    }
