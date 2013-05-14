/**
 * Copy Dialog for copy showinf the copy tree
 *
 */
CStudioAuthoring.Dialogs = CStudioAuthoring.Dialogs || {};


CStudioAuthoring.Dialogs.DialogCopy = CStudioAuthoring.Dialogs.DialogCopy || {

    copyContext : {
        "heading":"Copy",
        "description":"Please select any of the sub-pages you would like to batch copy.<br/> When pasting, any selected sub-pages and their positional heirarchy will be retained.",
        "actionButton":"Copy"

    },

    cutContext : {
        "heading":"Cut",
        "description":"Please select any of the sub-pages you would like to batch cut.<br/> When pasting, any selected sub-pages and their positional heirarchy will be retained.",
        "actionButton":"Cut"
    },
    /**
     * initialize module
     */
    initialize: function(config) {

    },

    /**
     * show dialog
     */
    showDialog: function(site, content, cut) {

        this.cut = cut;
        this.context = cut ? this.cutContext : this.copyContext;
        this.context.request = cut ? CStudioAuthoringContext.baseUri + "/service/cstudio/services/clipboard/cut?site=" + site :
                               CStudioAuthoringContext.baseUri + "/service/cstudio/services/clipboard/copy?site=" + site;
        this._self = this;
        this.item = content.item;
        this.dialog = this.createDialog(site, this.item);
        this.dialog.show();
        this.dialog.cfg.setProperty("zIndex", 100001);  // Update the z-index value to make it go over the site content nav

        //set focus on Copy button
        var copyButton = YDom.get("copyButton");
        if (copyButton) {
            CStudioAuthoring.Utils.setDefaultFocusOn(copyButton);
        }
    },

    closeDialog:function() {
        this.dialog.hide();
        var element = YDom.get("cstudio-wcm-popup-div");
        element.parentNode.removeChild(element);
    },
    
    /*
    createPopupHTML: function(item, flatMap) {
		var aURIs = [];
        item.parent = null;
        var popupHTML = [
			'<div id="copyCheckBoxItems">',
				this.traverse(item, flatMap, aURIs),
			'</div>',
			'<div style="position:absolute;top:0;right:0;width:225px;background-color:#FFF;">',
				aURIs.join(""),
			'</div>'
		].join("");
        return popupHTML;
    },*/

    createPopupHTML: function(item, flatMap) {
		var aURIs = [];
        item.parent = null;
        var popupHTML = [
			'<div id="copyCheckBoxItems" style="padding-left:5px;">',
				this.traverse(item, flatMap, aURIs),
			'</div>'
		].join("");
        return popupHTML;
    },
    
    traverse: function(item, flatMap, aURIs) {
        var itemIconClass = CStudioAuthoring.Utils.getIconFWClasses(item);
        var html = "<ul>" +
                   '<input type=\"checkbox\" id=\"' + item.uri + '\" checked=\"true\"/>' +
                   '<div class="' + itemIconClass + '" >' +
                   '<div>' + item.internalName + '</div> ' +
                   '</div>' ;
		aURIs && aURIs.push('<div style="margin:0 0 5px 0">' + item.browserUri + "</div>");
        flatMap[item.uri] = item;
        var children = item.children;
        if (children) {
            for (var i = 0; i < children.length; i++) {
                var child = children[i];
                child.parent = item;
                html += "<li>" + this.traverse(child, flatMap, aURIs) + "</li>";
            }
        }
        return html + "</ul>";
    },

    createSelectedItems:function(item, selectedItems) {
        var createItem = function(item, selectedItems) {
            if (selectedItems[item.uri] == "selected") {
                var newItem = {};
                newItem.uri = item.uri;
                var children = item.children;
                if (children) {
                    for (var i = 0; i < children.length; i++) {
                        var child = children[i];
                        var newChild = createItem(child, selectedItems);
                        if (newChild != null) {
                            if (!newItem.children) {
                                newItem.children = [];
                            }
                            newChild.uri = child.uri;
                            newItem.children.push(newChild);
                        }
                    }
                }
                return newItem;

            } else {
                return null;
            }
        };
        var rootItem = createItem(item, selectedItems);
        var pasteFormatItem = {};
        pasteFormatItem.item = [];
        pasteFormatItem.item.push(rootItem);
        return pasteFormatItem;
    },

    createDialog: function(site, item) {
        var context = CStudioAuthoring.Dialogs.DialogCopy.context;
        var flatMap = {};
        YDom.removeClass("cstudio-wcm-popup-div", "yui-pe-content");
        var html = this.createPopupHTML(item, flatMap);
        var newdiv = document.createElement("div");
        var divIdName = "cstudio-wcm-popup-div";
        newdiv.setAttribute("id", divIdName);
        newdiv.className = "yui-pe-content";
        newdiv.innerHTML = '<style>div#copyCheckBoxItems .status-icon{padding-left: 5px !important;}</style><div class="contentTypePopupInner" id="contentTypePopupInner">' +
                           '<div class="contentTypePopupContent" id="contentTypePopupContent"> ' +
                           '<div class="contentTypePopupHeader">' + context['heading'] + '</div> ' +
                           '<div>' + context['description'] + '</div> ' +
                           '<div class="copy-content-container">' +
                           '<h5>' +
                           '<span>Page</span>' +
                           '</h5>' +
                           '<div class="scrollBox">' +
                           html +
                           '</div>' +
                           '</div>' +
                           '<div class="contentTypePopupBtn"> ' +
                           '<input type="submit" class="cstudio-xform-button ok" id="copyButton" value="' + context['actionButton'] + '" />' +
                           '<input type="submit" class="cstudio-xform-button cancel" id="copyCancelButton" value="Cancel" />' +
                           '</div> ' +
                           '</div> ' +
                           '</div>';
        document.body.appendChild(newdiv);
        var rootElement = YDom.get(item.uri);
        rootElement.checked=true;
        var content_type_dialog = new YAHOO.widget.Dialog("cstudio-wcm-popup-div",
        { width : "608px",
            height : "444px",
            fixedcenter : true,
            visible : false,
            modal:true,
            close:false,
            constraintoviewport : true,
            underlay:"none",
            autofillheight: null
        });

        // Render the Dialog
        content_type_dialog.setBody("bd");
        content_type_dialog.render();

        var eventParams = {
            self: this
            //			path: path,
            //			selectTemplateCb: selectTemplateCb
        };

         function onCutCheckBoxSubmittedItemClick(event,matchedEl){
             function selectAll(checked) {
                 for (var key in flatMap) {
                     var aItem = flatMap[key];
                     var inputItem = YDom.get(aItem.uri);
                     inputItem.checked = checked;
                 }
             }

             selectAll(matchedEl.checked);
         }
        function onCopyCheckBoxSubmittedItemClick(event, matchedEl) {
            if (matchedEl.id == CStudioAuthoring.Dialogs.DialogCopy.item.uri) {
                matchedEl.checked = true;
                return;
            }
            var selectedItemURI = matchedEl.id;
            var selectedItem = flatMap[selectedItemURI];
            if (matchedEl.checked) {
                selectParents(selectedItem, true);

            } else {
                selectChildren(selectedItem, false);
            }


            function selectParents(selectedItem,checked) {
                while (selectedItem.parent != null) {
                    selectedItem = selectedItem.parent;
                    var inputElement = YDom.get(selectedItem.uri);
                    inputElement.checked = checked;
                }

            }

            function selectChildren(selectedItem,checked) {
                var children = selectedItem.children;
                if (children == null || children.length == 0) {
                    return;
                }
                for (var i = 0; i < children.length; i++) {
                    var child = children[i];
                    var uri = child.uri;
                    var inputChild = YDom.get(uri);
                    inputChild.checked = checked;
                    var selectedChild = flatMap[uri];
                    selectChildren(selectedChild,checked);
                }
            }
        }

        function onCopySubmit(event, matchedEl) {

            var container = YDom.get("contentTypePopupInner");
            var inputItems = container.getElementsByTagName("input");
            var selectedIds = {};
            for (var i = 0; i < inputItems.length; i++) {
                if (inputItems[i].checked) {
                    var selectedURI = inputItems[i].id;
                    selectedIds[selectedURI] = "selected";
                }

            }
            YDom.get("copyButton").disabled = true;
            YDom.get("copyCancelButton").disabled = true;
            var item = CStudioAuthoring.Dialogs.DialogCopy.item;

            var newItem = CStudioAuthoring.Dialogs.DialogCopy.createSelectedItems(item, selectedIds);
            var myJSON = YAHOO.lang.JSON.stringify(newItem);
            var oncomplete = {
                success:function() {
                    CStudioAuthoring.Dialogs.DialogCopy.closeDialog();
                    CStudioAuthoring.ContextualNav.WcmRootFolder.resetNodeStyles();
                },
                failure:function() {
                    YDom.get("copyButton").disabled = false;
                    YDom.get("copyCancelButton").disabled = false;
                }
            };  
            var request = context['request'];
            //var request = CStudioAuthoringContext.baseUri + "/service/cstudio/services/clipboard/cut?site=" + site; //Review I will remove hardcoding
            YAHOO.util.Connect.setDefaultPostHeader(false);
            YAHOO.util.Connect.initHeader("Content-Type", "application/json; charset=utf-8");
            YAHOO.util.Connect.asyncRequest('POST', request, oncomplete, myJSON);

        }

        if (CStudioAuthoring.Dialogs.DialogCopy.cut) {
            YAHOO.util.Event.delegate("contentTypePopupInner", "click", onCutCheckBoxSubmittedItemClick, "input[type=\"checkbox\"]");
        } else {
            YAHOO.util.Event.delegate("contentTypePopupInner", "click", onCopyCheckBoxSubmittedItemClick, "input[type=\"checkbox\"]");
        }
        YAHOO.util.Event.addListener("copyButton", "click", onCopySubmit);
        YAHOO.util.Event.addListener("copyCancelButton", "click",
                function() {
                    CStudioAuthoring.Dialogs.DialogCopy.closeDialog();

                }
                );

        return content_type_dialog;
    }



};

CStudioAuthoring.Module.moduleLoaded("dialog-copy", CStudioAuthoring.Dialogs.DialogCopy);
