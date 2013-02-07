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

package au.edu.anu.datacommons.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileTest.class);

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

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
	public void testGetPropertyString()
	{
		// Create properties.
		Properties prop = new Properties();
		prop.setProperty("Key1", "Value1");
		prop.setProperty("Key2", "Value2");

		// Save to file.
		File propFile = new File(tempDir.getRoot(), "test.properties");
		Properties propsF = null;
		try
		{
			writePropsToFile(prop, propFile);

			assertTrue(propFile.exists());
			assertTrue(propFile.length() > 0);

			propsF = new PropertiesFile(propFile);
			assertEquals("Value1", propsF.getProperty("Key1"));

			prop = new Properties();
			prop.setProperty("Key1", "New Value1");

			// If this test case fails here, increase sleep value.
			Thread.sleep(100);
			writePropsToFile(prop, propFile);
			
			assertEquals("New Value1", propsF.getProperty("Key1"));
			assertNull(propsF.getProperty("Key2"));
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (InterruptedException e)
		{
			failOnException(e);
		}
	}

	private void writePropsToFile(Properties prop, File file) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file);
			prop.store(fos, null);
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
