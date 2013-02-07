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

package gov.loc.repository.bagit.transfer;

import gov.loc.repository.bagit.FetchTxt;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

public class ChainSorterTests
{
	private ChainSorter<FetchTxt.FilenameSizeUrl> unit;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSortsByFirstThenSecond()
	{
		this.unit = new ChainSorter<FetchTxt.FilenameSizeUrl>(
				new FetchFilenameSorter(),
				new FetchSizeSorter());
		
		FetchTxt.FilenameSizeUrl[] items = new FetchTxt.FilenameSizeUrl[] {
				new FetchTxt.FilenameSizeUrl("/ddd", 300L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/ccc", 100L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/ddd", 200L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/bbb", 800L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/ccc", 200L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/aaa", 500L, "http://www.example.org/foo"),
				new FetchTxt.FilenameSizeUrl("/ddd", 100L, "http://www.example.org/foo")
		};
		
		Arrays.sort(items, this.unit);
		
		Assert.assertEquals(7, items.length);
		assertEquals("/aaa", 500L, items[0]);
		assertEquals("/bbb", 800L, items[1]);
		assertEquals("/ccc", 100L, items[2]);
		assertEquals("/ccc", 200L, items[3]);
		assertEquals("/ddd", 100L, items[4]);
		assertEquals("/ddd", 200L, items[5]);
		assertEquals("/ddd", 300L, items[6]);
	}
	
	public static void assertEquals(String path, Long length, FetchTxt.FilenameSizeUrl actual)
	{
		Assert.assertEquals(path, actual.getFilename());
		Assert.assertEquals(length, actual.getSize());
	}
}
