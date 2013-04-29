<#if showFormDef == false >
	<script>
		CStudioAuthoringContext = {
			user: "${userId}",
			role: "${authRole}", 
			site: "${siteId}",
			collabSandbox: "${collabSandbox}",
			baseUri: "${page.url.context}",
			authoringAppBaseUri: "${authoringServer}",
			formServerUri: "${formServerUrl}",
			previewAppBaseUri: "${previewServer}",
			contextMenuOffsetPage: false,
			brandedLogoUri: "/themes/cstudioTheme/images/app-logo.png",
			homeUri: "/page/site/${siteId}/dashboard",
			navContext: "default",
			cookieDomain: "${cookieDomain}"
		};
	</script>
</#if>
${webFormRendition}
