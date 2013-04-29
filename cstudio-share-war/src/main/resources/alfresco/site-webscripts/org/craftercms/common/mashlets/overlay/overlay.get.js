<import resource="classpath:alfresco/site-webscripts/org/craftercms/common/lib/common-server-api.js">

var contextNav = [];
var navcontext = determineContext(args["context"]); 
var site = CStudioAuthoring.Service.determineShareSite(url.templateArgs);

try {

	var contextNavModel = determineModel(site,navcontext);

	if (contextNavModel != null) {
		model.contextNavModel = contextNavModel;
	} else {
		model.contextNavModel = contextNav;
	}

} catch (e) {
	model.contextNavModel = "" + e;
}

/**
 * This method will reach out to alfresco for the context model but for now use
 * mock data
 * 
 * @param context
 * @return
 */
function determineModel(site,context) {
			
	try {
		
		//get contextual navigation
		var contextNavJson =remote.call("/cstudio/site/get-configuration?site="+encodeURIComponent(site) + "&path="+encodeURIComponent("/context-nav/contextual-nav.xml"));
							
		var config = eval('(' + contextNavJson + ')');
		
		var contexts = config.context;
		var retContext = [];
		
		if(!contexts.length && (contexts.name == "default" || context.name == navContext)) {
			retContext = contexts;
		}
		else {									 
			for(var i=0; i<contexts.length; i++) {
				var context = contexts[i];
				
				if(context.name == "default") {
					retContext = context;
				}
				else if(context.name == navContext) {
					retContext = context;
					break;
				}
			}
		}
				
		return retContext;			
	} 
	catch (err) { 
		if(contextNav == undefined) {
			contextNav = [];
		};
		
		return contextNav;
    }	
}

/**
 * In some cases context is passed in to this script explicitly, for example from an overly in a 
 * mash up use case (like preview server.)
 * In other cases it must be determined
 */
function determineContext(context) {
	
	// this seems to be the default at the moment 
	// -- we should change this to something more self documenting.
	var retContext = "/site/wcm/empty";

	if(context != undefined && context != null && context != "") {

 		retContext = context;
	}
	
	return retContext;
}
