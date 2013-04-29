
	<script>
		/**
		 * contextual variables 
		 * note: these are all fixed at the moment but will be dynamic
		 */
		CStudioAuthoringContext = {
			user: "${userId}",
			role: "${authRole}", 
			site: "${siteId}",
			baseUri: "${page.url.context}",
			authoringAppBaseUri: "${authoringServer}",
			formServerUri: "${formServerUrl}",
			previewAppBaseUri: "${previewServer}",
			liveAppBaseUri: "${liveServer}",
			contextMenuOffsetPage: true,
			brandedLogoUri:"/proxy/alfresco/cstudio/services/content/content-at-path?path=/cstudio/config/app-logo.png",
			homeUri: "/page/site/${siteId}/dashboard",
			navContext: "default",
			cookieDomain: "${cookieDomain}"
		};
<#if mode == "act">	
    <#include "/org/craftercms/common/mashlets/overlay/common-hook.ftl" >
<#elseif mode == "select">
    
	YEvent.onAvailable("cstudio-command-controls", function() {
		CStudioAuthoring.Utils.addCss('/service/cstudio/wcm/preview/overlay/css.css?baseUrl=' +
											 CStudioAuthoringContext.baseUri);
											 
		var formControls = new CStudioAuthoring.CommandToolbar("cstudio-command-controls", true);
		
		formControls.addControl("formSaveButton", "Add Item", function() { 

			var searchId = CStudioAuthoring.Utils.getQueryVariable(document.location.search, "searchId");
			var crossServerAccess = false;
			
	  		try {
	  			// unfortunately we cannot signal a form close across servers
	  			// our preview is in one server
	  			// our authoring is in another
	  			// in this case we just close the window, no way to pass back details which is ok in some cases
	  			if(window.opener.CStudioAuthoring) { }
	  		}
	  		catch(crossServerAccessErr) {
	  			crossServerAccess = true;
	  		}
	
			if(window.opener && !crossServerAccess) {
				
	      		if(window.opener.CStudioAuthoring) {
		
		      		var openerChildSearchMgr = window.opener.CStudioAuthoring.ChildSearchManager;
	
		      		if(openerChildSearchMgr) {
		      		
		      			var searchConfig = openerChildSearchMgr.searches[searchId];
		      			
		      			if(searchConfig) {
		      				var callback = searchConfig.saveCallback;
	
		      				if(callback) {
				      			var selectedContentTOs = CStudioAuthoring.SelectedContent.getSelectedContent();
		
								openerChildSearchMgr.signalSearchClose(searchId, selectedContentTOs); 
		      				}
		      				else {
								//TODO PUT THIS BACK 
		      					//alert("no success callback provided for seach: " + searchId);
		      				}
		      			}
		      			else {
		      				alert("unable to lookup child form callback for search:" + searchId);
		      			}
		      		}
		      		else {   	 
		 				alert("unable to lookup parent context for search:" + searchId);
		      		}	      			
	       		}
				
				window.close();
			}
			else {
				// no window opening context or cross server call
				// the only thing we can do is close the window
				window.close();
			}
		});
	
		formControls.addControl("formSaveButton", "Cancel", function() { 
			window.close();
		});
	}); 
</#if>

	</script>

	<div id="cstudio-wcm-search-wrapper">	

		<div id="cstudio-wcm-search-main">				
			<div id="cstudio-wcm-search-search-title" class="cstudio-wcm-searchResult-header"></div>
			<div id="cstudio-wcm-search-filter-controls" style="width:250px; min-height:550px; background-color:white; float:left; padding: 20px 30px; border-radius: 5px; float: left; border: 1px #ccc solid; margin-bottom: 10px;";"></div>
			 
		    <div id="cstudio-wcm-search-result" style="width:69%; border-radius: 5px; float: left; border: 1px #ccc solid; margin-bottom: 10px;  margin-left: 10px; overflow:hidden; position: absolute; left: 340px;">
			   <div id="cstudio-wcm-search-result-in-progress" class="cstudio-wcm-search-result-in-progress-img"></div>
				&nbsp;	
			</div>
			<div style="clear:both"></div>

		</div>
	</div>  
