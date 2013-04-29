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

import junit.framework.TestCase;

import org.junit.Ignore;
import org.craftercms.cstudio.loadtesting.actions.Authenticator;
import org.craftercms.cstudio.loadtesting.actions.GoLive;

/*
* This test is ignore, since it's done by many other cases.
*/
@Ignore
public class RunGoLive extends TestCase {

	public void testGoLive() throws Exception
	{
		Properties props = RunAll.getProperties();
		String username = props.getProperty("runAll.username");
		String password = props.getProperty("runAll.password");
		String fileName = props.getProperty("runAll.fileName");
		String alfrescoUrl = props.getProperty("alfrescoUrl");
		String authoringUrl = props.getProperty("authoringUrl");
		String publishChannel = props.getProperty("publishChannelName");
		String site = props.getProperty("site");
		Authenticator authenticator = new Authenticator(alfrescoUrl);
		String ticket = authenticator.getTicket(username, password);
		GoLive goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
		String result = goLive.getDependencies(fileName);
		goLive.goLive(result);
	}
}
