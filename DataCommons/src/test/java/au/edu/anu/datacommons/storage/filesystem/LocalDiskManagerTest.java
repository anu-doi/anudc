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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsNull;
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
public class LocalDiskManagerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDiskManagerTest.class);

	@Rule
	public TemporaryFolder rootDir = new TemporaryFolder();

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private LocalDiskManager ldm;

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
		LOGGER.info("Root: {}", rootDir.getRoot().getAbsolutePath());
		LOGGER.info("Temp: {}", tempDir.getRoot().getAbsolutePath());
		ldm = new LocalDiskManager(rootDir.getRoot().toPath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFullPath() {
		String path = ldm.createFullPath("a", "b", "c");
		assertThat(path, anyOf(endsWith("\\a\\b\\c"), endsWith("/a/b/c")));
	}

	@Test
	public void testMoveFile() throws Exception {
		File file = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(file, 2L, 10L, FileUtils.ONE_KB);
		long size = file.length();

		ldm.move(file.toPath(), "file.txt");
		Path target = Paths.get(rootDir.getRoot().getAbsolutePath(), "file.txt");
		assertThat(Files.isRegularFile(target), is(true));
		assertThat(Files.size(target), equalTo(size));
	}

	@Test
	public void testSaveInputStream() throws Exception {
		int size = 1024 * 1024;
		byte[] bytes = TestUtil.getRandomByteArray(size);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		String[] target = { "dir", "anotherdir", "file.txt" };
		try {
			ldm.write(bais, target);
		} finally {
			IOUtils.closeQuietly(bais);
		}

		Path targetPath = Paths.get(rootDir.getRoot().getAbsolutePath(), target);
		assertThat(Files.isRegularFile(targetPath), is(true));
		assertThat(Files.size(targetPath), is((long) size));
	}

	@Test
	public void testFileExists() throws Exception {
		Path file = tempDir.newFile().toPath();
		String[] targetFile = { "folder1", "folder2", "folder3", "file.txt" };

		ldm.move(file, targetFile);

		assertThat(ldm.fileOrDirExists(targetFile), is(true));
		assertThat(ldm.fileExists(targetFile), is(true));
	}

	@Test
	public void testEnumFilesInDir() throws Exception {
		List<Path> enumFiles;

		enumFiles = ldm.enumFiles(true, "/");
		assertThat(enumFiles, is(emptyIterable()));

		Path f1 = rootDir.newFile().toPath();
		Path f2 = rootDir.newFile().toPath();
		Path d1 = rootDir.newFolder().toPath();
		Path d1d1 = d1.resolve("subdir1");
		Files.createDirectory(d1d1);
		Path d1f1 = d1d1.resolve("file.txt");
		Files.createFile(d1f1);

		enumFiles = ldm.enumFiles(true, "/");
		assertThat(enumFiles, hasSize(3));
		LOGGER.trace("{}", enumFiles);
	}

	@Test
	public void testMultiThreadedWriteToSameFile() throws Exception {
		final int nThreads = 10;
		final String targetRelPath = "dir1/file.txt";
		final HashMap<byte[], Integer> streams = new HashMap<>();
		for (int i = 0; i < nThreads; i++) {
			int size = TestUtil.rand.nextInt(1024 * 1024 + 200);
			streams.put(TestUtil.getRandomByteArray(size), size);
		}

		ArrayList<Future<Exception>> futures = new ArrayList<Future<Exception>>();
		ExecutorService es = Executors.newFixedThreadPool(nThreads);
		for (byte[] buffer : streams.keySet()) {
			final ByteArrayInputStream fStream = new ByteArrayInputStream(buffer);
			futures.add(es.submit(new Callable<Exception>() {

				@Override
				public Exception call() throws Exception {
					try {
						ldm.write(fStream, targetRelPath);
					} catch (Exception e) {
						return e;
					} finally {
						IOUtils.closeQuietly(fStream);
					}
					return null;
				}
			}));
		}

		es.shutdown();
		for (Future<Exception> f : futures) {
			if (f.get() != null) {
				LOGGER.error(f.get().getMessage(), f.get());
			}
			assertThat(f.get(), is(nullValue()));
		}

		es.awaitTermination(1, TimeUnit.MINUTES);

		Path target = rootDir.getRoot().toPath().resolve(targetRelPath);
		assertThat(ldm.fileExists(targetRelPath), is(true));

		boolean success = false;
		for (byte[] buffer : streams.keySet()) {
			try (InputStream targetStream = Files.newInputStream(target);
					ByteArrayInputStream srcStream = new ByteArrayInputStream(buffer)) {
				if (IOUtils.contentEquals(targetStream, srcStream)) {
					success = true;
					break;
				}
			}
		}

		if (!success) {
			fail();
		}
	}
}
