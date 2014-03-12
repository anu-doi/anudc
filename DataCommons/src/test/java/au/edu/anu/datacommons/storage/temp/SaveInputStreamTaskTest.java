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

package au.edu.anu.datacommons.storage.temp;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class SaveInputStreamTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveInputStreamTaskTest.class);
	
	private SaveInputStreamTask sisTask;
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	@Rule
	public TemporaryFolder uploadDir = new TemporaryFolder();
	
	
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
		LOGGER.info("Temp Dir: {}", tempDir.getRoot().getAbsolutePath());
		LOGGER.info("Upload Dir: {}", uploadDir.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWithSizeAndMd5() throws Exception {
		Path srcFile = tempDir.newFile().toPath();
		String srcMd5 = createTempFile(srcFile);
		sisTask = new SaveInputStreamTask(uploadDir.getRoot().toPath(), Files.newInputStream(srcFile),
				Files.size(srcFile), srcMd5);
		UploadedFileInfo ufi = sisTask.call();
		assertThat(ufi.getFilepath().isAbsolute(), is(true));
		assertThat(Files.isRegularFile(ufi.getFilepath()), is(true));
		assertThat(Files.size(ufi.getFilepath()), is(Files.size(srcFile)));
		assertThat(ufi.getMd5(), is(srcMd5));
		LOGGER.trace("{}", ufi);
	}

	@Test
	public void testWithoutSizeAndMd5() throws Exception {
		Path srcFile = tempDir.newFile().toPath();
		String srcMd5 = createTempFile(srcFile);
		sisTask = new SaveInputStreamTask(uploadDir.getRoot().toPath(), Files.newInputStream(srcFile),
				-1, null);
		UploadedFileInfo ufi = sisTask.call();
		assertThat(ufi.getFilepath().isAbsolute(), is(true));
		assertThat(Files.isRegularFile(ufi.getFilepath()), is(true));
		assertThat(Files.size(ufi.getFilepath()), is(Files.size(srcFile)));
		assertThat(ufi.getMd5(), is(srcMd5));
		LOGGER.trace("{}", ufi);
	}
	
	@Test(expected = IOException.class)
	public void testWithIncorrectSize() throws Exception {
		Path srcFile = tempDir.newFile().toPath();
		String srcMd5 = createTempFile(srcFile);
		sisTask = new SaveInputStreamTask(uploadDir.getRoot().toPath(), Files.newInputStream(srcFile),
				Files.size(srcFile) - 1L, null);
		UploadedFileInfo ufi = sisTask.call();
	}
	
	@Test(expected = IOException.class)
	public void testWithIncorrectMd5() throws Exception {
		Path srcFile = tempDir.newFile().toPath();
		String srcMd5 = createTempFile(srcFile);
		sisTask = new SaveInputStreamTask(uploadDir.getRoot().toPath(), Files.newInputStream(srcFile),
				-1, "abc");
		UploadedFileInfo ufi = sisTask.call();
	}
	
	private String createTempFile(Path srcFile) throws IOException {
		return TestUtil.createFileOfSizeInRange(srcFile.toFile(), 1L, 4L, FileUtils.ONE_MB);
	}
	
}
