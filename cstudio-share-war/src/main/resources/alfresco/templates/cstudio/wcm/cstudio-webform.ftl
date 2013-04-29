<#if showFormDef>
	<@component componentId="global.cstudio-webform" chrome="" chromeless=true />

<#else>
	<#import "/cstudio/wcm/import/cstudio-xforms-template.ftl" as template />
	<#import "/org/alfresco/import/alfresco-layout.ftl" as layout />
	 
	<@template.header "transitional">
	   <#-- allow theme to be specified in url args - helps debugging themes -->
	   <#assign theme = (page.url.args.theme)!theme />
	
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/global.css" />
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/css/xforms.css" />
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/rte.css" />	
	   <link rel="stylesheet" type="text/css" href="${url.context}/themes/cstudioTheme/yui/assets/skin.css" />		   	
		
	   <script type="text/javascript" src="${url.context}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
	   <script type="text/javascript" src="${url.context}/yui/animation/animation-min.js"></script>
	   <script type="text/javascript" src="${url.context}/themes/cstudioTheme/js/global.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-common/common-api.js"></script>
	   <script type="text/javascript" src="${url.context}/components/cstudio-form/cstudio-forms.js"></script>	
	</@>
	
	<@template.body>
	
	   <div id="xformbd"> 
	       
	    <@region id="cstudio-webform" scope="global" protected=true />
	    </div>
	    
	     <div  id="uploadContainer" style="display:none" >
		    	<form action="#" 
							enctype="multipart/form-data" 
							method="post" 
							id="uploadForm"
							name="uploadForm">
							
					<input type="file" name="uploadFile"></input>
					<input type="button" id="uploadButton"  class="cstudio-xform-button" value="Upload"></input>
					<input type="hidden" id="cstudio-xform-is-edit" value="${isEdit}" />
				</form>	
		</div>	    
	</@>
</#if>
