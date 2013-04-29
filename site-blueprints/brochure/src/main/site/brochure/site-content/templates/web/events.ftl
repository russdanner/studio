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
						<#list model.events as event>
							<tr><td>Event Title:</td><td>${event.title}</td>
								<td rowspan="5">
                                    <iframe width="300" height="200" frameborder="0" scrolling="no" marginheight="0" marginwidth="0"
                                            src="http://maps.google.com/maps?q=${event.location?url}&oe=utf-8&t=m&z=16&iwloc=&output=embed"></iframe>
                                    <br /><small>
                                      <a href="http://maps.google.com/maps?q=${event.location?url}&oe=utf-8&t=m&z=16"
                                         style="color:#0000FF;text-align:left">View Larger Map</a>
                                    </small>
								</td>
							</tr>
							<tr><td>Description:</td><td>${event.description}</td></tr>
							<tr><td>Location:</td><td>${event.location}</td></tr>
							<tr><td>Start Time:</td><td>${event.startTime}</td></tr>
							<tr><td>End Time:</td><td>${event.endTime}</td></tr>
							<tr><td colspan="2"><hr></td></tr>
						</#list>
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

