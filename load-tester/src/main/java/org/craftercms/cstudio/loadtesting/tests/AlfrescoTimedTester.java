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
package org.craftercms.cstudio.loadtesting.tests;

import java.util.Properties;
import java.util.Random;

import junit.framework.TestCase;

import org.craftercms.cstudio.loadtesting.actions.AlfrescoWriteContent;
import org.craftercms.cstudio.loadtesting.actions.Authenticator;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;

public class AlfrescoTimedTester extends TestCase {

	// set 20 minutes 
	public static long DURATION_TICKET = 20 * 60 * 1000;
	
	public void testAlfrescoTimed() throws Exception
	{
		Properties props = RunAll.getProperties();
		String username = props.getProperty("runAll.username");
		String destination = TestingConstants.ROOT_PATH + props.getProperty("runAll.destination");
		String contentPrefix = props.getProperty("runAll.contentPrefix");
		String duration = props.getProperty("runAll.duration");
		long durationinMs = Integer.valueOf(duration) * 60 * 1000; 

		String baseFileName = props.getProperty("baseFileName");
		String alfrescoUrl = props.getProperty("alfrescoUrl");
		String authoringUrl = props.getProperty("authoringUrl");
		String writeDelayMin = props.getProperty("writeDelay.min");
		String writeDelayMax = props.getProperty("writeDelay.max");
		String versionable = props.getProperty("versionable");
		
		Authenticator authenticator = new Authenticator(alfrescoUrl);
		String ticket = authenticator.getTicket(username, username);
		
		int totalItems = 0;
		// create files
		AlfrescoWriteContent writeContent = new AlfrescoWriteContent(authoringUrl, username, versionable, ticket);
		int minWriteSleepTime = Integer.valueOf(writeDelayMin);
		int maxWriteSleepTime = Integer.valueOf(writeDelayMax);
		Random rand = new Random();

		long endTime = System.currentTimeMillis() + durationinMs;
		long ticketTime = System.currentTimeMillis() + DURATION_TICKET;
		do {
			String fileName = contentPrefix + "-gl" + totalItems++ + ".xml";
			System.out.println("writing a file at " + destination + "/" + fileName);
			writeContent.writeContent(destination, fileName, baseFileName);
			int timeInSec = rand.nextInt(maxWriteSleepTime - minWriteSleepTime + 1) + minWriteSleepTime;
			Thread.sleep(timeInSec * 1000);
			if (System.currentTimeMillis() >= ticketTime) {
				// refresh the ticket
				ticket = authenticator.getTicket(username, username);
				System.out.println("ticket is refreshed for " + username + ", new ticket:" + ticket);
				writeContent = null;
				writeContent = new AlfrescoWriteContent(authoringUrl, username, versionable, ticket);
				// reset the limit
				ticketTime = System.currentTimeMillis() + DURATION_TICKET;
			}
		} while (System.currentTimeMillis() < endTime);
	}
}
