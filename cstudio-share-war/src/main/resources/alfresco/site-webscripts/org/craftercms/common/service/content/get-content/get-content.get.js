var storeId = url.templateArgs["storeId"];
var contentPath = url.templateArgs["contentPath"];

var getContentServiceUri = "/api/node/content/avm/" + storeId + "/" + contentPath;

model.content = remote.call(getContentServiceUri);
