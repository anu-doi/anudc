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

package au.edu.anu.datacommons.storage.filesystem;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class LocalDiskManagerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDiskManagerTest.class);
	
	@Rule
	public TemporaryFolder rootDir = new TemporaryFolder();
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private LocalDiskManager ldm;
	
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
		LOGGER.info("Root: {}", rootDir.getRoot().getAbsolutePath());
		LOGGER.info("Temp: {}", tempDir.getRoot().getAbsolutePath());
		ldm = new LocalDiskManager(rootDir.getRoot().toPath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFullPath() {
		String path = ldm.createFullPath("a", "b", "c");
		assertThat(path, anyOf(endsWith("\\a\\b\\c"), endsWith("/a/b/c")));
	}

	@Test
	public void testMove() throws Exception {
		File file = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(file, 2L, 10L, FileUtils.ONE_KB);
		long size = file.length();
		
		ldm.move(file.toPath(), "/file.txt");
		Path target = Paths.get(rootDir.getRoot().getAbsolutePath(), "/file.txt");
		assertThat(Files.isRegularFile(target), is(true));
		assertThat(Files.size(target), equalTo(size));
	}
}
