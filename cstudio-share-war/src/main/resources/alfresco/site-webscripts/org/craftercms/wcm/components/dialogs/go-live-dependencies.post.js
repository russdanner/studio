var site = args.site;
var myTicket = cstudioApplicationService.currentApplicationContext().getTicket();
if (site == undefined || site == '') {
    status.code = 400;
    status.message = "Site must be provided.";
    status.redirect = true;
} else {
    var connector = remote.connect("alfresco");
    var resultJson = connector.post("/cstudio/wcm/dependency/get-dependencies?site="+site, requestbody.content, "text/xml");

    var result = eval('(' + resultJson + ')');
    model.dependencies = result;
    model.jsonDependencies = resultJson;
}
