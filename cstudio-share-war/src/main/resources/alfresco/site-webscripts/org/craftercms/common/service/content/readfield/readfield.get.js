function removeXmlDocType(xml) {
	var str = new String(xml);

	//Javascript E4X module has problems with XML header
	if ( str.substr(0,5).indexOf("?xml") != -1 ) {
		positionRootElement = str.indexOf("<", 10);//get first real tag
		str = str.substr( positionRootElement, str.length - 1 ); 
	}
	
	return str;
}


var siteId = args.siteId;
var id = args.contentPath;
var field = args.field;

var existingContentUri = "/cstudio/wcm/content/get-content" +
	"?site=" + siteId +
	"&path=" + id;

var modelInstanceXml = remote.call(existingContentUri);
var modifiedDom = removeXmlDocType(modelInstanceXml);

var xmlDoc = new XML(modifiedDom);


var content = xmlDoc[field].toString();

model.fieldContent = content;

