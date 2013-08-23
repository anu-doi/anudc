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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;
import au.edu.anu.datacommons.storage.tagfiles.AbstractKeyValueFile;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * Unit tests for DcStorage.
 * 
 * @author Rahul Khanna
 * 
 */
public class DcStorageTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageTest.class);

	private DcStorage dcStorage;
	private long pidCounter;

	@Rule
	public TemporaryFolder bagsRootDir = new TemporaryFolder();
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	@Rule
	public TemporaryFolder archiveRootDir = new TemporaryFolder();

	private Random random = new Random();

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
		LOGGER.info("Archive root: {}", archiveRootDir.getRoot().getAbsolutePath());
		LOGGER.info("Temp dir: {}", tempDir.getRoot().getAbsolutePath());
		dcStorage = new DcStorage(bagsRootDir.getRoot());
		dcStorage.archiveRootDir = archiveRootDir.getRoot();
	}

	@After
	public void tearDown() throws Exception {
		dcStorage.close();
	}

	@Test
	public void testFullWorkflow() throws IOException, InterruptedException {
		String pid = getNextPid();

		assertFalse(dcStorage.bagExists(pid));

		// Add a file.
		File file1 = tempDir.newFile();
		String file1Md5 = TestUtil.createFileOfSize(file1, 2L, FileUtils.ONE_MB);
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.addFileToBag(pid, file1, "a/File 1.txt");
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);

		assertTrue(dcStorage.bagExists(pid));
		assertTrue(new File(dcStorage.getBagDir(pid), "data/a/File 1.txt").isFile());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));

		// Read the contents of the file added as InputStream and verify MD5.
		InputStream fileStream = dcStorage.getFileStream(pid, "a/File 1.txt");
		assertTrue(MessageDigestHelper.fixityMatches(fileStream, Algorithm.MD5, file1Md5));

		// Add a file by downloading it from the web.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.addFileToBag(pid, new URL("http://samplepdf.com/sample.pdf"), "b/File 2.pdf");
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);

		assertTrue(new File(dcStorage.getBagDir(pid), "data/b/File 2.pdf").isFile());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));

		// Get a Bag Summary.
		BagSummary bagSummary = dcStorage.getBagSummary(pid);
		assertEquals(pid, bagSummary.getPid());
		assertEquals(0, bagSummary.getExtRefsTxt().size());
		assertEquals(2, bagSummary.getFileSummaryMap().size());
		Set<String> filepaths = bagSummary.getFileSummaryMap().keySet();
		for (String filepath : filepaths) {
			LOGGER.trace("Payload file: {}", filepath);
		}
		assertTrue(filepaths.contains("data/a/File 1.txt"));
		assertTrue(filepaths.contains("data/b/File 2.pdf"));

		// With 2 payload files, get a zip stream of the 2 files.
		ZipInputStream zipIs = new ZipInputStream(dcStorage.getFilesAsZipStream(pid, filepaths));
		checkZipStream(zipIs, filepaths, bagSummary.getFileSummaryMap());

		// Delete the previously added files.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.deleteFileFromBag(pid, "data/a/File 1.txt");
		dcStorage.deleteFileFromBag(pid, "data/b/File 2.pdf");
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);

		assertFalse(new File(dcStorage.getBagDir(pid), "data/a/File 1.txt").isFile());
		assertFalse(new File(dcStorage.getBagDir(pid), "data/b/File 2.pdf").isFile());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
	}

	@Test
	public void testExtRef() throws IOException, InterruptedException {
		String pid = getNextPid();
		ExtRefsTagFile extRefsTagFile;
		// Add 2 external reference.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.addExtRefs(pid, Arrays.asList("www.google.com.au", "www.facebook.com"));
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);

		File extRefsFile = new File(dcStorage.getBagDir(pid), ExtRefsTagFile.FILEPATH);
		assertTrue(extRefsFile.isFile());
		assertNotEquals(0L, extRefsFile.length());
		extRefsTagFile = new ExtRefsTagFile(extRefsFile);
		assertEquals(2, extRefsTagFile.size());
		assertTrue(verifyBagAt(extRefsFile.getParentFile()));

		// Delete one of the 2 external references.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.deleteExtRefs(pid, Arrays.asList("www.google.com.au"));
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);
		extRefsTagFile = new ExtRefsTagFile(extRefsFile);
		assertEquals(1, extRefsTagFile.size());
		assertTrue(verifyBagAt(extRefsFile.getParentFile()));

		// Delete the 1 remaining external reference.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		dcStorage.deleteExtRefs(pid, Arrays.asList("www.facebook.com"));
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);
		extRefsTagFile = new ExtRefsTagFile(extRefsFile);
		assertEquals(0, extRefsTagFile.size());
		assertTrue(verifyBagAt(extRefsFile.getParentFile()));
	}
	
	@Test
	public void testThreadedExtRef() throws IOException, InterruptedException, ExecutionException {
		final String pid = getNextPid();
		final int nExtRefs = 10;
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		
		ExecutorService workerPool = Executors.newCachedThreadPool();
		List<Future<Throwable>> futures = new ArrayList<Future<Throwable>>();
		for (int i = 0; i < nExtRefs; i++) {
			futures.add(workerPool.submit(new Callable<Throwable>() {

				@Override
				public Throwable call() throws Exception {
					try {
						dcStorage.addExtRefs(pid, Arrays.asList("www." + Thread.currentThread().getName() + ".com"));
					} catch (Throwable e) {
						return e;
					}
					return null;
				}
				
			}));
		}
		
		for (Future<Throwable> f : futures) {
			assertNull(f.get());
		}
		futures.clear();
		
		shutdownExecutor(dcStorage.threadPool, 1, TimeUnit.MINUTES);
		ExtRefsTagFile extRefsTagFile = new ExtRefsTagFile(new File(dcStorage.getBagDir(pid), ExtRefsTagFile.FILEPATH));
		assertEquals(nExtRefs, extRefsTagFile.size());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
		
		BagSummary bs = dcStorage.getBagSummary(pid);
		assertEquals(10, bs.getExtRefsTxt().size());
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		for (String url : bs.getExtRefsTxt().values()) {
			final String fUrl = url;
			futures.add(workerPool.submit(new Callable<Throwable>() {

				@Override
				public Throwable call() throws Exception {
					try {
						dcStorage.deleteExtRefs(pid, Arrays.asList(fUrl));
					} catch (Throwable e) {
						return e;
					}

					return null;
				}
				
			}));
		}
		
		for (Future<Throwable> f : futures) {
			assertNull(f.get());
		}
		futures.clear();
		
		extRefsTagFile = new ExtRefsTagFile(new File(dcStorage.getBagDir(pid), ExtRefsTagFile.FILEPATH));
		assertEquals(0, extRefsTagFile.size());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
	}

	@Test
	public void testThreadedAdditionsAndModificationsToSamePid() throws IOException, InterruptedException,
			ExecutionException {
		ExecutorService workerPool = Executors.newCachedThreadPool();
		final String pid = getNextPid();
		int nFiles = 10;
		Map<File, String> fileMap = new HashMap<File, String>(nFiles);
		List<Future<Throwable>> futures = new ArrayList<Future<Throwable>>();

		fileMap.putAll(createFiles(tempDir.getRoot(), nFiles));

		// Add the created files to a bag.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		for (Entry<File, String> entry : fileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Throwable>() {

				@Override
				public Throwable call() throws Exception {
					try {
						dcStorage.addFileToBag(pid, fEntry.getKey(), fEntry.getKey().getName());
					} catch (FileNotFoundException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					}
					return null;
				}

			}));
		}

		for (Future<Throwable> f : futures) {
			assertNull(f.get());
		}
		futures.clear();

		shutdownExecutor(dcStorage.threadPool, nFiles, TimeUnit.MINUTES);
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));

		BagSummary bagSummary = dcStorage.getBagSummary(pid);
		assertEquals(fileMap.size(), bagSummary.getFileSummaryMap().size());
		for (Entry<String, FileSummary> fsMapEntry : bagSummary.getFileSummaryMap().entrySet()) {
			assertTrue(fileMap.containsValue(fsMapEntry.getValue().getMessageDigests()
					.get(Algorithm.MD5.javaSecurityAlgorithm)));
		}

		// Create 10 new files and replace existing files with the new ones.
		Map<File, String> replacementFileMap = new HashMap<File, String>(fileMap);
		for (File oldFile : fileMap.keySet()) {
			assertFalse(oldFile.isFile());
			File file = tempDir.newFile(oldFile.getName());
			String md5 = TestUtil.createFileOfSizeInRange(file, 1L, 5L, FileUtils.ONE_MB);
			replacementFileMap.put(file, md5);
		}

		assertEquals(fileMap.size(), replacementFileMap.size());
		assertThat(fileMap.keySet(), everyItem(isIn(replacementFileMap.keySet())));
		assertThat(fileMap.values(), everyItem(not(isIn(replacementFileMap.values()))));

		// Replce existing files in the bag with the replacement files created.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		for (Entry<File, String> entry : replacementFileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Throwable>() {

				@Override
				public Throwable call() throws Exception {
					try {
						dcStorage.addFileToBag(pid, fEntry.getKey(), fEntry.getKey().getName());
					} catch (FileNotFoundException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					}
					return null;
				}
			}));
		}

		for (Future<?> f : futures) {
			assertNull(f.get());
		}
		futures.clear();

		shutdownExecutor(dcStorage.threadPool, nFiles, TimeUnit.MINUTES);
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
		bagSummary = dcStorage.getBagSummary(pid);
		for (Entry<String, FileSummary> fsMapEntry : bagSummary.getFileSummaryMap().entrySet()) {
			assertTrue(replacementFileMap.containsValue(fsMapEntry.getValue().getMessageDigests()
					.get(Algorithm.MD5.javaSecurityAlgorithm)));
		}

		File[] payloadFiles = dcStorage.getPayloadDir(pid).listFiles();
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
		List<Future<Throwable>> futures = new ArrayList<Future<Throwable>>();

		// Create nFiles temp files.
		fileMap.putAll(createFiles(tempDir.getRoot(), nFiles));

		// Add created files to the bag and save as the same file.
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		for (Entry<File, String> entry : fileMap.entrySet()) {
			final Entry<File, String> fEntry = entry;
			futures.add(workerPool.submit(new Callable<Throwable>() {

				@Override
				public Throwable call() throws Exception {
					try {
						dcStorage.addFileToBag(pid, fEntry.getKey(), "Single file.data");
					} catch (FileNotFoundException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
						return e;
					}
					return null;
				}

			}));
		}

		for (Future<Throwable> f : futures) {
			assertNull(f.get());
		}
		futures.clear();

		shutdownExecutor(dcStorage.threadPool, nFiles, TimeUnit.MINUTES);
		File file = new File(dcStorage.getPayloadDir(pid), "Single File.data");
		assertTrue(file.isFile());
		String md5 = MessageDigestHelper.generateFixity(file, Algorithm.MD5);
		assertThat(fileMap, hasValue(md5));
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
	}

	@Test
	public void testAddFileThenImmediatelyDelete() throws IOException, InterruptedException {
		String pid = getNextPid();
		dcStorage.threadPool = Executors.newSingleThreadExecutor();
		File file = tempDir.newFile();
		String md5 = TestUtil.createFileOfSize(file, 5L, FileUtils.ONE_MB);
		String filepath = "File.txt";
		dcStorage.addFileToBag(pid, file, filepath);
		dcStorage.deleteFileFromBag(pid, filepath);
		shutdownExecutor(dcStorage.threadPool, 30, TimeUnit.SECONDS);
		assertFalse(new File(dcStorage.getPayloadDir(pid), filepath).isFile());
		assertTrue(verifyBagAt(dcStorage.getBagDir(pid)));
	}

	private Map<? extends File, ? extends String> createFiles(File root, int nFiles) throws IOException {
		Map<File, String> fileMap = new HashMap<File, String>(nFiles);
		for (int i = 0; i < nFiles; i++) {
			File file = tempDir.newFile();
			String md5 = TestUtil.createFileOfSizeInRange(file, 1L, 5L, FileUtils.ONE_MB);
			fileMap.put(file, md5);
			assertTrue(file.isFile());
			LOGGER.trace("Created file {} ({}) MD5: {}", file.getName(),
					FileUtils.byteCountToDisplaySize(file.length()), md5);
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
	 * @param fsMap
	 * @throws IOException
	 */
	private void checkZipStream(ZipInputStream zipIs, Collection<String> filepaths, FileSummaryMap fsMap)
			throws IOException {
		int nZipEntries = 0;
		for (ZipEntry zipEntry = zipIs.getNextEntry(); zipEntry != null; zipEntry = zipIs.getNextEntry()) {
			LOGGER.trace("Checking ZipEntry {}", zipEntry.getName());
			ByteArrayOutputStream extractedFileOutStream = null;
			byte[] fileContents;
			try {
				extractedFileOutStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[8192];
				for (int numBytesRead = zipIs.read(buffer); numBytesRead != -1; numBytesRead = zipIs.read(buffer)) {
					extractedFileOutStream.write(buffer, 0, numBytesRead);
				}
				fileContents = extractedFileOutStream.toByteArray();
			} finally {
				IOUtils.closeQuietly(extractedFileOutStream);
			}
			ByteArrayInputStream extractedFileInStream = null;
			try {
				extractedFileInStream = new ByteArrayInputStream(fileContents);
				assertEquals(
						fsMap.getFileSummary("data/" + zipEntry.getName()).getMessageDigests()
								.get(Algorithm.MD5.javaSecurityAlgorithm),
						MessageDigestHelper.generateFixity(extractedFileInStream, Algorithm.MD5));
			} finally {
				IOUtils.closeQuietly(extractedFileInStream);
			}
			nZipEntries++;
		}
		assertEquals(fsMap.size(), nZipEntries);
	}

	private synchronized String getNextPid() {
		return format("test:{0}", pidCounter++);
	}

	private boolean verifyBagAt(File bagDir) throws IOException {
		Bag bag = DcStorage.bagFactory.createBag(bagDir, LoadOption.BY_FILES);
		SimpleResult result = bag.verifyValid();
		for (SimpleMessage sm : result.getSimpleMessages()) {
			LOGGER.trace("Code: {}, MessageType: {}, Message: {}, Subject: {}", sm.getCode(), sm.getMessageType(),
					sm.getMessage(), sm.getSubject());
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
		customTagFiles.add(new PronomFormatsTagFile(bagDir));
		customTagFiles.add(new VirusScanTagFile(bagDir));
		customTagFiles.add(new FileMetadataTagFile(bagDir));
		customTagFiles.add(new TimestampsTagFile(bagDir));
		for (BagFile bagFile : payloadFiles) {
			for (AbstractKeyValueFile tagFile : customTagFiles) {
				if (!tagFile.containsKey(bagFile.getFilepath())) {
					LOGGER.trace("Tagfile {} doesn't contain entry for payload file {}", tagFile.getFile().getName(),
							bagFile.getFilepath());
					result.setSuccess(false);
				}
			}
		}

		return result.isSuccess();
	}

	private void shutdownExecutor(ExecutorService exec, long timeout, TimeUnit unit) throws InterruptedException {
		exec.shutdown();
		exec.awaitTermination(timeout, unit);
	}
}
