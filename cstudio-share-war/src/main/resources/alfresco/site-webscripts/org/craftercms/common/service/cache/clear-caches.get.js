var response = "";
	
try {
	
	cstudioFormService.flushCache();	
	response = "success";
}
catch(err) {
	response = "error: "+err;
}

model.response = response;
