CStudioForms.Datasources.ConfiguredList = CStudioForms.Datasources.ConfiguredList ||  
function(id, form, properties, constraints)  {
   	this.id = id;
   	this.form = form;
   	this.properties = properties;
   	this.constraints = constraints;
	this.callbacks = [];
	var _self = this;
	
	for(var i=0; i<properties.length; i++) {
		var property = properties[i]
		if(property.name == "listName") {
			var cb = { 
				success: function(config) {
					var values = config.values;
					if(!values.length) {
						values = [ values.value ];
					}
					
					_self.list = values;
					
					for(var j=0; j<_self.callbacks.length; j++) {
						_self.callbacks[j].success(values);
					}
				},
				failure: function() {
				}
			};
			
			CStudioAuthoring.Service.lookupConfigurtion(
					CStudioAuthoringContext.site, 
					"/form-control-config/configured-lists/" + property.value + ".xml",
					cb);
				
		}
	}
	
	return this;
}

YAHOO.extend(CStudioForms.Datasources.ConfiguredList, CStudioForms.CStudioFormDatasource, {

    getLabel: function() {
        return "Configured List of Values";
    },

   	getInterface: function() {
   		return "item";
   	},

	getName: function() {
		return "configured-list";
	},
	
	getSupportedProperties: function() {
		return [
			{ label: "List Name", name: "listName", type: "string" }
		];
	},

	getSupportedConstraints: function() {
		return [
			{ label: "Required", name: "required", type: "boolean" },
		];
	},
	
	getList: function(cb) {
		if(!this.list) {
			this.callbacks[this.callbacks.length] = cb;
		}
		else {
			cb.success(this.list);
		}
	},
	

});

CStudioAuthoring.Module.moduleLoaded("cstudio-forms-controls-configured-list", CStudioForms.Datasources.ConfiguredList);