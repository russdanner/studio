<#import "alfresco-common.ftl" as common />
<#--
   Template "head" macro.
   Includes preloaded YUI assets and essential site-wide libraries.
-->                                                                           
<#assign DEBUG=false>

<#macro header doctype="strict">
<#if doctype = "strict">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#else>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
</#if>
<#-- allow theme to be specified in url args - helps debugging themes -->
<#assign theme = (page.url.args.theme)!theme />

<#-- Look up page title from message bundles where possible -->
<#assign pageTitle = page.title />
<#if page.titleId??>
<#assign pageTitle = (msg(page.titleId))!page.title />
</#if>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Crafter Studio</title>

<!-- Shortcut Icons -->
   <link rel="shortcut icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" /> 
   <link rel="icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" />

<!-- Site-wide YUI Assets -->
   <#if theme = 'default'>
   <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/default/skin.css" />
   <#else>
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/skin.css" />   
   </#if>
<#-- Selected components preloaded here for better UI experience. -->
<#if DEBUG>
<!-- Common YUI components: DEBUG -->
   <script type="text/javascript" src="${url.context}/yui/yahoo/yahoo-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/event/event-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/dom/dom-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/animation/animation-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/logger/logger-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/connection/connection-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/element/element-beta-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/get/get-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/yuiloader/yuiloader-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/button/button-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/container/container-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/menu/menu-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/json/json-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/selector/selector-debug.js"></script>
<#else>
<!-- Common YUI components: RELEASE -->
   <script type="text/javascript" src="${url.context}/yui/utilities/utilities.js"></script>
   <script type="text/javascript" src="${url.context}/yui/button/button-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/container/container-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/menu/menu-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/json/json-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/selector/selector-min.js"></script> 
   <script type="text/javascript" src="${url.context}/yui/connection/connection-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/element/element-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
   <link rel="stylesheet" type="text/css" href="${url.context}/yui//container/assets/skins/sam/container.css"/>
</#if>

<!-- Site-wide Common Assets -->
   <script type="text/javascript" src="${url.context}/themes/cstudioTheme/js/global.js"></script>
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/base.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard-presentation.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/presentation.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/global.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/contextNav.css"/>
   <script type="text/javascript" src="${url.context}/js/bubbling.v1.5.0.js"></script>
   <script type="text/javascript" src="${url.context}/js/flash/AC_OETags.js"></script>
   <script type="text/javascript" src="${url.context}/service/messages.js?locale=${locale}"></script>
   <script type="text/javascript" src="${url.context}/js/alfresco.js"></script>
   <script type="text/javascript" src="${url.context}/js/forms-runtime.js"></script>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants.DEBUG = ${DEBUG?string};
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_URI_RELATIVE = "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_FEED_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco-feed/";
      Alfresco.constants.THEME = "cstudioTheme";
      Alfresco.constants.URL_CONTEXT = "${url.context}/";
      Alfresco.constants.URL_PAGECONTEXT = "${url.context}/page/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
      Alfresco.constants.URL_FEEDSERVICECONTEXT = "${url.context}/feedservice/";
      Alfresco.constants.USERNAME = "${user.name!""}";
   //]]></script>
   <@common.uriTemplate />
   <@common.htmlEditor htmlEditor="tinyMCE"/>


<!-- Component Assets -->
${head}

<!-- Template Assets -->
<#nested>

<!-- MSIE CSS fix overrides -->
   <!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/ie6.css" /><![endif]-->
   <!--[if IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/ie7.css" /><![endif]-->
</head>
</#macro>

<#macro htmlEditorAssets>
   <!-- HTML Editor Assets -->
   <script type="text/javascript" src="${page.url.context}/modules/editors/tiny_mce/tiny_mce.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/editors/tiny_mce.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/editors/yui_editor.js"></script>      
</#macro>

<#--
   Template "body" macro.
   Pulls in main template body.
-->
<#macro body>
<body class="yui-skin-cstudioTheme">
   <div class="sticky-wrapper">
      <div id="doc3">
<#-- Template-specific body markup -->
<#nested>
      </div>
      <div class="sticky-push"></div>
   </div>
</#macro>


<#--
   Template "footer" macro.
   Pulls in template footer.
-->
<#macro footer>
   <div class="sticky-footer">
<#-- Template-specific footer markup -->
<#nested>
   </div>
<#-- This function call MUST come after all other component includes. -->
   <div id="alfresco-yuiloader"></div>
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents();
   //]]></script>
</body>
</html>
</#macro>
