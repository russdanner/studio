{ count: '${clipTOs?size}', type: 'content', items: [
		<#list clipTOs as clip>
			{	path: '${clip.path}',
				cut: '${clip.isCutFlag()?string}',
				deep: '${clip.isDeep()?string}'
			}<#if clip_has_next>,</#if>			
		</#list>		
	]
}
