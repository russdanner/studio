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
		status.message = "Schedule Submission failure";
		status.redirect = true;
	}    
	var scheduleToGoLiveResponse = eval('(' + resultJson + ')');
	var scheduledDate =  requestbody.content;
	try{
		var requestedJSON = eval('(' + requestbody.content + ')');
		var scheduledDateString = requestedJSON.scheduledDate.split("T");
		var splitedDate = scheduledDateString[0].split("-");
		var months = ["January", "February", "March", "April", "May", "June", 
		              "July","August", "September", "October", "November", "December"];
		var monthName = months[splitedDate[1] - 1];
		var formatedDate = monthName + " " + splitedDate[2] + ", " + splitedDate[0];  
		model.scheduledDate = formatedDate;
	}catch(ex){
		model.scheduledDate = "Error!! converting date [developers only]";
	}
	model.scheduleToGoLiveResponse = scheduleToGoLiveResponse;	
}

