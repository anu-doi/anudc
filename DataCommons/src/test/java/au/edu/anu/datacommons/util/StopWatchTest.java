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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
public class StopWatchTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(StopWatchTest.class);
	
	private StopWatch sw;
	
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
		sw = new StopWatch();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRealFunctionality() throws Exception {
		final int millis = 100;
		sw.start();
		Thread.sleep(millis);
		sw.stop();
		assertThat(sw.getTimeElapsedMillis(), allOf(greaterThanOrEqualTo(millis - 30L), lessThanOrEqualTo(millis + 30L)));
	}

	@Test
	public void testFormat() throws Exception {
		sw.start();
		sw.stop = new Date(sw.start.getTime() + 100L);
		LOGGER.trace("{}", sw.getTimeElapsedFormatted());
		assertThat(sw.getTimeElapsedFormatted(), is("0.100"));

		sw.stop = new Date(sw.start.getTime() + TimeUnit.MINUTES.toMillis(2L) + TimeUnit.SECONDS.toMillis(3L)
				+ TimeUnit.MILLISECONDS.toMillis(123L));
		LOGGER.trace("{}", sw.getTimeElapsedFormatted());
		assertThat(sw.getTimeElapsedFormatted(), is("2:03.123"));

		sw.stop = new Date(sw.start.getTime() + TimeUnit.HOURS.toMillis(14L) + TimeUnit.MINUTES.toMillis(9L)
				+ +TimeUnit.SECONDS.toMillis(3L) + TimeUnit.MILLISECONDS.toMillis(123L));
		LOGGER.trace("{}", sw.getTimeElapsedFormatted());
		assertThat(sw.getTimeElapsedFormatted(), is("14:09:03.123"));
	}
	
	@Test
	public void testRate() throws Exception {
		sw.start();
		sw.stop = new Date(sw.start.getTime() + TimeUnit.MINUTES.toMillis(1L));
		String rate = sw.getRate(60L * FileUtils.ONE_MB);
		assertThat(rate, is("1.00 MB/sec"));
		
		sw.stop = new Date(sw.start.getTime() + TimeUnit.SECONDS.toMillis(1L));
		rate = sw.getRate(10L * FileUtils.ONE_MB);
		LOGGER.trace(rate);
	}
}
