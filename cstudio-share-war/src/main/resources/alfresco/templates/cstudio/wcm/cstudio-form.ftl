<#import "/cstudio/wcm/import/cstudio-xforms-template.ftl" as template />
<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />

<@template.header "transitional">
   <#-- allow theme to be specified in url args - helps debugging themes -->
   <#assign theme = (page.url.args.theme)!theme /> 

		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />

		<link rel="stylesheet" type="text/css" href="${url.context}/yui/menu/assets/skins/sam/menu.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/button/assets/skins/sam/button.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/calendar/assets/skins/sam/calendar.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/fonts/fonts-min.css" />
		<link rel="stylesheet" type="text/css" href="${url.context}/yui/container/assets/skins/sam/container.css" />
        <link rel="stylesheet" type="text/css" href="${url.context}/yui/editor/assets/skins/sam/editor.css" />

	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/global.css" />
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/rte.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/rte.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/skin.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/contextNav.css"/>


	   <script type="text/javascript" src="${url.context}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/event-delegate/event-delegate.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/animation/animation-min.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/element/element-min.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/container/container-min.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/menu/menu-min.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/button/button-min.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/calendar/calendar-min.js"></script>
       <script type="text/javascript" src="${url.context}/yui/datasource/datasource-min.js"></script>
	   <script type="text/javascript" src="${url.context}/modules/editors/tiny_mce/tiny_mce.js"></script>

	   <script type="text/javascript" src="${url.context}/themes/cstudioTheme/js/global.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-common/common-api.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-common/amplify-core.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-forms/forms-engine.js"></script>	
	</@>
	 
	<@template.body>
 	  <@region id="cstudio-form" scope="global" />
		<div id="formContainer"></div>
	</@>