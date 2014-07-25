
var conn = remote.connect("alfresco");
var response = conn.get("/cstudio/site/get-blueprints");
var blueprintsJSON = eval('(' + response + ')');

model.blueprints = blueprintsJSON.blueprints;
model.sitePresets = [{id: "cstudio-site-dashboard", title: "cstudio-site-dashboard"}];
