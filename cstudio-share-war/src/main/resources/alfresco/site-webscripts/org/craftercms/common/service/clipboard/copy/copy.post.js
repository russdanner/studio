var site = args.site;
var jsonRequest=requestbody.content;
if(!site) {
   status.code = 400;
   status.message = "Site is required.";
   status.redirect = true;
}
var serviceResponse=null;
var cut=args.cut;
if(!cut){
   serviceResponse=cstudioClipboardService.copy(site,jsonRequest,true);
}else{
 serviceResponse=cstudioClipboardService.cut(site,jsonRequest);
}

model.success = serviceResponse.success;
model.message = serviceResponse.message;
