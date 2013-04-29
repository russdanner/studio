{ 
	"queryResults":[
		<#list queryResults.entries as reportItem>
			{ "dataItems": [ 
				<#list reportItem.data?keys as dataItemKey>
					{ "key": "${dataItemKey}", "value" : "${reportItem.data[dataItemKey]}" },
				</#list>
			]},
		</#list>],

	"visualizationCode": { "library" : "${visualization.library}", "controller" : "${visualization.controller}" }
}
