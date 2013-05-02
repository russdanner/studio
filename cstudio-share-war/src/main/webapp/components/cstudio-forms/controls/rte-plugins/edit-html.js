/* global CStudioAuthoring, CStudioAuthoringContext, CStudioForms, YAHOO, CodeMirror, tinymce */

CStudioAuthoring.Module.requireModule(
	'codemirror',
    '/components/cstudio-common/codemirror/lib/codemirror.js',
	{  },
	{ moduleLoaded: function() {

		CStudioAuthoring.Utils.addJavascript('/components/cstudio-common/codemirror/mode/xml/xml.js');
		CStudioAuthoring.Utils.addJavascript('/components/cstudio-common/codemirror/mode/javascript/javascript.js');
		CStudioAuthoring.Utils.addJavascript('/components/cstudio-common/codemirror/mode/htmlmixed/htmlmixed.js');
		CStudioAuthoring.Utils.addJavascript('/components/cstudio-common/codemirror/mode/css/css.js');
		CStudioAuthoring.Utils.addCss('/components/cstudio-common/codemirror/lib/codemirror.css');
		CStudioAuthoring.Utils.addCss('/themes/cstudioTheme/css/template-editor.css');

		var YDom = YAHOO.util.Dom,
			componentSelector = '.crComponent';

		CStudioForms.Controls.RTE.EditHTML = CStudioForms.Controls.RTE.EditHTML || {
			init: function(ed, url) {
				var t = this;

				ed.addCommand('mceEditHtmlCode', function() {

					if (!ed.controlManager.get('edithtml').active) {
						// Enable code view
						ed.controlManager.setActive('edithtml', true);
						t.enableCodeView(ed);
					} else {
						// Disable code view
						ed.controlManager.setActive('edithtml', false);
						t.disableCodeView(ed);
					}
				});

				ed.addButton('edithtml', {
					title : 'Edit Code',
					cmd : 'mceEditHtmlCode',
					image: CStudioAuthoringContext.authoringAppBaseUri+'/themes/cstudioTheme/images/icons/code-edit.gif'
				});
			},

			resizeCodeView : function (editor, defaults) {
				var rteControl = editor.contextControl,
					cmWidth = rteControl.containerEl.clientWidth - rteControl.codeModeXreduction;

				// Reset the inline styles
				defaults.forEach( function(el) {
					for (var style in el.styles) {
						el.element.style[style] = el.styles[style];
					}
				});
				editor.codeMirror.setSize(cmWidth, 100);	// the scrollheight depends on the width of the codeMirror so first we set the width (the 100 value could be replaced for anything)
				editor.contextControl.resizeCodeMirror(editor.codeMirror);
			},

			/*
			 * Look for all the components in the editor's content. Detach each component body from the DOM (so it's not visible in code mode)
			 * and attach it to the component root element as a DOM attribute. When switching back to text mode, the component body will be 
			 * re-attached where it was
			 *
			 * @param editor
			 * @param componentSelector : css selector used to look for all components in the editor
			 */
			collapseComponents : function collapseComponents (editor, componentSelector) {
				var componentsArr = YAHOO.util.Selector.query(componentSelector, editor.getBody());

				editor['data-components'] = {};

				componentsArr.forEach( function(component) {
					editor['data-components'][component.id] = Array.prototype.slice.call(component.childNodes);	// Copy children and store them in an attribute
					component.innerHTML = '';
				});
			},

			extendComponents : function extendComponents (editor, componentSelector) {
				var componentsArr = YAHOO.util.Selector.query(componentSelector, editor.getBody());

				componentsArr.forEach( function(component) {
					component.innerHTML = '';	// Replace any existing content with the original component content
					// The user may have changed the component id so test to see if the component ID exists
					if (editor['data-components'][component.id]) {
						editor['data-components'][component.id].forEach( function(child) {
							component.appendChild(child);	// restore component children
						});
					}
				});
				delete editor['data-components'];
			},

			enableCodeView : function (editor) {
				var rteControl = editor.contextControl,
					rteContainer = YAHOO.util.Selector.query('.cstudio-form-control-rte-container', rteControl.containerEl, true);

				editor.onDeactivate.dispatch(editor, null);	// Fire tinyMCE handlers for onDeactivate
				YDom.replaceClass(rteControl.containerEl, 'text-mode', 'code-mode');
				this.collapseComponents(editor, componentSelector);
				editor.codeTextArea.value = editor.getContent();

				if (!editor.codeMirror) {
					// console.log('Loading codeMirror');
					editor.codeMirror = CodeMirror.fromTextArea(editor.codeTextArea, {
						mode: 'htmlmixed',
						lineNumbers: true,
						lineWrapping: true,
						smartIndent: true,	// Although this won't work unless there are opening and closing HTML tags
						onFocus : function () {
							rteControl.form.setFocusedField(rteControl);
						},
						onChange : function (ed) {
							rteControl.resizeCodeMirror(ed);
						}
					});
				} else {
					editor.codeMirror.setValue(editor.codeTextArea.value);
				}
				// We resize codeMirror each time in case the user has resized the window
				this.resizeCodeView(editor, [ { 'element' : rteContainer,
												'styles' : { 'maxWidth' : 'none', 'width' : 'auto', 'marginLeft' : 'auto' }},
											{ 'element' : YDom.get(editor.id + '_tbl'),
												'styles' : { 'width' : 'auto' }} ]);
				editor.codeMirror.focus();
				editor.codeMirror.scrollTo(0,0);	// Scroll to the top of the editor window
				rteControl.scrollToTopOfElement(rteControl.containerEl, 30);
			},

			disableCodeView : function (editor) {
				var rteControl = editor.contextControl,
					rteContainer = YAHOO.util.Selector.query('.cstudio-form-control-rte-container', rteControl.containerEl, true);

				editor.setContent(editor.codeMirror.getValue());
				this.extendComponents(editor, componentSelector);
				rteControl.resizeTextView(rteControl.containerEl, rteControl.rteWidth, { 'rte-container' : rteContainer, 'rte-table' : YDom.get(editor.id + '_tbl') });
				YDom.replaceClass(rteControl.containerEl, 'code-mode', 'text-mode');
				editor.getWin().scrollTo(0,0);	// Scroll to the top of the editor window

				rteControl.clearTextEditorSelection();
				editor.focus();

				rteControl.scrollToTopOfElement(rteControl.containerEl, 30);
			},

			createControl: function(n, cm) {
				return null;
			},

			getInfo: function() {
				return {
					longname: 'Crafter Studio Edit Code',
					author: 'Crafter Software',
					authorurl: 'http://www.craftercms.org',
					infourl: 'http://www.craftercms.org',
					version: '1.0'
				};
			}
		};

		tinymce.create('tinymce.plugins.CStudioEditHTMLPlugin', CStudioForms.Controls.RTE.EditHTML);
		tinymce.PluginManager.add('edithtml', tinymce.plugins.CStudioEditHTMLPlugin);

		CStudioAuthoring.Module.moduleLoaded('cstudio-forms-controls-rte-edit-html', CStudioForms.Controls.RTE.EditHTML);

	}} );
