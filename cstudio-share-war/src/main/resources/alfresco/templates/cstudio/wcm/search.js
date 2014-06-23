<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var site = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);

model.searchResultTemplates = CStudioAuthoring.Service.retrieveAvailableSearchResultTemplates(site);

model.searchFilterTemplates = CStudioAuthoring.Service.retrieveAvailableSearchFilterTemplates(site);

var siteObj = CStudioAuthoring.Service.getSite(site);

if(siteObj!=undefined){
    model.cookieDomain=siteObj.cookieDomain;
    }


