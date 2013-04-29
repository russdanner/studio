var site = args.site;
var jsonRequest=requestbody.content;
if(!site) {
   status.code = 400;
   status.message = "Site is required.";
   status.redirect = true;
}

var serviceResponse=cstudioClipboardService.cut(site,jsonRequest);
model.success = serviceResponse.success;
model.message = serviceResponse.message;
