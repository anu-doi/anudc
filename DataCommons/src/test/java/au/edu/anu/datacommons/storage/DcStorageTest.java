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

import static java.text.MessageFormat.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.utilities.SimpleMessage;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.AnyDocumentImpl.AnyImpl;
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
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.tagfiles.AbstractKeyValueFile;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
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

	@Test
	public void testBasicAddDelete() throws Exception {
		String pid = getNextPid();
		assertFalse(dcStorage.bagExists(pid));

		Path srcFile = tempDir.newFile().toPath();
		TestUtil.createFileOfSizeInRange(srcFile.toFile(), 2L, 5L, FileUtils.ONE_KB);

		// Add a file.
		String targetFilepath = "c/File 3.doc";
		dcStorage.addFile(pid, srcFile.toFile(), targetFilepath);
		assertTrue(Files.isRegularFile(getPayloadDir(pid).resolve(targetFilepath)));

		// Delete the added file
		dcStorage.deleteItem(pid, "c/File 3.doc");
		assertFalse(Files.isRegularFile(getPayloadDir(pid).resolve(targetFilepath)));
	}

	@Test
	public void testFullWorkflow() throws IOException, InterruptedException {
		String pid = getNextPid();

		assertFalse(dcStorage.bagExists(pid));

		// Add a file.
		File file1 = tempDir.newFile();
		String file1Md5 = TestUtil.createFileOfSizeInRange(file1, 2L, 4L, FileUtils.ONE_MB);
		String file1TargetPath = "a/File 1.txt";
		dcStorage.addFile(pid, file1, file1TargetPath);

		assertTrue(Files.isRegularFile(getPayloadDir(pid).resolve(file1TargetPath)));

		// Read the contents of the file added as InputStream and verify MD5.
		assertTrue(MessageDigestHelper.fixityMatches(Files.newInputStream(getPayloadDir(pid).resolve(file1TargetPath)),
				Algorithm.MD5, file1Md5));

		// Add a file by downloading it from the web.
		File file2 = tempDir.newFile();
		String file2Md5 = TestUtil.createFileOfSizeInRange(file2, 3L, 5L, FileUtils.ONE_MB);
		String file2TargetPath = "b/File 2.pdf";
		dcStorage.addFile(pid, file2, file2TargetPath);

		assertTrue(Files.isRegularFile(getPayloadDir(pid).resolve(file2TargetPath)));

		// With 2 payload files, get a zip stream of the 2 files.
		when(threadPoolSvc.submitCachedPool((Callable<?>) org.mockito.Matchers.any())).thenAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ExecutorService tp = Executors.newSingleThreadExecutor();
				tp.submit((Callable<?>) invocation.getArguments()[0]);
				tp.shutdown();
				return null;
			}
			
		});
		ZipInputStream zipIs = new ZipInputStream(dcStorage.createZipStream(pid, Arrays.asList(file1TargetPath, file2TargetPath)));
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
	public void testThreadedAdditionsAndModificationsToSamePid() throws IOException, InterruptedException,
			ExecutionException {
		ExecutorService workerPool = Executors.newCachedThreadPool();
		final String pid = getNextPid();
		int nFiles = 10;
		Map<File, String> srcFileMap = new HashMap<File, String>(nFiles);
		List<Future<Void>> futures = new ArrayList<Future<Void>>();

		srcFileMap.putAll(createFiles(tempDir.getRoot(), nFiles));

		// Add the created files to a bag.
		for (Entry<File, String> entry : srcFileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					dcStorage.addFile(pid, fEntry.getKey(), fEntry.getKey().getName());
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
					dcStorage.addFile(pid, fEntry.getKey(), fEntry.getKey().getName());
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
	public void testThreadedUpdatingSameFileSamePid() throws IOException, InterruptedException, ExecutionException {
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
					dcStorage.addFile(pid, fEntry.getKey(), "Single file.data");
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
			try (ByteArrayOutputStream extractedFileOutStream = new ByteArrayOutputStream()){
				byte[] buffer = new byte[8192];
				for (int numBytesRead = zipIs.read(buffer); numBytesRead != -1; numBytesRead = zipIs.read(buffer)) {
					extractedFileOutStream.write(buffer, 0, numBytesRead);
				}
				fileContents = extractedFileOutStream.toByteArray();
			}
			ByteArrayInputStream extractedFileInStream = null;
			try {
				extractedFileInStream = new ByteArrayInputStream(fileContents);
				assertEquals(filepaths.get(zipEntry.getName()), MessageDigestHelper.generateFixity(extractedFileInStream, Algorithm.MD5));
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

	private boolean verifyBagAt(File bagDir) throws IOException {
		Bag bag = DcStorage.bagFactory.createBag(bagDir, LoadOption.BY_FILES);
		SimpleResult result = bag.verifyValid();
		for (SimpleMessage sm : result.getSimpleMessages()) {
			LOGGER.trace("Code: {}, MessageType: {}, Message: {}, Subject: {}", new Object[]{sm.getCode(), sm.getMessageType(),
					sm.getMessage(), sm.getSubject()});
			if (sm.getObjects() != null) {
				for (String obj : sm.getObjects()) {
					LOGGER.trace("\t{}", obj);
				}
			}
		}

		// Verify each tag file's entry's present in each tag file manifest.
		Collection<BagFile> tagFiles = bag.getTags();
		for (Manifest tagManifest : bag.getTagManifests()) {
			for (BagFile tagFile : tagFiles) {
				if (!tagFile.getFilepath().startsWith("tagmanifest") && !tagManifest.containsKey(tagFile.getFilepath())) {
					LOGGER.trace("Tagfile {} is not present in tag manifest {}", tagFile.getFilepath(),
							tagManifest.getFilepath());
					result.setSuccess(false);
				}
			}
		}

		// Verify each custom tag file contains an entry for each payload file.
		Collection<BagFile> payloadFiles = bag.getPayload();
		List<AbstractKeyValueFile> customTagFiles = new ArrayList<AbstractKeyValueFile>();
		customTagFiles.add(new PronomFormatsTagFile(new File(bagDir, PronomFormatsTagFile.FILEPATH)));
		customTagFiles.add(new VirusScanTagFile(new File(bagDir, VirusScanTagFile.FILEPATH)));
		customTagFiles.add(new FileMetadataTagFile(new File(bagDir, FileMetadataTagFile.FILEPATH)));
		customTagFiles.add(new TimestampsTagFile(new File(bagDir, TimestampsTagFile.FILEPATH)));
		customTagFiles.add(new PreservationMapTagFile(new File(bagDir, PreservationMapTagFile.FILEPATH)));
		for (BagFile bagFile : payloadFiles) {
			for (AbstractKeyValueFile tagFile : customTagFiles) {
				if (!tagFile.containsKey(bagFile.getFilepath())) {
					LOGGER.trace("Tagfile {} doesn't contain entry for payload file {}", tagFile.getFile().getName(),
							bagFile.getFilepath());
					result.setSuccess(false);
				}
			}
		}

		// Verify that there's a payload file for each entry in each custom manifest.
		for (AbstractKeyValueFile ctf : customTagFiles) {
			for (String key : ctf.keySet()) {
				boolean exists = false;
				for (BagFile plFile : payloadFiles) {
					if (plFile.getFilepath().equals(key)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					LOGGER.trace("Tag file {} contains entry for payload file {} that doesn't exist", ctf.getFile()
							.getName(), key);
					result.setSuccess(false);
				}
			}
		}

		return result.isSuccess();
	}

	private Path getBagDir(String pid) {
		return bagsRootDir.getRoot().toPath().resolve(DcStorage.convertToDiskSafe(pid));
	}
	
	private Path getPayloadDir(String pid) {
		return getBagDir(pid).resolve("data/");
	}
}
