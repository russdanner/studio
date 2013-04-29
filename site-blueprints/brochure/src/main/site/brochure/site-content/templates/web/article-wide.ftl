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
				
				<div class="center_content_pages">
					<table>
						<tr>
							<td width="10xp"> </td>
							<td valign="top"><@ice id="body">${model.body_html}</@ice></td>
							<td width="10xp"> </td>
						</tr>
					</table>
					<#include "/templates/web/components/common-footer.ftl" />
				</div>
			</div>
		</div>
        <#include "/templates/web/components/sch.ftl" />
	</body>
</html>

