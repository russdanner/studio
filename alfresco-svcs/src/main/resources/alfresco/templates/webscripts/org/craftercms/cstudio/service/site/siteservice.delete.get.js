// define basic functions
var siteName = args.site;
var blueprintName = "default";

// handle site config
		var sitesConfigRoot = companyhome.childByNamePath("cstudio/config/sites");
		sitesConfigRoot.childByNamePath(siteName).remove();

 // handle site content
        var sitesRoot = companyhome.childByNamePath("wem-projects");
        sitesRoot.childByNamePath(siteName).remove();

 // handle site forms
        var sitesFormsRoot = companyhome.childByNamePath("cstudio/config/forms");
        sitesFormsRoot.childByNamePath(siteName).remove();

// handle site models
        var sitesModelsRoot = companyhome.childByNamePath("cstudio/model-prototypes");
        sitesModelsRoot.childByNamePath(siteName).remove();

authoringSiteService.deleteSite(siteName);
