/**
 * Call dependency webscript on alfresco
 * 
 * @method POST
 */
var site = args.site;

var connector = remote.connect("alfresco");
var url = "/cstudio/notification/get-message?site=" + site + "&key=scheduling-policy"
var schedulePolicy = connector.get(url);
if (schedulePolicy.status != 200) {
	status.code = 400;
	status.message = "Schedule policy fetch failure";
	status.redirect = true;
}    
model.schedulePolicy = schedulePolicy;







