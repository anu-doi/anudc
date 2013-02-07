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

package au.edu.anu.dcbag.fido;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.fido.PythonExecutor;

public class PythonExecutorTest
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
	public void testGetOutputAsString() throws IOException
	{
		String compString = "usage: fido.py [-h] [-v] [-q] [-recurse] [-zip] [-nocontainer] [-input INPUT]";
		PythonExecutor pExec = new PythonExecutor(new File("C:\\Rahul\\Programs\\Fido\\fido.py"));
		pExec.execute();
		String outString = pExec.getOutputAsString();
		LOGGER.info(outString);
		assertTrue("Output String doesn't match.", outString.substring(0, outString.indexOf(System.getProperty("line.separator"))).equals(compString));
	}

}
