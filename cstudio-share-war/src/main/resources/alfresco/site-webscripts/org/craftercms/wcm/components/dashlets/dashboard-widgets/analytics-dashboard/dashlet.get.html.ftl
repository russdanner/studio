<#if widgetVisible >
	<div id="${widgetId}">
	</div>
	
	<script language="javascript">
    YAHOO.util.Event.onDOMReady(function(){
		var dashboard = new CStudioAuthoringWidgets.AnalyticsDashboard('${widgetId}','${context.pageId}');
		dashboard.render();
    });
	</script>


</#if> 
