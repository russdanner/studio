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
	var resultJson = connector.post("/cstudio/wcm/workflow/submit-to-go-live?site="+site, requestbody.content, "application/json");
	if (status.code != 200) {
		status.code = 400;
		status.message = "Go Live Submission failure";
		status.redirect = true;
	}    
	var submitToGoLiveResponse = eval('(' + resultJson + ')');
	model.submitToGoLiveResponse = submitToGoLiveResponse;
}

