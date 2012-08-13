package au.edu.anu.dcclient.stopwatch;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.stopwatch.StopWatch;

public class StopWatchTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StopWatchTest.class);

	private StopWatch time;

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
		time = new StopWatch();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetElapsedMillis()
	{
		time.start();
		quietSleep(500);
		time.end();
		LOGGER.info(MessageFormat.format("{0,number,integer}", time.getElapsedInMillis()));

		assertTrue(time.getElapsedInMillis() > 450);
		assertTrue(time.getElapsedInMillis() < 550);
	}

	@Test
	public void testGetFriendlyElapsed()
	{
		time.start();
		time.end();
		time.endTimeInMs = time.startTimeInMs + (1 * 60 * 60 * 1000) + (2 * 60 * 1000) + (3 * 1000) + 4;

		String friendlyStr = time.getFriendlyElapsed();
		LOGGER.info(MessageFormat.format("{0}", time.getFriendlyElapsed()));
		assertEquals("1 hours 2 mins 3 sec 4 ms", friendlyStr);
	}

	private void quietSleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
