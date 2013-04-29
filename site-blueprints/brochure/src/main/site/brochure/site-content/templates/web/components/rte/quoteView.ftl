<#if model['../componentId']??>
	<#assign componentId = model['../componentId'] />
<#else>
	<#assign componentId = model['file-name']?substring(0, model['file-name']?length - 4) />
</#if>

<#if RequestParameters['preview']??>
	<#assign containerClass = '' />	
<#else>
	<#assign containerClass = '' />
</#if>

<div id="o_${componentId}" class="${containerClass}">
	<#if model['quoteStyle'] == 'wide'>
		<#assign quoteStyle = 'quoteComponentCenter' />
		<#assign quoteBottomText = 'quoteBottomText' />
	<#else>
		<#if model['quoteStyle'] == 'right-rail'>
			<#assign quoteStyle = 'quoteComponentRight' />
			<#assign quoteBottomText = 'quoteBottomText quoteBottomTextRight'/>
		<#else>
			<#assign quoteStyle = 'quoteComponent'/>
			<#assign quoteBottomText = 'quoteBottomText quoteBottomTextStandard'/>
		</#if>
	</#if>
	<#if model['alignment'] == 'inline-block'>
		<#assign format = 'display' />
	<#else>
		<#if model['alignment'] == 'center'>
			<#assign format = 'text-align' />
		<#else>
			<#assign format = 'float' />
		</#if>
	</#if>

	<div style="${format}:<#if model['quoteStyle'] != 'wide'>${model['alignment']}<#else></#if>;
			<#if model['alignment'] == 'center'>clear:both;<#else></#if>
			padding-left:${model['paddingLeft']}px;
			padding-right:${model['paddingRight']}px;
			padding-top:${model['paddingTop']}px;
			padding-bottom:${model['paddingBottom']}px;">
	<div class="${quoteStyle} formatQuote" style="<#if model['alignment'] == 'center'>margin:0 auto;text-align:left<#else></#if>">
		<div styleClass="quoteRightText" rendered="${model['quoteHeadline']}">
			${model['quoteHeadline']}
		<div>		
		<div class="quoteTopRight">
		    	<div class="quoteTopLeft">
				<div class="quoteTopCenter">
				</div>
			</div>
		</div>
	 	<div class="quoteCenterLeft">
	    	<div class="quoteCenterRight">
	        	<div class="quoteCenterContent_small">
				<div class="quoteCenterTextForQuote <#if model['quoteStyle'] = 'wide'>' quoteCenterTextELarge '<#else>' quoteCenterTextLarge'</#if>">
					${model['quote']}
				</div>
	        	</div>
	     	</div>
 	</div>
	<div class="quoteBottomRight">
		<div class="quoteBottomLeft">
			<div class="quoteBottomCenter"></div>
		</div>
	 </div>	    
	    <table class="quoteBottom">
		   <tbody>
			<tr>
				<td class="quoteBottomImage">
				<#if RequestParameters['preview']??>
					<#if model['thumbnail']=="">
						<img src="/static-assets/images/quote_thumbnail.png" >	
					<#else>
						<img src="${model['thumbnail']}" >
					</#if>
				<#else>
					<#if model['thumbnail']=="">
						<img src="/static-assets/images/quote_thumbnail.png" >	
					<#else>
						<img src="${model['thumbnail']}" >
					</#if>
				</#if>
				</td>
				<td class="${quoteBottomText}"><span>${model['name']}</span>
					<h:outputText value="${model['jobTitle']}" rendered="${model['jobTitle']}"/>
					<h:outputText value=", " rendered="${model['jobTitle']}"/>
					${model['company']}
				</td>
			</tr>
		   </tbody>
	    </table>
	    
      </div></div></div>
    </div>
    <script>
		jQuery(document).ready(function() {
			jQuery('.formatQuote').each(function(){
				if(jQuery(this).css('text-align') == 'center'){
					jQuery(this).css('text-align','left');
					jQuery(this).css('margin','0 auto');
				}
			});
		});
	</script>
 
</div>