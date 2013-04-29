clipboardService.copy('cstudio', '/sites/website/downloads/index.xml', false);
clipboardService.copy('cstudio', 'CStudio2ndCopy', true);
clipboardService.copy('cstudio', 'CStudio2ndCopy', false);
clipboardService.cut('cstudio', 'CStudio1stCut');
model.items1 = clipboardService.getItems('cstudio');
clipboardService.copy('readiness', 'Rdy2ndCopy', false);
clipboardService.copy('readiness', 'Rdy2ndCopy', true);
clipboardService.cut('readiness', 'Rdy1stCut');
model.items2 = clipboardService.getItems('readiness');

 

