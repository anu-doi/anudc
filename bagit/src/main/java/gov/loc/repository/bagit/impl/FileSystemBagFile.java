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

package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.DeclareCloseable;
import gov.loc.repository.bagit.filesystem.FileNode;

import java.io.Closeable;
import java.io.InputStream;

public class FileSystemBagFile implements BagFile, DeclareCloseable {
	private String filepath;
	private FileNode fileNode;
	
	public FileSystemBagFile(String filepath, FileNode fileNode) {
		this.filepath = filepath;
		this.fileNode = fileNode;
	}
	
	public FileNode getFileNode() {
		return this.fileNode;
	}
		
	public InputStream newInputStream() {
		return this.fileNode.newInputStream();
	}
	
	public String getFilepath() {
		return this.filepath;
	}
	
	public boolean exists() {
		return this.fileNode.exists();
	}
	
	public long getSize() {
		return this.fileNode.getSize();
	}
	
	@Override
	public Closeable declareCloseable() {
		return this.fileNode.getFileSystem();
	}
}
