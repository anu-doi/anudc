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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import gov.loc.repository.bagit.filesystem.FileNode;

public class FileFileNode extends AbstractFileNode implements FileNode {

	protected FileFileNode(File file, FileFileSystem fileSystem) {
		super(file, fileSystem);
	}

	@Override
	public long getSize() {
		return this.file.length();
	}

	@Override
	public InputStream newInputStream() {		
		try {
			return new BufferedInputStream(new FileInputStream(this.file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean exists() {
		//Exists and is a file
		return this.file.isFile();
	}

}
