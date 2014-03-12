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

import static java.text.MessageFormat.format;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Callable;
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

/**
 * @author Rahul Khanna
 *
 */
public class LockManagerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LockManagerTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private LockManager<Path> fileLockMgr;
	
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
		fileLockMgr = new LockManager<Path>();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		final int nThreads = 3;
		final Path[] path = {Paths.get("abc"), Paths.get("abc/"), Paths.get("ABC")};
		ExecutorService es = Executors.newFixedThreadPool(nThreads);
		final AtomicInteger counter = new AtomicInteger(0);
		ArrayList<Future<Exception>> futures = new ArrayList<>();

		for (int i = 0; i < nThreads; i++) {
			futures.add(es.submit(new TestLockTask(counter, this.fileLockMgr, path[i], TestLockTask.LockType.WRITE)));
		}

		es.shutdown();
		es.awaitTermination(1, TimeUnit.MINUTES);

		for (Future<Exception> f : futures) {
			if (f.get() != null) {
				fail(f.get().getMessage());
			}
		}
	}

	private static class TestLockTask implements Callable<Exception> {
		public enum LockType {
			READ, WRITE;
		}
		
		private AtomicInteger aInt;
		private LockManager<Path> fileLockMgr;
		private Path path;
		private LockType lockType;
		
		public TestLockTask(AtomicInteger aInt, LockManager<Path> mgr, Path path, LockType lockType) {
			this.aInt = aInt;
			this.fileLockMgr = mgr;
			this.path = path;
			this.lockType = lockType;
		}
		
		@Override
		public Exception call() throws Exception {
			Exception e = null;
			try {
				LOGGER.trace("{}: Obtaining {} lock...", Thread.currentThread().getName(), lockType.toString());
				if (lockType == LockType.READ) {
					fileLockMgr.readLock(path);
				} else if (lockType == LockType.WRITE) {
					fileLockMgr.writeLock(path);
				} else {
					throw new RuntimeException();
				}
				LOGGER.trace("{}: Obtained {} lock.", Thread.currentThread().getName(), lockType.toString());
				if (aInt.get() != 0) {
					e = new Exception(format("{0}: Counter expected 0, was {1}", Thread.currentThread()
							.getName(), aInt.get()));
				}
				aInt.incrementAndGet();
				Thread.sleep(500);
				if (aInt.get() != 1) {
					e = new Exception(format("{0}: Counter expected 1, was {1}", Thread.currentThread()
							.getName(), aInt.get()));
				}
				aInt.decrementAndGet();
			} finally {
				LOGGER.trace("{}: Releasing {} lock...", Thread.currentThread().getName(), lockType.toString());
				if (lockType == LockType.READ) {
					fileLockMgr.readUnlock(path);
				} else if (lockType == LockType.WRITE) {
					fileLockMgr.writeUnlock(path);
				} else {
					throw new RuntimeException();
				}
				LOGGER.trace("{}: Released {} lock.", Thread.currentThread().getName(), lockType.toString());
			}
			return e;
		}
		
	}
}
