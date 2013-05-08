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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.craftercms.cstudio.loadtesting.actions.GoLive;
import org.craftercms.cstudio.loadtesting.actions.LoadDashboard;
import org.craftercms.cstudio.loadtesting.actions.WriteContent;
import org.craftercms.cstudio.loadtesting.constants.TestingConstants;
import org.craftercms.cstudio.loadtesting.utils.TestingBase;
import org.craftercms.cstudio.loadtesting.utils.TestingUtils;

public class RunAllTimed extends TestingBase {
	// set 20 minutes 
	public static long DURATION_TICKET = 20 * 60 * 1000;
	
	public void testRunAllTimed() throws Exception
	{
		Properties props = getProperties();
		String destination = props.getProperty("runAll.destination");
		String contentPrefix = props.getProperty("runAll.contentPrefix");
		String duration = props.getProperty("runAll.duration");
		boolean keepFolders = Boolean.valueOf(props.getProperty("runAll.keepFolders"));
		long durationinMs = Integer.valueOf(duration) * 60 * 1000; 
			
		String shareUrl = props.getProperty("shareUrl");
		String writeDelayMin = props.getProperty("writeDelay.min");
		String writeDelayMax = props.getProperty("writeDelay.max");
		String readDelayMin = props.getProperty("readDelay.min");
		String readDelayMax = props.getProperty("readDelay.max");
		String skipGoLiveMin = props.getProperty("skipGoLive.min");
		String skipGoLiveMax = props.getProperty("skipGoLive.max");
		String numOfItemsMax = props.getProperty("numOfItems.max");
		String readRepeat = props.getProperty("readRepeat");
		String writeRepeat = props.getProperty("writeRepeat");

		GoLive goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
		
		int totalCount = 1;
		int totalItems = 0;
		// create files
		WriteContent writeContent = new WriteContent(authoringUrl, site, username, ticket);
		LoadDashboard loadDashboard = new LoadDashboard(authoringUrl, shareUrl, site, username, ticket);
		int max = Integer.valueOf(numOfItemsMax);
		int writeFrequency = Integer.valueOf(writeRepeat);
		int readFrequency = Integer.valueOf(readRepeat);
		int minReadSleepTime = Integer.valueOf(readDelayMin);
		int maxReadSleepTime = Integer.valueOf(readDelayMax);
		int minWriteSleepTime = Integer.valueOf(writeDelayMin);
		int maxWriteSleepTime = Integer.valueOf(writeDelayMax);
		int minSkipGoLiveCount = Integer.valueOf(skipGoLiveMin);
		int maxSkipGoLiveCount = Integer.valueOf(skipGoLiveMax);
		Random rand = new Random();
		
		long endTime = System.currentTimeMillis() + durationinMs;
		long ticketTime = System.currentTimeMillis() + DURATION_TICKET;
		ArrayList<String> contents = new ArrayList<String>();
		int numOfItems = rand.nextInt(max) + 1;
		int numOfSkips = rand.nextInt(maxSkipGoLiveCount - minSkipGoLiveCount + 1) + minSkipGoLiveCount;
		int currItems = 0;
		int currSkips = 0;
		do {
			System.out.println("[COUNT] " + username + ": total count: " + totalCount + ", number of Items to go live: " + numOfItems);
			int numOfWrites = 1;
			int numOfReads = 1;
			while (numOfReads <= readFrequency) {
				//loadDashboard
				System.out.println("[COUNT] " + username + ": reading at iteration " + totalCount);
				System.out.println("[COUNT] " + username + ": " + numOfReads + " read out of " + readFrequency);
				try {
					loadDashboard.loadDashboard();
				} catch (Exception e) {
					System.out.println("[FAILURE] " + username + ": loading dashboard." + "\n" + e.getMessage());
				}
				int timeInSec = rand.nextInt(maxReadSleepTime - minReadSleepTime + 1) + minReadSleepTime;
				Thread.sleep(timeInSec * 1000);
				numOfReads++;
			} // end of reads
			while (numOfWrites <= writeFrequency) {
				System.out.println("[COUNT] " + username + ": writing at iteration " + totalCount);
				System.out.println("[COUNT] " + username + ": " + numOfWrites + " write out of " + writeFrequency);
				String folderName = contentPrefix + "-gl" + totalItems++;
				String folderPath = destination + '/' + folderName;
				String contentPath = TestingConstants.ROOT_PATH + folderPath;
				try {
					writeContent.writeContent(contentPath, baseFolderName, baseFolderType, "index.xml", folderName);
					if (currSkips < numOfSkips) { 
						currSkips++;
					} else {
						contents.add(contentPath + "/index.xml");
						currItems++;
					}
					if (totalItems == 1) {
						contentPath = TestingConstants.ROOT_PATH + destination;
						folderName = destination.substring(destination.lastIndexOf('/') + 1);
						writeContent.writeContent(contentPath, baseFolderName, baseFolderType, "index.xml", folderName, false);
					}
					System.out.println("[COUNT] " + username + ": currItems: " + currItems + ", numToGoLive: " + numOfItems + ", totalItems: " + totalItems + ", currSkips: " + currSkips + ", total Skips: " + numOfSkips);
				} catch (Exception e) {
					System.out.println("[FAILURE] " + username + ": write content " + contentPath + '\n' + e.getMessage());
				}
				int timeInSec = rand.nextInt(maxWriteSleepTime - minWriteSleepTime + 1) + minWriteSleepTime;
				Thread.sleep(timeInSec * 1000);
				
				// once it reaches a certain number of items, go live and reset the counter
				if (numOfItems == currItems) {
					System.out.println("[COUNT] " + username + ": items created: " + contents);
					try {
						goLive(goLive, contents);
					} catch (Exception e) {
						System.out.println("[FAILURE] " + username + ": go live of " + contents);
					}
					numOfItems = rand.nextInt(max) + 1;
					numOfSkips = rand.nextInt(maxSkipGoLiveCount - minSkipGoLiveCount + 1) + minSkipGoLiveCount;
					currItems = 0;
					currSkips = 0;
					contents.clear();
					Thread.sleep(timeInSec * 1000);
				}
				numOfWrites++;
			} // end of writes 
			if (System.currentTimeMillis() >= ticketTime) {
				// refresh the ticket
				ticket = authenticator.getTicket(username, password);
				System.out.println("ticket is refreshed for " + username + ", new ticket:" + ticket);
				writeContent = null;
				writeContent = new WriteContent(authoringUrl, site, username, ticket);
				goLive = null;
				goLive = new GoLive(authoringUrl, site, username, ticket, publishChannel);
				loadDashboard = null;
				loadDashboard = new LoadDashboard(authoringUrl, shareUrl, site, username, ticket);
				// reset the limit
				ticketTime = System.currentTimeMillis() + DURATION_TICKET;
			}
			totalCount++;
		} while (System.currentTimeMillis() < endTime);

		if (!contents.isEmpty()) {
			try {
				goLive(goLive, contents);
			} catch (Exception e) {
				System.out.println("[FAILURE] " + username + ": go live of " + contents);
			}
			Thread.sleep(maxWriteSleepTime * 1000);
		}
		if (!keepFolders)
			deleteOne(destination, 5);
	}

	/**
	 * go live
	 * 
	 * @param goLive
	 * @param content
	 * @throws Exception
	 */
	private static void goLive(GoLive goLive, ArrayList<String> content) throws Exception {
		String result = goLive.getDependencies(content);
		goLive.goLive(result);
	}

	/** 
	 * write the list of files created to a file
	 * 
	 * @param username
	 * @param buffer
	 * @param fileName
	 * @throws IOException
	 */
	private static void writeToFile(String username, StringBuffer buffer, String fileName) throws IOException {
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
			TestingUtils.writeLog(username, "RunAllTimed", "generating " + fileName + " failed. items are:");
			System.out.println(buffer.toString());
		} finally {
			if (out != null) {
				out.close();
			}
		}				
	}
}
