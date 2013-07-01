package au.edu.anu.datacommons.storage.completer.virusscan;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.ScanResult;
import au.edu.anu.datacommons.test.util.TestUtil;

public class ClamScanTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScanTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private ClamScan clamScan;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		clamScan = new ClamScan("dc7-dev2.anu.edu.au", 3310, 120000);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPing() {
		assertTrue(clamScan.ping());
	}
	
	@Test
	public void testScan() throws IOException {
		File file = tempDir.newFile();
		TestUtil.fillRandomData(file, 10);
		ScanResult result = clamScan.scan(new FileInputStream(file));
		LOGGER.trace(result.getResult());
	}
}
