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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
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
public class SavePartStreamTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SavePartStreamTaskTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private SavePartStreamTask spsTask;
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSavePartStreamTask() throws Exception {
		Path uploadDir = tempDir.getRoot().toPath();
		final int nParts = 5;

		List<Path> files = createSampleFiles(nParts);
		long expectedLength = calculateTotalSize(files); 
		String expectedMd5 = computeMd5(files);
		
		UploadedFileInfo ufi = null;
		final String fileId = "Test";
		for (int i = 0; i < nParts; i++) {
			try (InputStream stream = new BufferedInputStream(Files.newInputStream(files.get(i)))) {
				spsTask = new SavePartStreamTask(uploadDir, fileId, stream, i + 1, i + 1 == nParts, expectedLength, expectedMd5);
				ufi = spsTask.call();
				if (i < nParts - 1) {
					assertThat(ufi, is(nullValue()));
				} else {
					assertThat(ufi, is(notNullValue()));
				}
			}
		}
		assertThat(ufi, is(notNullValue()));
		assertThat(Files.isRegularFile(ufi.getFilepath()), is(true));
		assertThat(ufi.getFilepath().getFileName().toString(), is(fileId));
		assertThat(Files.size(ufi.getFilepath()), is(ufi.getSize()));
		assertThat(computeMd5(Arrays.asList(ufi.getFilepath())), is(expectedMd5));
	}

	private List<Path> createSampleFiles(int nFiles) throws IOException {
		List<Path> files = new ArrayList<Path>(nFiles);
		for (int i = 0; i < nFiles; i++) {
			File file = tempDir.newFile();
			String md5 = TestUtil.createFileOfSizeInRange(file, 5, 10, FileUtils.ONE_MB);
			files.add(file.toPath());
		}
		return files;
	}

	private long calculateTotalSize(List<Path> files) throws IOException {
		long size = 0L;
		for (Path file : files) {
			size += Files.size(file);
		}
		return size;
	}

	private String computeMd5(List<Path> files) throws NoSuchAlgorithmException, IOException {
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[8192];
		for (Path file : files) {
			DigestInputStream is = new DigestInputStream(new BufferedInputStream(Files.newInputStream(file)), md5Digest);
			for (int nBytesRead = is.read(buffer); nBytesRead != -1; nBytesRead = is.read(buffer)) {
				
			}
		}
		return Hex.encodeHexString(md5Digest.digest());
	}

}
