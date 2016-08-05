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

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class ClamScanTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScanTest.class);
	
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
		cs = new ClamScan();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOkStream() throws Exception {
		ByteArrayInputStream inMemStream = TestUtil.createInMemStream(1024 * 1024);
		String scanResult = cs.scan(inMemStream);
		LOGGER.trace("[{}]", scanResult);
		assertThat(scanResult, Matchers.endsWith("OK"));
	}

	@Test
	public void testVirusStream() throws Exception {
		// encoding the virus string as base64 so this file isn't flagged as infected by virus scanners
		String virusStrBase64 = "WDVPIVAlQEFQWzRcUFpYNTQoUF4pN0NDKTd9JEVJQ0FSLVNUQU5EQVJELUFOVElWSVJVUy1URVNU" + 
				"LUZJTEUhJEgrSCo=";
		ByteArrayInputStream inMemStream = new ByteArrayInputStream(Base64.getDecoder().decode(virusStrBase64));
		String scanResult = cs.scan(inMemStream);
		LOGGER.trace("[{}]", scanResult);
		assertThat(scanResult, Matchers.endsWith("Eicar-Test-Signature FOUND"));
		
	}
}
