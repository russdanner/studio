<#assign id = args.htmlid>
<#assign dashboardconfig=config.scoped['Dashboard']['dashboard']>
<#assign jsid = args.htmlid?js_string>
<script type="text/javascript">
</script>

<div class="dashlet my-sites">
   <div class="title">${msg("header")}</div>
   <div class="toolbar flat-button">
   	 <p>This tool imports AVM project artifacts in to an existing Crafter CMS project.  The tool will convert your webforms, migrate your XML models, and port your files.</p>
      <div class="hidden">
         <div class="clear"></div>
      </div>
   </div>
   <div id="${id}-sites" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   		<div style='margin-left:10px; margin-right: 10px;'>
	        <p>Choose the AVM project you wish to convert</p>
	   		<select id='avmsite'>
	   			<#list avmprojects as avmproj>
					<option value="${avmproj.store}">${avmproj.name}</option>
	   			</#list>
	   		</select>
	   		<br/>
	   		<br/>
	   		<p>Choose the Crafter CMS project you wish to import the AVM project in to</p>
	   		<select id='crsite'>
	   			<#list crprojects as crproj>
					<option value="${crproj.shortName}">${crproj.title}</option>
	   			</#list>
	   		</select>
	   		<br/>
	   		<br/>
	   		<span class="yui-button yui-push-button" style='margin-left: 75px;'>
				<span class="first-child">
					<button id='avm2crImportBtn' class="yui-button yui-push-button" type="button" style="width: 100px;  height: 30px;">Import</button>
				</span>
			</span>
   		</div>
   </div>
</div>
<script>
	var avm2crImportBtnEl = document.getElementById('avm2crImportBtn');
	avm2crImportBtnEl.onclick = function() {
		// lookup project IDs
		var avmproj = document.getElementById('avmsite').value;
		var crproj = document.getElementById('crsite').value;
		var formCount = 0;
		var contentImported = false;
		var avmforms;
		
		//show dialog
		YAHOO.namespace("avm2cr");
        var content = "";
        content.innerHTML = "";
        if (!YAHOO.avm2cr.wait) {
            YAHOO.avm2cr.wait = 
				new YAHOO.widget.Panel("wait", { 
					width: "240px", 
					fixedcenter: true, 
					close: false, 
					draggable: false, 
					zindex:4,
					modal: true,
					visible: false});
    
            YAHOO.avm2cr.wait.setHeader("Importing site");
            YAHOO.avm2cr.wait.setBody("Initializing");

            YAHOO.avm2cr.wait.render(document.body);
        }

		YAHOO.avm2cr.wait.show();

		//import webforms
		var lookupProjCb = {
			success: function(response) {
				avmforms = eval('('+response.responseText+')');
				avmforms = avmforms.avmProjectForms;
				
				for(var i=0; i<avmforms.length; i++) {
					var form = avmforms[i];
					YAHOO.avm2cr.wait.setBody("Importing Form:" + form.title);
					var convertFormsServiceUrl = Alfresco.constants.URL_CONTEXT +
						"proxy/alfresco/avmtocr/importform/action?storeId="
						 + avmproj+"&formId="+form.name+"&crProject="+crproj;
				
					var converFormCb = {
						success: function(response) {
							YAHOO.avm2cr.wait.setBody("Imported Form:" + form.title);
							formCount++;
							if(contentImported && formCount == avmforms.length) {
								YAHOO.avm2cr.wait.hide();
							}
						},
						failure: function() {
							YAHOO.avm2cr.wait.setBody("Import of form failed:" + form.title);
						}
					};
						
					YAHOO.util.Connect.asyncRequest("GET", convertFormsServiceUrl, converFormCb);
				}
			},
			failure: function(err) {
				alert("import failed to lookup avm webforms");
			}
		}
		
		YAHOO.avm2cr.wait.setBody("loading webforms");
		var getFormsServiceUrl = Alfresco.constants.URL_CONTEXT + 
		"proxy/alfresco/avmtocr/webproject/forms/list?projectId="+avmproj;
		
		YAHOO.util.Connect.asyncRequest("GET", getFormsServiceUrl, lookupProjCb);
		
		// import content items
		var importContentServiceUrl = Alfresco.constants.URL_CONTEXT +
		"proxy/alfresco/avmtocr/processavmfiles/action?storeId=" + 
		avmproj+"&crProject="+crproj;

		var importContentCb = {
			success: function(response) {
				contentImported = true;
				if(avmforms && (formCount == avmforms.length)) {
					YAHOO.avm2cr.wait.hide();
				}
			},
			failure: function() {
				alert("error importing content");
			}
		};

		YAHOO.util.Connect.asyncRequest("GET", importContentServiceUrl, importContentCb);
	}
</script>