/**
 * Call dependency webscript on alfresco
 * 
 * @method POST
 */
var site = args.site;
if (site == undefined || site == '') {
	status.code = 400;
	status.message = "Site must be provided.";
	status.redirect = true;
} else {
	var connector = remote.connect("alfresco");
	var resultJson = connector.post("/cstudio/wcm/workflow/reject?site="+site, requestbody.content, "application/json");
	if (resultJson.status != 200) {
		status.code = 400;
		status.message = "Reject items failure";
		status.redirect = true;
	}    
	var rejectResponse = eval('(' + resultJson + ')');
	model.rejectResponse = rejectResponse;
}

