var site = args.site;
if (site == undefined || site == '') {
	status.code = 400;
	status.message = "Reject dependencies - site must be provided.";
	status.redirect = true;
} else {
	var connector = remote.connect("alfresco");
	
	var resultJson = connector.post("/cstudio/wcm/dependency/get-dependencies?site="+site, requestbody.content, "text/xml");
	var result = eval('(' + resultJson + ')');	
    model.dependencies = result; // insert into model for ftl use
    model.jsonDependencies = resultJson; // insert into model - for js use
    
    // call the rejection-messages service 
 	var rejectionMessages = connector.get("/cstudio/notification/get-rejection-messages?site="+site);
 	var rejectionMessagesList = eval('(' + rejectionMessages + ')'); 	
 	model.rejectionMessagesList = rejectionMessagesList; // insert into model - for ftl use
 	model.jsonRejectionMessagesList = rejectionMessages; // insert into model - for js use
   
}
