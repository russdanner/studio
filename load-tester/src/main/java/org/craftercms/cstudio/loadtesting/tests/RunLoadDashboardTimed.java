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

import org.craftercms.cstudio.loadtesting.actions.Authenticator;
import org.craftercms.cstudio.loadtesting.actions.LoadDashboard;

public class RunLoadDashboardTimed extends TestCase {

	// set 20 minutes 
	public static long DURATION_TICKET = 20 * 60 * 1000;
	
	public void testLoadDashboardTimed() throws Exception
	{
		Properties props = RunAll.getProperties();
		String username = props.getProperty("runAll.username");
		String password = props.getProperty("runAll.password");
		String duration = props.getProperty("runAll.duration");
		long durationinMs = Integer.valueOf(duration) * 60 * 1000; 
		String alfrescoUrl = props.getProperty("alfrescoUrl");
		String authoringUrl = props.getProperty("authoringUrl");
		String shareUrl = props.getProperty("shareUrl");
		String site = props.getProperty("site");
		String readDelayMin = props.getProperty("readDelay.min");
		String readDelayMax = props.getProperty("readDelay.max");
		int minSleepTime = Integer.valueOf(readDelayMin);
		int maxSleepTime = Integer.valueOf(readDelayMax);
		Random rand = new Random();
		
		Authenticator authenticator = new Authenticator(alfrescoUrl);
		String ticket = authenticator.getTicket(username, password);
		LoadDashboard loadDashboard = new LoadDashboard(authoringUrl, shareUrl, site, username, ticket);

		long endTime = System.currentTimeMillis() + durationinMs;
		System.out.println(durationinMs);
		long ticketTime = System.currentTimeMillis() + DURATION_TICKET;
		do {
			//loadDashboard
			loadDashboard.loadDashboard();
			int timeInSec = rand.nextInt(maxSleepTime - minSleepTime + 1) + minSleepTime;
			Thread.sleep(timeInSec * 1000);
			if (System.currentTimeMillis() >= ticketTime) {
				// refresh the ticket
				ticket = authenticator.getTicket(username, username);
				System.out.println("ticket is refreshed for " + username + ", new ticket:" + ticket);
				loadDashboard = null;
				loadDashboard = new LoadDashboard(authoringUrl, shareUrl, site, username, ticket);
				// reset the limit
				ticketTime = System.currentTimeMillis() + DURATION_TICKET;
			}
		} while (System.currentTimeMillis() < endTime);
	}

}
