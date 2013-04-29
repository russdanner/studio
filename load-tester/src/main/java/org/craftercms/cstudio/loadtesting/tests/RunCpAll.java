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

import java.io.File;
import java.util.Properties;

import org.craftercms.cstudio.loadtesting.actions.CopyPaste;
import org.craftercms.cstudio.loadtesting.actions.GoLive;
import org.craftercms.cstudio.loadtesting.actions.WriteContent;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingBase;

public class RunCpAll extends TestingBase {

	public void testRunCpAll() throws Exception
	{
		Properties props = getProperties();
		String fileName = props.getProperty("runAll.fileName");
		String source = TestingConstants.ROOT_PATH + props.getProperty("runCopyPaste.source");
		String destination = props.getProperty("runCopyPaste.destination");
		String folderName = props.getProperty("runCopyPaste.folderName");

		String folderPath = destination + '/' + folderName;
		String path = TestingConstants.ROOT_PATH + folderPath;
		
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		writeContent.writeContent(path, baseFolderName, baseFolderType, "index.xml", folderName);
		
		CopyPaste copyPaste = new CopyPaste(authoringUrl, site, username, ticket);
		copyPaste.pasteContent(fileName, source, path);
		GoLive goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
		String result = goLive.getDependencies(fileName);
		goLive.goLive(result);
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		deleteOne(folderPath, 5);
	}

}
