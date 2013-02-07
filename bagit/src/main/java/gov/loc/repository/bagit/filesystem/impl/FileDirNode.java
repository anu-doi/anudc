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
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileNode;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

public class FileDirNode extends AbstractFileNode implements DirNode {

	private Map<String,FileSystemNode> childrenMap = null;
	private Collection<FileSystemNode> children = null;  
	
	protected FileDirNode(File file, FileFileSystem fileSystem) {
		super(file, fileSystem);
		
		assert file.isDirectory();
	}

	private synchronized void lazyInit() {
		if (this.childrenMap != null) return;
		
		this.childrenMap = new HashMap<String, FileSystemNode>();
		
		for(File child : this.file.listFiles()) {
			FileSystemNode childNode = null;
			if (child.isDirectory()) {
				childNode = new FileDirNode(child, this.fileSystem);
			} else if (child.isFile()) {
				childNode = new FileFileNode(child, this.fileSystem);
			} else {
				throw new RuntimeException(MessageFormat.format("{0} is not a file or directory.", child));
			}
			this.childrenMap.put(childNode.getName(), childNode);
		}
		
		this.children = Collections.unmodifiableCollection(this.childrenMap.values());
	}
	
	@Override
	public Collection<FileSystemNode> listChildren() {
		this.lazyInit();
		return this.children;
	}

	@Override
	public FileNode childFile(String name) {
		this.lazyInit();
		FileSystemNode child = this.childrenMap.get(name);
		if (child != null && child instanceof FileNode) return (FileNode)child;
		return null;
	}

	@Override
	public DirNode childDir(String name) {
		this.lazyInit();
		FileSystemNode child = this.childrenMap.get(name);
		if (child != null && child instanceof DirNode) return (DirNode)child;
		return null;
	}
	
	@Override
	public Collection<FileSystemNode> listChildren(FileSystemNodeFilter filter) {
		return NodeHelper.listChildren(this, filter);
	}
	
	@Override
	public Collection<FileSystemNode> listDescendants() {
		return NodeHelper.listDescendants(this, null, null);
	}
	
	@Override
	public Collection<FileSystemNode> listDescendants(
			FileSystemNodeFilter filter, FileSystemNodeFilter descentFilter) {
		return NodeHelper.listDescendants(this, filter, descentFilter);
	}
	

}
