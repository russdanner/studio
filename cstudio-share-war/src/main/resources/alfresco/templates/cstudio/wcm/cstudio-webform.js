var showFormDef = (page.url.args.showFormDef) ? true : false;
model.showFormDef = showFormDef;

if (page.url.args.edit != undefined && page.url.args.edit == 'true')
{	
	model.isEdit = 'true';
}
else
{
	model.isEdit = 'false';
}


var site = CStudioAuthoring.Service.determineShareSite(page.url.templateArgs);
var siteObj = CStudioAuthoring.Service.getSite(site);

if(siteObj!=undefined){
    model.cookieDomain=siteObj.cookieDomain;
}

