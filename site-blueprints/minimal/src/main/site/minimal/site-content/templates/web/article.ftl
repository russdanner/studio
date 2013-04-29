<#include "/templates/system/common/cstudio-support.ftl">
<#include "/templates/web/navigation/navigation.ftl">

<!DOCTYPE html >
<!--  Website template by freewebsitetemplates.com  -->
<html>

<head>
	<title>Minimalistic Web Template</title>
	<meta  charset="iso-8859-1" />
	<link href="/static-assets/css/style.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/js/jquery.core.js')}"></script>
	<script type="text/javascript" src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/js/crafter-support-1-0-0.js')}"></script>
</head>

<body>

	  <div id="background">
			  <div id="page">
			  
					 <div class="header">
						<div class="footer">
							<div class="body">
									<div id="sidebar">
									    <a href="index.html"><img id="logo" src="/static-assets/images/logo.gif" with="154" height="74" alt="" title=""/></a>
										
                                        <ul class='navigation'>
                                            <li><a href="/">HOME</a></li>
                                            <@renderNavigation "/site/website", 1 />
                                        </ul>

										
										<div class="connect">
										    <a href="http://facebook.com/freewebsitetemplates" class="facebook">&nbsp;</a>
											<a href="http://twitter.com/fwtemplates" class="twitter">&nbsp;</a>
											<a href="http://www.youtube.com/fwtemplates" class="vimeo">&nbsp;</a>
										</div>
										
										<div class="footenote">
										  <span>&copy; Copyright &copy; 2011.</span>
										  <span><a href="index.html">Company name</a> all rights reserved</span>
										</div>
										
									</div>
									<div id="content">
									          <div class="content">
											    <ul>
													<li  style='list-style-type:none;'>
															<@ice id="body">${model.body!""}</@ice>
													</li>
												</ul>
										     </div>
									</div>
							</div>
						</div>
					 </div>
					 <div class="shadow">
					 </div>
			  </div>    
	  </div>    
<@cstudioOverlaySupport/>	
<#include "/templates/system/common/components-support.ftl" />
</body>
</html>
