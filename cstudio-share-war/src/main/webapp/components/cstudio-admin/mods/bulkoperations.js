//CStudioAuthoring.Utils.addCss("/components/cstudio-admin/mods/logging.css");
CStudioAdminConsole.Tool.BulkOperations = CStudioAdminConsole.Tool.BulkOperations ||  function(config, el)  {
    this.containerEl = el;
    this.config = config;
    this.types = [];
    return this;
}

/**
 * Overarching class that drives the content type tools
 */
YAHOO.extend(CStudioAdminConsole.Tool.BulkOperations, CStudioAdminConsole.Tool, {
    renderWorkarea: function() {
        var workareaEl = document.getElementById("cstudio-admin-console-workarea");

        workareaEl.innerHTML =
            "<div id='bulk-ops'>" +
                "</div>";

        var actions = [];

        CStudioAuthoring.ContextualNav.AdminConsoleNav.initActions(actions);

        this.renderJobsList();
    },

    renderJobsList: function() {

        CStudioAdminConsole.Tool.BulkOperations.rename = function() {
            var srcPath = document.getElementById("bulk-rename-src-path").value;
            var targetPath = document.getElementById("bulk-rename-target-path").value;
            if (srcPath && targetPath) {
                var serviceUri = "/proxy/alfresco/cstudio/util/bulk-rename?site="+CStudioAuthoringContext.site+"&srcPath="+srcPath+"&targetPath="+targetPath;
                var renameOpMessage = document.getElementById("bulk-rename-message");

                var cb = {
                    success:function() {
                        renameOpMessage.innerHTML = "Bulk rename successful";
                    },
                    failure: function() {
                        renameOpMessage.innerHTML = "Bulk rename failed";
                    }
                }
                YConnect.asyncRequest("POST", CStudioAuthoring.Service.createServiceUri(serviceUri), cb);
                renameOpMessage.innerHTML = "Executing bulk rename ...";
            }
        };

        CStudioAdminConsole.Tool.BulkOperations.golive = function() {
            var path = document.getElementById("bulk-golive-path").value;
            if (path) {
                var serviceUri = "/proxy/alfresco/cstudio/util/bulk-golive?site="+CStudioAuthoringContext.site+"&path="+path;
                var goLiveOpMessage = document.getElementById("bulk-golive-message");
                var cb = {
                    success:function() {
                        goLiveOpMessage.innerHTML = "Bulk Go Live successful";
                    },
                    failure: function() {
                        goLiveOpMessage.innerHTML = "Bulk Go Live successful";
                    }
                }

                YConnect.asyncRequest("POST", CStudioAuthoring.Service.createServiceUri(serviceUri), cb);
                goLiveOpMessage.innerHTML = "Executing bulk Go Live ...";
            }
        };


        var loggerLisEl = document.getElementById("bulk-ops");

        loggerLisEl.innerHTML =
            "<div id='bulk-rename'><p><h2>Bulk Rename</h2></p><p>" +
                "Source path: <input type='text' id='bulk-rename-src-path' /><br/>" +
                "Target path: <input type='text' id='bulk-rename-target-path' /><br/>" +
                "<input type='button' value='Rename' onclick='CStudioAdminConsole.Tool.BulkOperations.rename()' /></p>" +
                "<p id='bulk-rename-message'></p></div>" +
                "<hr/>" +
                "<div id='bulk-golive'><p><h2>Bulk Go Live</h2></p><p>" +
                "Path to Publish: <input type'text' id='bulk-golive-path'/><br/>" +
                "<input type='button' value='Go Live' onclick='CStudioAdminConsole.Tool.BulkOperations.golive()' /></p>" +
                "<p id='bulk-golive-message'></p></div>";

    }
});

CStudioAuthoring.Module.moduleLoaded("cstudio-console-tools-bulkoperations",CStudioAdminConsole.Tool.BulkOperations);