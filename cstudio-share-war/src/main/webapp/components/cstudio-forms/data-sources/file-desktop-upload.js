CStudioForms.Datasources.FileDesktopUpload = CStudioForms.Datasources.FileDesktopUpload ||  
function(id, form, properties, constraints)  {
   	this.id = id;
   	this.form = form;
   	this.properties = properties;
   	this.constraints = constraints;
	
	return this;
}

YAHOO.extend(CStudioForms.Datasources.FileDesktopUpload, CStudioForms.CStudioFormDatasource, {

    getLabel: function() {
        return "File uploaded from desktop";
    },

	/**
	 * action called when user clicks insert file
	 */
	insertFileAction: function(insertCb) {
		this._self = this;
		var site = CStudioAuthoringContext.site;
		var path = "/static-assets/files"; // default
		var isUploadOverwrite = true;
		
		for(var i=0; i<this.properties.length; i++) {
			if(this.properties[i].name == "repoPath") {
				path = this.properties[i].value;
			
				path = this.processPathsForMacros(path);
			}
		}

		var callback = { 
			success: function(fileData) {
				var url = this.context.createPreviewUrl(path + "/" + fileData.fileName);
				fileData.previewUrl = url
				fileData.relativeUrl = path + "/" + fileData.fileName
				insertCb.success(fileData);
			}, 

			failure: function() {
				insertCb.failure("An error occurred while uploading the file."); 
			},

			context: this 
		};
	
		CStudioAuthoring.Operations.uploadAsset(site, path, isUploadOverwrite, callback);
	},

	/**
	 * create preview URL
	 */
	createPreviewUrl: function(filePath) {
		return CStudioAuthoringContext.previewAppBaseUri + filePath + "?crafterSite=" + CStudioAuthoringContext.site;
	},

	/**
	 * clean up preview URL so that URL is canonical
	 */
	cleanPreviewUrl: function(previewUrl) {
		var url = previewUrl;
		
		if(previewUrl.indexOf(CStudioAuthoringContext.previewAppBaseUri) != -1) {
			url =  previewUrl.substring(CStudioAuthoringContext.previewAppBaseUri.length);
		}
		
		return url;	
	},

	deleteFile : function(path) {
	},

   	getInterface: function() {
   		return "file";
   	},

	getName: function() {
		return "file-desktop-upload";
	},
	
	getSupportedProperties: function() {
		return [
			{ label: "Repository Path", name: "repoPath", type: "string" }
		];
	},

	getSupportedConstraints: function() {
		return [
			{ label: "Required", name: "required", type: "boolean" },
		];
	}

});

CStudioAuthoring.Module.moduleLoaded("cstudio-forms-controls-file-desktop-upload", CStudioForms.Datasources.FileDesktopUpload);