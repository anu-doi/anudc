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

package au.edu.anu.dcclient.collection;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Set;

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

			for (String key : collInfo.getCreateCollMap().keySet())
			{
				LOGGER.info("Key: {}", key);
				for (String value : collInfo.getCreateCollMap().get(key))
					LOGGER.info("\tValue: {}", value);
			}
			
			assertTrue(collInfo.getCreateCollMap().containsKey("Key1"));
			assertTrue(collInfo.getCreateCollMap().containsKey("Key2"));
			assertTrue(collInfo.getCreateCollMap().containsKey("Key3"));

			assertEquals(3, collInfo.getCreateCollMap().size());
			assertEquals(2, collInfo.getCreateCollMap().get("Key1").size());
			assertTrue(collInfo.getCreateCollMap().get("Key3").get(0).equals("value with = symbol"));
			
			// Check relationship set.
			Set<String[]> relSet = collInfo.getRelationSet();
			assertEquals(2, relSet.size());
			for (String[] rel : relSet)
			{
				LOGGER.info("Relationship: {}-{}", rel[0], rel[1]);
				if (rel[0].equals("isOutputOf"))
					assertEquals("test:1", rel[1]);
				if (rel[0].equals("isPartOf"))
					assertEquals("test:2", rel[1]);
			}
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
