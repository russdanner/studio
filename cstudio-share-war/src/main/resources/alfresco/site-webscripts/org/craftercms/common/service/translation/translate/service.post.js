// get parameter values
var siteId = args.site;
var fromLang = args.fl;
var toLang = args.tl;
var format = args.format;

model.result = cstudioTranslationService.translate(requestbody.getContent(), fromLang, toLang, format);