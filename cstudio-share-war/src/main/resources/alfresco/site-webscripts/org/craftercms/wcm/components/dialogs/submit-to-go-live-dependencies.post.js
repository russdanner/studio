var site = args.site;
if (site == undefined || site == '') {
	status.code = 400;
	status.message = "Site must be provided.";
	status.redirect = true;
} else {
	var connector = remote.connect("alfresco");
	var resultJson = connector.post("/cstudio/wcm/dependency/get-dependencies?site="+site, requestbody.content, "text/xml");
	if (resultJson.status != 200) {
		status.code = 400;
		status.message = "Get dependencies failure";
		status.redirect = true;
	}    

	var result = eval('(' + resultJson + ')');	
    model.dependencies = result;
    model.jsonDependencies = resultJson;
}
