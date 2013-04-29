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
						<tr><td>${model.title}</td></tr>
						<tr><td>${model.description}</td></tr>
						<tr><td>${model.location}</td></tr>
						<tr><td>${model.startTime}</td></tr>
						<tr><td>${model.endTime}</td></tr>
					</table>
					<#include "/templates/web/components/common-footer.ftl" />
				</div>
			</div>
		</div>
        <#include "/templates/web/components/sch.ftl" />
        <script>
        CStudioAuthoring.InContextEdit.initializeEditRegion(
                                    'wide-ice',
                                    '/site/website'+ document.location.pathname + '/index.xml',
                                    'body');

        CStudioAuthoring.InContextEdit.initializeEditRegion(
                                    'rightrail-ice',
                                    '/site/website'+ document.location.pathname + '/index.xml',
                                    'rightRail');
        </script>
	</body>
</html>

