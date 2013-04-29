var markup = requestbody.content;
var cleanHtml = markup;
var formatOnlyFlag = false;

if(args.formatOnly && args.formatOnly == "true") {
    formatOnlyFlag = true;
}

	try {
		
		var cleanupConfig = cstudioCleanHtmlService.createConfiguration();
		
        if(cleanupConfig != null) {

			/* http://tidy.sourceforge.net/docs/quickref.html */

			if(formatOnlyFlag == true) {
                cleanupConfig.addParam("doctype", "loose"); 
                cleanupConfig.addParam("clean", "no");
				cleanupConfig.addParam("output-xhtml", "no");
                cleanupConfig.addParam("output-xml", "no");
				cleanupConfig.addParam("tidy-mark", "no");
				cleanupConfig.addParam("numeric-entities", "no");
				cleanupConfig.addParam("show-body-only", "yes");
                cleanupConfig.addParam("join-styles", "no");
                
				cleanupConfig.addParam("escape-cdata", "yes");
				cleanupConfig.addParam("quote-marks", "no");
				cleanupConfig.addParam("indent", "auto");
				cleanupConfig.addParam("indent-spaces", "2");
				cleanupConfig.addParam("wrap", "0");
				cleanupConfig.addParam("char-encoding", "UTF-8");
			}
			else {
				cleanupConfig.addParam("bare", "yes");
				cleanupConfig.addParam("word-2000", "yes");
				cleanupConfig.addParam("clean", "yes");
				cleanupConfig.addParam("hide-endtags", "no");
				cleanupConfig.addParam("quiet", "no");
				cleanupConfig.addParam("show-warnings", "yes");
				cleanupConfig.addParam("tidy-mark", "no");
				cleanupConfig.addParam("output-xml", "yes");
				cleanupConfig.addParam("force-output", "yes");
				cleanupConfig.addParam("markup", "yes");
				cleanupConfig.addParam("show-body-only", "yes");
				cleanupConfig.addParam("indent-attributes", "no");
				cleanupConfig.addParam("wrap", "0");
				cleanupConfig.addParam("wrap-attributes", "no");
				cleanupConfig.addParam("char-encoding", "UTF-8");
				cleanupConfig.addParam("numeric-entities", "yes");
				cleanupConfig.addParam("hide-comments", "yes");
				cleanupConfig.addParam("trim-empty-elements", "no");
				cleanupConfig.addParam("join-classes", "no");
				cleanupConfig.addParam("break-before-br", "no");
			}
		}

		cleanHtml = cstudioCleanHtmlService.cleanMarkup(markup, cleanupConfig);
		if(cleanHtml==''){ //workaround when tidy eats up all the content
			model.markup = markup;
		}else{
			model.markup = cleanHtml;
		}		
		
	}
	catch(err) {
		//cleanHtml = "<!-- tidy-err: "+err +" -->"+ cleanHtml;
		  status.code = 400;
		  status.message = "TidyError during cleanup";
		  model.markup=err;
	}
