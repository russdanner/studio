// get parameter values
var siteId = args.site;
var webPropertyId = args.webPropertyId;
var reportId = args.reportId;
var filter = args.filter;

//get the analytics configuration for the website
var analyticsConfig = analyticsConfigFn(siteId, "/analytics/report-config.xml");

var username = analyticsConfig.credentials.username;
var password = analyticsConfig.credentials.password;
var report = lookupReportFn(webPropertyId, reportId, analyticsConfig);

// prepare the query
var query = cstudioAnalyticsService.createQuery();
query.setUsername(username);
query.setPassword(password);
query.setTableId(report.tableId);
query.setStartDate(report.startDate);
query.setEndDate(report.endDate);

//add dimensions
var dimensions = report.query.dimensions.split(",");
for (var l = 0; l < dimensions.length; l++) {
  query.addDimension(dimensions[l]);
}

// add metrics
var metrics = report.query.metrics.split(",");
for (var k = 0; k < metrics.length; k++) {
  query.addMetric(metrics[k]);
}


if(filter) {
	filter = filter.replace(".eq.", "==");
	query.setFilters(filter);
}
// set sort
//query.setSort(report.query.sort);

// maker the query
var queryResults = cstudioAnalyticsService.query(query);

// build the service response
model.username = username;
model.password = password;
model.tableId = report.tableId;
model.startDate = report.startDate;
model.endDate = report.endDate;
model.queryResults = queryResults;
model.visualization = {  library: report.presentation.library, controller: report.presentation.controller };

/**
 * make service call for report configuration
 * @param site
 * @param configId
 * @returns
 */
function analyticsConfigFn(site, configId) {
  var serviceUrl = "/cstudio/site/get-configuration";
  serviceUrl += "?site=" + site;
  serviceUrl += "&path=" + configId;

  var respJson = remote.call(serviceUrl);
  var result = eval("(" + respJson + ")");
  return result;
}
;

/**
 * lookup the report from the configuration
 * @param webPropertyId
 * @param reportId
 * @param config
 * @returns
 */
function lookupReportFn(webPropertyId, reportId, config) {
  var property;
  var report;

  if (!config.sites.length) { // array of 1
    config.sites = [ config.sites.site ];
  }

  for (var i = 0; i < config.sites.length; i++) {
    var cSite = config.sites[i];

    if (cSite.webPropertyId == webPropertyId) {
      property = cSite;
      break;
    }
  }

  if (property) {
    if (!property.reports.length) { // array of 1
      property.reports = [ property.reports.report ];
    }

    for (var j = 0; j < property.reports.length; j++) {
      var cReport = property.reports[j];

      if (cReport.reportId == reportId) {
        report = cReport;
        break;
      }
    }
  }

  return report;
};