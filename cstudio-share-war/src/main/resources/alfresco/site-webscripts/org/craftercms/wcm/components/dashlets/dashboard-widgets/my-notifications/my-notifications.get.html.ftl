<#if widgetVisible >
	<#include "../common-dashboard-widget-body.ftl" >
</#if>

<script language="javascript">
	new CStudioAuthoringWidgets.MyNotificationsDashboard('${widgetId}','${context.pageId}');
</script>
