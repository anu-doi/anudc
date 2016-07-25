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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

import au.edu.anu.datacommons.test.util.TestStopWatch;

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
		Map<String, String> randMap = generateRandomKeyValues(nItems, 64, 128);
		
		File file = tempDir.newFile();
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		kvFile.putAll(randMap);
		assertThat(kvFile.hasUnsavedChanges(), is(true));
		long start_ns = System.nanoTime();
		kvFile.write();
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		String timeElapsed = String.valueOf((System.nanoTime() - start_ns) / 1000000.0);
		LOGGER.info("Serialising {} entries took {} millisec", nItems, timeElapsed);
		
		compareMaps(randMap, file);
	}

	@Test
	public void testProgressiveWrite() throws IOException {
		final int nItems = 100;
		Map<String, String> randMap = generateRandomKeyValues(nItems, 64, 128);
		
		File file = tempDir.newFile();
		for (Entry<String, String> entry : randMap.entrySet()) {
			kvFile = new KeyValueFileImpl(file);
			kvFile.put(entry.getKey(), entry.getValue());
			kvFile.write();
		}
		
		compareMaps(randMap, file);
	}
	
	@Test
	public void testThreadAddition() throws IOException, InterruptedException, ExecutionException {
		final int nItems = 10000;
		Map<String, String> randMap = generateRandomKeyValues(nItems, 8, 16);
		ExecutorService execSvc = Executors.newCachedThreadPool();
		List<Future<?>> futures = new ArrayList<Future<?>>();
		
		File file = tempDir.newFile();
		kvFile = new KeyValueFileImpl(file);
		for (Entry<String, String> entry : randMap.entrySet()) {
			final Entry<String, String> fSrcEntry = entry;
			futures.add(execSvc.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					kvFile.put(fSrcEntry.getKey(), fSrcEntry.getValue());
					return null;
				}
				
			}));
		}
		
		for (Future<?> f : futures) {
			f.get();
		}
		
		execSvc.shutdown();
		execSvc.awaitTermination(1, TimeUnit.MINUTES);
		compareMaps(randMap, kvFile);		
	}
	
	@Test
	public void testSplitLines() throws IOException {
		File file = tempDir.newFile();
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
		File file = tempDir.newFile();
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
		File file = tempDir.newFile();
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
	
	@Test
	public void testBlankValue() throws Exception {
		File file = tempDir.newFile();
		KeyValueFileImpl kvFile = new KeyValueFileImpl(file);
		kvFile.put("key1", "value");
		kvFile.put("key2", "");
		kvFile.put("key3", null);
		kvFile.write();
		logFileContents(file);
		
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile, hasEntry("key1", "value"));
		assertThat(kvFile, hasEntry("key2", ""));
		assertThat(kvFile, hasEntry("key3", ""));
	}
	
	@Test
	public void testUnicode() throws Exception {
		File file = tempDir.newFile();
		KeyValueFileImpl kvFile = new KeyValueFileImpl(file);
		String[] kv = {"펙퍼펱펿폌펥펟펅퍮퍜퍤펥폚폓폎펣폏펪폒폚", "圵垐垇坖垜埁埒圧圕圚㋷㊬㉹"};
		kvFile.put(kv[0], kv[1]);
		kvFile.write();
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile, hasEntry(kv[0], kv[1]));
	}
	
	@Test
	public void testInsertionRetrievalOrder() throws Exception {
		File file = tempDir.newFile();
		kvFile = new KeyValueFileImpl(file);
		Map<String, String> randomMap = generateRandomKeyValues(1000, 64, 128);
		kvFile.putAll(randomMap);

		for (int i = 0; i < 5; i++) {
			Iterator<Entry<String, String>> expecteds = kvFile.entrySet().iterator();
			Iterator<Entry<String, String>> actuals = kvFile.entrySet().iterator();
			while (actuals.hasNext()) {
				Entry<String, String> actual = actuals.next();
				Entry<String, String> expected = expecteds.next();
				assertThat(actual.getKey(), is(expected.getKey()));
				assertThat(actual.getValue(), is(expected.getValue()));
			}
			assertThat(expecteds.hasNext(), is(false));
		}
	}
	
	@Test
	public void testSerialize() throws Exception {
		File file = tempDir.newFile();
		kvFile = new KeyValueFileImpl(file);
		kvFile.putAll(generateRandomKeyValues(1000, 64, 128));
		
		assertThat(kvFile.entrySet(), hasSize(1000));
		for (int i = 0; i < 5; i++) {
			InputStream expected = kvFile.serialize();
			InputStream actual = kvFile.serialize();
			assertThat(IOUtils.contentEquals(expected, actual), is(true));
		}
		
		kvFile.putAll(generateRandomKeyValues(1000, 64, 128));
		
	}
	
	@Test
	public void testUnparsableLine() throws Exception {
		File file = tempDir.newFile();
		try (FileWriter writer = new FileWriter(file)) {
			writer.write("K1: V1\r\n");
			writer.write(":\r\n");
			writer.write("K2: V2\r\n");
		}
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile.size(), is(2));
		assertThat(kvFile.hasUnsavedChanges, is(true));
		kvFile.write();
		
		long nLines = 0L;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				nLines++;
				assertThat(line, isOneOf("K1: V1", "K2: V2"));
			}
		}
	}
	
	@Test
	public void testHasUnsavedChanges() throws Exception {
		File file = tempDir.newFile();
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		kvFile.put("Key1", "Value1");
		assertThat(kvFile.hasUnsavedChanges(), is(true));
		kvFile.put("Key2", "Value2");
		assertThat(kvFile.hasUnsavedChanges(), is(true));
		kvFile.write();
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		kvFile.put("Key3", "Value3");
		assertThat(kvFile.hasUnsavedChanges(), is(true));
		kvFile.write();
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		
		kvFile = new KeyValueFileImpl(file);
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		kvFile.put("Key3", "Value3");
		assertThat(kvFile.hasUnsavedChanges(), is(false));
		kvFile.put("Key3", "NewValue3");
		assertThat(kvFile.hasUnsavedChanges(), is(true));
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

	private Map<String, String> generateRandomKeyValues(int num, int keySizeInBytes, int valueSizeInBytes) {
		Map<String, String> map = new HashMap<String, String>(num);
		Random rand = new Random();
		
		byte[] keyBuffer = new byte[keySizeInBytes];
		byte[] valBuffer = new byte[valueSizeInBytes];
		for (int i = 0; i < num; i++) {
			rand.nextBytes(keyBuffer);
			String key = Base64.encodeBase64String(keyBuffer);
			rand.nextBytes(valBuffer);
			String value = Base64.encodeBase64String(valBuffer);
			if (!map.containsKey(key)) {
				map.put(key, value);
			} else {
				i--;
			}
		}
		return map;
	}

	private void compareMaps(Map<String, String> randMap, Map<String, String> kvMap) throws IOException {
		assertEquals(randMap.size(), kvMap.size());
		assertThat(randMap.entrySet(), everyItem(isIn(kvMap.entrySet())));
		assertThat(kvMap.entrySet(), everyItem(isIn(randMap.entrySet())));
	}
	
	private void compareMaps(Map<String, String> randMap, File file) throws IOException {
		kvFile = new KeyValueFileImpl(file);
		compareMaps(randMap, kvFile);
	}

	private class KeyValueFileImpl extends AbstractKeyValueFile {
		private static final long serialVersionUID = 1L;
		
		private File file;
		
		public KeyValueFileImpl(File file) throws IOException {
			super(new FileInputStream(file));
			this.file = file;
		}

		@Override
		public String getFilepath() {
			return null;
		}
		
		public synchronized void write() throws IOException {
			Files.copy(this.serialize(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			this.setHasUnsavedChanges(false);
		}
	}
	
	private class KeyValueFileImplSpaceSeparator extends AbstractKeyValueFile {
		private static final long serialVersionUID = 1L;
		private File file;
		
		public KeyValueFileImplSpaceSeparator(File file) throws IOException {
			super(new FileInputStream(file));
			this.file = file;
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

		@Override
		public String getFilepath() {
			return null;
		}
		
		public synchronized void write() throws IOException {
			Files.copy(this.serialize(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			this.setHasUnsavedChanges(false);
		}

	}
}
