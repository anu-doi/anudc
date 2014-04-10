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

package au.edu.anu.datacommons.storage.info;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class RecordDataInfoServiceTest {
	private static final String PID = "test:1";

	private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataInfoServiceTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Mock
	private TagFilesService tagFileSvc;
	
	@InjectMocks
	private RecordDataInfoService rdiSvc;
	
	private RecordDataInfo rdi;
	private List<Path> plFiles;
	
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
		MockitoAnnotations.initMocks(this);
		when(tagFileSvc.getEntryValue(PID, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM)).thenReturn(
				Long.toString(100, 10) + "." + Long.toString(10, 10));
		plFiles = createPayloadDirTree();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateRdi() throws Exception {
		rdi = rdiSvc.createRecordDataInfo(PID, tempDir.getRoot().toPath());
		assertThat(rdi, is(notNullValue()));
		assertThat(rdi.getPid(), is(PID));
		assertThat(rdi.getFiles(), is(notNullValue()));
		int[] count = countFilesAndDirs(rdi);
		assertThat(count[1], is(plFiles.size()));
		assertThat(count[0], is(4));
		assertThat(rdi.getRecordSize(), is(100L));
		assertThat(rdi.getRecordNumFiles(), is(10L));
	}
	
	@Test
	public void testCreateDirLimitedRdi() throws Exception {
		int[] count;

		rdi = rdiSvc.createDirLimitedRecordDataInfo(PID, tempDir.getRoot().toPath(), "");
		assertThat(rdi, is(notNullValue()));
		assertThat(rdi.getFiles(), is(notNullValue()));
		count = countFilesAndDirs(rdi);
		assertThat(count[1], is(4));
		assertThat(count[0], is(2));
		assertThat(rdi.getRecordSize(), is(100L));
		assertThat(rdi.getRecordNumFiles(), is(10L));

		rdi = rdiSvc.createDirLimitedRecordDataInfo(PID, tempDir.getRoot().toPath(), "folder1/");
		assertThat(rdi, is(notNullValue()));
		assertThat(rdi.getFiles(), is(notNullValue()));
		count = countFilesAndDirs(rdi);
		assertThat(count[1], is(5));
		assertThat(count[0], is(2));
		assertThat(rdi.getRecordSize(), is(100L));
		assertThat(rdi.getRecordNumFiles(), is(10L));

		rdi = rdiSvc.createDirLimitedRecordDataInfo(PID, tempDir.getRoot().toPath(), "folder1/subfolder1/");
		assertThat(rdi, is(notNullValue()));
		assertThat(rdi.getFiles(), is(notNullValue()));
		count = countFilesAndDirs(rdi);
		assertThat(count[1], is(6));
		assertThat(count[0], is(3));
		assertThat(rdi.getRecordSize(), is(100L));
		assertThat(rdi.getRecordNumFiles(), is(10L));

		rdi = rdiSvc.createDirLimitedRecordDataInfo(PID, tempDir.getRoot().toPath(),
				"folder1/subfolder1/subsubfolder1/");
		assertThat(rdi, is(notNullValue()));
		assertThat(rdi.getFiles(), is(notNullValue()));
		count = countFilesAndDirs(rdi);
		assertThat(count[1], is(7));
		assertThat(count[0], is(3));
		assertThat(rdi.getRecordSize(), is(100L));
		assertThat(rdi.getRecordNumFiles(), is(10L));
	}
	
	@Test
	public void testCreateDirLimitedRdiNonExistentFolder() throws Exception {
		rdi = rdiSvc.createDirLimitedRecordDataInfo(PID, tempDir.getRoot().toPath(), "nonExistentFolder/");
		assertThat(rdi.getPid(), is(PID));
		assertThat(rdi.getFiles(), hasSize(0));
	}
	
	/**
	 * Counts the number of files and directories in a Record Data Info object.
	 * 
	 * @param rdi
	 * @return int[] where int[0] is number of directories, int[1] is number of files.
	 */
	private int[] countFilesAndDirs(RecordDataInfo rdi) {
		int[] count = new int[2];
		count[0] = 0;
		count[1] = 0;
		for (FileInfo fi : rdi.getFiles()) {
			if (fi.getType() == Type.DIR) {
				count[0]++;
			} else {
				count[1]++;
			}
		}
		return count;
	}
	
	private List<Path> createPayloadDirTree() throws Exception {
		Path plDir = tempDir.getRoot().toPath();
		List<Path> plFiles = new ArrayList<Path>();
		
		plFiles.addAll(createFiles(plDir, 4));
		plFiles.addAll(createFiles(plDir.resolve("folder1/"), 5));
		plFiles.addAll(createFiles(plDir.resolve("folder1/subfolder1/"), 6)); 
		plFiles.addAll(createFiles(plDir.resolve("folder1/subfolder1/subsubfolder1/"), 7)); 
		
		plFiles.addAll(createFiles(plDir.resolve("emptyFolder/"), 0));
		return plFiles;
	}
	
	private List<Path> createFiles(Path dir, int nFiles) throws IOException {
		if (!Files.isDirectory(dir)) {
			Files.createDirectory(dir);
		}
		List<Path> files = new ArrayList<Path>();
		for (int i = 0; i < nFiles; i++) {
			Path file = Files.createTempFile(dir, "RdiTest", null);
			TestUtil.createFileOfSizeInRange(file.toFile(), 256L, 4096L);
			files.add(file);
		}
		return files;
	}
	
	/**
	 * Helper method to log details of an RDI while debugging unit tests.
	 * 
	 * @param rdi
	 */
	@SuppressWarnings("unused")
	private void logRdi(RecordDataInfo rdi) {
		LOGGER.trace("{} items", rdi.getFiles().size());
		for (FileInfo fi : rdi.getFiles()) {
			if (fi.getType() == Type.FILE) {
				LOGGER.trace("\t{} ({})", fi.getRelFilepath(), fi.getFriendlySize());
			} else if (fi.getType() == Type.DIR) {
				LOGGER.trace("\t{} {}", fi.getRelFilepath(), fi.getType().toString());
			}
		}
	}
}
