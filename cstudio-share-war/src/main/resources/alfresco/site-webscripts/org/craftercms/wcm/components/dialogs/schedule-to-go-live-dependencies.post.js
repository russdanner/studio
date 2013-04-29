var site = args.site;
var myTicket = cstudioApplicationService.currentApplicationContext().getTicket();
if (site == undefined || site == '') {
	status.code = 400;
	status.message = "Site must be provided.";
	status.redirect = true;
} else {
	var connector = remote.connect("alfresco");
	var resultJson = connector.post("/cstudio/wcm/dependency/get-dependencies?site="+site, requestbody.content, "text/xml");
	var resultChannels = connector.post("/cstudio/publish/get-available-publishing-channels?site=" + site, requestbody.content, "application/json");

	var result = eval('(' + resultJson + ')');	
    model.dependencies = result;
    model.jsonDependencies = resultJson;
    model.channels = eval('(' + resultChannels + ')');
}
