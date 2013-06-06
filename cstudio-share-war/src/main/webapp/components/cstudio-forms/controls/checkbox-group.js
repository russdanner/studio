CStudioForms.Controls.CheckBoxGroup = CStudioForms.Controls.CheckBoxGroup ||  
function(id, form, owner, properties, constraints, readonly)  {
	this.owner = owner;
	this.owner.registerField(this);
	this.errors = []; 
	this.properties = properties;
	this.constraints = constraints;
	this.inputEl = null;
	this.countEl = null;
	this.required = false;
	this.value = "_not-set";
	this.form = form;
	this.id = id;
	this.readonly = readonly;
	this.minSize = 0;
	// Stores the type of data the control is now working with (this value is fetched from the datasource controller)
	this.dataType = null;

	amplify.subscribe("/datasource/loaded", this, this.onDatasourceLoaded);

	return this;
}

YAHOO.extend(CStudioForms.Controls.CheckBoxGroup, CStudioForms.CStudioFormField, {

    getLabel: function() {
        return "Grouped Checkboxes";
    },

	getRequirementCount: function() {
      		var count = 0;

            if(this.minSize > 0){
	            count++;
            }
      		
      		return count;
    },

    validate : function () {
    	if(this.minSize > 0) {
			if(this.value.length < this.minSize) {
				this.setError("minCount", "# items are required");
				this.renderValidation(true, false);
			}
			else {
				this.clearError("minCount");
				this.renderValidation(true, true);
			}
		}
		else {
			this.renderValidation(false, true);
		}
		this.owner.notifyValidation();
    },

    onDatasourceLoaded: function ( data ) {
    	if (this.datasourceName === data.name && !this.datasource) {
    		var datasource = this.form.datasourceMap[this.datasourceName];
    		this.datasource = datasource;
			this.dataType = datasource.getDataType();
			datasource.getList(this.callback);
    	}
    },

	render: function(config, containerEl, isValueSet) {
		this.containerEl = containerEl;
		this.config = config;
		
		var _self = this,
			datasource = null;
		
		for(var i=0;i<config.constraints.length;i++){
			var constraint = config.constraints[i];
			
			if(constraint.name == "minSize" && constraint.value != ""){
				this.minSize = parseInt(constraint.value);
			}
		}

		for(var i=0; i<config.properties.length; i++) {
			var prop = config.properties[i];

			if(prop.name == "datasource") {
				if(prop.value && prop.value != "") {
					this.datasourceName = (Array.isArray(prop.value))?prop.value[0]:prop.value;	
				}
			}

			if(prop.name == "selectAll" && prop.value == "true"){
				this.selectAll = true;
			}
			
			if(prop.name == "readonly" && prop.value == "true"){
				this.readonly = true;
			}
		}

        if(this.value === "_not-set" || this.value === "") {
            this.value = [];
        }

		var cb = {
			success: function(list) {
				var keyValueList = list,

					// setValue will provide an array with the values that were checked last time the form was saved (datasource A).
					// If someone decides to tie this control to a different datasource (datasource B): none, some or all of values 
					// from datasource A may be present in datasource B. If there were values checked in datasource A and they are 
					// also found in datasource B, then they will remain checked. However, if there were values checked in 
					// datasource A that are no longer found in datasource B, these need to be removed from the control's value.
					newValue = [],		
					rowEl, textEl, inputEl;
				
				containerEl.innerHTML = "";
				var titleEl = document.createElement("span");
		  		    YAHOO.util.Dom.addClass(titleEl, 'cstudio-form-field-title');
					titleEl.innerHTML = config.title;
				
				var controlWidgetContainerEl = document.createElement("div");
				YAHOO.util.Dom.addClass(controlWidgetContainerEl, 'cstudio-form-control-input-container');
		
				var validEl = document.createElement("span");
					YAHOO.util.Dom.addClass(validEl, 'cstudio-form-control-validation');
					controlWidgetContainerEl.appendChild(validEl);
		
				var groupEl = document.createElement("div");
				groupEl.className = "checkbox-group";

				if (_self.selectAll && !_self.readonly) {
					rowEl = document.createElement("label");
					rowEl.className = "checkbox select-all";
					rowEl.setAttribute("for", _self.id + "-all");

					textEl = document.createElement("span");
					textEl.innerHTML = "Select All";
		
					inputEl = document.createElement("input");
					inputEl.type = "checkbox";
					inputEl.checked = false;
					inputEl.id = _self.id + "-all";

					YAHOO.util.Event.on(inputEl, 'focus', function(evt, context) { context.form.setFocusedField(context) }, _self);
					YAHOO.util.Event.on(inputEl, 'change', _self.toggleAll, inputEl, _self);

					rowEl.appendChild(inputEl);
					rowEl.appendChild(textEl);
					groupEl.appendChild(rowEl);
				}

				controlWidgetContainerEl.appendChild(groupEl);

				for(var j=0; j<keyValueList.length; j++) {
					var item = keyValueList[j];
		
					rowEl = document.createElement("label");
					rowEl.className = "checkbox";
					rowEl.setAttribute("for", _self.id + "-" + item.key);

					textEl = document.createElement("span");
					textEl.innerHTML = item.value;
		
					inputEl = document.createElement("input");
					inputEl.type = "checkbox";

					if (_self.isSelected(item.key)) {
						newValue.push(_self.updateDataType(item));
						inputEl.checked = true;
					} else {
						inputEl.checked = false;
					}

					inputEl.id = _self.id + "-" + item.key;
					
					if(_self.readonly == true){
						inputEl.disabled = true;
					}
		
					YAHOO.util.Event.on(inputEl, 'focus', function(evt, context) { context.form.setFocusedField(context) }, _self);
					YAHOO.util.Event.on(inputEl, 'change', _self.onChange, inputEl, _self);
					inputEl.context = _self;
					inputEl.item = item;

					rowEl.appendChild(inputEl);
					rowEl.appendChild(textEl);
					groupEl.appendChild(rowEl);
				}
				_self.value = newValue;
				_self.form.updateModel(_self.id, _self.getValue());

				var helpContainerEl = document.createElement("div");
                YAHOO.util.Dom.addClass(helpContainerEl, 'cstudio-form-field-help-container');
                controlWidgetContainerEl.appendChild(helpContainerEl);

                _self.renderHelp(config, helpContainerEl);
				
				var descriptionEl = document.createElement("span");
					YAHOO.util.Dom.addClass(descriptionEl, 'cstudio-form-field-description');
					descriptionEl.innerHTML = config.description;

				containerEl.appendChild(titleEl);
				containerEl.appendChild(controlWidgetContainerEl);
				containerEl.appendChild(descriptionEl);

                // Check if the value loaded is valid or not
                _self.validate();
			}
		}

		if(isValueSet) {
			var datasource = this.form.datasourceMap[this.datasourceName];
			// This render method is currently being called twice (on initialization and on the setValue).
			// We need the value to know which checkboxes should be checked or not so restrict the rendering to only 
			// after the value has been set.
			if(datasource){
				this.datasource = datasource;
				this.dataType = datasource.getDataType();
				datasource.getList(cb);
			}else{
				this.callback = cb;
			}
		}
	},

	toggleAll: function (evt, el) {
		var ancestor = YAHOO.util.Dom.getAncestorByClassName(el, "checkbox-group"),
			checkboxes = YAHOO.util.Selector.query('.checkbox input[type="checkbox"]', ancestor),
			_self = this;
		
		this.value = [];
		this.value.length = 0;
		if (el.checked) {
			// select all
			checkboxes.forEach( function (el) {
				var valObj = {}

				el.checked = true;
				if (el.item) {
					// the select/deselect toggle button doesn't have an item attribute
					valObj.key = el.item.key;
					valObj[this.dataType] = el.item.value;
					_self.value.push(valObj);
				}
			});
		} else {
			// unselect all
			checkboxes.forEach( function (el) {
				el.checked = false;
			});
		}
		this.form.updateModel(this.id, this.getValue());
		this.validate();
	},
	
	onChange: function(evt, el) {
		var checked = (el.checked);
		
		if(checked) {
			this.selectItem(el.item.key, el.item.value);
		}
		else {
			this.unselectItem(el.item.key);
		}
		this.form.updateModel(this.id, this.getValue());
		this.validate();
	},

	isSelected: function(key) {
		var selected = false;
		var values = this.getValue();
		
		for(var i=0; i<values.length; i++) {
			if(values[i].key == key) {
				selected = true;
				break;
			}
		}
		return selected;
	},

	getIndex: function(key) {
		var index = -1;
		var values = this.getValue();
		
		for(var i=0; i<values.length; i++) {
			if(values[i].key == key) {
				index = i;
				break;
			}
		}
		
		return index;
	},
	
	selectItem: function(key, value) {
		var valObj = {};

		if(!this.isSelected(key)) {
			valObj.key = key;
			valObj[this.dataType] = value;

			this.value[this.value.length] = valObj;
		}
	},
	
	unselectItem: function(key) {
		var index = this.getIndex(key);
		
		if(index != -1) {
			this.value.splice(index, 1);
		}		
	},
	
	getValue: function() {
		return this.value;
	},

	updateDataType : function updateDataType (valObj) {
		if (this.dataType) {
			for (var prop in valObj) {
				if (prop.match(/value/)) {
					if (prop !== this.dataType) {
						// Rename the property (e.g. "value") to the current data type ("value_s")
						valObj[this.dataType] = valObj[prop];
						delete valObj[prop];
					}
				}
			}
			return valObj;	
		} else {
			return valObj;    // return the value object as it was (for backwards compatibility)
		}
	},
	
	setValue: function(value) {
		if(value === "") {
			value = [];
		}
		
		this.value = value;
		this.form.updateModel(this.id, this.getValue());
		this.render(this.config, this.containerEl, true);
	},
		
	getName: function() {
		return "checkbox-group";
	},
	
	getSupportedProperties: function() {
		return [
			{ label: "Data Source", name: "datasource", type: "datasource:item" },
			{ label: 'Show "Select All"', name: "selectAll", type: "boolean" },
			{ label: "Readonly", name: "readonly", type: "boolean" }
        ];
	},

	getSupportedConstraints: function() {
		return [
			{ label: "Minimum Selection", name:"minSize", type: "int"}
		];
	}

});

CStudioAuthoring.Module.moduleLoaded("cstudio-forms-controls-checkbox-group", CStudioForms.Controls.CheckBoxGroup);

