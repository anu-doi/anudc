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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
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
	private static final String EICAR_VIRUS = new String(
			Base64.decodeBase64("WDVPIVAlQEFQWzRcUFpYNTQoUF4pN0NDKTd9JEVJQ0FSLVNUQU5EQVJELUFOVElWSVJVUy1URVNULUZJTEUhJEgrSCo="));

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
	public void testClamScanPing() throws Exception {
		assertThat(cs.ping(), is(true));
	}
	
	@Test
	public void testClamScanStats() throws Exception {
		String result = cs.stats();
		assertThat(result, is(notNullValue()));
	}
	
	@Test
	public void testClamScanInputStreamNoVirus() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(TestUtil.getRandomByteArray(1024));
		String scanResult = cs.scan(bais);
		assertThat(scanResult, endsWith("OK"));
	}
	
	@Test
	public void testClamScanInputStreamWithVirus() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(EICAR_VIRUS.getBytes(StandardCharsets.UTF_8));
		String scanResult = cs.scan(bais);
		assertThat(scanResult, endsWith("Eicar-Test-Signature FOUND"));
	}
}
