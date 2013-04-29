<#if model['../componentId']??>
	<#assign componentId = model['../componentId'] />
<#else>
	<#assign componentId = model['file-name']?substring(0, model['file-name']?length - 4) />
</#if>

<#if RequestParameters['preview']??>
	<#assign containerClass = 'crComponent' />	
<#else>
	<#assign containerClass = '' />
</#if>

<div id="o_${componentId}" class="${containerClass}">

<#if model['alignment'] == 'inline-block'>
	<#assign format = "display" />
<#else>
	<#if model['alignment'] == 'center'>
		<#assign format = "text-align" />
	<#else>
		<#assign format = "float" />
	</#if>
</#if>

<div style="${format}:${model['alignment']}; <#if model['alignment'] == 'center'>'clear:both';<#else>''</#if>;
padding-left:${model['paddingLeft']}px;padding-right:${model['paddingRight']}px;padding-top:${model['paddingTop']}px;padding-bottom:${model['paddingBottom']}px;">
	<div id="flashContent" class="formatFlashContent" 
	style="height:${model['fallbackImage /@height']}px; width:${model['fallbackImage /@width']}px;overflow:hidden;<#if model['alignment'] == 'center'>'margin:0 auto;text-align:center'<#else>''</#if>">
	
		<#if RequestParameters['preview']??>
			<div title="${model['fallbackAtlText']}"><img src="${model['fallbackImage']}"/></div>
		<#else>
			<object id="object${componentId}" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="${model['fallbackImage /@width']}" height="${model['fallbackImage /@height']}">
				<param name="movie" value="${model['flashFile']}" />
				<param name="wmode" value="transparent" />

				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${model['flashFile']}" width="${model['fallbackImage /@width']}" height="${model['fallbackImage /@height']}">
				<!--<![endif]-->
					<a href="${model['fallbackImageUrl']}" target="<#if model['urlOpensIn'] == 'true'>'_blank'<#else>'_self'</#if>">
					<img src="${model['fallbackImage']}" title="${model['fallbackAtlText']}" alt="${model['fallbackAtlText']}"/></a>
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
			 </object>
		
		</#if>
		<#-->
		 <c:choose>
		<c:when test="#{facesContext.externalContext.requestParameterMap.get('preview') != 'true'}">

			 <object id="object#{componentId}" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="#{componentRes['fallbackImage /@width']}" height="#{componentRes['fallbackImage /@height']}">
				<param name="movie" value="#{swfSrc}" />
				<param name="wmode" value="transparent" />

				<!--[if !IE]>-->
				<#-->
				<object type="application/x-shockwave-flash" data="#{swfSrc}" width="#{componentRes['fallbackImage /@width']}" height="#{componentRes['fallbackImage /@height']}">
				<!--<![endif]-->
				<#-->
					<a href="#{flash.fallbackImageUrl}" target="#{flash.urlOpensIn eq 'true' ? '_blank' : '_self'}">
					<img src="${flash.fallbackImage}" title="${flash.fallbackAtlText}" alt="${flash.fallbackAtlText}"/></a>
				<!--[if !IE]>-->
				<#-->
				</object>
				<!--<![endif]-->
				<#--
			 </object>
		 	
		</c:when>
		<c:otherwise>
			<div title="${flash.fallbackAtlText}"><img src="#{flash.fallbackImage}"/></div>
		</c:otherwise>
		</c:choose>
		<-->

	</div>
    <script>
		swfobject.registerObject("object${componentId}", "9.0.115", "expressInstall.swf");

	</script>
</div>
</div>	
