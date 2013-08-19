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

package au.edu.anu.datacommons.storage.tagfiles;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
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

/**
 * @author Rahul Khanna
 *
 */
public class AbstractKeyValueFileTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyValueFileTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private KeyValueFileImpl kvFile;
	
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
		LOGGER.info("Using temp dir: {}", tempDir.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHugeMap() throws IOException {
		final int nItems = 10000;
		Map<String, String> randMap = generateRandomKeyValues(nItems);
		
		File file = new File(tempDir.getRoot(), "KeyValFile.txt");
		assertFalse(file.isFile());
		kvFile = new KeyValueFileImpl(file);
		assertFalse(file.isFile());
		kvFile.putAll(randMap);
		long start_ns = System.nanoTime();
		kvFile.write();
		LOGGER.info("Writing {} entries took {} millisec", nItems, String.valueOf((System.nanoTime() - start_ns) / 1000000.0));
		
		kvFile = new KeyValueFileImpl(file);
		assertEquals(randMap.size(), kvFile.size());
		assertThat(randMap.entrySet(), everyItem(isIn(kvFile.entrySet())));
	}
	
	@Test
	public void testSplitLines() throws IOException {
		File file = tempDir.newFile("KeyValFile.txt");
		assertTrue(file.isFile());
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(": ");
			writer.newLine();
			writer.write("data/BagIt Specification.pdf: OK,359,fmt/20,\"Acrobat PDF 1.6 - Portable");
			writer.newLine();
			writer.write("   Document Format\",\"PDF 1.6\",63647,\"STDIN\",\"application/pdf\",\"signature\"");
			writer.newLine();
			writer.write("data/A really really long filename blah blah");
			writer.newLine();
			writer.write("   blah blah blah this should be in a new line: Value blah blah blah");
			writer.newLine();
			writer.write("   next line of value.");
			writer.newLine();
		} finally {
			IOUtils.closeQuietly(writer);
		}

		kvFile = new KeyValueFileImpl(file);
		assertEquals(2, kvFile.size());
		assertThat(kvFile, hasEntry("data/BagIt Specification.pdf", "OK,359,fmt/20,\"Acrobat PDF 1.6 - Portable "
				+ "Document Format\",\"PDF 1.6\",63647,\"STDIN\",\"application/pdf\",\"signature\""));
		assertThat(
				kvFile,
				hasEntry("data/A really really long filename blah blah blah blah blah this should be in a new line",
						"Value blah blah blah next line of value."));
		for (Entry<String, String> entry : kvFile.entrySet()) {
			LOGGER.trace("{}: {}", entry.getKey(), entry.getValue());
		}
	}
	
	@Test
	public void testEscapedCharInKeys() throws IOException {
		File file = new File(tempDir.getRoot(), "KeyValFile.txt");
		KeyValueFileImpl keyVal = new KeyValueFileImpl(file);
		String key = "abc:x:y:z";
		String value = "value:value";
				
		keyVal.put(key, value);
		keyVal.write();
		
		logFileContents(file);
		
		keyVal = new KeyValueFileImpl(file);
		assertThat(keyVal.entrySet(), hasSize(1));
		assertThat(keyVal, hasEntry(key, value));
	}
	
	@Test
	public void testDiffSeparator() throws IOException {
		File file = new File(tempDir.getRoot(), "KeyValFile.txt");
		KeyValueFileImplSpaceSeparator keyVal = new KeyValueFileImplSpaceSeparator(file);
		String key = "a=bc=";
		String value = "xyz";
		keyVal.put(key, value);
		keyVal.write();
		logFileContents(file);
		
		keyVal = new KeyValueFileImplSpaceSeparator(file);
		assertThat(keyVal.entrySet(), hasSize(1));
		assertThat(keyVal, hasEntry(key, value));
	}

	private void logFileContents(File file) throws IOException {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				sb.append(line);
				sb.append("\r\n");
			}
		} finally {
			IOUtils.closeQuietly(reader);
		}
		LOGGER.debug("File {}:\r\n{}", file.getName(), sb.toString());
	}

	private Map<String, String> generateRandomKeyValues(int num) {
		Map<String, String> map = new HashMap<String, String>(num);
		Random rand = new Random();
		
		byte[] keyBuffer = new byte[8];
		byte[] valBuffer = new byte[16];
		for (int i = 0; i < num; i++) {
			rand.nextBytes(keyBuffer);
			String key = Base64.encodeBase64String(keyBuffer);
			rand.nextBytes(valBuffer);
			String value = Base64.encodeBase64String(valBuffer);
			map.put(key, value);
		}
		return map;
	}

	private class KeyValueFileImpl extends AbstractKeyValueFile {
		private static final long serialVersionUID = 1L;
		
		public KeyValueFileImpl(File file) throws IOException {
			super(file);
		}
	}
	
	private class KeyValueFileImplSpaceSeparator extends AbstractKeyValueFile {
		private static final long serialVersionUID = 1L;

		public KeyValueFileImplSpaceSeparator(File file) throws IOException {
			super(file);
		}

		@Override
		protected String getSeparator() {
			return "  ";
		}
		
		@Override
		protected String serializeEntry(Entry<String, String> entry) {
			return entry.getValue() + getSeparator() + entry.getKey(); 
		}
		
		@Override
		protected String[] unserializeKeyValue(String line) {
			String[] parts = line.split(getSeparator(), 2);
			String temp = parts[0];
			parts[0] = parts[1];
			parts[1] = temp;
			return parts;
		}
	}
}
