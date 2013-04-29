<#import "/cstudio/wcm/import/cstudio-xforms-template.ftl" as template />
<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />

<@template.header "transitional">
   <#-- allow theme to be specified in url args - helps debugging themes -->
   <#assign theme = (page.url.args.theme)!theme /> 

		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/global.css" />
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/rte.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/skin.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/console.css" />	
	   	   
		<script type="text/javascript" src="${url.context}/yui/animation/animation-min.js"></script>
		<script type="text/javascript" src="${url.context}/yui/element/element-min.js"></script>
		<script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop-min.js"></script>
		<script type="text/javascript" src="${url.context}/yui/resize/resize-min.js"></script>
		
		<script type="text/javascript" src="${url.context}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>

        <script type="text/javascript" src="${url.context}/modules/editors/tiny_mce/tiny_mce.js"></script>

	   <script type="text/javascript" src="${url.context}/themes/cstudioTheme/js/global.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-common/common-api.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-common/amplify-core.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-admin/base.js"></script>	



		<link rel="stylesheet" type="text/css" href="${url.context}/yui/fonts/fonts-min.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/grids/grids-min.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/resize/assets/skins/sam/resize.css" />

	</@>
	 
	<@template.body>
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
			cookieDomain: "${cookieDomain}",
			isAuthoringConsole: true
		};
		    <#include "/org/craftercms/common/mashlets/overlay/common-hook.ftl" >

	</script>
		<div id="admin-console" class="categories-panel-active"></div>
	</@>