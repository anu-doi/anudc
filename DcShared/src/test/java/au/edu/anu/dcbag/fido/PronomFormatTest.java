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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.anu.datacommons.storage.info.PronomFormat;
import au.edu.anu.datacommons.storage.info.PronomFormat.MatchStatus;

public class PronomFormatTest
{
	private PronomFormat fmt;
	
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
	public void testFidoFileFormatOK()
	{
		fmt = new PronomFormat(
				"OK,251,fmt/20,\"Acrobat PDF 1.6 - Portable Document Format\",\"PDF 1.6\",63647,\"C:\\Rahul\\eBooks\\B,A.pdf\",\"application/pdf\",\"signature\"");
		assertEquals(fmt.getMatchStatus(), MatchStatus.valueOf("OK"));
		// TODO Add assertion of other values.
	}
	
	@Test
	public void testFidoFileFormatKO()
	{
		fmt = new PronomFormat("KO,47,,,,9540095,\"C:\\Rahul\\eBooks\\PHP Manual.chm\",,\"fail\"");
		assertEquals(fmt.getMatchStatus(), MatchStatus.valueOf("KO"));
		// TODO Add assertions.
	}

}
