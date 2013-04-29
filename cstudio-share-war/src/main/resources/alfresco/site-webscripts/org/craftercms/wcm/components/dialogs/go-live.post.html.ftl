<div class="acnBox">
	<#if goliveResponse.result =="failure">
		<h3 class="padBottom">Go Live: Error</h3>
	<#else>
		<h3 class="padBottom">Go Live: Complete</h3>
	</#if>
	<p>${goliveResponse.message}</p>
	<div class="acnSubmitButtons">
  	<span><input border="0" type="button" onClick="CStudioAuthoring.Operations.pageReload();" value="OK" /></span>
  </div>
</div>
