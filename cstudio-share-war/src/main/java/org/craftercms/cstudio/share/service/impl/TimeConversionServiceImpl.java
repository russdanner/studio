/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.share.service.impl;

import org.craftercms.cstudio.share.service.api.TimeConversionService;

import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Javascript apparently doesn't support DLT rules well so we are allowing java
 * to do the heavy lifting on time conversions
 */
public class TimeConversionServiceImpl implements TimeConversionService {

	public String convertTimezone(String time, String sourceTZ, String destTZ, String format) 
	throws Exception {
		final String DATE_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";
		format = (!"".equals(format)) ? format : DATE_TIME_FORMAT;
	
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		Date specifiedTime;
		if (sourceTZ != null && !"".equals(sourceTZ))
			sdf.setTimeZone(TimeZone.getTimeZone(sourceTZ));
		else
			sdf.setTimeZone(TimeZone.getDefault()); // default to server's
													// timezone
		specifiedTime = sdf.parse(time);

		// switch timezone
		if (destTZ != null && !"".equals(destTZ))
			sdf.setTimeZone(TimeZone.getTimeZone(destTZ));
		else
			sdf.setTimeZone(TimeZone.getDefault()); // default to server's
													// timezone

		return sdf.format(specifiedTime);
	}
}