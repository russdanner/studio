<#if widgetVisible >
	<#include "../common-dashboard-widget-body.ftl" >
</#if>

<script language="javascript">
	new CStudioAuthoringWidgets.RecentlyMadeLiveDashboard('${widgetId}','${context.pageId}');
</script>
