var YDom = YAHOO.util.Dom;
var YEvent = YAHOO.util.Event;


/**
 * PreviewSync
 */
CStudioAuthoring.ContextualNav.AdminConsole = CStudioAuthoring.ContextualNav.AdminConsole || {

	/**
	 * initialize module
	 */
	initialize: function(config) {

		if(config.name == "admin-console") {			
			if(config && config.params && config.params.roles) {
				var roles = (config.params.roles.length) ? config.params.roles : [config.params.roles.role];

				CStudioAuthoring.Service.lookupAuthoringRole(CStudioAuthoringContext.site, CStudioAuthoringContext.user, {
					success: function(userRoles) {
						var allowed = false;
						var userRoles = userRoles.roles;
						
						for(var j=0; j < userRoles.length; j++) {
							var userRole = userRoles[j];
						
							for(var i=0; i < roles.length; i++) {
								var role = roles[i];
							
								if(userRole == role) {
									allowed = true;
									break;
								}
							}
						}

						if(allowed == true) {
							this.initialized = true;	
							var dropdownInnerEl = config.containerEl;
				
							var parentFolderEl = document.createElement("div");
							var parentFolderLinkEl = document.createElement("a");
							parentFolderEl.appendChild(parentFolderLinkEl);
							YDom.addClass(parentFolderLinkEl, "acn-admin-console");
				
							parentFolderLinkEl.id = "admin-console";
							parentFolderLinkEl.innerHTML = "Admin Console";
							parentFolderLinkEl.onclick = function() {
                            document.location = CStudioAuthoringContext.authoringAppBaseUri +
				"/page/site/" + CStudioAuthoringContext.site + "/cstudio-admin-console";     
                            };
							
							dropdownInnerEl.appendChild(parentFolderEl);
						}
												
					},
					failure: function() {
						
					}
				});
			}			
		}
	}
}

CStudioAuthoring.Module.moduleLoaded("admin-console", CStudioAuthoring.ContextualNav.AdminConsole);
