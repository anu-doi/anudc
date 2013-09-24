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

package au.edu.anu.datacommons.storage.completer.preserve;

import static java.text.MessageFormat.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.test.util.TestUtil;
import au.gov.naa.digipres.xena.kernel.XenaException;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;

/**
 * @author Rahul Khanna
 * 
 */
public class PreservationFormatConverterTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreservationFormatConverterTest.class);

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private PreservationFormatConverter pfc;

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
		LOGGER.trace("Using temp dir: {}", tempDir.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// ------------- Archives -------------
	@Test
	public void testZip() throws Exception {
		convertFile(getResourceFile("Sample zip.zip"), null);
	}

	// ------------- Audio Files -------------
	@Test
	public void testAiffNonCompression() throws Exception {
		convertFile(getResourceFile("aiff-16.snd"), "flac");
	}

	@Test
	public void testAiffApple() throws Exception {
		convertFile(getResourceFile("Tone.aiff"), "flac");
	}

	@Test
	public void testMp3() throws Exception {
		convertFile(getResourceFile("Tone.mp3"), "flac");
	}

	@Test
	public void testWave() throws Exception {
		convertFile(getResourceFile("Tone.wav"), "flac");
	}

	@Test
	public void testFlac() throws Exception {
		convertFile(getResourceFile("Tone.flac"), null);
	}

	@Test
	public void testOgg() throws Exception {
		convertFile(getResourceFile("Tone.ogg"), "flac");
	}

	// ------------- Emails -------------
	@Test
	public void testEml() throws Exception {
		convertFile(getResourceFile("Sample eml.eml"), null);
	}

	// ------------- Images - Raster -------------
	@Test
	public void testBmp() throws Exception {
		convertFile(getResourceFile("Sample bmp.bmp"), "png");
	}

	@Test
	public void testJpg() throws Exception {
		convertFile(getResourceFile("Sample jpg.jpg"), null);
	}

	@Test
	public void testGif() throws Exception {
		convertFile(getResourceFile("Sample gif.gif"), null);
	}

	@Test
	public void testTif() throws Exception {
		convertFile(getResourceFile("Sample tif.tif"), null);
	}

	@Test
	public void testPng() throws Exception {
		convertFile(getResourceFile("Sample png.png"), null);
	}

	// ------------- Office Documents -------------
	@Test
	public void testDocx() throws Exception {
		convertFile(getResourceFile("Word 2010 sample.docx"), "odt");
	}

	@Test
	public void testDoc() throws Exception {
		convertFile(getResourceFile("Word 2003 sample.doc"), "odt");
	}

	@Test
	public void testRtf() throws Exception {
		convertFile(getResourceFile("Rtf Sample.rtf"), "odt");
	}

	@Test
	public void testXlsx() throws Exception {
		convertFile(getResourceFile("Excel 2010 Sample.xlsx"), "ods");
	}

	@Test
	public void testXls() throws Exception {
		convertFile(getResourceFile("Excel 2003 Sample.xls"), "ods");
	}

	@Test
	public void testPdf() throws Exception {
		convertFile(getResourceFile("Pdf Sample.pdf"), null);
	}

	// ------------- Plain Text -------------
	@Test
	public void testCsv() throws Exception {
		convertFile(getResourceFile("Csv Sample.csv"), null);
	}

	// ------------- Video -------------
	@Ignore("Video files are not converted")
	@Test
	public void testAvi() throws Exception {
		convertFile(getResourceFile("Sample avi.avi"), null);
	}

	// ------------- Website -------------
	@Test
	public void testAsp() throws Exception {
		convertFile(getResourceFile("Sample asp.asp"), null);
	}

	// ------------- Website archive -------------
	@Test
	public void testMht() throws Exception {
		convertFile(getResourceFile("Sample mht.mht"), null);
	}

	// ------------- Random file -------------
	@Test
	public void testRandomBinaryFiles() throws Exception {
		String[] filenames = { "a.txt", "a.wav", "a.pdf", "a.docx", "a.fits", "a.ods" };
		File randomFile = tempDir.newFile();
		TestUtil.createFileOfSizeInRange(randomFile, 10L, 50L, FileUtils.ONE_KB);

		for (int i = 0; i < filenames.length; i++) {
			File renamedFile = new File(randomFile.getParentFile(), filenames[i]);
			if (!randomFile.renameTo(renamedFile)) {
				fail(format("Unable to rename {0}", randomFile.getAbsolutePath()));
			}
			randomFile = renamedFile;
			convertFile(randomFile, null);
		}
	}

	private void convertFile(File file, String expectedExtension) throws XenaException, IOException {
		LOGGER.trace("Converting file {} to preservation format...", file.getAbsolutePath());
		pfc = new PreservationFormatConverter(file, tempDir.getRoot());
		NormaliserResults results = pfc.convert();

		if (expectedExtension != null) {
			assertNotNull(results);
			File outputFile = new File(results.getDestinationDirString(), results.getOutputFileName());
			assertTrue(outputFile.isFile());
			assertTrue(outputFile.length() > 0L);
			assertThat(outputFile.getName(), endsWith(expectedExtension));
			LOGGER.trace("Converted File: {}", results.getOutputFileName());
		} else {
			assertNull(results);
		}
	}

	private File getResourceFile(String filename) {
		File file = null;
		try {
			URI uri = this.getClass().getResource(filename).toURI();
			file = new File(uri);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		return file;
	}
}
