<#if widgetVisible >
	<#include "../common-dashboard-widget-body.ftl" >
</#if>

<script language="javascript">
	new CStudioAuthoringWidgets.MyRecentActivityDashboard('${widgetId}','${context.pageId}');
</script>
