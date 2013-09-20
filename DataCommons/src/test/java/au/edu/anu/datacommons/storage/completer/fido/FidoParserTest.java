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

package au.edu.anu.datacommons.storage.completer.fido;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Random;

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

import au.edu.anu.datacommons.storage.info.PronomFormat;
import au.edu.anu.datacommons.storage.info.PronomFormat.MatchStatus;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class FidoParserTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FidoParserTest.class);
	
	private FidoParser fidoParser;
	private File fileToId;
	
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
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFidoPdfFile() {
		try {
			fileToId = new File(this.getClass().getResource("BagIt Specification.pdf").toURI());
			fidoParser = new FidoParser(fileToId);
			PronomFormat fileFormat = fidoParser.getFileFormat();
			matchValues(fileFormat);
		} catch (IOException e) {
			failOnException(e);
		} catch (URISyntaxException e) {
			failOnException(e);
		}
	}

	@Test
	public void testFidoContainerDocumentFile() {
		try {
			fileToId = new File(this.getClass().getResource("Container.odt").toURI());
			fidoParser = new FidoParser(fileToId);
			assertThat(fidoParser.getFidoStr(), containsString("fmt/291"));
			assertThat(fidoParser.getFidoStr(), containsString("OpenDocument Text"));
			assertThat(fidoParser.getFidoStr(), containsString("ODF 1.2 text"));
			assertThat(fidoParser.getFidoStr(), containsString("application/vnd.oasis.opendocument.text"));
		} catch (IOException e) {
			failOnException(e);
		} catch (URISyntaxException e) {
			failOnException(e);
		}
	}

	
	@Test
	public void testFidoGarbageFile() throws IOException {
		fileToId = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(fileToId, 2L, 6L, FileUtils.ONE_MB);
		fidoParser = new FidoParser(fileToId);
		PronomFormat fileFormat = fidoParser.getFileFormat();
		assertEquals(fileToId.getAbsolutePath(), fileFormat.getFileName());
		assertEquals(fileToId.length(), fileFormat.getFileSize());
		assertEquals(MatchStatus.KO, fileFormat.getMatchStatus());
	}
	
	@Test
	public void testFidoGarbageStream() throws IOException {
		fileToId = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(fileToId, 2L, 6L, FileUtils.ONE_MB);
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(fileToId);
			fidoParser = new FidoParser(fileStream, "data/abc xyz.log");
			assertNotNull(fidoParser.getFidoStr());
			assertTrue(fidoParser.getFidoStr().length() > 0);
			assertEquals(PronomFormat.MatchStatus.OK, fidoParser.getFileFormat().getMatchStatus());
			assertEquals("x-fmt/62", fidoParser.getFileFormat().getPuid());
			PronomFormat fileFormat = fidoParser.getFileFormat();
			assertEquals(fileToId.length(), fileFormat.getFileSize());
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
	}
	
	@Test
	public void testLogFileFromStream() throws IOException {
		String filename = "test-log.log";
		InputStream fileStream = this.getClass().getResourceAsStream(filename);
		try {
			fidoParser = new FidoParser(fileStream, filename);
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		assertNotNull(fidoParser.getFidoStr());
		assertTrue(fidoParser.getFidoStr().length() > 0);
		PronomFormat fileFormat = fidoParser.getFileFormat();
		assertEquals(PronomFormat.MatchStatus.OK, fileFormat.getMatchStatus());
		assertEquals(filename, fileFormat.getFileName());
		assertEquals("x-fmt/62", fileFormat.getPuid());
		LOGGER.trace(fileFormat.getFormatName());
	}

	private void matchValues(PronomFormat format) {
		assertEquals(MatchStatus.OK, format.getMatchStatus());
		assertEquals("fmt/20", format.getPuid());
		assertEquals("application/pdf", format.getMimeType());
		assertEquals(63647L, format.getFileSize());
		assertEquals("PDF 1.6", format.getSigName());
		assertEquals("Acrobat PDF 1.6 - Portable Document Format", format.getFormatName());
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
