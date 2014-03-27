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

package au.edu.anu.datacommons.storage.completer.virusscan;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.codec.binary.Base64;
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
public class ClamScanTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScanTest.class);

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private ClamScan cs;

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
		cs = new ClamScan();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClamScanNoVirus() throws Exception {
		File file = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(file, 2L, 5L, FileUtils.ONE_MB);
		String result = cs.scan(file.toPath());
		assertThat(result, endsWith("OK"));
	}

	@Test
	public void testClamScanWithVirus() throws Exception {
		// Virus string base64 encoded so this source file isn't flagged as infected.
		final String virusStr = new String(
				Base64.decodeBase64("WDVPIVAlQEFQWzRcUFpYNTQoUF4pN0NDKTd9JEVJQ0FSLVNUQU5EQVJELUFOVElWSVJVUy1URVNULUZJTEUhJEgrSCo="));
		File file = tempDir.newFile();
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			writer.write(virusStr);
		}
		assertThat(Files.isRegularFile(file.toPath()), is(true));
		assertThat(Files.size(file.toPath()), greaterThan(0L));
		String result = cs.scan(file.toPath());
		assertThat(result, endsWith("Eicar-Test-Signature FOUND"));
	}

}
