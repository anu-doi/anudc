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
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.bagit.filesystem.FileSystem;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.utilities.FilenameHelper;

public abstract class AbstractFileNode implements FileSystemNode {

	protected File file;
	protected FileFileSystem fileSystem;
	private String filepath;
	private String name;
	
	protected AbstractFileNode(File file, FileFileSystem fileSystem) {
		this.file = file;
		this.fileSystem = fileSystem;
		
		if(fileSystem.getRoot() == null) {
			this.filepath = "";
			this.name = null;
		} else {		
			//Using absolute instead of canonical so symlinks name are not dereferenced
			this.filepath = FilenameHelper.removeBasePath(fileSystem.getFile().getAbsolutePath(), file.getAbsolutePath());
			this.name = file.getName();
		}
	}
		
	public File getFile() {
		return this.file;
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
	public boolean isSymlink() {
		try {
			return FileUtils.isSymlink(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
