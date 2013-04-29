<#assign activePage = page.url.templateArgs.pageid!"">
<#assign siteTitle><#if profile.title != "">${profile.title?html}<#else>${profile.shortName}</#if></#assign>
      <!-- dashboard title -->
		<div id="pageTitle">
			<div class="dashHeader"><h1>${msg("header.site", "<span>${siteTitle} Dashboard</span>")}</h1></div>
			<!--
			 <ul id="pageNav">
				<li>  |  </li>
				<li><a href="#">Change Site</a>  <span class="ttSortDn"></span></li>
			</ul> -->
		</div>
	  <!-- end of dashboard title -->

