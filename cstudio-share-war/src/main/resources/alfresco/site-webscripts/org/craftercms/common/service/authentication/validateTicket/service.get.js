function removeXmlDocType(xml) {
    var str = new String(xml);

    //Javascript E4X module has problems with XML header
    if ( str.substr(0,5).indexOf("?xml") != -1 ) {
        positionRootElement = str.indexOf("<", 10);//get first real tag
        str = str.substr( positionRootElement, str.length - 1 );
    }

    return str;
}

var ticket = url.templateArgs['ticket'];
var conn = remote.connect("alfresco");
var result = conn.get("/api/login/ticket/" + ticket);

model.code = "code";
model.message = "message";

status.code = 200;

var resultStr = new String(result.getText());

if (resultStr != undefined && resultStr.length > 0) {
    var resultDom = removeXmlDocType(resultStr);
    var xmlDoc = new XML(resultDom);

    var statusCodeVal = xmlDoc["status"]["code"].text().toXMLString();
    var statusMessageVal = xmlDoc["message"].text().toXMLString();
    var ticketResponse = xmlDoc.text().toXMLString();
    if (ticketResponse != undefined && ticketResponse.match(/^TICKET.*/g)) {
        statusCodeVal = "200";
        statusMessageVal = ticketResponse;
    }

    model.code = statusCodeVal;
    model.message = statusMessageVal;
} else {
    model.code = "404";
    model.message = "Ticket not found";
}
