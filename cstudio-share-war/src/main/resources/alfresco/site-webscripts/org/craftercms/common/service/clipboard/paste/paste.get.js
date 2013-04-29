var site = args.site;
var parentPath = args.parentPath;

if(!site) {
   status.code = 400;
   status.message = "Site is required.";
   status.redirect = true;
}

if(!parentPath) {
   status.code = 400;
   status.message = "parentPath is required.";
   status.redirect = true;
}
var serviceResponse=null;
serviceResponse	 = cstudioClipboardService.paste(site, parentPath);

model.success = serviceResponse.success;
model.message = serviceResponse.message;
