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

package au.edu.anu.datacommons.storage.archive;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import au.edu.anu.datacommons.storage.archive.ArchiveTask.Operation;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class ArchiveTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveTaskTest.class);
	private static final String PID = "test:1";
	
	@Rule
	public TemporaryFolder archiveRootDir = new TemporaryFolder();
	@Rule
	public TemporaryFolder stagingDir = new TemporaryFolder();
	
	
	
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
		LOGGER.info("Archive Root: {}", archiveRootDir.getRoot().getAbsolutePath());
		LOGGER.info("Staging Root: {}", stagingDir.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testArchive() throws Exception {
		File file1 = stagingDir.newFile("Test file.txt");
		String md5 = TestUtil.fillRandomData(file1, 3L);
		ArchiveTask task = new ArchiveTask(archiveRootDir.getRoot(), PID, file1, Manifest.Algorithm.MD5, Operation.DELETE);
		File archivedFile = task.call();
		assertTrue(archivedFile.isFile());
		assertEquals(md5, MessageDigestHelper.generateFixity(archivedFile, Algorithm.MD5));
		LOGGER.trace("Done");
	}

	@Test
	public void testMultiArchive() throws Exception {
		int nFiles = 10;
		Map<File, String> files = new HashMap<File, String>(nFiles);
		Random rand = new Random();
		for (int i = 0; i < nFiles; i++) {
			File file = stagingDir.newFile();
			String md5 = TestUtil.fillRandomData(file, rand.nextInt(5) + 1);
			files.put(file, md5);
		}
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<File>> futures = new ArrayList<Future<File>>();
		for (File file : files.keySet()) {
			ArchiveTask task = new ArchiveTask(archiveRootDir.getRoot(), PID, file, Algorithm.MD5, Operation.REPLACE);
			futures.add(threadPool.submit(task));
		}
		
		List<File> archivedFile = new ArrayList<File>();
		for (Future<File> f : futures) {
			File af = f.get();
			archivedFile.add(af);
			assertTrue(af.isFile());
			LOGGER.trace("Archived file {}", af.getAbsolutePath());
		}
		
		File pidArchiveDir = new File(archiveRootDir.getRoot(), DcStorage.convertToDiskSafe(PID));
		assertEquals(nFiles, pidArchiveDir.listFiles().length);
	}
}
