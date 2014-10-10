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

package au.edu.anu.datacommons.storage.event.tasks;

import static org.mockito.Mockito.verify;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.test.util.TestUtil;
import au.edu.anu.datacommons.util.Util;

/**
 * @author Rahul Khanna
 *
 */
public class BagCompletionTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagCompletionTaskTest.class);
	
	private static final String PID = "test:1";
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Mock
	private TagFilesService tagFilesSvc;
	
	private BagCompletionTask bcTask;
	private Path bagDir;
	
	private long expectedOctetCount = 0L;
	private long expectedStreamCount = 0L;
	
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
		LOGGER.info("Temp Dir: {}", tempDir.getRoot().getAbsolutePath());
		bagDir = tempDir.getRoot().toPath();
		MockitoAnnotations.initMocks(this);
		bcTask = new BagCompletionTask(PID, null, null, tagFilesSvc, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBagCompletion() throws Exception {
		createPlDirTree();
		bcTask.call();
		verify(tagFilesSvc).addEntry(PID, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_EXTERNAL_IDENTIFIER, PID);
		verify(tagFilesSvc).addEntry(Mockito.eq(PID), Mockito.eq(BagInfoTagFile.class),
				Mockito.eq(BagInfoTxtImpl.FIELD_BAGGING_DATE), Mockito.anyString());
		verify(tagFilesSvc).addEntry(PID, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM,
				Long.toString(expectedOctetCount, 10) + "." + Long.toString(expectedStreamCount));
		verify(tagFilesSvc).addEntry(PID, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_BAG_SIZE,
				Util.byteCountToDisplaySize(expectedOctetCount));
	}
	
	private Path createPlDirTree() throws IOException {
		Path plDir = bagDir.resolve("data/");
		this.expectedOctetCount += createFiles(plDir, 4);
		this.expectedStreamCount += 4;
		
		Path subDir = plDir.resolve("subdir/");
		this.expectedOctetCount += createFiles(subDir, 5);
		this.expectedStreamCount += 5;
		
		Path hiddenSubdir = plDir.resolve(".preserve/");
		// Not adding files in the hidden directory to expecteds - they shouldn't be counted.
		createFiles(hiddenSubdir, 3);
		
		return plDir;
	}
	
	private long createFiles(Path dir, int nFiles) throws IOException {
		long totalSize = 0L;
		if (!Files.isDirectory(dir)) {
			Files.createDirectory(dir);
		}
		for (int i = 0; i < nFiles; i++) {
			Path f = Files.createTempFile(dir, "BagCompTask", "Test");
			TestUtil.createFileOfSizeInRange(f.toFile(), 100, 200);
			totalSize += Files.size(f);
		}
		return totalSize;
	}
}
