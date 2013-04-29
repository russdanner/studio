var YDom = YAHOO.util.Dom;
var YEvent = YAHOO.util.Event;


/**
 * PreviewSync
 */
CStudioAuthoring.ContextualNav.PreviewSync = CStudioAuthoring.ContextualNav.PreviewSync || {

	/**
	 * initialize module
	 */
	initialize: function(config) {

		if(config.name == "previewsync") {			
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
							parentFolderEl.style.paddingTop = "8px";
							var parentFolderLinkEl = document.createElement("a");
							parentFolderEl.appendChild(parentFolderLinkEl);
							YDom.addClass(parentFolderLinkEl, "acn-previewsync");
				
							parentFolderLinkEl.id = "previewsync";
							parentFolderLinkEl.innerHTML = "Preview Sync";
							parentFolderLinkEl.onclick = function() {
				                CStudioAuthoring.Service.previewServerSyncAll(CStudioAuthoringContext.site, {	success: function() { 
				                						alert("Preview server synch-all initiated.");
				                					}, 
				                					failure: function() {}
				                				});
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

CStudioAuthoring.Module.moduleLoaded("previewsync", CStudioAuthoring.ContextualNav.PreviewSync);
