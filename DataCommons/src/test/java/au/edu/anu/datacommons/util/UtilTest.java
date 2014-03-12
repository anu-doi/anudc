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

package au.edu.anu.datacommons.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class UtilTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilTest.class);
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Map<Long, String> testParams = new HashMap<Long, String>();
		
		testParams.put(900L * 1024L * 1024L - 400L, "899 MB");
		testParams.put(90L * 1024L * 1024L - 400L, "89.9 MB");
		testParams.put(9L * 1024L * 1024L - 400L, "8.99 MB");
		testParams.put(1024L * 1024L, "1.00 MB");
		testParams.put(1024L * 1024L - 1L, "1023 KB");
		testParams.put(1023L, "1023 bytes");
		testParams.put(1L, "1 byte");
		testParams.put(2L, "2 bytes");
		
		for (Entry<Long, String> i : testParams.entrySet()) {
			String result = Util.byteCountToDisplaySize(i.getKey());
			LOGGER.trace("{}: {}", i.getKey(), result);
			assertThat(result, is(i.getValue()));
		}
	}

}
