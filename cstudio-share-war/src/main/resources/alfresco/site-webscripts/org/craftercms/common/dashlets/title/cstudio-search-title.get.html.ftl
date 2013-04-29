<div id="pageTitle">
	<div class="dashHeader"><h1>${msg("header.searchresults")}</h1></div>
		<#if page.url.templateArgs.site??>
      		<ul id="pageNav">
		   		<li>  |  </li>
		    	<li> <a href="${url.context}/page/site/${page.url.templateArgs.site}/dashboard">${msg("header.backlink", profile.title?html)}</a></li>
		    </ul>
		</#if>		
    </div>
<div class="clear"></div>
