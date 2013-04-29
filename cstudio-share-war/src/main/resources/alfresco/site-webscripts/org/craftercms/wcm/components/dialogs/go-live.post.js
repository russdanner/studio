/**
 * Call dependency webscript on alfresco
 * 
 * @method POST
 */
var site = args.site;
var myTicket = cstudioApplicationService.currentApplicationContext().getTicket();
if (site == undefined || site == '') {
	status.code = 400;
	status.message = "Site must be provided.";
	status.redirect = true;
} else {
	var connector = remote.connect("alfresco");
	var resultJson = connector.post("/cstudio/wcm/workflow/go-live?site="+site, requestbody.content, "application/json");
	if (resultJson.status != 200) {
		status.code = 400;
		status.message = "Go Live Submission failure";
		status.redirect = true;
	}    
	var goliveResponse = eval('(' + resultJson + ')');
	model.goliveResponse = goliveResponse;
}

