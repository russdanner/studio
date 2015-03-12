// extract parameters
var site = args.site;
var body = requestbody.content;

model.result = dmWorkflowService.submitToDelete(site, body);
