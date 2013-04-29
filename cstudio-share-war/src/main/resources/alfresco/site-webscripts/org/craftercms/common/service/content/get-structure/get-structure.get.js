var project = url.templateArgs["project"];
var sandbox = url.templateArgs["sandbox"];
var path = url.templateArgs["path"];

var getContentServiceUri = "/api/wcm/webprojects/"+project+"/sandboxes/"+sandbox+"/assets/www/avm_webapps/ROOT/"+path

model.content = remote.call(getContentServiceUri);
