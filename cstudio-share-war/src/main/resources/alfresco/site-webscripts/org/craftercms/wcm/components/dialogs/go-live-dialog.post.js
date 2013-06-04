var site = args.site;
var myTicket = cstudioApplicationService.currentApplicationContext().getTicket();
if (site == undefined || site == '') {
    status.code = 400;
    status.message = "Site must be provided.";
    status.redirect = true;
} else {
    var connector = remote.connect("alfresco");
    var resultChannels = connector.post("/cstudio/publish/get-available-publishing-channels?site=" + site, requestbody.content, "application/json");
    model.channels = eval('(' + resultChannels + ')');
}
