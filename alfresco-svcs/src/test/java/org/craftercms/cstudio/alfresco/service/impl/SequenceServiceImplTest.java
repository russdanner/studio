/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.alfresco.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;

import org.craftercms.cstudio.alfresco.service.api.SequenceService;
import org.craftercms.cstudio.alfresco.service.exception.SequenceException;

public class SequenceServiceImplTest extends TestCase {

	private static final String APPLICATION_CONTEXT = "file:src/test/resources/core/service-context.xml";
	private static ConfigurableApplicationContext configurationApplicationContext = null;
	private SequenceService sequenceService = null;

	private Set<Long> sequenceNumbers = new HashSet<Long>();

	private String namespace1 = "three";
	private String namespace2 = "four";

	@Before
	public void setUp() throws Exception {
		if (configurationApplicationContext == null) {
			configurationApplicationContext = new FileSystemXmlApplicationContext(
					APPLICATION_CONTEXT);
		}
		sequenceService = (SequenceService) configurationApplicationContext
				.getBean("SequenceService");
		assertNotNull(sequenceService);
		clean();
	}
	
	@After
	public void tearDown() {
		clean();
	}

	/**
	 * This test case is for creating multiple namespaces and getting sequence
	 * numbers from them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSequenceService() throws Exception {
		String randomNamespace = System.currentTimeMillis() + "";
		sequenceService.createSequence(namespace1);
		assertTrue(sequenceService.sequenceExists(namespace1));
		assertFalse(sequenceService.sequenceExists(randomNamespace));

		try {
			long emptyNum1 = sequenceService.next(randomNamespace);
			// this should fail since we're providing a namespace
			// that does not exist and setting create = false
			fail();
		} catch (SequenceException e) {
		}

		long seqNum2 = sequenceService.next(namespace2, true);
		assertTrue(sequenceService.sequenceExists(namespace2));

		long seqNum = sequenceService.next();
		long seqNum1 = sequenceService.next(namespace1);

		for (int count = 1; count < 1500; count++) {
			long next = sequenceService.next(namespace1);
			assertEquals(next, count + seqNum1);
			if ((count % 3) == 0) {
				long oneNext = sequenceService.next(namespace2);
				assertEquals(oneNext, count / 3 + seqNum2);
			}
			if ((count % 10) == 0) {
				long defaultNext = sequenceService.next();
				assertEquals(defaultNext, count / 10 + seqNum);
			}
		}
	}

	/**
	 * This test case is for running multiple threads to get sequence numbers
	 * from the same namespace
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultiThreadedSequenceService() throws Exception {
		sequenceService.createSequence(namespace1);
		sequenceService.createSequence(namespace2);
		assertTrue(sequenceService.sequenceExists(namespace1));
		assertTrue(sequenceService.sequenceExists(namespace2));

		int numOfThreads = 10;
		int totalNumOfRuns = 100;
		TestRunnable[] trs = new TestRunnable[numOfThreads];
		for (int i = 0; i < numOfThreads; i++) {
			trs[i] = new SequenceServiceTest("tr" + (i + 1), totalNumOfRuns);
		}
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		try {
			mttr.runTestRunnables();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private class SequenceServiceTest extends TestRunnable {

		private String name;
		private int total;

		private SequenceServiceTest(String name, int total) {
			this.name = name;
			this.total = total;
		}

		/**
		 * each thread gets a sequence number from the same namespace and put
		 * into the sequence number set. The test fails if the same number is
		 * found from the set
		 * 
		 */
		@Override
		public void runTest() throws Throwable {
			for (int count = 1; count <= total; count++) {
				long twoNext = sequenceService.next(namespace2);
//				System.out.println(name + " got " + twoNext + " for " +
//				namespace2);
				addResult(twoNext);
				Random random = new Random(System.currentTimeMillis());
				long i = Math.abs(random.nextLong()) % 1;
				Thread.sleep(i);
			}
		}

		// not synchronized to ensure the random order.
		private void addResult(long number) {
			if (sequenceNumbers.contains(new Long(number))) {
				fail();
			} else {
				sequenceNumbers.add(number);
			}
		}

	}

	/**
	 * clean up namespaces
	 */
	private void clean() {
		try {
			sequenceService.deleteSequence(namespace1);
		} catch (SequenceException e) {
			System.out.println(e);
		}
		try {
			sequenceService.deleteSequence(namespace2);
		} catch (SequenceException e) {
			System.out.println(e);
		}
	}
	
}
