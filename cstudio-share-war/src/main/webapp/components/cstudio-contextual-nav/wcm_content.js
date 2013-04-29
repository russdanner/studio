/**
 * Active Content Plugin
 */
CStudioAuthoring.ContextualNav.WcmActiveContentMod = CStudioAuthoring.ContextualNav.WcmActiveContentMod || {

	initialized: false,
	
	/**
	 * initialize module
	 */
	initialize: function(config) {
		if(!CStudioAuthoring.ContextualNav.WcmActiveContent) {
			this.renderActiveContent();
			CStudioAuthoring.ContextualNav.WcmActiveContent.init();
		}
	},
	
	renderActiveContent: function() {
    var YDom = YAHOO.util.Dom,
        YEvent = YAHOO.util.Event,
        navWcmContent,
        _this; // Reference to CStudioAuthoring.ContextualNav.WcmActiveContent
     	contextPath = location.protocol + "//" + location.hostname + ":" + location.port;

    /**
     * WCM Site Dropdown Contextual Active Content
     */
    _this = CStudioAuthoring.register({
        "ContextualNav.WcmActiveContent": {
            options: [
                { name: "Edit", allowAuthor: true, allowAdmin: true, allowBulk: false, renderId: "Edit" },
                { name: "Submit to Go Live", allowAuthor: true, allowAdmin: false, allowBulk: true, renderId: "SimpleSubmit"  },
                { name: "Delete", allowAuthor: false, allowAdmin: true, allowBulk: true, renderId: "Delete"  },
                { name: "Submit for Delete", allowAuthor: true, allowAdmin: false, allowBulk: true, renderId: "ScheduleForDelete"  },
                { name: "Reject", allowAuthor: true, allowAdmin: true, allowBulk: true, renderId: "Reject"  },
                { name: "Schedule", allowAuthor: true, allowAdmin: true, allowBulk: true, renderId: "ApproveSchedule"  },
                { name: "Go Live Now", allowAuthor: true, allowAdmin: true, allowBulk: true, renderId: "Approve"  },                
                { name: "Duplicate", allowAuthor: true, allowAdmin: true, allowBulk: false, renderId: "Duplicate" },
                { name: "History", allowAuthor: true, allowAdmin: true, allowBulk: false, renderId: "VersionHistory" }
            ],

            /**
             * initialize widget
             */
            init: function() {
                CStudioAuthoring.Events.contentSelectUpdate.subscribe(function(evtName, contentTO) {
                    _this.drawNav();
                });
                for(var i = 0,
                        opts = this.options,
                        l = opts.length,
                        opt = opts[0]; i<l; opt = opts[++i]) {
                    opts[i].renderer = this["render" + opt.renderId];
                }
                navWcmContent = _this;
                YEvent.onAvailable("acn-active-content", function(parentControl) {
                    parentControl.containerEl = YDom.get("acn-active-content");
                    navWcmContent.drawNav();
                    
                    
                    CStudioAuthoring.Events.moduleActiveContentReady.fire();                    
                }, this);
            },
            /**
             * render the navigation bar
             */
            drawNav: function() {
                var selectedContent = CStudioAuthoring.SelectedContent.getSelectedContent();

				var callback = {
			        success: function(isWrite, perms) {
						this._self._drawNav(selectedContent, isWrite, perms);

	                    if(CStudioAuthoringContext.isPreview == true
	                    && selectedContent[0].disabled == true) { 
		                   var noticeEl = document.createElement("div");
		                    this._self.containerEl.parentNode.parentNode.appendChild(noticeEl);
		                   YDom.addClass(noticeEl, "acnDisabledContent");
		                   noticeEl.innerHTML = "Page is disabled: When deployed, a live visitor will see page not found (404) error.";
	                    
	                    }

                    },
					failure: function() {
						//TDOD: log error, not mute it
					},
					
					selectedContent: selectedContent,
					_self: this
                };

				if(CStudioAuthoring.SelectedContent.getSelectedContent().length != 0) {
					// TODO: This looks like a bug to me, why are we only checking the first item?
					// the context nav always shows the options available for the aggregate.
			    	this.checkWritePermission(selectedContent[0].uri, callback);
			    	
				} else {
					this.renderSelectNone();
				}

            },
            
			/**
             * draw navigation after security check on item
			 */
 			_drawNav: function(selectedContent, isWrite, perms) {
                var icon = "";
                var isAdmin = (CStudioAuthoringContext.role == "admin");
                var isBulk = true;
                var isRelevant = true;
                var state = "";
                var prevState = "";
                var auxIcon = "";
                var isInFlight = false;
                if(selectedContent.length == 0) {
                    this.renderSelectNone();
                } else {
                    if(selectedContent.length > 1) {
                        var i, auxState, count = 0, iconsCount = 0, l = selectedContent.length, newFileFlag = true;
                        for(i=0; i< l; i++) {
                            auxState = CStudioAuthoring.Utils.getContentItemStatus(selectedContent[i], true);
                            auxIcon = CStudioAuthoring.Utils.getIconFWClasses(selectedContent[i]);
                            if (newFileFlag && !selectedContent[i].newFile) {
                                newFileFlag = false;
                            }
                            if (prevState != auxState) {
                                prevState = auxState;
                                state += auxState + "|";
                                count++;
                            }
                            if (icon != auxIcon) {
                                icon = auxIcon;
                                iconsCount++;
                            }
                            if (selectedContent[i].deleted) {
                                isRelevant = false;
                            }
                        }
                        (count > 1) && (icon = "");
                        (iconsCount > 1) && (icon = "");
                        if (newFileFlag) {
                            state += "*";
                        }
                    } else {
                        isBulk = false;
                        state = CStudioAuthoring.Utils.getContentItemStatus(selectedContent[0], true);
                        icon = CStudioAuthoring.Utils.getIconFWClasses(selectedContent[0]);
                        isInFlight = selectedContent[0].inFlight;
                        
                        if(selectedContent[0].lockOwner != "") {
                        	if(selectedContent[0].lockOwner != CStudioAuthoringContext.user) {
                            	isWrite = false;
                        	}
                        	else {
                            	isWrite = true;                        		
                        	}
                        }


                        if (selectedContent[0].deleted) {
                            isRelevant = false;
                        }
                        if (selectedContent[0].newFile) {
                            state += "*";
                        }
                    }
                    
                    this.renderSelect(icon, state, isBulk, isAdmin, isRelevant, isInFlight, isWrite, perms);
                }
                //add class to remove border from last item - would be more efficient using YUI Selector module, but it's currently not loaded
                var itemContainer = document.getElementById('acn-active-content');
                if (itemContainer.hasChildNodes()){
                    var lastItem = itemContainer.lastChild;
                    lastItem.className += ' acn-link-last';
					// override background for first menu item
					if (itemContainer.children.length > 0) {
						var secondItem = itemContainer.children[1];
						if (secondItem) {
							secondItem.style.background = 'none';
						}
					}
                }
			},
 
 			/**
 			 * check permissions on the given path 
 			 */
            checkWritePermission: function(path, callback) {
				//Get user permissions to get read write operations
				var checkPermissionsCb = {
		        	success: function(results) {
						var isWrite = CStudioAuthoring.Service.isWrite(results.permissions);
						var isUserAllowed = CStudioAuthoring.Service.isUserAllowed(results.permissions);
						
						if (isWrite && isUserAllowed) {
							isWrite = true;
						} else {
							isWrite = false;
						}
						callback.success(isWrite, results.permissions);
					},
					failure: function() { }
	        	};
				CStudioAuthoring.Service.getUserPermissions(CStudioAuthoringContext.site, path, checkPermissionsCb);
			},

            /**
             * select none
             */
            renderSelectNone: function() {
                this.containerEl.innerHTML = "";
            },

            /**
             * render many items
             */
            renderSelect: function(icon, state, isBulk, isAdmin, isRelevant, isInFlight, isWrite, perms) {
                this.containerEl.innerHTML = "";
                var navLabelEl = document.createElement("div");
                YDom.addClass(navLabelEl, [icon, 'context-nav-title-element'].join(" "));
                if(!isBulk) {
                    if (isInFlight) {
                        navLabelEl.innerHTML = " " + state;
                    } else {
                        navLabelEl.innerHTML = " " + state + ":";
                    }
                } else {
                    var statSplit = state.split("|");
                    if (icon != "" && statSplit.length == 2) {
                        if (statSplit[1] == "*") {
                            navLabelEl.innerHTML = " " + statSplit[0] + "*:";
                        } else {
                            navLabelEl.innerHTML = " " + statSplit[0] + ":";
                        }
                    } else {
                        navLabelEl.innerHTML = " Items (Mixed States):";
                    }
                }
                this.containerEl.appendChild(navLabelEl);
                for(var i=0; i<this.options.length; i++) {
                    var option = this.options[i];
                    if (isInFlight != undefined && isInFlight != null) {
                        option.isInFlight = isInFlight;
                    }
                    if(!option.renderer) {
                        navWcmContent.createNavItem(option, isBulk, isAdmin, true, false, perms);
                    } else{
                        option.renderer.render(option, isBulk, isAdmin, state, isRelevant, isWrite, perms);
                    }
                }
            },
            /**
             * render new option
             */
            renderNew: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite) {
                    option.onclick = function() {
                            CStudioAuthoring.Operations.createNewContent(
                                CStudioAuthoringContext.site,
                                CStudioAuthoring.SelectedContent.getSelectedContent()[0].uri);
                    };
                    _this.createNavItem(option, isBulk, isAdmin, true, !isWrite);
                }
            },
            /**
             * handle edit
             */
            renderEdit: {
                render: function(option, isBulk, isAdmin, state,  isRelevant, isWrite) {
            		var editCallback = {
            			success: function() {
            				this.callingWindow.location.reload(true);
            			},
            			failure: function() {			
            			},
            			callingWindow : window
            		};

            		var viewCb = {
            			success: function() {
               			},
            			failure: function() {			
            			},
            			callingWindow : window
            		};
            		
                    var content = CStudioAuthoring.SelectedContent.getSelectedContent()[0];
                    option.onclick = function() {

                        if (isWrite == false) {
                            CStudioAuthoring.Operations.viewContent(
                                content.form,
                                CStudioAuthoringContext.siteId,
                                content.uri,
                                content.nodeRef,
                                content.uri,
                                false,
                                viewCb);
                        }
                        else {
                            CStudioAuthoring.Operations.editContent(
                                content.form,
                                CStudioAuthoringContext.siteId,
                                content.uri,
                                content.nodeRef,
                                content.uri,
                                false,
                                editCallback);
                        }
                    };
                    // relevant flag, allowing document & banner to be editable from Search result
                    // allowing banner type component 
                    // alowing crafter-level-descriptor.xml
                    var rflag = ((content.previewable || content.document 
                                  || ( (content.component) && ( (content.contentType == "/cstudio-com/component/general/banner") 
                                  || (content.contentType.indexOf("level-descriptor") !=-1 ) ) )) && (state.indexOf("Delete") == -1));
					 //if item is deleted and in the go live queue , enable edit.			  
					if(state.indexOf("Submitted for Delete")>=0 || state.indexOf("Scheduled for Delete")>=0) {
                        rflag =  true;
                    }

					/** for edit, if in read-only mode, it should display View, not Edit **/
					if (isWrite == false) {
						option.name = "View";
					}
					else {
						option.name = "Edit";
					}			

                    _this.createNavItem(option, isBulk, isAdmin, rflag, false, !isWrite);
                }
            },
            /**
             * handle duplicate
             */
            renderDuplicate: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite) {
            		if(isWrite) {
	            		var duplicateContentCallback = {
	                		success : function() {
	                			YDom.get("duplicate-loading").style.display = "none";
	                		},
	                		failure: function() {
	                			YDom.get("duplicate-loading").style.display = "none";
	                		}
	                	};
	                    var content = CStudioAuthoring.SelectedContent.getSelectedContent()[0];
	                    option.onclick = function() {
	                    	YDom.get("duplicate-loading").style.display = "block";                    	
	                        CStudioAuthoring.Operations.duplicateContent(
	                                CStudioAuthoringContext.site,
	                                content.uri,
	                                duplicateContentCallback);
	                    };
	
	                    if(content.document || content.component) { // for doc and components disable dublicate link
	                        isRelevant = false;
	                    } else {
	                        isRelevant = content.previewable;	
	                    }
	                    _this.createNavItem(option, isBulk, isAdmin, isRelevant, !isWrite);
            		}
                }
            },
            /**
             * render submit option
             */
            renderSimpleSubmit: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite, perms) {
				
					if(CStudioAuthoring.Service.isPublishAllowed(perms)) {
						return;
					}
					
					if(isWrite) {
	                    var isRelevant = false;
	                    if (state.indexOf("In Progress")>=0 || state.indexOf("Deleted")>=0 || state.indexOf("Submitted") != -1) {
	                        isRelevant = true;
	                    }
	                    if(state.indexOf("Submitted for Delete")>=0 || state.indexOf("Scheduled for Delete")>=0) {
	                        isRelevant = true;
	                    }
	
	                    //Check for live items
	                    var content = CStudioAuthoring.SelectedContent.getSelectedContent();
	                    if (isRelevant && content && content.length >= 1) {
	                        for (var conIdx=0; conIdx<content.length; conIdx++) {
	                            var auxState = CStudioAuthoring.Utils.getContentItemStatus(content[conIdx]);
	                            if (auxState == "Live") {
	                                isRelevant = false;
	                                break;
	                            }
	                        }
	                    }
	
	                    option.onclick = function() {
	                            CStudioAuthoring.Operations.submitContent(
	                                CStudioAuthoringContext.site,
	                                CStudioAuthoring.SelectedContent.getSelectedContent());
	                    };
	                    _this.createNavItem(option, isBulk, isAdmin, isRelevant, !isWrite);
	                }
	            }
            },

	        renderScheduleForDelete: {
		    	render: function(option, isBulk, isAdmin, state, showFlag, isWrite) {
	            	var isRelevant = false;
	                    
	                if(isWrite) {
		                    //Schedule for Delete link should visible only from wcm search pages.
		                    var isInSearchForm = YDom.getElementsByClassName("cstudio-search-result");
		                    if (showFlag && isInSearchForm && isInSearchForm.length >= 1) {
		                        if (showFlag) {
		                            isRelevant = true;
		                        }
		                    
		                        if(state.indexOf("Submitted for Delete")>=0 || state.indexOf("Scheduled for Delete")>=0) {
		                            isRelevant = true;
		                        }
		                        option.onclick = function(){
		                            CStudioAuthoring.Operations.deleteContent(
		                                CStudioAuthoring.SelectedContent.getSelectedContent());
			           		}
		                }
	                    
		            	_this.createNavItem(option, isBulk, isAdmin, isRelevant, !isWrite);
		       		}

	            }
            },
            
            renderDelete: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite) {
                	if(isWrite) {
	                    var isRelevant = true;
	                    var isAdminFlag = isAdmin;
	                    if(state.indexOf("Submitted for Delete")>=0 || state.indexOf("Scheduled for Delete")>=0) {
	                        isRelevant = false;
	                        isAdminFlag =  false;
	                    }
	                    option.onclick = function() {
	                        CStudioAuthoring.Operations.deleteContent(
	                            CStudioAuthoring.SelectedContent.getSelectedContent());
	                    }
	                    _this.createNavItem(option, isBulk, isAdminFlag, true, !isWrite);
	                }
                }
            },
            renderVersionHistory: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite) {
					if(isWrite){
                    	option.onclick = function() {
                        	CStudioAuthoring.Operations.viewContentHistory(
                        		CStudioAuthoring.SelectedContent.getSelectedContent()[0]);
                    	}
                    	//Making this link false as this feature is not yet completed.
                    	_this.createNavItem(option, isBulk, isAdmin, true, !isWrite);
					}
                }
            },
            /**
             * render approve / golive option
             */
            renderApprove: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite, perms) {
                    if(CStudioAuthoring.Service.isPublishAllowed(perms)) {
	                    var lowerstate = state.toLowerCase(),
	                        isRelevant = true;
	                    if ( lowerstate.indexOf("live") != -1) {
	                      isRelevant = false;
	                    }
	                    option.onclick = function() {
	                            CStudioAuthoring.Operations.approveContent(
	                                CStudioAuthoringContext.site,
	                                CStudioAuthoring.SelectedContent.getSelectedContent());
	                    };
	                    _this.createNavItem(option, isBulk, isAdmin, isRelevant, false);
                    }
                }
            },
            /**
             * render approve-schedule option
             */
            renderApproveSchedule: {
                render: function(option, isBulk, isAdmin, state,  isRelevant, isWrite, perms) {
                    if(CStudioAuthoring.Service.isPublishAllowed(perms)) {
	                    var lowerstate = state.toLowerCase(),
	                        isRelevant = true;
	                    if ( lowerstate.indexOf("live") != -1) {
	                      isRelevant = false;
	                    }
	                    option.onclick = function() {
	                    	CStudioAuthoring.Operations.approveScheduleContent(
	                                CStudioAuthoringContext.site,
	                                CStudioAuthoring.SelectedContent.getSelectedContent() );
	                    };
	                    _this.createNavItem(option, isBulk, isAdmin, isRelevant, false);
                    }
                }
            },
            /**
             * render reject option
             */
            renderReject: {
                render: function(option, isBulk, isAdmin, state, isRelevant, isWrite, perms) {
                    if(CStudioAuthoring.Service.isPublishAllowed(perms)) {
	                    var isRelevant = false;
	                    
	                    if ( (state.indexOf("Submitted") != -1 || state.indexOf("Scheduled") != -1 || state.indexOf("Deleted") != -1) &&
	                          state != "Scheduled" ) {
	                        isRelevant = true;
	                    }
	
	                    //Check that all selected items are from go-live queue or not
	                    var content = CStudioAuthoring.SelectedContent.getSelectedContent();
	                    if (isRelevant && content && content.length >= 1) {
	                        for (var conIdx=0; conIdx<content.length; conIdx++) {
	                            var auxState = CStudioAuthoring.Utils.getContentItemStatus(content[conIdx]);
	                            if ( (auxState.indexOf("Submitted") != -1 || auxState.indexOf("Scheduled") != -1 || auxState.indexOf("Deleted") != -1) &&
	                                  auxState != "Scheduled") {
	                                  //Here is special case for sheduled for delted items.
	                                  if ((auxState == "Submitted for Delete" || auxState == "Scheduled for Delete") && !content[conIdx].submitted && content[conIdx].scheduled) {
	                                    isRelevant = false;
	                                    break;
	                                  } else {
	                                    isRelevant = true;
	                                  }
	                            } else {
	                                isRelevant = false;
	                                break;
	                            }
	                        }
	                    }
	
	                    option.onclick = function() {
	                            CStudioAuthoring.Operations.rejectContent(
	                                CStudioAuthoringContext.site,
	                                CStudioAuthoring.SelectedContent.getSelectedContent());
	                    };
	                    _this.createNavItem(option, isBulk, isAdmin, isRelevant, false);
                    }
                }
            },
            /**
             * copy paste needs to be dynamic.  if there are items on the clipboard
             * it needs to say paste.  if there are no items on the cliploard it needs to
             * say copy.
             * question: how do you clear the clipboard or see whats on it?  I think we may want this to
             * be a dropdown?
             */
            renderclipboard: {
                render: function(option) {
                    option.name = "Copy";
                    _this.createNavItem(option, true, true);
                }
            },
            /**
             * create simple name item
             */
            createNavItem: function(item, isBulk, isAdmin, isRelevant, disableItem) {
                var parentEl = this.containerEl;
                var showItem = (!item.isInFlight && ((isAdmin && item.allowAdmin) || (!isAdmin && item.allowAuthor)));
                if(showItem) {
                    /* Do not attach items if links are not relevant */
                    if(!isRelevant || (isBulk && !item.allowBulk))
                        return;

                    var linkContainerEl = document.createElement("div"),
                        linkEl = document.createElement("a");
                                        
                    YDom.addClass(linkContainerEl, "acn-link");
                    linkEl.innerHTML = item.name;
                    YDom.addClass(linkEl, "cursor");
                    linkEl.style.cursor = 'pointer';

					if(disableItem == true) {
				    	YDom.addClass(linkEl, "acn-link-disabled");
					/* not setting onclick either*/
				    } else {
                    	if(item.onclick) {
                        	linkEl.onclick = item.onclick;
                    	} else {
                        	linkEl.onclick = function(){ alert("no event handler associated"); };
                    	}
					}

                    var dividerEl = document.createElement("div");
                    dividerEl.id = "acn-render";

                    parentEl.appendChild(linkContainerEl);
                    linkContainerEl.appendChild(linkEl);
                    
                    /**
                     * adding ajax status image for item who has renderId
                     */
                    if(item.renderId != null) {
                    	var loadingImageEl = document.createElement("img");
                    		loadingImageEl.id = item.renderId.toLowerCase() + "-loading";
                    		loadingImageEl.src = contextPath + CStudioAuthoringContext.baseUri + "/themes/cstudioTheme/images/treeview-loading.gif";                    		                    		
                    	linkContainerEl.appendChild(loadingImageEl);                    		
                    }
                }
            },

			isAllowedEditForSelection: function(){     	
            	var contentItem = CStudioAuthoring.SelectedContent.getSelectedContent()[0];
            	
            	// Edits etc are not allowed on asset items.
            	if (contentItem.asset){
            		return false;
            	}
            	
				return true;
				
			},        

            areSomeSelectedItemsLockedOut : function() {
            	var itemLockedOut = false;
            	var selectedItems = CStudioAuthoring.SelectedContent.getSelectedContent();           	
            	for (var i = 0; !itemLockedOut && i < selectedItems.length; i++) {
            		if (CStudioAuthoring.Utils.isLockedOut(selectedItems[i])) {
            			itemLockedOut = true;
            		}
            	}
            	
            	return itemLockedOut;
            }
        }
    });
	}
}

CStudioAuthoring.Module.moduleLoaded("wcm_content", CStudioAuthoring.ContextualNav.WcmActiveContentMod);
