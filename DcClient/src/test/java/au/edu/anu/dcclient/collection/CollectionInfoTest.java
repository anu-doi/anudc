package au.edu.anu.dcclient.collection;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionInfoTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionInfoTest.class);
	
	private static File collInfoFile;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		collInfoFile = new File(CollectionInfoTest.class.getResource("collinfo.txt").toURI());
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
	public void testCollectionInfo()
	{
		CollectionInfo collInfo = null;
		try
		{
			collInfo = new CollectionInfo(collInfoFile);

			for (String key : collInfo.keySet())
			{
				LOGGER.info("Key: {}", key);
				for (String value : collInfo.get(key))
					LOGGER.info("\tValue: {}", value);
			}
			
			assertTrue(collInfo.containsKey("Key1"));
			assertTrue(collInfo.containsKey("Key2"));
			assertTrue(collInfo.containsKey("Key3"));

			assertEquals(3, collInfo.size());
			assertEquals(2, collInfo.get("Key1").size());
			assertTrue(collInfo.get("Key3").get(0).equals("value with = symbol"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetPid()
	{
		try
		{
			File tempFile = File.createTempFile("CollInfo", "JUnit");
			FileUtils.copyFile(collInfoFile, tempFile);
			CollectionInfo collInfo = new CollectionInfo(tempFile);
			assertNull(collInfo.getPid());
			collInfo.setPid("test:1");
			
			assertNotNull(collInfo.getPid());
			
			collInfo = new CollectionInfo(tempFile);
			assertNotNull(collInfo.getPid());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
}
