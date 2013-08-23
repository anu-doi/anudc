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

package au.edu.anu.datacommons.storage;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Manifest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
public class PartTempFileTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PartTempFileTaskTest.class);

	private PartTempFileTask ptTask;
	private File stagingDir;
	private File partsRootDir;

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

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
		partsRootDir = tempDir.newFolder("partsRoot");
		LOGGER.info("Parts root dir: {}", partsRootDir);
		stagingDir = tempDir.newFolder("staging");
		LOGGER.info("Staging dir: {}", stagingDir);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMergeParts() throws Exception {
		// Create part files in staging and calculate the expected MD5.
		final int nParts = 10;
		List<File> sourcePartFiles = createFiles(nParts);
		String expectedMd = calcMd(sourcePartFiles);

		// Execute PartTempFileTask for each.
		File mergedFile = null;
		for (int part = 1; part <= nParts; part++) {
			FileInputStream partStream = null;
			try {
				partStream = new FileInputStream(sourcePartFiles.get(part - 1));
				ptTask = new PartTempFileTask(partStream, part, (part == nParts), partsRootDir, "PartTempFileTask.txt");
				ptTask.setExpectedMessageDigest(Manifest.Algorithm.MD5, expectedMd);
				mergedFile = ptTask.call();
				if (part == nParts) {
					assertNotNull(mergedFile);
				} else {
					assertNull(mergedFile);
				}
			} finally {
				IOUtils.closeQuietly(partStream);
			}
		}

		// Verify the contents of the merged file.
		assertTrue(mergedFile.isFile());
		assertEquals(sumPartSizes(sourcePartFiles), mergedFile.length());
		verifyMergedFile(sourcePartFiles, mergedFile);
	}

	@Test
	public void testMergePartsIncorrectMd() throws Exception {
		// Create part files in staging.
		final int nParts = 2;
		List<File> sourcePartFiles = createFiles(nParts);

		// Execute PartTempFileTask for each.
		File mergedFile = null;
		for (int part = 1; part <= nParts; part++) {
			FileInputStream partStream = null;
			try {
				partStream = new FileInputStream(sourcePartFiles.get(part - 1));
				ptTask = new PartTempFileTask(partStream, part, (part == nParts), partsRootDir, "PartTempFileTask.txt");
				ptTask.setExpectedMessageDigest(Manifest.Algorithm.MD5, "abc");
				try {
					mergedFile = ptTask.call();
				} catch (IOException e) {
					if (part != nParts) {
						fail(e.getMessage());
					}
				}
				if (part != nParts) {
					assertNull(mergedFile);
				}
			} finally {
				IOUtils.closeQuietly(partStream);
			}
		}
	}

	private void verifyMergedFile(List<File> sourcePartFiles, File mergedFile) throws FileNotFoundException,
			IOException {
		InputStream mergedStream = null;
		byte[] sourceBuffer = new byte[8192];
		try {
			mergedStream = new BufferedInputStream(new FileInputStream(mergedFile));
			for (File f : sourcePartFiles) {
				FileInputStream sourcePartStream = null;
				try {
					sourcePartStream = new FileInputStream(f);
					for (int nBytesRead = sourcePartStream.read(sourceBuffer); nBytesRead != -1; nBytesRead = sourcePartStream
							.read(sourceBuffer)) {
						for (int i = 0; i < nBytesRead; i++) {
							assertEquals(sourceBuffer[i], (byte) mergedStream.read());
						}
					}
				} finally {
					IOUtils.closeQuietly(sourcePartStream);
				}
			}
		} finally {
			IOUtils.closeQuietly(mergedStream);
		}
	}

	private String calcMd(List<File> sourcePartFiles) throws NoSuchAlgorithmException, IOException {
		MessageDigest digester = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[8192];
		for (File f : sourcePartFiles) {
			DigestInputStream dis = null;
			try {
				dis = new DigestInputStream(new FileInputStream(f), digester);
				while (dis.read(buffer) != -1)
					;
			} finally {
				IOUtils.closeQuietly(dis);
			}
		}
		return new String(Hex.encodeHex(digester.digest())).toLowerCase();
	}

	private long sumPartSizes(List<File> sourcePartFiles) {
		long totalSize = 0L;
		for (File f : sourcePartFiles) {
			totalSize += f.length();
		}
		return totalSize;
	}

	private List<File> createFiles(int nFiles) throws IOException {
		List<File> createdFiles = new ArrayList<File>();
		for (int i = 0; i < nFiles; i++) {
			File file = File.createTempFile("PartTempFileTaskTest", null, stagingDir);
			TestUtil.createFileOfSizeInRange(file, 1L, 10L, FileUtils.ONE_MB);
			createdFiles.add(file);
			LOGGER.trace("Created part file: {} ({})", file.getName(), FileUtils.byteCountToDisplaySize(file.length()));
		}
		return createdFiles;
	}
}
