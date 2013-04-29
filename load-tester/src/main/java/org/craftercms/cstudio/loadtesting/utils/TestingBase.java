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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.craftercms.cstudio.loadtesting.actions.Authenticator;
import org.craftercms.cstudio.loadtesting.actions.GoDelete;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;

import junit.framework.TestCase;

public class TestingBase extends TestCase
{
	protected String alfrescoUrl;
	protected String authoringUrl;
	protected String site;
	protected String baseFolderName;
	protected String baseFolderType;
	protected String publishChannel;
	protected String username;
	protected String password;
	protected String ticket;
	protected Authenticator authenticator;

	public void setUp() throws Exception {
		Properties props = getProperties();
		alfrescoUrl = props.getProperty("alfrescoUrl");
		authoringUrl = props.getProperty("authoringUrl");
		site = props.getProperty("site");
		baseFolderName = props.getProperty("baseFolderName");
		baseFolderType = props.getProperty("baseFolderType");
		publishChannel = props.getProperty("publishChannelName");

		authenticator = new Authenticator(alfrescoUrl);
		username = props.getProperty("runAll.username");
		password = props.getProperty("runAll.password");
		ticket = authenticator.getTicket(username, password);
		
	}

	public void deleteOne(String item, int delaySeconds) throws Exception {
		if (delaySeconds > 0)
			Thread.sleep(delaySeconds * 1000);
		GoDelete goDelete = new GoDelete(authoringUrl, site, username, ticket);
		String result = goDelete.getDependencies(item);
		goDelete.deleteContent(result);
	}

	public void deleteAll(List<String> contents, int delaySeconds) throws Exception {
		if (delaySeconds > 0)
			Thread.sleep(delaySeconds * 1000);
		GoDelete goDelete = new GoDelete(authoringUrl, site, username, ticket);
		for (String folder : contents) {
			String result = goDelete.getDependencies(folder);
			goDelete.deleteContent(result);
		}
	}

	private static Properties props;

	public static Properties getProperties() throws IOException {
		if (props == null) {
			synchronized (TestingBase.class) {
				if (props == null) {
					props = new Properties();
					InputStream in = null;
					try {
						in = TestingBase.class.getResourceAsStream(TestingConstants.PROP_FILE);
						props.load(in);
						File file = new File(
							System.getProperty("user.home") + "/.loadtesting" + TestingConstants.PROP_FILE);
						if (file.exists()) {
							System.out.println("Loading " + file.getPath());
							in.close();
							in = new FileInputStream(file);
							props.load(in);
						}
						String config = System.getProperty("loadtesting.config");
						file = (config != null ? new File(config) : null);
						if (file != null && file.canRead()) {
							System.out.println("Loading " + file.getPath());
							in.close();
							in = new FileInputStream(file);
							props.load(in);
						}
					} finally {
						if (in != null)
							in.close();
					}
				}
			}
		}
		return props;
	}
}
