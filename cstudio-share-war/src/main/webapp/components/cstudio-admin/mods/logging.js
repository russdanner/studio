CStudioAuthoring.Utils.addCss("/components/cstudio-admin/mods/logging.css");
CStudioAdminConsole.Tool.Logging = CStudioAdminConsole.Tool.Logging ||  function(config, el)  {
	this.containerEl = el;
	this.config = config;
	this.types = [];
	return this;
}

/**
 * Overarching class that drives the content type tools
 */
YAHOO.extend(CStudioAdminConsole.Tool.Logging, CStudioAdminConsole.Tool, {
	renderWorkarea: function() {
		var workareaEl = document.getElementById("cstudio-admin-console-workarea");
		
		workareaEl.innerHTML = 
			"<div id='logger-list'>" +
			"</div>";
			
			var actions = [];

			CStudioAuthoring.ContextualNav.AdminConsoleNav.initActions(actions);
			
			this.renderJobsList();
	},
	
	renderJobsList: function() {
		
		CStudioAdminConsole.Tool.Logging.setLevel = function(index, level) {
			var logger = CStudioAdminConsole.Tool.Logging.loggers[index];
			if(logger) {
	
				var serviceUri = "/proxy/alfresco/cstudio/logging/set-level?logger="+logger.name+"&level="+level;
		
				var cb = {
					success:function() {
					},
					failure: function() {
					}
				}
				
				YConnect.asyncRequest("GET", CStudioAuthoring.Service.createServiceUri(serviceUri), cb);
			}
		};

		
		var loggerLisEl = document.getElementById("logger-list");
		
		loggerLisEl.innerHTML = 
			"<table id='loggerTable' class='cs-loggerlist'>" +
			 	"<tr>" +
				 	"<th class='cs-loggerlist-heading'>Logger</th>" +
				 	"<th class='cs-loggerlist-heading'>Current Level</th>" +
    			 	"<th class='cs-loggerlist-heading'>Change Level To</th>" +
				 "</tr>" + 
			"</table>";
	
			cb = {
				success: function(response) {
					var loggers = eval("(" + response.responseText + ")").loggers;
					CStudioAdminConsole.Tool.Logging.loggers = loggers;
					
					var jobsTableEl = document.getElementById("loggerTable");
					for(var i=0; i<loggers.length; i++) {
						var logger = loggers[i];
						var trEl = document.createElement("tr");

						var rowHTML = 				 	
				 			"<td class='cs-loggerlist-detail'>" + logger.name + "</td>" +
				 			"<td class='cs-loggerlist-detail'>" + logger.level + "</td>" +
				 			"<td class='cs-loggerlist-detail'>"+
				 			  "<a onclick=\"CStudioAdminConsole.Tool.Logging.setLevel("+i+ ",\'debug\'); return false;\">debug</a>&nbsp;&nbsp;"+
				 			  "<a onclick=\"CStudioAdminConsole.Tool.Logging.setLevel("+i+ ",\'warn\'); return false;\">warn</a>&nbsp;&nbsp;"+
				 			  "<a onclick=\"CStudioAdminConsole.Tool.Logging.setLevel("+i+ ",\'info\'); return false;\">info</a>&nbsp;&nbsp;"+
				 			  "<a onclick=\"CStudioAdminConsole.Tool.Logging.setLevel("+i+ ",\'error\'); return false;\">error</a>"+
				 			"</td>";
					 	
					 	trEl.innerHTML = rowHTML;
				 		jobsTableEl.appendChild(trEl);
					}
				},
				failure: function(response) {
				},
				
				self: this
			};
			
			var serviceUri = "/proxy/alfresco/cstudio/logging/loggers";

			YConnect.asyncRequest("GET", CStudioAuthoring.Service.createServiceUri(serviceUri), cb);
	}
});
	
CStudioAuthoring.Module.moduleLoaded("cstudio-console-tools-logging",CStudioAdminConsole.Tool.Logging);