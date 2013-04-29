<!DOCTYPE html>
<!-- Conditional comment for mobile ie7 http://blogs.msdn.com/b/iemobile/ -->
<!--[if IEMobile 7 ]> <html class="no-js iem7" lang="en"> <![endif]-->
<!--[if (gt IEMobile 7)|!(IEMobile)]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

	<head>
		<#include "/templates/web/components/mobile_common-head.ftl">
		<link rel="stylesheet" type="text/css" href="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/mobile/css/iphone.css')}" media="screen" />
	</head>

	<body>
		<#include "/templates/web/components/iphone_common-header.ftl" />
			
		<div data-role="page" data-title="Acme - Making our customers proud">
			<div data-role="content" data-theme="d">
				<section class="shout-box-container">
                    <#list model.shoutBoxes.item as shoutBox>
                        <div id="cstudio-component-${shoutBox.key}" class='cstudio-component-ice'>
                            <@renderComponent component = shoutBox />
                        </div>
                    </#list>
				</section>
				<section class="graphic-navbar">
					<h1>Latest Projecs</h1>
					<div data-role="navbar">
						<ul>
							<li><a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p1.jpg')}" alt="" width="190" height="97" /></a></li>
							<li><a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p2.jpg')}" alt="" width="190" height="97" /></a></li>
							<li><a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p3.jpg')}" alt="" width="190" height="97" /></a></li>
						</ul>
					</div>
				</section>
				<section class="sign-up">
					<h1>Newsletter Sign Up</h1>
					<p>Sign up for our news letter today and get updates on our latest news and events!</p>
					<form>
						<input id="email" name="e" type="email" placeholder="Your email">
						<input data-theme="b" id="signup" type="submit" value="Sign up">
					</form>
				</section>
			</div>                		
        	<#include "/templates/web/components/mobile_page-footer.ftl" />
    	</div> <!-- page -->
    	<#include "/templates/web/components/mobile_common-footer.ftl" />
	</body>
</html>