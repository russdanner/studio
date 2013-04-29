<#if widgetVisible >
	<#include "../common-dashboard-widget-body.ftl" >
</#if>

<script language="javascript">
	new CStudioAuthoringWidgets.ApprovedScheduledItemsDashboard('${widgetId}','${context.pageId}');
</script>
