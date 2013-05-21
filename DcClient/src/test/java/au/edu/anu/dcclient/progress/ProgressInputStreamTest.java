package au.edu.anu.dcclient.progress;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

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

public class ProgressInputStreamTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgressInputStreamTest.class);
	private File file;
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private ProgressInputStream pis;
	private long listenerCalledCount;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		listenerCalledCount = 0;
		file = tempDir.newFile();
		writeRandomBytes(file, 5L * 1024L * 1024L);
		pis = new ProgressInputStream(new FileInputStream(file), file.length());
		pis.addPropertyChangeListener(createPropertyChangeListener());
	}

	@After
	public void tearDown() throws Exception {
		assertTrue(listenerCalledCount >= 1 && listenerCalledCount <= 100);
		pis.close();
	}

	/**
	 * Tests the read() method by reading a file one byte at a time.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRead() throws IOException {
		int numBytesRead = 0;
		while (pis.read() != -1)
			numBytesRead++;
		
		assertEquals(file.length(), numBytesRead);
		
		LOGGER.trace("Done");
	}
	
	/**
	 * Tests the read(buffer) method by reading a file one buffer at a time.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadBuffer() throws IOException {
		byte[] buffer = new byte[1024];
		long buffersRead = 0;
		for (int bytesRead = pis.read(buffer); bytesRead != -1; bytesRead = pis.read(buffer)) {
			buffersRead++;
		}
		
		assertEquals(getExpectedBufferReads(file.length(), buffer.length), buffersRead);
		
		LOGGER.trace("Done");
	}
	
	/**
	 * Tests the read(buffer, offset, length) method by read a file one buffer at the file using the offset and length.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadBufferOffLen() throws IOException {
		byte[] buffer = new byte[2048];
		int off = 512;
		int len = 1024;
		initByteArrayWith0(buffer);
		long buffersRead = 0;
		for (int bytesRead = pis.read(buffer, off, len); bytesRead != -1; bytesRead = pis.read(buffer, off, len)) {
			buffersRead++;
		}
		
		assertEquals(getExpectedBufferReads(file.length(), len), buffersRead);
		
		LOGGER.trace("Done");
	}

	/**
	 * Initialises a byte array with 0x00s.
	 * @param buffer
	 */
	private void initByteArrayWith0(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0x00;
		}
	}

	private long getExpectedBufferReads(long fileSizeInBytes, int bufferSizeInBytes) {
		long expectedBufferReads = fileSizeInBytes / bufferSizeInBytes;
		if (fileSizeInBytes % bufferSizeInBytes > 0) {
			expectedBufferReads++;
		}
		return expectedBufferReads;
	}

	private void writeRandomBytes(File targetFile, long numBytes) throws IOException {
		Random rand = new Random(); 
		BufferedOutputStream outStream = null;
		byte[] buffer = new byte[1024];
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(targetFile));
			for (int i = 0; i < numBytes / buffer.length; i++) {
				rand.nextBytes(buffer);
				outStream.write(buffer);
			}
		} finally {
			IOUtils.closeQuietly(outStream);
		}
		assertEquals(numBytes, file.length());
	}

	private PropertyChangeListener createPropertyChangeListener() {
		return new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				listenerCalledCount++;
				LOGGER.debug("Event propertyName: {}, oldValue: {}, newValue: {}.", evt.getPropertyName(),
						evt.getOldValue(), evt.getNewValue());
			}
		};
	}
}
