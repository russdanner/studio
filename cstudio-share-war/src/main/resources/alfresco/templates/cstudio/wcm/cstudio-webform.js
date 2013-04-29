var showFormDef = (page.url.args.showFormDef) ? true : false;
model.showFormDef = showFormDef;

if (page.url.args.edit != undefined && page.url.args.edit == 'true')
{	
	model.isEdit = 'true';
}
else
{
	model.isEdit = 'false';
}
