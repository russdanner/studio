
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
			<div id="cstudio-wcm-search-filter-controls"></div>		
			<div style="clear:both;"></div>
			<br />
			<span>Keywords (optional):</span>
			<br />
			<input type="text" name="keywords" id="cstudio-wcm-search-keyword-textbox"  value="${keywords}"/>

			<input type="hidden" id="cstudio-wcm-search-presearch"  value="${presearch}" />
						
			<input type="button" id="cstudio-wcm-search-button" value="Search">		
			<div id="cstudio-wcm-search-result-header">
				<div id="cstudio-wcm-search-result-header-container">  			
					<span class="cstudio-wcm-search-result-header">Search Results</span>
					<span id="cstudio-wcm-search-message-span"></span>			
					<span id="cstudio-wcm-search-result-header-count"></span>
					<a id="cstudio-wcm-search-description-toggle-link" href="javascript:void(0)" onClick="CStudioSearch.toggleResultDetail(CStudioSearch.DETAIL_TOGGLE);"></a>
					
					<div class="filters">
						<div class="cstudio-wcm-search-result-header-pagination"> 
							Show:<input type="text" 
										class="cstudio-wcm-search-result-header-pagination-textbox" 
										maxlength="3" 
										value="20"
										id="cstudio-wcm-search-item-per-page-textbox"
										name="total"/>
						</div>
						<div class="cstudio-wcm-search-result-header-sort">
							Sort:<select id="cstudio-wcm-search-sort-dropdown" name="sortBy">
							<!-- items added via ajax -->
							</select>
						</div>
					</div>
				</div>
			</div>			
			<div id="cstudio-wcm-search-result">
			   <div id="cstudio-wcm-search-result-in-progress" class="cstudio-wcm-search-result-in-progress-img"></div>
				&nbsp;	
			</div>

			<div class="cstudio-wcm-search-pagination">
				<div id="cstudio-wcm-search-pagination-controls"></div>
			</div>
		

		</div>
	</div>  
