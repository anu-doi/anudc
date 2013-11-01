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

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;

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

import au.edu.anu.datacommons.storage.temp.TempFileTask;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class TempFileTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileTaskTest.class);

	private static URL fileUrl;
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private TempFileTask tfs;
	private File savedFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileUrl = new URL("http://hr.anu.edu.au/__documents/info-for/new-staff/induction-guide.pdf");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(savedFile);
	}

	/**
	 * Downloads a file from a URL with an expected Message Digest.
	 */
	@Test
	public void testSaveTempUrl() throws Exception {
		tfs = new TempFileTask(fileUrl, tempDir.getRoot());
		tfs.setExpectedMessageDigest(Manifest.Algorithm.MD5, "19857a231313ad5f6fdd8923ae415d2c");
		savedFile = tfs.call();
		
		assertTrue(savedFile.exists());
		assertTrue(savedFile.length() > 0);
	}

	
	@Test
	public void testSaveTempInputStream() throws Exception{
		int size = 10 * 1024 * 1024;
		byte[] randomByteArray = TestUtil.getRandomByteArray(size);
		MessageDigest md = null;
		md = MessageDigest.getInstance(Algorithm.MD5.javaSecurityAlgorithm);
		
		String expectedMd = new String(Hex.encodeHex(md.digest(randomByteArray), true));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(randomByteArray);
		tfs = new TempFileTask(bis, tempDir.getRoot());
		tfs.setExpectedMessageDigest(Algorithm.MD5, expectedMd);
		savedFile = tfs.call();
		
		assertTrue(savedFile.exists());
		assertEquals(size, savedFile.length());
	}
	
	@Test(expected = IOException.class)
	public void testSaveTempUrlWithException() throws Exception {
		tfs = new TempFileTask(fileUrl, tempDir.getRoot());
		tfs.setExpectedMessageDigest(Manifest.Algorithm.MD5, "b606e3998f6f3da1cb4646940db0f297");
		savedFile = tfs.call();
	}
}
