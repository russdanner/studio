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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * provide some basic logging capabilities for the scripting environment
 */
public class ConsoleLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogger.class);

    @Deprecated
	public void log(String message) {
        this.info(message);
	}

    public void info(String message) {
        LOGGER.info(message);
    }

    public void debug(String message) {
        LOGGER.debug(message);
    }

    public void error(String message) {
        LOGGER.error(message);
    }

    public void warn(String message) {
        LOGGER.warn(message);
    }
}
