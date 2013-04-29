/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.cstudio.loadtesting.utils;

import java.util.Date;

import org.craftercms.cstudio.loadtesting.constants.TestingConstants;

public class TestingUtils {

	public static void writeLog(String username, String action, String result, long duration) {
		System.out.println("[WORKFLOW_TUNING] " + TestingConstants.DATE_OUTPUT_FORMAT.format(new Date()) + "," + username + "," + action + "," + result + "," + duration);
	}

	public static void writeLog(String username, String action, String result) {
		System.out.println("[WORKFLOW_TUNING] " + TestingConstants.DATE_OUTPUT_FORMAT.format(new Date()) + "," + username + "," + action + "," + result);
	}
}
