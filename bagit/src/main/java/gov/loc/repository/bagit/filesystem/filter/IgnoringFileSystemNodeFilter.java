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

package gov.loc.repository.bagit.filesystem.filter;

import java.util.List;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;
import gov.loc.repository.bagit.utilities.FilenameHelper;

public class IgnoringFileSystemNodeFilter implements FileSystemNodeFilter {

	private List<String> ignoreAdditionalDirectories;
	private boolean ignoreSymlinks;
	private String relativeFilepath = null;
	
	public IgnoringFileSystemNodeFilter(List<String> ignoreAdditionalDirectories, boolean ignoreSymlinks) {
		assert ignoreAdditionalDirectories != null;
		this.ignoreAdditionalDirectories = ignoreAdditionalDirectories;
		this.ignoreSymlinks = ignoreSymlinks;
	}
	
	public void setRelativeFilepath(String relativeFilepath) {
		this.relativeFilepath = relativeFilepath;
	}
	
	@Override
	public boolean accept(FileSystemNode fileSystemNode) {
		String filepath = fileSystemNode.getFilepath();
		if (relativeFilepath != null) filepath = FilenameHelper.removeBasePath(relativeFilepath, filepath);
		if (this.ignoreSymlinks && fileSystemNode.isSymlink()) return false;
		if ((fileSystemNode instanceof DirNode) && this.ignoreAdditionalDirectories.contains(filepath)) return false;
		return true;
	}

}
