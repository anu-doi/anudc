package au.edu.anu.datacommons.storage.archive;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.archive.ArchiveItem.Operation;
import au.edu.anu.datacommons.test.util.TestUtil;

public class ArchiveTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveTaskTest.class);
	private static final int NUM_FILES = 5;
	private static final String PID = "test:1";
	
	private ArchiveTask archiveTask;
	private Map<File, String> filesToArchive;
	private Collection<ArchiveItem> archivedItems = null;
	
	@Rule
	public TemporaryFolder sourceDir = new TemporaryFolder();
	
	@Rule
	public TemporaryFolder archiveBaseDir = new TemporaryFolder();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ArchiveTask.archiveBaseDir = archiveBaseDir.getRoot();
		LOGGER.debug("Archive Base Dir: {}", archiveBaseDir.getRoot().getAbsolutePath());

		filesToArchive = new HashMap<File, String>(NUM_FILES);
		for (int i = 0; i < NUM_FILES; i++) {
			File createdFile = sourceDir.newFile();
			int sizeInMB = TestUtil.RANDOM.nextInt(5) + 1;
			String md5 = TestUtil.fillRandomData(createdFile, sizeInMB);
			filesToArchive.put(createdFile, md5);
			assertTrue(createdFile.exists());
			assertEquals(sizeInMB * 1024 * 1024, createdFile.length());
			LOGGER.trace("Created {} ({}) MD5: {}", createdFile.getAbsolutePath(),
					FileUtils.byteCountToDisplaySize(createdFile.length()), md5);
		}

		archiveTask = new ArchiveTask();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testArchiveFiles() {
		for (Entry<File, String> entry : filesToArchive.entrySet()) {
			ArchiveItem item = new ArchiveItem(PID, entry.getKey(), Algorithm.MD5, entry.getValue(), Operation.DELETE);
			archiveTask.addArchiveItem(item);
		}
		try {
			archivedItems = archiveTask.call();
		} catch (Exception e) {
			failOnException(e);
		}
		
		assertNotNull(archivedItems);
		assertEquals(NUM_FILES, archivedItems.size());
		for (ArchiveItem item : archivedItems) {
			assertTrue(item.getArchivedFile().exists());
			assertFalse(item.getFileToArchive().exists());
			assertEquals(item.getMessageDigest(), MessageDigestHelper.generateFixity(item.getArchivedFile(), item.getAlgorithm()));
		}

		LOGGER.trace("Done");
	}
	
	@Test
	public void testArchiveFilesNullMd() {
		for (Entry<File, String> entry : filesToArchive.entrySet()) {
			ArchiveItem item = new ArchiveItem(PID, entry.getKey(), null, null, Operation.REPLACE);
			archiveTask.addArchiveItem(item);
		}
		try {
			archivedItems = archiveTask.call();
		} catch (Exception e) {
			failOnException(e);
		}
		
		assertNotNull(archivedItems);
		assertEquals(NUM_FILES, archivedItems.size());
		for (ArchiveItem item : archivedItems) {
			assertTrue(item.getArchivedFile().exists());
			assertFalse(item.getFileToArchive().exists());
		}
		
		LOGGER.trace("Done");
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
