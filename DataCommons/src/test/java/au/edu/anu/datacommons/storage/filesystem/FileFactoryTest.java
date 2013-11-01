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

package au.edu.anu.datacommons.storage.filesystem;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.filesystem.FileFactory;

/**
 * @author Rahul Khanna
 * 
 */
public class FileFactoryTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileFactoryTest.class);
	private final int CAPACITY = 10;
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private FileFactory ff;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOGGER.trace("Temp dir: {}", TEMP_DIR);
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
		ff = new FileFactory(CAPACITY);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFile() {
		assertThat(ff.cache, is(empty()));
		String path1 = "Abc1.txt";
		File f1 = ff.getFile(TEMP_DIR, path1);
		assertThat(ff.cache, hasSize(1));
		assertThat(ff.getFile(TEMP_DIR, path1), sameInstance(f1));

		String path2 = "Abc2.txt";
		File f2 = ff.getFile(TEMP_DIR, path2);
		assertThat(ff.getFile(TEMP_DIR, path2), sameInstance(f2));
	}

	@Test
	public void testMaxCapacity() {
		int capacity = ff.cache.remainingCapacity();
		assertEquals(CAPACITY, capacity);
		List<File> files = new ArrayList<File>(capacity);
		for (int i = 0; i < capacity; i++) {
			files.add(ff.getFile(TEMP_DIR, "File-" + String.valueOf(i)));
		}

		assertEquals(0, ff.cache.remainingCapacity());
		File newFile = ff.getFile(TEMP_DIR, "NewFile");
		assertThat(ff.cache, not(hasItem(files.get(0))));
		assertThat(ff.cache, hasItem(newFile));
		assertEquals(newFile, ff.cache.peek());

		logQueue(ff.cache);

		File file5 = ff.getFile(files.get(5).getAbsolutePath());
		assertThat(file5, sameInstance(files.get(5)));
		assertEquals(file5, ff.cache.peek());

		logQueue(ff.cache);
	}

	@Test
	public void testThreaded() throws InterruptedException, ExecutionException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		int nThreads = 10;
		final AtomicInteger ai = new AtomicInteger(0);
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (int i = 0; i < nThreads; i++) {
			futures.add(threadPool.submit(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					File f = ff.getFile(TEMP_DIR, "File.txt");
					synchronized (f) {
						ai.getAndIncrement();
						LOGGER.trace("Start {} {}", f.hashCode(), ai.get());
						if (ai.get() != 1) {
							return false;
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							return false;
						}
						ai.decrementAndGet();
						LOGGER.trace("End {} {}", f.hashCode(), ai.get());
						if (ai.get() != 0) {
							return false;
						}
					}
					return true;
				}
			}));
		}
		for (Future<Boolean> f : futures) {
			assertTrue(f.get().booleanValue());
		}

		threadPool.shutdown();
		try {
			threadPool.awaitTermination(nThreads, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testThreadedParentChild() throws InterruptedException, ExecutionException {
		final File parent = ff.getFile(TEMP_DIR);
		final File child = ff.getFile(parent, "abc.txt");
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		futures.add(threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				synchronized(parent) {
					LOGGER.trace("Start parent");
					Thread.sleep(200);
					LOGGER.trace("End parent");
				}
				return true;
			}
			
		}));
		futures.add(threadPool.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				synchronized(child) {
					LOGGER.trace("Start child");
					Thread.sleep(200);
					LOGGER.trace("End child");
				}
				return true;
			}
			
		}));
		for (Future<Boolean> f : futures) {
			assertTrue(f.get().booleanValue());
		}

		threadPool.shutdown();
		try {
			threadPool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	
	}

	private void logQueue(Queue<File> q) {
		Iterator<File> iter = q.iterator();
		LOGGER.trace("Queue contents in order:");
		while (iter.hasNext()) {
			LOGGER.trace(iter.next().getAbsolutePath());
		}

	}
}
