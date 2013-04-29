var site = args.site;
var path = args.path;

if (!site) {
    status.code = 400;
    status.message = "Site is required.";
    status.redirect = true;
} else {
    var connector = remote.connect("alfresco");

    var resultJson = connector.call("/cstudio/wcm/clipboard/duplicate?site=" + site + "&path=" + path);
    var duplicateJSON = eval('(' + resultJson + ')');

    var duplicatePath = duplicateJSON.path;
    var contentType = connector.call("/cstudio/wcm/contenttype/get-content-type-by-path?site=" + site + "&path=" + path);
    var contentTypeJSON = eval('(' + contentType + ')');
    var formName = contentTypeJSON.form;
    var formPath = "/page/site/cstudio/cstudio-webform?form=" + formName + "&id=" + duplicatePath + "&path=" + duplicatePath + "&edit=true&draft=true";
    model.path = duplicatePath;
    model.formId = formName;
}
