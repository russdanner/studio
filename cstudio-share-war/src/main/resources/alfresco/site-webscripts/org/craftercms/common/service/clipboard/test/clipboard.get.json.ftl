{
	"status-cstudio": "CStudio Clipboard", 
	"items-cstudio": 
	[
	<#list items1 as item>
		{
			"path": "${item.getPath()?string}", 
			"cutFlag": "${item.isCutFlag()?string}", 
			"deep": "${item.isDeep()?string}"
		}<#if item_has_next>,</#if>
	</#list>
	], 
	
	"status-rdy": "Readiness Clipboard", 
	"items-rdy": 
	[
	<#list items2 as item>
		{
			"path": "${item.getPath()?string}", 
			"cutFlag": "${item.isCutFlag()?string}", 
			"deep": "${item.isDeep()?string}"
		}<#if item_has_next>,</#if>
	</#list>
	] 
}
