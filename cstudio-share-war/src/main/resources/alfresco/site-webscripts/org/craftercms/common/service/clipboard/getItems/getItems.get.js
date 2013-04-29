var site = args.site;
 
if(!site) {
   status.code = 400;
   status.message = "Site is required.";
   status.redirect = true;
}

if(cstudioClipboardService) {
	var collection = cstudioClipboardService.getItems(site);
	 
	if(collection) {
		model.clipTOs = collection;
	}
	else {
		model.clipTOs = new Array();
	}
}
else {
	model.clipTOs = new Array();
}
	
