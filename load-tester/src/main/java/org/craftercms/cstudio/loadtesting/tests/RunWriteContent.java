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

import org.craftercms.cstudio.loadtesting.actions.WriteContent;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingBase;

public class RunWriteContent extends TestingBase
{
	private String testRoot;
	private String destination;
	private String baseFileName;
	private String baseFileType;

	public void setUp() throws Exception {
		super.setUp();
		Properties props = RunAll.getProperties();
		testRoot = props.getProperty("runAll.destination");
		destination = TestingConstants.ROOT_PATH + testRoot;
		baseFileName = props.getProperty("baseFileName");
		baseFileType = props.getProperty("baseFileType");
	}

	public void testWriteContent() throws Exception
	{
		String name = "example.xml";
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		writeContent.writeContent(destination, baseFileName, baseFileType, name, name);
		deleteOne(testRoot + '/' + name, 1);
	}

	public void testWriteFolderContent() throws Exception
	{
		String name = "folderPage";
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		String path = destination + '/' + name;
		writeContent.writeContent(path, baseFileName, baseFolderType, "index.xml", name);
		deleteOne(testRoot + '/' + name, 1);
	}
}
