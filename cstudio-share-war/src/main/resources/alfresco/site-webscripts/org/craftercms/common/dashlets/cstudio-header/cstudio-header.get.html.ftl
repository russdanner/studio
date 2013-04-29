<script>
	/**
	 * contextual variables 
	 * note: these are all fixed at the moment but will be dynamic
	 */
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
		brandedLogoUri: "/proxy/alfresco/cstudio/services/content/content-at-path?path=/cstudio/config/app-logo.png",
		homeUri: "/page/site/${siteId}/dashboard",
		navContext: "default",
		cookieDomain: "${cookieDomain}",
		openSiteDropdown: ${openSiteDropdown},
		isPreview: false
	};

   	if(CStudioAuthoringContext.role === "") {
   		document.location = CStudioAuthoringContext.baseUri;
   	}

	<#include "/org/craftercms/common/mashlets/overlay/common-hook.ftl" >
</script>
