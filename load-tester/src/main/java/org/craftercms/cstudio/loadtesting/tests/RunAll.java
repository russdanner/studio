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

import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.loadtesting.actions.GoLive;
import org.craftercms.cstudio.loadtesting.actions.WriteContent;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingBase;

public class RunAll extends TestingBase
{
	public void testRunAll() throws Exception
	{
		Properties props = getProperties();
		String destination = props.getProperty("runAll.destination");
		String contentPrefix = props.getProperty("runAll.contentPrefix");
		String numOfItems = props.getProperty("runAll.numOfItems");
		String delay = props.getProperty("runAll.delay");
		
		ArrayList<String> folders = new ArrayList<String>();
		ArrayList<String> contents = new ArrayList<String>();
		
		// create files
		if (!StringUtils.isNumeric(numOfItems) || !StringUtils.isNumeric(delay)) {
			System.out.println("the number of contents to create and the delay between writes must be numeric.");
			return;
		}
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		int max = Integer.valueOf(numOfItems);
		int sleepTime = Integer.valueOf(delay);
		for (int count = 1; count <= max; count++) {
			String folderName = contentPrefix + "-gl" + count;
			String folderPath = destination + '/' + folderName;
			String contentPath = TestingConstants.ROOT_PATH + folderPath;
			writeContent.writeContent(contentPath, baseFolderName, baseFolderType, "index.xml", folderName);
			folders.add(folderPath);
			contents.add(contentPath + "/index.xml");
			Thread.sleep(sleepTime);
		}

		GoLive goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
		String result = goLive.getDependencies(contents);
		goLive.goLive(result);

		deleteAll(folders, 5);
	}

}
