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

import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

public class AndFileSystemNodeFilter implements FileSystemNodeFilter {

	private FileSystemNodeFilter[] filters;
	
	public AndFileSystemNodeFilter(FileSystemNodeFilter... filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean accept(FileSystemNode fileSystemNode) {
		for(FileSystemNodeFilter filter : this.filters) {
			if (! filter.accept(fileSystemNode)) return false;
		}
		return true;
	}
	
}
