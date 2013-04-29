if (typeof CStudioAuthoring == "undefined" || !CStudioAuthoring) {
	var CStudioAuthoring = {};
};

/**
 * server side services
 */
CStudioAuthoring.Service = CStudioAuthoring.Service || {

	/**
	 * determine current user
	 */
	determineCurrentUser: function() {
		return context.user;
	},

	/**
	 * given a web project ID return the base HTTP address
	 * of the preview server
	 */
	getSite : function(shareSiteId) {
		var site;
		var respJson = remote.call("/cstudio/site/get-site?key="+shareSiteId);

		try {
			var result = eval("("+respJson+")");
			
			if (result) {
				site = result.site;
			}
		}
		catch(err) {
			// TODO: log error
		}
		return site;
	},
	
	
	/**
	 * determine the current user role
	 */
	determineAuthoringRoleForUser: function(site, user) {
		var respJson = remote.call("/cstudio/permission/get-user-roles?site=" + site + "&user=" + user);
		try{
			var contentResults = eval("(" + respJson + ")");
			var roles = contentResults.roles;
			var role = ""; // assume user is not a member of the site
			if (roles != undefined) {
				for (var i = 0; i < roles.length; i++) {
					if (roles[i] == "admin") {
						role = "admin";
						break;
					}  else if (roles[i] != "") {
						role = "author";
					} 
				}
			}
			return role;
		}
		catch(err) {
			return "error";
		}
				
	},
	
	/**
	 * determine the current share site
	 */
	determineShareSite: function(templateArgs) {
		return (templateArgs["site"] != undefined) ? templateArgs["site"] : "undefined";
	},
	
	/**
	 * given a share site determine which web project to use
	 */
	determineWebProject: function(shareSiteId) {
		// for now there is a 1-1, not so in the future
		return shareSiteId;
	},

	/**
	 * given a web project ID return the base HTTP address
	 * of the preview server
	 */
	determineAuthoringServer: function(shareSiteId) {
		
		var retServer = "undefined";
		
		var respJson = remote.call("/cstudio/site/get-authoring-server-url?site="+shareSiteId);

		try{
		
			var server = eval("("+respJson+")");
			
			if(server) {
				retServer = server.url;
			}
			else {
				retServer = "nourl, json: "+respJson;
			}
		}
		catch(err) {
			retServer = "error:" + err;
		}
				
		return retServer;
	},
	
	/**
	 * given a web project ID return the base HTTP address
	 * of the preview server
	 */
	determineFormServer: function(shareSiteId) {
		
		var retServer = "undefined";
		
		var respJson = remote.call("/cstudio/site/get-form-server-url?site="+shareSiteId);

		try{
		
			var server = eval("("+respJson+")");
			
			if(server) {
				retServer = server.url;
			}
			else {
				retServer = "nourl, json: "+respJson;
			}
		}
		catch(err) {
			retServer = "error:" + err;
		}
				
		return retServer;
	},
	
	
	/**
	 * given a web project ID  return the collaborative sandbox 
	 */
	determineCollabSandbox: function(shareSiteId) {
		
		var retServer = "undefined";
		
		var respJson = remote.call("/cstudio/site/get-collab-sandbox?site="+shareSiteId);

		try{
		
			var site = eval("("+respJson+")");
			
			if(server) {
				retServer = site.sandbox;
			}
			else {
				retServer = "no sandbox, json: "+respJson;
			}
		}
		catch(err) {
			retServer = "error:" + err;
		}
				
		return retServer;
	},
	
	/**
	 * given a web project ID return the base HTTP address
	 * of the live server
	 */
	determineLiveServer: function(shareSiteId) {
		
		var retServer = "undefined";
		
		var respJson = remote.call("/cstudio/site/get-live-server-url?site="+shareSiteId);

		try{
		
			var server = eval("("+respJson+")");
			
			if(server) {
				retServer = server.url;
			}
			else {
				retServer = "nourl, json: "+respJson;
			}
		}
		catch(err) {
			retServer = "error:" + err;
		}
				
		return retServer;
	},
	
	/**
	 * given a web project ID return the base HTTP address
	 * of the preview server
	 */
	determinePreviewServer: function(shareSiteId) {
		
		var retServer = "undefined";
		
		var respJson = remote.call("/cstudio/site/get-preview-server-url?site="+shareSiteId);

		try{
		
			var previewServer = eval("("+respJson+")");
			
			if(previewServer) {
				retServer = previewServer.url;
			}
			else {
				retServer = "nourl, json: "+respJson;
			}
		}
		catch(err) {
			retServer = "error:" + err;
		}
				
		return retServer;
	},

	/**
	 * given a web project ID return the Admin Email address
	 */
	determineAdminEmailAddress: function(shareSiteId) {
		
		var retEmail = "UNSET";
		
		var respJson = remote.call("/cstudio/site/get-admin-email-address?site="+shareSiteId);

		try{
		
			var adminEmail = eval("("+respJson+")");
			
			if(adminEmail && adminEmail.email && adminEmail.email.replace(/^\s+|\s+$/g, '') != '') {
				retEmail = adminEmail.email;
			}
		}
		catch(err) {
			//retEmail = "error:" + err;
		}
				
		return retEmail;
	},

	/**
 	 * retrieve go live queue items
 	 */
	retrieveGoLiveQueueItems: function(siteId) {

		var respJson = remote.call("/cstudio/wcm/workflow/get-go-live-items" + "?site=" + siteId);
	
		var items =  eval("("+respJson+")");

		return items;
	},

	/**
	 * given a site look up the available search templates
	 * TODO: build this service and move templates to alfresco
	 * The reason is, we don't want any domain specific code in the war -- deploying a new
	 * content type should not involve a new release
	 */
	retrieveAvailableSearchResultTemplates: function(siteId) {
		var results = new Array();
		results.push(page.url.context + '/components/cstudio-search/results/image.js');	
		results.push(page.url.context + '/components/cstudio-search/results/flash.js');
		
		return results;
	},

	/**
	 * given a site look up the available filter templates
	 * TODO: build this service and move templates to alfresco
	 * The reason is, we don't want any domain specific code in the war -- deploying a new
	 * content type should not involve a new release
	 */
	retrieveAvailableSearchFilterTemplates: function(siteId) {

		var results = new Array();
		results.push(page.url.context + '/components/cstudio-search/filters/javascript.js');		
		results.push(page.url.context + '/components/cstudio-search/filters/css.js');
		results.push(page.url.context + '/components/cstudio-search/filters/image.js');
		results.push(page.url.context + '/components/cstudio-search/filters/xhtml.js');
		results.push(page.url.context + '/components/cstudio-search/filters/flash.js');
		results.push(page.url.context + '/components/cstudio-search/filters/content-type.js');

		return results;
	}, 

	/**
	 * given a site look up the available gallery result templates
	 */
	retrieveAvailableGalleryResultTemplates: function(siteId) {
		var results = new Array();
		results.push(page.url.context + '/components/cstudio-search/results/gallery.js');	
		results.push(page.url.context + '/components/cstudio-search/results/gallery-flash.js');
		
		return results;
	},

	/**
	 * given a site look up the available gallery filter templates
	 */
	retrieveAvailableGalleryFilterTemplates: function(siteId) {
		var results = new Array();
		results.push(page.url.context + '/components/cstudio-search/filters/gallery.js');
		results.push(page.url.context + '/components/cstudio-search/filters/gallery-flash.js');

		return results;
	}, 

	encodeHTMLForSearch: function(val) {
		// HTML Encode 
		//return val.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
		// split join is faster
		return val.split("&").join("&amp;").split("<").join("&lt;").split(">").join("&gt;").split('"').join("&quot;");
	},
	
    /**
     * Figure out what cookie-domain we read from and write to.
     */
    determineCookieDomain: function() {
            var respJson = remote.call("/cstudio/site/get-cookie-domain");

            var result =  eval("(" + respJson + ")");
            var cookieDomain = result.cookieDomain;

            return cookieDomain;
    },
    /**
     * determine the current share site
     * This can use url.templateArgs or args.
     */
    determineShareSite: function(associativeArray) {
        return (associativeArray["site"] != undefined) ? associativeArray["site"] : "undefined";
    }
};	
