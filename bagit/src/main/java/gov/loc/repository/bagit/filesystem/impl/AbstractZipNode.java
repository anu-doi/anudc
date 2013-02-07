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
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.utilities.FilenameHelper;

public class AbstractZipNode implements FileSystemNode {

	protected String filepath;
	protected String name = null;
	protected ZipFileSystem fileSystem;
	
	protected AbstractZipNode(String filepath, ZipFileSystem fileSystem) {
		this.filepath = filepath;
		//Root
		if (! filepath.equals("")) {
			this.name = FilenameHelper.getName(filepath);
		}
		this.fileSystem = fileSystem;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getFilepath() {
		return this.filepath;
	}

	@Override
	public FileSystem getFileSystem() {
		return this.fileSystem;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (! (obj instanceof AbstractZipNode)) return false;
		final AbstractZipNode that = (AbstractZipNode)obj;
		return this.filepath.equals(that.getFilepath());
		
	}
	
	public int hashCode() {
		return 23 + this.filepath.hashCode();
	}
	
	@Override
	public boolean isSymlink() {
		return false;
	}


}
