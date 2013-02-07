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

import java.io.File;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileNode;
import gov.loc.repository.bagit.filesystem.FileSystem;

public class FileFileSystem implements FileSystem {

	private File file;
	private DirNode dirNode;
	
	public FileFileSystem(File file) {
		assert file != null;		
		if (! file.isDirectory()) throw new RuntimeException("Not a directory");		
		this.file = file;
		
		this.dirNode = new FileDirNode(file, this);
	}
	
	@Override
	public void close() {
		//Do nothing
	}
	
	@Override
	public void closeQuietly() {
		//Do nothing
	}

	@Override
	public DirNode getRoot() {
		return this.dirNode;
	}

	@Override
	public File getFile() {
		return this.file;
	}
	
	@Override
	public FileNode resolve(String filepath) {
		File resolvedFile = new File(this.file, filepath);
		return new FileFileNode(resolvedFile, this);
	}

}
