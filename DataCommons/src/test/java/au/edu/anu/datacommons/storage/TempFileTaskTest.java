package au.edu.anu.datacommons.storage;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
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

	private static URL fileUrl;

	private TempFileTask tfs;
	private File savedFile;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileUrl = new URL("http://hr.anu.edu.au/__documents/info-for/new-staff/induction-guide.pdf");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(savedFile);
	}

	@Test
	public void testSaveTempUrl() {
		try {
			tfs = new TempFileTask(fileUrl);
			tfs.setExpectedMessageDigest(Manifest.Algorithm.MD5, "19857a231313ad5f6fdd8923ae415d2c");
			savedFile = tfs.call();

			assertTrue(savedFile.exists());
			assertTrue(savedFile.length() > 0);
		} catch (Exception e) {
			failOnException(e);
		}
		LOGGER.trace("Done");
	}

	@Test
	public void testSaveTempInputStream() {
		int size = 10 * 1024 * 1024;
		byte[] randomByteArray = TestUtil.getRandomByteArray(size);
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(Algorithm.MD5.javaSecurityAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			failOnException(e);
		}
		
		String expectedMd = new String(Hex.encodeHex(md.digest(randomByteArray))).toLowerCase();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(randomByteArray);
		try {
			tfs = new TempFileTask(bis);
			tfs.setExpectedMessageDigest(Algorithm.MD5, expectedMd);
			savedFile = tfs.call();

			assertTrue(savedFile.exists());
			assertEquals(size, savedFile.length());
		} catch (Exception e) {
			failOnException(e);
		}
		LOGGER.trace("Done");
	}
	
	@Test(expected = IOException.class)
	public void testSaveTempUrlWithException() throws Exception {
		tfs = new TempFileTask(fileUrl);
		tfs.setExpectedMessageDigest(Manifest.Algorithm.MD5, "b606e3998f6f3da1cb4646940db0f297");
		savedFile = tfs.call();
		fail("IOException expected");
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
