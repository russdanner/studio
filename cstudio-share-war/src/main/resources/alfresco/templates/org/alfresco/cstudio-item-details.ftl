<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-details-panel.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard-presentation.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard-presentation.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/presentation.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/global.css" />

</@>

<@templateBody>

   <div id="hd">
      <@region id="cstudio-header" scope="global" protected=true />
   </div>

   <div id="bd">
      <div class="yui-gc">
         <div class="yui-u first">
                <br/><br/><br/>
               <@region id="web-preview" scope="template"/>
         </div>
      </div>

   </div>
</@>

<@templateFooter>
    <div id="ft">
       <@region id="cstudio-footer" scope="global" protected=true />
    </div>
</@>
