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

package au.edu.anu.datacommons.storage.info;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import au.edu.anu.datacommons.util.Util;

@XmlType
public class FileInfo implements Comparable<FileInfo> {
	public enum Type {
		DIR, FILE
	};

	private String pid;
	private String filename;
	private String relFilepath;
	private FileInfo.Type type;
	private long size;
	private FileTime lastModified;
	
	private FileInfo parent;
	private Path path;
	private Set<FileInfo> children = new HashSet<>();
	
	private Map<String, String> messageDigests;
	private PronomFormat pronomFormat;
	private Map<String, String[]> metadata;
	private String scanResult;
	private String presvPath;

	@XmlElement
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@XmlElement
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@XmlElement
	public String getRelFilepath() {
		return relFilepath;
	}

	public void setRelFilepath(String relFilepath) {
		this.relFilepath = relFilepath;
	}

	@XmlElement
	public FileInfo.Type getType() {
		return type;
	}

	public void setType(FileInfo.Type type) {
		this.type = type;
	}

	@XmlElement
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@XmlElement
	public String getFriendlySize() {
		return Util.byteCountToDisplaySize(this.size);
	}
	
	@XmlElement
	public FileTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(FileTime lastModified) {
		this.lastModified = lastModified;
	}
	
	@XmlElement
	public FileInfo getParent() {
		return parent;
	}

	public void setParent(FileInfo parent) {
		this.parent = parent;
	}
	
	@XmlTransient
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	@XmlElement
	public Set<FileInfo> getChildren() {
		return children;
	}

	public void addChild(FileInfo child) {
		this.children.add(child);
	}
	
	@XmlTransient
	public Set<FileInfo> getChildren(String sortBy) {
		// TODO Implement custom sorting
		TreeSet<FileInfo> sortedSet = new TreeSet<>();
		sortedSet.addAll(this.getChildren());
		return sortedSet;
	}
	
	@XmlTransient
	public Set<FileInfo> getChildrenRecursive() {
		TreeSet<FileInfo> recursiveSet = new TreeSet<>();
		
		addChildren(recursiveSet, this);
		return recursiveSet;
	}
	
	private void addChildren(Set<FileInfo> set, FileInfo parent) {
		for (FileInfo child : parent.getChildren()) {
			set.add(child);
			if (!child.getChildren().isEmpty()) {
				addChildren(set, child);
			}
		}
	}
	
	@XmlElementWrapper
	public Map<String, String> getMessageDigests() {
		return messageDigests;
	}

	public void setMessageDigests(Map<String, String> messageDigests) {
		this.messageDigests = messageDigests;
	}

	@XmlElement
	public PronomFormat getPronomFormat() {
		return pronomFormat;
	}

	public void setPronomFormat(PronomFormat pronomFormat) {
		this.pronomFormat = pronomFormat;
	}

	@XmlElementWrapper
	public Map<String, String[]> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String[]> metadata) {
		this.metadata = metadata;
	}

	@XmlElement
	public String getScanResult() {
		return scanResult;
	}

	public void setScanResult(String scanResult) {
		this.scanResult = scanResult;
	}

	@XmlElement
	public String getPresvPath() {
		return presvPath;
	}

	public void setPresvPath(String presvPath) {
		this.presvPath = presvPath;
	}
	
	@Override
	public int compareTo(FileInfo o) {
		if (this.type == Type.DIR && o.type == Type.FILE) {
			return -1;
		} else if (this.type == Type.FILE && o.type == Type.DIR) {
			return 1;
		} else {
			return Paths.get(this.relFilepath).compareTo(Paths.get(o.relFilepath));
		}
	}
	
	@Override
	public String toString() {
		return getRelFilepath();
	}
}