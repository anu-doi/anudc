package au.edu.anu.dcbag.clamscan;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClamScanTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamScanTest.class);
	
	private ClamScan clamScan;
	
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
	public void testPing() {
		clamScan = new ClamScan("dc7-dev2.anu.edu.au", 3310);
		assertTrue(clamScan.ping());
	}
}
