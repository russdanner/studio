<#include "/templates/system/common/cstudio-support.ftl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<#include "/templates/web/components/common-head.ftl">
	</head>

	<body>

		<div id="wrap">
			<div class="top_corner"></div>
			<div id="main_container">
    			<#include "/templates/web/components/common-header.ftl" />
                
				<div class="middle_banner">               
				
					<@ice id="entryPromos"></@ice>

					<div class="featured_slider">
						<!-- begin: sliding featured banner -->
						<div id="featured_border" class="jcarousel-container">
							<div id="featured_wrapper" class="jcarousel-clip">
								<ul id="featured_images" class="jcarousel-list">
                                <#assign promos = model.entryPromos.item>
                                <#list promos as promo>
                                    <li><img src="${urlTransformationService.transform('toWebAppRelativeUrl', promo.image)}" width="965" height="280" alt="" /></li>
                                </#list>
								</ul>
							</div>
				
							<div id="featured_positioner_desc" class="jcarousel-container">
								<div id="featured_wrapper_desc" class="jcarousel-clip">
									<ul id="featured_desc" class="jcarousel-list">
                                    <#list promos as promo>
                                        <li><div><p>${promo.caption_html}</p></div></li>
                                    </#list>
									</ul>
								</div>
							</div>
				
							<ul id="featured_buttons" class="clear_fix">
                            <#list 1..promos?size as idx>
                                <li>${idx}</li>
                            </#list>
							</ul>
						</div>
						<!-- end: sliding featured banner -->
					</div>
	          
        			<div class="center_content">
        				<@ice id="shoutBoxes">
	        				<#list model.shoutBoxes.item as shoutBox>
		        				<@ice component=shoutBox>
			        				<section class="shout-box-container">
			        					<@renderComponent component=shoutBox />
									</section>
	        					</@ice>
	        				</#list>
	        			</@ice>

			            <div class="left_block_wide">
                		
                			<h2>Latest Projects</h2>
								<a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p1.jpg')}" alt="" title="" border="0" class="projects" /></a>
               	 				<a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p2.jpg')}" alt="" title="" border="0" class="projects" /></a>
                				<a href="#"><img src="${urlTransformationService.transform('toWebAppRelativeUrl', '/static-assets/images/p3.jpg')}" alt="" title="" border="0" class="projects" /></a>
                			</div>
            
            				<div class="right_block">
            					<h2>Newsletter Sign up</h2>
                				<p>Sign up for our news leter today and get updates on our latest news and events!</p>
				                <form id="newsletter">
                					<input type="text" name="" class="newsletter_input" />
                					<input type="submit" name="" class="newsletter_submit" value="Sign up" />
             				   </form>
            				</div>
					        <div class="clear"></div>
					        
        				</div>
        			</div>
        			
        			<#include "/templates/web/components/common-footer.ftl" />
    			 </div> 
			</div>
			<#include "/templates/web/components/sch.ftl" />
		</body>
</html>