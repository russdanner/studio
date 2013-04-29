<#if widgetVisible >
	<#include "../common-dashboard-widget-body.ftl" >
</#if>

<script language="javascript">
	new CStudioAuthoringWidgets.GoLiveQueueDashboard('${widgetId}','${context.pageId}');
</script>
