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
package org.craftercms.cstudio.impl.servicedist.alfscript;


import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.api.log.*;

import java.util.Map;

/**
 * wrap logging so that it is available in alfresco scripting layer
 * @author russdanner
 */
public class LoggingScript extends BaseProcessorExtension {

	/**
	 * return a map of loggers
	 */
	public Map<String, Logger> getCurrentLoggers() {
		return LoggerFactory.getLoggers();
	}

	/**
	 * set a logger's level
	 * @param name the name of the logger
	 * @param level the level to set
	 */
	public void setLoggerLevel(String name, String level) {
		LoggerFactory.setLoggerLevel(name, level);
	}

}
