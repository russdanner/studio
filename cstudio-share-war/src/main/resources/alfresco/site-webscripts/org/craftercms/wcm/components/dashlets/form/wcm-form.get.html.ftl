
	<script>
		/**
		 * contextual variables 
		 * note: these are all fixed at the moment but will be dynamic
		 */
		CStudioAuthoringContext = {
			user: "${userId}",
			role: "${authRole}", 
			site: "${siteId}",
			baseUri: "${page.url.context}",
			authoringAppBaseUri: "${authoringServer}",
			formServerUri: "${formServerUrl}",
			previewAppBaseUri: "${previewServer}",
			liveAppBaseUri: "${liveServer}",
			contextMenuOffsetPage: true,
			brandedLogoUri:"/proxy/alfresco/cstudio/services/content/content-at-path?path=/cstudio/config/app-logo.png",
			homeUri: "/page/site/${siteId}/dashboard",
			navContext: "default",
			cookieDomain: "${cookieDomain}"
		};

		 YAHOO.util.Event.onDOMReady(function() {
			var formId = CStudioAuthoring.Utils.getQueryVariable(location.search, "form");
			CStudioForms.engine.render(formId, "default", "formContainer");
		 });
	</script>

