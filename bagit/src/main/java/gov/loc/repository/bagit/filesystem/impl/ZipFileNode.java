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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import gov.loc.repository.bagit.filesystem.FileNode;

public class ZipFileNode extends AbstractZipNode implements FileNode {

	private ZipArchiveEntry entry;

	protected ZipFileNode(ZipArchiveEntry entry, String filepath, ZipFileSystem fileSystem) {
		super(filepath, fileSystem);
		this.entry = entry;
	}

	public ZipArchiveEntry getEntry() {
		return this.entry;
	}
	
	@Override
	public long getSize() {
		return this.entry.getSize();
	}

	@Override
	public InputStream newInputStream() {
		if (this.entry == null) {
			throw new RuntimeException("Does not exist");
		}
		
		try {
			return this.fileSystem.getZipfile().getInputStream(this.entry);
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean exists() {
		return this.entry != null;
	}
	
}
