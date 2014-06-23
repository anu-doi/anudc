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

package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.edu.anu.datacommons.tasks.ThreadPoolService;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * Unit tests for DcStorage.
 * 
 * @author Rahul Khanna
 * 
 */
public class DcStorageTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageTest.class);

	@InjectMocks
	private DcStorage dcStorage;
	private long pidCounter;

	@Mock
	private StorageEventListener listener;
	@Mock
	private RecordDataInfoService rdiSvc;
	@Mock
	private ThreadPoolService threadPoolSvc;

	@Rule
	public TemporaryFolder bagsRootDir = new TemporaryFolder();
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		pidCounter = 1L;
		LOGGER.info("Bags root: {}", bagsRootDir.getRoot().getAbsolutePath());
		LOGGER.info("Temp dir: {}", tempDir.getRoot().getAbsolutePath());
		dcStorage = new DcStorage(bagsRootDir.getRoot());
		dcStorage.ff = new FileFactory(200);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Add a file to a collection, then delete it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingleAddUpdateDelete() throws Exception {
		final String pid = getNextPid();
		assertFalse(dcStorage.bagExists(pid));

		Path srcFile = tempDir.newFile().toPath();
		TestUtil.createFileOfSizeInRange(srcFile.toFile(), 2L, 5L, FileUtils.ONE_KB);
		long srcFileSize = Files.size(srcFile);
		
		// Add a file.
		String targetFilepath = "c/File 3.doc";
		dcStorage.addFile(pid, new UploadedFileInfo(srcFile, Files.size(srcFile), null), targetFilepath);
		Path targetFile = getPayloadDir(pid).resolve(targetFilepath);
		assertTrue(Files.isRegularFile(targetFile));
		assertThat(Files.size(targetFile), is(srcFileSize));

		// Replace the added file with an updated file.
		srcFile = tempDir.newFile().toPath();
		String srcMd5 = TestUtil.createFileOfSizeInRange(srcFile.toFile(), 6L, 10L, FileUtils.ONE_KB);
		srcFileSize = Files.size(srcFile);
		dcStorage.addFile(pid, new UploadedFileInfo(srcFile, Files.size(srcFile), srcMd5), targetFilepath);
		assertThat(Files.isRegularFile(targetFile), is(true));
		assertThat(Files.size(targetFile), is(srcFileSize));
		
		// Delete the added file
		dcStorage.deleteItem(pid, "c/File 3.doc");
		assertFalse(Files.isRegularFile(targetFile));
	}
	
	@Test
	public void testZipStream() throws Exception {
		String pid = getNextPid();

		assertFalse(dcStorage.bagExists(pid));

		// Add a file.
		File file1 = tempDir.newFile();
		String file1Md5 = TestUtil.createFileOfSizeInRange(file1, 2L, 4L, FileUtils.ONE_MB);
		String file1TargetPath = "a/File 1.txt";
		dcStorage.addFile(pid, new UploadedFileInfo(file1.toPath(), Files.size(file1.toPath()), null), file1TargetPath);

		assertTrue(Files.isRegularFile(getPayloadDir(pid).resolve(file1TargetPath)));

		// Read the contents of the file added as InputStream and verify MD5.
		assertTrue(MessageDigestHelper.fixityMatches(Files.newInputStream(getPayloadDir(pid).resolve(file1TargetPath)),
				Algorithm.MD5, file1Md5));

		// Add another file.
		File file2 = tempDir.newFile();
		String file2Md5 = TestUtil.createFileOfSizeInRange(file2, 3L, 5L, FileUtils.ONE_MB);
		String file2TargetPath = "b/File 2.pdf";
		dcStorage.addFile(pid, new UploadedFileInfo(file2.toPath(), Files.size(file2.toPath()), null), file2TargetPath);

		assertTrue(Files.isRegularFile(getPayloadDir(pid).resolve(file2TargetPath)));

		// With 2 payload files, get a zip stream of the 2 files.
		when(threadPoolSvc.submitCachedPool((Callable<?>) org.mockito.Matchers.any())).thenAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ExecutorService tp = Executors.newSingleThreadExecutor();
				tp.submit((Callable<?>) invocation.getArguments()[0]);
				tp.shutdown();
				return null;
			}

		});
		ZipInputStream zipIs = new ZipInputStream(dcStorage.createZipStream(pid,
				Arrays.asList(file1TargetPath, file2TargetPath)));
		Map<String, String> filepaths = new HashMap<String, String>();
		filepaths.put(file1TargetPath, file1Md5);
		filepaths.put(file2TargetPath, file2Md5);
		checkZipStream(zipIs, filepaths);

		// Delete the previously added files.
		dcStorage.deleteItem(pid, file1TargetPath);
		dcStorage.deleteItem(pid, file2TargetPath);

		assertFalse(Files.isRegularFile(getPayloadDir(pid).resolve(file1TargetPath)));
		assertFalse(Files.isRegularFile(getPayloadDir(pid).resolve(file2TargetPath)));
	}

	@Test
	public void testThreadedAdditionsAndModificationsToSamePid() throws Exception {
		ExecutorService workerPool = Executors.newCachedThreadPool();
		final String pid = getNextPid();
		final int nFiles = 10;
		Map<File, String> srcFileMap = new HashMap<File, String>(nFiles);
		List<Future<Void>> futures = new ArrayList<Future<Void>>();

		srcFileMap.putAll(createFiles(tempDir.getRoot(), nFiles));

		// Add the created files to a bag.
		for (Entry<File, String> entry : srcFileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					dcStorage.addFile(pid,
							new UploadedFileInfo(fEntry.getKey().toPath(), Files.size(fEntry.getKey().toPath()), null),
							fEntry.getKey().getName());
					return null;
				}

			}));
		}

		for (Future<Void> f : futures) {
			f.get();
		}
		futures.clear();

		// Create 10 new files and replace existing files with the new ones.
		Map<File, String> replacementFileMap = new HashMap<File, String>(srcFileMap);
		for (File oldFile : srcFileMap.keySet()) {
			assertFalse(oldFile.isFile());
			File file = tempDir.newFile(oldFile.getName());
			String md5 = TestUtil.createFileOfSizeInRange(file, 1L, 5L, FileUtils.ONE_MB);
			replacementFileMap.put(file, md5);
		}

		assertEquals(srcFileMap.size(), replacementFileMap.size());
		assertThat(srcFileMap.keySet(), everyItem(isIn(replacementFileMap.keySet())));
		assertThat(srcFileMap.values(), everyItem(not(isIn(replacementFileMap.values()))));

		// Replce existing files in the bag with the replacement files created.
		for (Entry<File, String> entry : replacementFileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					UploadedFileInfo ufi = new UploadedFileInfo(fEntry.getKey().toPath(), Files.size(fEntry.getKey()
							.toPath()), null);
					dcStorage.addFile(pid, ufi, fEntry.getKey().getName());
					return null;
				}
			}));
		}

		for (Future<?> f : futures) {
			f.get();
		}
		futures.clear();

		File[] payloadFiles = getPayloadDir(pid).toFile().listFiles();
		for (int i = 0; i < payloadFiles.length; i++) {
			if (payloadFiles[i].isFile()) {
				replacementFileMap.containsValue(MessageDigestHelper.generateFixity(payloadFiles[i], Algorithm.MD5));
			}
		}
	}

	@Test
	public void testThreadedUpdatingSameFileSamePid() throws Exception {
		ExecutorService workerPool = Executors.newCachedThreadPool();
		final String pid = getNextPid();
		int nFiles = 10;
		Map<File, String> fileMap = new HashMap<File, String>(nFiles);
		List<Future<Void>> futures = new ArrayList<>();

		// Create nFiles temp files.
		fileMap.putAll(createFiles(tempDir.getRoot(), nFiles));

		// Add created files to the bag and save as the same file.
		for (Entry<File, String> entry : fileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					dcStorage.addFile(pid,
							new UploadedFileInfo(fEntry.getKey().toPath(), Files.size(fEntry.getKey().toPath()), null),
							"Single file.data");
					return null;
				}

			}));
		}

		for (Future<?> f : futures) {
			f.get();
		}
		futures.clear();

		File file = getPayloadDir(pid).resolve("Single File.data").toFile();
		assertTrue(file.isFile());
		String md5 = MessageDigestHelper.generateFixity(file, Algorithm.MD5);
		LOGGER.trace("Final payload file has MD5: {}", md5);
		assertThat(fileMap, hasValue(md5));
	}

	@Test
	public void testFilepathsWithHiddenDirs() {
		Map<String, Boolean> dataset = new HashMap<String, Boolean>();
		dataset.put("data/.preserve/abc.txt", true);
		dataset.put("data/abc.txt", false);
		dataset.put("data/.abc.txt", true);
		dataset.put("data/.dir/.abc.txt", true);
		dataset.put(".data/.abc.txt", true);

		for (Entry<String, Boolean> entry : dataset.entrySet()) {
			assertThat(DcStorage.containsHiddenDirs(entry.getKey()), is(entry.getValue().booleanValue()));
		}
	}

	private Map<? extends File, ? extends String> createFiles(File root, int nFiles) throws IOException {
		Map<File, String> fileMap = new HashMap<File, String>(nFiles);
		for (int i = 0; i < nFiles; i++) {
			File file = tempDir.newFile();
			String md5 = TestUtil.createFileOfSizeInRange(file, 1L, 5L, FileUtils.ONE_MB);
			fileMap.put(file, md5);
			assertTrue(file.isFile());
		}
		return fileMap;
	}

	/**
	 * Checks that a ZipStream contains specified number of entries for the specified filenames.
	 * 
	 * @param zipIs
	 *            ZipInputStream to check
	 * @param filepaths
	 *            Collection of relative filepaths that the zip's entry should contain.
	 * @param list
	 * @param fsMap
	 * @throws IOException
	 */
	private void checkZipStream(ZipInputStream zipIs, Map<String, String> filepaths) throws IOException {
		int nZipEntries = 0;
		for (ZipEntry zipEntry = zipIs.getNextEntry(); zipEntry != null; zipEntry = zipIs.getNextEntry()) {
			LOGGER.trace("Checking ZipEntry {}", zipEntry.getName());
			byte[] fileContents;
			try (ByteArrayOutputStream extractedFileOutStream = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[8192];
				for (int numBytesRead = zipIs.read(buffer); numBytesRead != -1; numBytesRead = zipIs.read(buffer)) {
					extractedFileOutStream.write(buffer, 0, numBytesRead);
				}
				fileContents = extractedFileOutStream.toByteArray();
			}
			ByteArrayInputStream extractedFileInStream = null;
			try {
				extractedFileInStream = new ByteArrayInputStream(fileContents);
				assertEquals(filepaths.get(zipEntry.getName()),
						MessageDigestHelper.generateFixity(extractedFileInStream, Algorithm.MD5));
			} finally {
				IOUtils.closeQuietly(extractedFileInStream);
			}
			nZipEntries++;
		}
		assertEquals(filepaths.size(), nZipEntries);
	}

	private synchronized String getNextPid() {
		return format("test:{0}", pidCounter++);
	}

	private Path getBagDir(String pid) {
		return bagsRootDir.getRoot().toPath().resolve(DcStorage.convertToDiskSafe(pid));
	}

	private Path getPayloadDir(String pid) {
		return getBagDir(pid).resolve("data/");
	}
}
