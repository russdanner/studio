<!DOCTYPE html>
<!-- Conditional comment for mobile ie7 http://blogs.msdn.com/b/iemobile/ -->
<!--[if IEMobile 7 ]> <html class="no-js iem7" lang="en"> <![endif]-->
<!--[if (gt IEMobile 7)|!(IEMobile)]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

	<head>
		<#include "/templates/web/components/mobile_common-head.ftl">
		<link rel="stylesheet" type="text/css" href="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/mobile/css/ipad.css')}" media="screen" />
	</head>

	<body>
		<#include "/templates/web/components/ipad_common-header.ftl" />
		
		<div data-role="page" data-title="Acme - Making our customers proud">
			
			<div class="two-col" data-role="content" data-theme="d">
				<article class="main-col">
					<header>
						<h1>Page Title</h1>
					</header>
					<div id="main-content">
						${model.body_html}
					</div>
				</article>
				<aside class="side-col">
					<div id="aside-content">
						${model.rightRail_html}
					</div>
				</aside>
			</div>
			<#include "/templates/web/components/mobile_page-footer.ftl" />
		</div> <!-- page -->
		<#include "/templates/web/components/mobile_common-footer.ftl" />
	</body>
</html>