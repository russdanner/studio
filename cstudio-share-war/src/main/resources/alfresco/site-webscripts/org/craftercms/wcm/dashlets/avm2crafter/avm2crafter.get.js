var avmprojectsJson = remote.call("/avmtocr/webprojects/list");
var avmprojects = eval('('+avmprojectsJson+')'); 

var crprojectsJson = remote.call("/api/people/admin/sites?roles=user&size=100");
var crprojectsAll = eval('('+crprojectsJson+')'); 
var crprojects = [];

for(var i=0; i<crprojectsAll.length; i++) {
	if(crprojectsAll[i].sitePreset == "cstudio-site-dashboard") {
		crprojects[crprojects.length] = crprojectsAll[i];
	}
}

 
model.avmprojects = avmprojects.avmProjects;
model.crprojects = crprojects;
