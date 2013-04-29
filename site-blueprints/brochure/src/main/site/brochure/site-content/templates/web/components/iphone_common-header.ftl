<div id="branding">
	<h1>
		<a href="${urlTransformationService.transform('toWebAppRelativeUrl', '/')}">
			<img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/mobile/images/logo.gif')}" alt="" width="80" height="40" />
		</a>
	</h1>
	<div class="menu-tab">
		<#include "/templates/web/components/top-nav.ftl" />
		<a href="#" class="toggle-menu">
			<span class="icon"></span>
			<span>Menu</span>
		</a>
	</div>
</div>
