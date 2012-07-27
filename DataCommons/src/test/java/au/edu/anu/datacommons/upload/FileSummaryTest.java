package au.edu.anu.datacommons.upload;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.FileSummary;

public class FileSummaryTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetFriendlySize()
	{
		// 12 MB
		FileSummary mbSummary = new FileSummary("abc.txt", 1024L * 1024L * 12L, "some format", "fmt/20", "e75658d2e33d15dbed9940118712b902", "downloadUri");
		LOGGER.info("{} bytes will be displayed as: {}", mbSummary.getSizeInBytes(), mbSummary.getFriendlySize());
		assertTrue(mbSummary.getFriendlySize().equals("12 MB"));
		
		// 1024 GB
		FileSummary gbSummary = new FileSummary("abc.txt", 1024L * 1024L * 1024L * 1001L, "some format", "fmt/20", "e75658d2e33d15dbed9940118712b902", "downloadUri");
		LOGGER.info("{} bytes will be displayed as: {}", gbSummary.getSizeInBytes(), gbSummary.getFriendlySize());
		assertTrue(mbSummary.getFriendlySize().equals("1,001 GB"));
	}

}
