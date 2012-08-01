package au.edu.anu.dcclient.collection;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionInfoTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionInfoTest.class);
	
	private CollectionInfo collInfo;
	
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
		collInfo = new CollectionInfo(new File(CollectionInfoTest.class.getResource("collinfo.txt").toURI()));
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testCollectionInfo()
	{
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

}
