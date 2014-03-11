/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage.tagfiles;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;

/**
 * @author Rahul Khanna
 *
 */
public class TagFilesServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesServiceTest.class);
	
	@Rule
	public TemporaryFolder bagsRoot = new TemporaryFolder();
	
	private TagFilesService tfSvc;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LOGGER.trace("Using Bags Root: {}", bagsRoot.getRoot().toPath().toString());
		tfSvc = new TagFilesService(bagsRoot.getRoot().toPath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		final String pid = "test:123";
		createPidDir(pid);
		tfSvc.addEntry(pid, FileMetadataTagFile.class, "key1", "val1");
		LOGGER.trace("{}", "Test");
	}

	@Test
	public void testMultiThreaded() throws Exception {
		final int nThreads = 4;
		final String[] pids = { "test:1", "test:2", "test:3", "test:4" };

		for (String pid : pids) {
			createPidDir(pid);
		}

		List<Future<Void>> futures = new ArrayList<>(nThreads);
		ExecutorService threadPool = Executors.newFixedThreadPool(pids.length * nThreads);
		for (int i = 0; i < nThreads; i++) {
			for (int j = 0; j < pids.length; j++) {
				final int fI = i;
				final int fJ = j;
				futures.add(threadPool.submit(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						tfSvc.addEntry(pids[fJ], FileMetadataTagFile.class, "FileMeta_Pid" + String.valueOf(fJ + 1)
								+ "_" + String.valueOf(fI), "Value" + String.valueOf(fI));
						tfSvc.addEntry(pids[fJ], PreservationMapTagFile.class, "Presv_Pid" + String.valueOf(fJ + 1)
								+ "_" + String.valueOf(fI), "Value" + String.valueOf(fI));
						return null;
					}
					
				}));
			}
		}
		
		
		
		threadPool.shutdown();
		threadPool.awaitTermination(1, TimeUnit.MINUTES);
		for (Future<Void> f : futures) {
			f.get();
		}

		LOGGER.trace("Done");
	}

	private void createPidDir(String pid) throws IOException {
		Path bagRoot = bagsRoot.getRoot().toPath();
		Files.createDirectory(bagRoot.resolve(DcStorage.convertToDiskSafe(pid)));
	}
}
