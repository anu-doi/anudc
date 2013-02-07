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

import java.util.ArrayList;
import java.util.Collection;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

public class NodeHelper {
	public static Collection<FileSystemNode> listDescendants(DirNode baseNode, FileSystemNodeFilter filter, FileSystemNodeFilter descentFilter) {
		Collection<FileSystemNode> fileSystemNodes = new ArrayList<FileSystemNode>();
		listDescendants(baseNode, filter, descentFilter, fileSystemNodes);
		return fileSystemNodes;
	}
	
	private static void listDescendants(DirNode baseNode, FileSystemNodeFilter filter, FileSystemNodeFilter descentFilter, Collection<FileSystemNode> fileSystemNodes) {
		for(FileSystemNode child : baseNode.listChildren()) {
			if (filter == null || filter.accept(child)) {
				fileSystemNodes.add(child);
			}
			if(child instanceof DirNode && (descentFilter == null || descentFilter.accept(child))) {
				listDescendants((DirNode)child, filter, descentFilter, fileSystemNodes);
			}
		}
	}

	public static Collection<FileSystemNode> listChildren(DirNode baseNode, FileSystemNodeFilter filter) {
		Collection<FileSystemNode> fileSystemNodes = new ArrayList<FileSystemNode>();
		listChildren(baseNode, filter, fileSystemNodes);
		return fileSystemNodes;
		
	}

	
	private static void listChildren(DirNode baseNode, FileSystemNodeFilter filter, Collection<FileSystemNode> fileSystemNodes) {
		for(FileSystemNode child : baseNode.listChildren()) {
			if (filter == null || filter.accept(child)) {
				fileSystemNodes.add(child);
			}
		}
	}

}
