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

package au.edu.anu.datacommons.tasks;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
public class ThreadPoolServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolServiceTest.class);
	
	private ThreadPoolService tpSvc;
	
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
		tpSvc = new ThreadPoolService(2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tpSvc.close();
	}

	@Test
	public void test() throws Exception {
		List<Future<Void>> futures = new ArrayList<>();
		
		futures.add(tpSvc.submit(new SampleCallable()));
		futures.add(tpSvc.submit(new SampleCallable()));
		futures.add(tpSvc.submitCachedPool(new SampleCallable()));
		
		
		for (Future<Void> f : futures) {
			f.get();
		}
	}

	private static class SampleCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			Thread.sleep(1000);
			LOGGER.trace("{}", Thread.currentThread().getName());
			return null;
		}
		
	}
}
