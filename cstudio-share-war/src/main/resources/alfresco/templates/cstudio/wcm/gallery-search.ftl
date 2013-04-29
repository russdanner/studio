<#import "/cstudio/wcm/import/cstudio-xforms-template.ftl" as template />
<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />

<@template.header "transitional">
   <#-- allow theme to be specified in url args - helps debugging themes -->
   <#assign theme = (page.url.args.theme)!theme /> 
   
   <script type="text/javascript" src="${url.context}/yui/animation/animation-min.js"></script>
   <script type="text/javascript" src="${url.context}/themes/cstudioTheme/js/global.js"></script>
   <script type="text/javascript" src="${url.context}/components/cstudio-form/swfobject.js"></script>

   <script type="text/javascript" src="${url.context}/components/cstudio-common/common-api.js"></script>
   <script type="text/javascript" src="${url.context}/components/cstudio-search/gallery-search.js"></script>

	<!-- filter templates -->
   <script type="text/javascript" src="${url.context}/components/cstudio-search/filters/common.js"></script>
   <script type="text/javascript" src="${url.context}/components/cstudio-search/filters/default.js"></script>
	<#list searchFilterTemplates as filterTemplate>
       <script type="text/javascript" src="${filterTemplate}"></script> 	
	</#list>

	<!-- result templates -->
    <script type="text/javascript" src="${url.context}/components/cstudio-search/results/default.js"></script>
	<#list searchResultTemplates as resultTemplate>
       <script type="text/javascript" src="${resultTemplate}"></script> 	
	</#list>

   <link href="${url.context}/themes/cstudioTheme/css/icons.css" type="text/css" rel="stylesheet">
   <link href="${url.context}/yui/container/assets/container.css" type="text/css" rel="stylesheet">
   <link href="${url.context}/themes/cstudioTheme/css/gallery.css" type="text/css" rel="stylesheet">
</@>

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
		
		<#include "/org/craftercms/common/mashlets/overlay/common-hook.ftl" >
	</script>

<@template.body>
	<div class="cstudio-wcm-gallery-wrapper">
		<div id="cstudio-wcm-search-wrapper">
			<div id="cstudio-wcm-search-main cstudio-wcm-gallery-search-main">
				<div id="cstudio-wcm-search-search-title" class="cstudio-wcm-searchResult-header cstudio-wcm-galleryResult-header"></div>

				<div id="cstudio-wcm-gallery-filters" class="cstudio-wcm-gallery-filters">
					<div id="cstudio-wcm-search-filter-controls" style="display:none;"></div>

					<div style="clear:both;"></div>
					<input type="hidden" id="cstudio-wcm-search-presearch"  value="true" />

					<div>
						<div class="cstudio-wcm-gallery-filters-box">
							<div class="cstudio-wcm-gallery-filters-type">
								Keywords:
							</div>
							<div class="cstudio-wcm-gallery-filters-value">
								<input type="text" name="keywords" id="cstudio-wcm-search-keyword-textbox"  value="${keywords}"/>
							</div>
						</div>

						<div class="cstudio-wcm-gallery-filters-box">
							<div class="cstudio-wcm-gallery-filters-type">
								Type:
							</div>
							<div class="cstudio-wcm-gallery-filters-value">
								<select id="cstudio-wcm-search-type-dropdown" name="cstudio-wcm-search-type-dropdown">
									<option value="gallery">Image</option>
									<option value="gallery-flash">Flash</option>
								</select>
							</div>
						</div>

						<div class="cstudio-wcm-gallery-filters-box">
							<div class="cstudio-wcm-gallery-filters-type">
								Time:
							</div>
							<div class="cstudio-wcm-gallery-filters-value">
								<select id="cstudio-wcm-search-time-dropdown" name="cstudio-wcm-search-time-dropdown">
									<option value="all">All</option>
									<option value="last-24-hours">Last 24 hours</option>
									<option value="last-week">Last Week</option>
								</select>
							</div>
						</div>

						<div class="cstudio-wcm-gallery-search-button">
							<input type="button" id="cstudio-wcm-search-button" value="Search">
						</div>
					</div>
				</div>

				<div id="cstudio-wcm-gallery-results" class="cstudio-wcm-gallery-results">
					<div id="cstudio-wcm-search-result-header" class="cstudio-wcm-gallery-search-results-header">
						<div id="cstudio-wcm-search-result-header-container" class="cstudio-wcm-gallery-header-container">
							<span class="cstudio-wcm-search-result-header cstudio-wcm-gallery-search-results-heading">Search Results</span>
							<span id="cstudio-wcm-search-message-span"></span>			
							<span id="cstudio-wcm-search-result-header-count"></span>
							<a id="cstudio-wcm-search-description-toggle-link" href="javascript:void(0)" onClick="CStudioSearch.toggleResultDetail(CStudioSearch.DETAIL_TOGGLE);"></a>
							<span class="cstudio-wcm-search-result-header-pagination cstudio-wcm-gallery-result-header-pagination"> 
								Show:<input type="text" 
											class="cstudio-wcm-search-result-header-pagination-textbox cstudio-wcm-gallery-result-header-pagination-textbox" 
											maxlength="3" 
											value="20"
											id="cstudio-wcm-search-item-per-page-textbox"
											name="total"/>
							</span>
							<span class="cstudio-wcm-search-result-header-sort cstudio-wcm-gallery-result-header-sort">
								Sort:<select id="cstudio-wcm-search-sort-dropdown" name="sortBy">
								<!-- items added via ajax -->
								</select>
							</span>
						</div>
					</div>

					<div id="cstudio-wcm-search-result" class="cstudio-wcm-gallery-result">
					   <div id="cstudio-wcm-search-result-in-progress" class="cstudio-wcm-search-result-in-progress-img"></div>
						&nbsp;	
					</div>

					<div class="cstudio-wcm-search-pagination cstudio-wcm-result-pagination cstudio-wcm-gallery-result-pagination">
						<div id="cstudio-wcm-search-pagination-controls"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</@>