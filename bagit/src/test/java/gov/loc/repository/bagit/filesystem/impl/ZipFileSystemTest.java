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

package gov.loc.repository.bagit.filesystem.impl;

import gov.loc.repository.bagit.filesystem.FileSystem;
import gov.loc.repository.bagit.utilities.ResourceHelper;

import java.io.File;

import org.junit.Before;

public class ZipFileSystemTest extends AbstractFileSystemTest {
		
	File rootFile;

	
	@Before
	public void setup() throws Exception {
		this.rootFile =  ResourceHelper.getFile("file_systems/test.zip");
		assert (this.rootFile.exists());
		assert (this.rootFile.isFile());
	}
	
	@Override
	FileSystem getFileSystem() {
		return new ZipFileSystem(this.rootFile);
	}
		
}
