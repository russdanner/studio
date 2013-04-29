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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;

import org.craftercms.cstudio.loadtesting.actions.GoLive;
import org.craftercms.cstudio.loadtesting.actions.WriteContent;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingBase;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class RunCreateSite extends TestingBase {

	//public static int WORKFLOW_DELAY = 1000 * 10;
	private String baseFileName;
	private String baseFileType;
	
	public void testRunCreateSite() throws Exception
	{
		Properties props = getProperties();
		String testRoot = props.getProperty("runCreateSite.destination");
		String destination = testRoot.equals("/") ? 
				TestingConstants.ROOT_PATH : (TestingConstants.ROOT_PATH + testRoot);
		String startLevel = props.getProperty("runCreateSite.startLevel");
		String delay = props.getProperty("runCreateSite.delay");
		String workflowDelay = props.getProperty("runCreateSite.workflowDelay");
		
		int sleepTime = Integer.valueOf(delay);
		int workflowSleepTime = Integer.valueOf(workflowDelay);
		int level = Integer.valueOf(startLevel);
		
		baseFileName = props.getProperty("baseFileName");
		baseFileType = props.getProperty("baseFileType");
		String level1Names = props.getProperty("level1Names");
		String level2Names = props.getProperty("level2Names");
		String level3Names = props.getProperty("level3Names");
		String level4Names = props.getProperty("level4Names");
		
		String [] level1Folders = level1Names.split(",");
		String [] level2Folders = level2Names.split(",");
		String [] level3Folders = level3Names.split(",");
		String [] level4Folders = level4Names.split(",");

		if (level == 1) {
			// create level 1 folders
			createFolders(ticket, site, authoringUrl, username, destination, level1Folders, 
					sleepTime, "root.txt", publishChannel, 1, 0);
			TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing the next level.");
			Thread.sleep(workflowSleepTime);
			TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
			
			// create level 2 folders
			for (String level1Folder : level1Folders) {
				String level1FolderPath = getPath(level1Folder);
				String level1FileName = username + "-" + level1FolderPath + ".txt";
				String parentPath = destination + "/" + level1FolderPath;
				createFolders(ticket, site, authoringUrl, username, parentPath, level2Folders, 
						sleepTime, level1FileName, publishChannel, 2, 0);
				TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
				Thread.sleep(workflowSleepTime);
				TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
			}
			
			// create level 3 folders and files
			for (String level1Folder : level1Folders) {
				for (String level2Folder : level2Folders) {
					String level1FolderPath = getPath(level1Folder);
					String level2FolderPath = getPath(level2Folder);
					String level2FileName = username + "-" + level1FolderPath + "-" + level2FolderPath + ".txt";
					String parentPath = destination + "/" + level1FolderPath + "/" + level2FolderPath;
					createFolders(ticket, site, authoringUrl, username, parentPath, level3Folders, 
							sleepTime, level2FileName, publishChannel, 3, 2);
					TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
					Thread.sleep(workflowSleepTime);
					TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
				}
			}
			
			// create level 4 folders and files
			for (String level1Folder : level1Folders) {
				for (String level2Folder : level2Folders) {
					for (String level3Folder : level3Folders) {
						String level1FolderPath = getPath(level1Folder);
						String level2FolderPath = getPath(level2Folder);
						String level3FolderPath = getPath(level3Folder);
						String level3FileName = username + "-" + level1FolderPath + "-" + level2FolderPath + "-" + level3FolderPath + ".txt";
						String parentPath = destination + "/" + level1FolderPath + "/" + level2FolderPath + "/" + level3FolderPath;
						createFolders(ticket, site, authoringUrl, username, parentPath, level4Folders, 
								sleepTime, level3FileName, publishChannel, 4, 3);
						TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
						Thread.sleep(workflowSleepTime);
						TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
					}
				}
			}
		} else if (level == 2) {
			String level1FileName = username + "-" + getPath(destination) + ".txt";
			String rootPath = destination;
			createFolders(ticket, site, authoringUrl, username, rootPath, level2Folders, 
					sleepTime, level1FileName, publishChannel, 2, 0);
			TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
			Thread.sleep(workflowSleepTime);
			TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
			
			// create level 3 folders and files
			for (String level2Folder : level2Folders) {
				String level1FolderPath = getPath(destination);
				String level2FolderPath = getPath(level2Folder);
				String level2FileName = username + "-" + level1FolderPath + "-" + level2FolderPath + ".txt";
				String parentPath = destination + "/" + level2FolderPath;
				createFolders(ticket, site, authoringUrl, username, parentPath, level3Folders, 
						sleepTime, level2FileName, publishChannel, 3, 2);
				TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
				Thread.sleep(workflowSleepTime);
				TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
			}
			
			// create level 4 folders and files
			for (String level2Folder : level2Folders) {
				for (String level3Folder : level3Folders) {
					String level1FolderPath = getPath(destination);
					String level2FolderPath = getPath(level2Folder);
					String level3FolderPath = getPath(level3Folder);
					String level3FileName = username + "-" + level1FolderPath + "-" + level2FolderPath + "-" + level3FolderPath + ".txt";
					String parentPath = destination + "/" + level2FolderPath + "/" + level3FolderPath;
					createFolders(ticket, site, authoringUrl, username, parentPath, level4Folders, 
							sleepTime, level3FileName, publishChannel, 4, 3);
					TestingUtils.writeLog(username, "RunCreateSite", "waiting before writing to the next folder.");
					Thread.sleep(workflowSleepTime);
					TestingUtils.writeLog(username, "RunCreateSite", "done waiting.");
				}
			}
			level1Folders = level2Folders;
		}
		ArrayList<String> folders = new ArrayList<String>();
		for (String folder : level1Folders) {
			folders.add(testRoot + '/' + folder);
		}
		deleteAll(folders, 5);
	}
	

	private static String getPath(String name) {
		return name.toLowerCase().replaceAll("\\s", "-");
	}
	
	private void createFolders(String ticket, String site, String authoringUrl, String username, 
			String parent, String[] folders, 
			int sleepTime, String fileName, String publishChannel, int level, int numOfFiles) throws Exception
	{
		// create files
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		StringBuffer buffer = new StringBuffer();
		for (String folder : folders) {
			String folderPath = getPath(folder);
			String contentPath = parent + "/" + folderPath + "/index.xml";
			writeContent.writeContent(parent + "/" + folderPath, baseFolderName, baseFolderType, "index.xml", folder);
			buffer.append(contentPath + "\n");
			Thread.sleep(sleepTime);
		}
		if (numOfFiles > 0) {
			for (int index = 1; index <= numOfFiles; index++) {
				String contentFileName = "level" + level + "-file" + index + ".xml";
				String contentPath = parent + "/" + contentFileName;
				writeContent.writeContent(parent, baseFileName, baseFileType, contentFileName, 
						"Level " + level + " File " + index);
				buffer.append(contentPath + "\n");
				Thread.sleep(sleepTime);
			}
		}
		BufferedWriter out = null;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			FileWriter fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(buffer.toString());
			out.close();
		} catch (Exception e) { 
			TestingUtils.writeLog(username, "RunCreateSite", "generating " + fileName + " failed. items are:");
			System.out.println(buffer.toString());
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		GoLive goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
		String result = goLive.getDependencies(fileName);
		goLive.goLive(result);
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

}
