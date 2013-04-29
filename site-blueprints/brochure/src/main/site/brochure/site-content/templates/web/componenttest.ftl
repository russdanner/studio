<#include "/templates/system/common/cstudio-support.ftl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<#include "/templates/web/components/common-head.ftl">
	</head>
	<body>
		<#assign idx=0 />
		<div id="wrap">
			<div class="top_corner"></div>
			
			<div id="main_container">
				
				<#include "/templates/web/components/common-header.ftl" />
				
				<div class="center_content_pages">
					<table cellpadding="0" cellspacing="0">
						<tr>
							<td width="10px"> </td>
							<td width="630px" valign="top">
								<@componentZone id="bodyModules">
									<#if model.bodyModules?? && model.bodyModules.item??>
				        				<#list model.bodyModules.item as module>
					        				    <@draggableComponent component=module counter=idx >
						        					<@renderComponent component=module />
						        				</@draggableComponent>
						        				<#assign idx=idx+1 />
				        				</#list>
				        			</#if>
								</@componentZone>
							</td>
							<td width="10px"> </td>
							
							<td width="300px" valign="top">							
								<@componentZone id="railModules">
									<#if model.railModules?? && model.railModules.item??>
				        				<#list model.railModules.item as module>
					        				    <@draggableComponent component=module counter=idx>
						        					<@renderComponent component=module />
						        				</@draggableComponent>
						        				<#assign idx=idx+1 />
				        				</#list>
				        			</#if>
			        			</@componentZone>
							</td>
							<td width="10px"> </td>
						</tr>
					</table>
					<!--include "/templates/web/components/about-author.ftl" / -->
					<#include "/templates/web/components/common-footer.ftl" />
				</div>
			</div>
		</div>
        <#include "/templates/web/components/sch.ftl" />
	</body>
</html>