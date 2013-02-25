package au.edu.anu.datacommons.storage;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.test.util.TestUtil;

public class TempFileTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileTaskTest.class);

	private TempFileTask tfs;

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
	public void testSaveTempUrl() {
		File savedFile = null;
		try {
			tfs = new TempFileTask(new URL("http://samplepdf.com/sample.pdf"));
			savedFile = tfs.call();

			assertTrue(savedFile.exists());
			assertTrue(savedFile.length() > 0);
		} catch (Exception e) {
			failOnException(e);
		} finally {
			savedFile.delete();
		}
		LOGGER.trace("Done");
	}

	@Test
	public void testSaveTempInputStream() {
		File savedFile = null;
		int size = 10240;
		ByteArrayInputStream bis = new ByteArrayInputStream(TestUtil.getRandomByteArray(size));
		try {
			tfs = new TempFileTask(bis);
			savedFile = tfs.call();

			assertTrue(savedFile.exists());
			assertEquals(size, savedFile.length());
		} catch (Exception e) {
			failOnException(e);
		} finally {
			savedFile.delete();
		}
		LOGGER.trace("Done");
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
