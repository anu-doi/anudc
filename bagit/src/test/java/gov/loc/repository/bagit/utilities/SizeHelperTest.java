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

package gov.loc.repository.bagit.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class SizeHelperTest {

	@Test
	public void testGetSize() {
		assertEquals("0.01 KB", SizeHelper.getSize(10L));
		assertEquals("0.1 KB", SizeHelper.getSize(100L));
		assertEquals("1 KB", SizeHelper.getSize(1024L));
		assertEquals("1.5 KB",SizeHelper.getSize(1500L));
		assertEquals("1 MB", SizeHelper.getSize(1048576L));
		assertEquals("1 MB", SizeHelper.getSize(1048577L));
		assertEquals("1.1 MB", SizeHelper.getSize(1148576L));
		assertEquals("1 GB", SizeHelper.getSize(1073741824L));
		assertEquals("1 TB", SizeHelper.getSize(1099511627776L));
		
	}

}
