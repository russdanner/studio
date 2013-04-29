<div id="${widgetId}" class="ttTableGroup">
    <div class="ttHdr">
        <div class="ttWidgetHdr">
            <span class="ttClose" style="cursor:pointer;" id="widget-toggle-${widgetId}"
                  onclick="return WcmDashboardWidgetCommon.toggleWidget('${widgetId}','${context.pageId}');"></span>
        	<span class="dashboard-widget-title" onclick="return WcmDashboardWidgetCommon.toggleWidget('${widgetId}','${context.pageId}');">${widgetName}</span>
        <#if showTotalRecordCount>
            (<span class='cstudio-dash-totalcount' id='${widgetId}-total-count'></span>)
        </#if>
        </div>

        <ul id="ttNav" class='cstudio-widget-controls'>
            <#if collapsible>
            <li>
                <a id="expand-all-${widgetId}" class="widget-expand-state" href="#"
                   OnClick="return WcmDashboardWidgetCommon.toggleAllItems('${widgetId}');">Collapse All</a>
            </li>
            </#if>
        </ul>

        <!-- TODO Sajan please change this to classes with generic names .. what wwere they thinking?! -->
    <#if enableSetResultLimit>
        <div class="recently-made-live-right">
            <div class="recently-made-live" style="margin-right:0px;">
                Show: <input id="widget-showitems-${widgetId}" type="text" maxlength="3" value="10"
                             class="serchLimitInput"/>
            </div>
        </div>
    </#if>
    </div>


    <div id="sortedBy-${widgetId}" style="display:none"></div>
    <div id="sort-type-${widgetId}" style="display:none"></div>

    <div id="${widgetId}-body" style="display:none">

    </div>
</div>
