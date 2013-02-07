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

public class FilenameHelperTest {

	@Test
	public void testRemoveBasePath() throws Exception {
		assertEquals("bar.html", FilenameHelper.removeBasePath("/foo", "/foo/bar.html"));
		assertEquals("bar.html", FilenameHelper.removeBasePath("c:/foo", "c:\\foo\\bar.html"));
		assertEquals("foo/bar.html", FilenameHelper.removeBasePath("c:\\", "c:\\foo\\bar.html"));
	}

	@Test(expected=Exception.class)
	public void testRemoveBadBasePath() throws Exception {
		FilenameHelper.removeBasePath("/xfoo", "/foo/bar.html");
	}

	@Test
	public void testNormalizePathSeparators() {
		assertEquals("data/foo.txt", FilenameHelper.normalizePathSeparators("data/foo.txt"));
		assertEquals("data/foo.txt", FilenameHelper.normalizePathSeparators("data\\foo.txt"));
	}
	
	@Test
	public void testNormalizePath() {
		assertEquals("data/foo.txt", FilenameHelper.normalizePath("data/foo.txt"));
		assertEquals("data\\foo.txt", FilenameHelper.normalizePath("data\\foo.txt"));
		assertEquals("/data/foo.txt", FilenameHelper.normalizePath("/data/foo.txt"));
		assertEquals("\\data\\foo.txt", FilenameHelper.normalizePath("\\data\\foo.txt"));
		assertEquals("data/foo.txt", FilenameHelper.normalizePath("./data/foo.txt"));
		assertEquals("data\\foo.txt", FilenameHelper.normalizePath(".\\data\\foo.txt"));
		assertEquals("data/foo.txt", FilenameHelper.normalizePath("data/./foo.txt"));
		assertEquals("data\\foo.txt", FilenameHelper.normalizePath("data\\.\\foo.txt"));
		assertEquals("foo.txt", FilenameHelper.normalizePath("data/../foo.txt"));
		assertEquals("foo.txt", FilenameHelper.normalizePath("data\\..\\foo.txt"));
		assertEquals("data/foo.txt", FilenameHelper.normalizePath("data/dir1/../foo.txt"));
		assertEquals("data\\foo.txt", FilenameHelper.normalizePath("data\\dir1\\..\\foo.txt"));

	}
	
}
