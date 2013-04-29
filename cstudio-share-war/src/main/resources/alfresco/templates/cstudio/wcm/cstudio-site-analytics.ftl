<#import "import/custom-cstudio-alfresco-template.ftl" as template />
<#import "../../org/alfresco/import/alfresco-layout.ftl" as layout />

<@template.header "transitional">
   <#-- allow theme to be specified in url args - helps debugging themes -->
   <#assign theme = (page.url.args.theme)!theme />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/dashboard-presentation.css" />  
   
   <div id="hd">
      <@region id="cstudio-header" scope="global" protected=true />
   </div>    
</@>

<@template.body>
    <div id="bd"> 

       <@region id="cstudio-dashboard-title" scope="global" protected=true />

       <@layout.grid gridColumns gridClass "component" />
    </div>
</@>

<@template.footer>
  
    <div id="ft">
       <@region id="cstudio-footer" scope="global" protected=true />
    </div>
</@>
