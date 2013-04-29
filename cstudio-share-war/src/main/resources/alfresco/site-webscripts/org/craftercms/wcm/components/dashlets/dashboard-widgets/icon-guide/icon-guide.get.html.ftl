<div id="${widgetId}" style="width:290px;">
	<div class="ttHdr" >
		<div class="ttWidgetHdr">		  	
			<span 	class="ttClose" 
					id="widget-toggle-${widgetId}" 
					onclick="return WcmDashboardWidgetCommon.toggleWidget('${widgetId}','${context.pageId}');"> 
			</span>	
			${widgetName} 
		</div>
    </div>			

	<div id="${widgetId}-body" style="display:none">

        <div id="icon-guide-widget" class="headerIcon clearfix" style="width:290px;">
          <div class="iconLeft">
      		<div class="iconPaper"></div>
      		<div class="iconName">Navigation Page</div>
            <div class="iconPlainPaper"></div>
      		<div class="iconName">Floating Page</div>

            <div class="iconPuzzle"></div>
      		<div class="iconName">Component</div>
            <div class="iconDoc"></div>
      		<div class="iconName">Document</div>
            <div class="iconSpace">*</div>
      		<div class="iconName">New Page</div>
            <div class="iconText">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Disabled Page</div>

      </div>
      <div class="iconRight">
      		<div class="iconPen"></div>
      		<div class="iconNameR">In Progress</div>
            <div class="iconFlag"></div>
      		<div class="iconNameR">Submitted</div>
            <div class="iconSchedule"></div>
      		<div class="iconNameR">Scheduled</div>				
            <div class="iconDelete"></div>
            <div class="iconNameR">Deletion</div>
            <div class="iconInFlight"></div>
            <div class="iconNameR">Processing</div>
            <div class="iconLocked"></div>
            <div class="iconNameR">In Edit</div>				
      </div>


	</div>
</div>
<br/>

<script language="javascript">
	new CStudioAuthoringWidgets.IconGuideDashboard('${widgetId}','${context.pageId}');
</script>
