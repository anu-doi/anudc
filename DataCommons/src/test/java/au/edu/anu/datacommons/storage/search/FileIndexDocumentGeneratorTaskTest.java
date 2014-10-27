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

package au.edu.anu.datacommons.storage.search;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.TikaCoreProperties;
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

import au.edu.anu.datacommons.storage.provider.StorageProvider;

/**
 * @author Rahul Khanna
 *
 */
public class FileIndexDocumentGeneratorTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileIndexDocumentGeneratorTaskTest.class);
	
	private static final String PID = "test:1";
	private static final String REL_PATH = "file.txt";
	private static final String BAGIT_PDF = "au/edu/anu/datacommons/storage/completer/fido/BagIt Specification.pdf";
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Mock
	private StorageProvider sp;
	
	private FileIndexDocumentGeneratorTask docGenTask;
	private File plFile;
	
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
		MockitoAnnotations.initMocks(this);
		LOGGER.trace("Using temp dir: {}", tempDir.getRoot().getAbsolutePath());
		File plDir = tempDir.newFolder("data");
		plFile = new File(plDir, REL_PATH);
		FileUtils.writeStringToFile(plFile, "This is a test String", "UTF-8");
		docGenTask = new FileIndexDocumentGeneratorTask(PID, REL_PATH, sp, "test:1/file.txt");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExtractTextFromPdf() throws Exception {
		InputStream fileStream = this.getClass().getClassLoader()
				.getResourceAsStream(BAGIT_PDF);
		docGenTask.extractMetadataAndContents(fileStream);
		String contents = docGenTask.contents.toString();
		assertThat(contents, not(isEmptyOrNullString()));
		LOGGER.trace("Contents: {}", contents);
	}
	
	@Test
	public void testExtractTextFromText() throws Exception {
		try (InputStream fileStream = new FileInputStream(plFile)) {
			docGenTask.extractMetadataAndContents(fileStream);
			String contents = docGenTask.contents.toString();
			assertThat(contents, not(isEmptyOrNullString()));
			LOGGER.trace("Contents: {}", contents);
		}
	}

	@Test
	public void testDocGenWhenFileNotExist() throws Exception {
		plFile.delete();
		assertThat(plFile.isFile(), is(false));
		Mockito.when(sp.fileExists(PID, REL_PATH)).thenReturn(false);
		StorageSolrDoc doc = docGenTask.call();
		assertThat(doc, is(not(nullValue())));
		assertThat(doc.getId(), is(not(nullValue())));
		assertThat(doc.getName(), is(nullValue()));
		assertThat(doc.getExt(), is(nullValue()));
		assertThat(doc.getSize(), is(0L));
		assertThat(doc.getLast_modified(), is(nullValue()));
		assertThat(doc.getMime_type(), is(nullValue()));
		assertThat(doc.getTitle(), is(nullValue()));
		assertThat(doc.getAuthors(), is(nullValue()));
		assertThat(doc.getMetadata().entrySet(), is(empty()));
		assertThat(doc.getContent(), is(nullValue()));
		
	}
}
