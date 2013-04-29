var time = args.time;
var srcTimezone = args.srcTimezone;
var destTimezone = args.destTimezone;
var dateFormat = args.dateFormat;

var convertedTimezone = cstudioTimeConversionService.convertTimezone(time, srcTimezone, destTimezone, dateFormat);

model.originalTime = time;
model.dateFormat = dateFormat;
model.srcTimezone = srcTimezone;
model.destTimezone = destTimezone;
model.convertedTimezone = convertedTimezone;
