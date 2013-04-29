CStudioForms.Controls.AutoFilename = CStudioForms.Controls.AutoFilename ||  
function(id, form, owner, properties, constraints)  {
	var _self = this;
	this.owner = owner;
	this.owner.registerField(this);
	this.errors = []; 
	this.properties = properties;
	this.constraints = constraints;
	this.inputEl = null;
	this.countEl = null;
	this.required = true;
	this.value = "_not-set";
	this.form = form;
	this.id = "file-name";
	this.contentAsFolder = true;
		
	return this;
}

YAHOO.extend(CStudioForms.Controls.AutoFilename, CStudioForms.CStudioFormField, {

    getLabel: function() {
        return "Auto Filename";
    },
    
	render: function(config, containerEl) {
		// this widget has no visual presentation
	},

	getValue: function() {
		return this.value;
	},

    setValue: function(value) {
        var filename = value;

        if(filename == "") {
            // if value has not been set, use the item's object ID as the filename
            filename = this.form.model["objectId"] + ".xml";
        }

        this.value = filename;

        if(this.contentAsFolder == true || this.contentAsFolder == "true") {
            this.form.updateModel("file-name", "index.xml");
            this.form.updateModel("folder-name", this.form.model["objectId"]);
            //this.value = "index.xml";
        }
        else {
            this.form.updateModel("file-name", filename);
            obj.form.updateModel("folder-name", "");
        }
    },

	getName: function() {
		return "auto-filename";
	},
	
	getSupportedProperties: function() {
		return [
			];
	},

	getSupportedConstraints: function() {
		return [ // required is assumed
		];
	}

});


CStudioAuthoring.Module.moduleLoaded("cstudio-forms-controls-auto-filename", CStudioForms.Controls.AutoFilename);