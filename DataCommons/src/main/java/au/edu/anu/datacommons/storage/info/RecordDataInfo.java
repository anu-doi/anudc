package au.edu.anu.datacommons.storage.info;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import au.edu.anu.datacommons.util.Util;

/**
 * Data structure class to hold information about data files in a collection record. 
 * 
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class RecordDataInfo {
	private String pid;
	private Collection<FileInfo> files = new ArrayList<FileInfo>();
	private long recordSize;
	private long recordNumFiles;
	private long dirSize;
	private long dirNumFiles;
	
	private Collection<String> extRefs;

	@XmlElement
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@XmlElementWrapper
	public Collection<FileInfo> getFiles() {
		return files;
	}

	public void setFiles(Collection<FileInfo> files) {
		this.files = files;
	}
	
	@XmlElement
	public long getRecordSize() {
		return recordSize;
	}

	public void setRecordSize(long recordSize) {
		this.recordSize = recordSize;
	}
	
	@XmlElement
	public long getRecordNumFiles() {
		return recordNumFiles;
	}

	public void setRecordNumFiles(long recordNumFiles) {
		this.recordNumFiles = recordNumFiles;
	}

	@XmlElement
	public long getDirSize() {
		return dirSize;
	}

	public void setDirSize(long dirSize) {
		this.dirSize = dirSize;
	}

	@XmlElement
	public long getDirNumFiles() {
		return dirNumFiles;
	}

	public void setDirNumFiles(long dirNumFiles) {
		this.dirNumFiles = dirNumFiles;
	}

	@XmlElementWrapper
	public Collection<String> getExtRefs() {
		return extRefs;
	}

	public void setExtRefs(Collection<String> extRefs) {
		this.extRefs = extRefs;
	}
	

	public Collection<FileInfo> getFiles(String dir) {
		Path targetDir;
		if (dir == null || dir.equals("") || dir.equals("/")) {
			targetDir = null;
		} else {
			targetDir = Paths.get(dir);
		}
		
		Collection<FileInfo> filtered = new TreeSet<>(getFiles());
		Iterator<FileInfo> iter = filtered.iterator();
		while (iter.hasNext()) {
			FileInfo iFileInfo = iter.next();
			Path iFileParent = Paths.get(iFileInfo.getRelFilepath()).getParent();
			if (targetDir != null) {
				if (!targetDir.equals(iFileParent)) {
					iter.remove();
				}
			} else {
				if (iFileParent != null) {
					iter.remove();
				}
			}
		}
		return filtered;
	}
	
	public List<FileInfo> getParents(String source) {
		List<FileInfo> parents = new ArrayList<FileInfo>();
		Path sourcePath = Paths.get(source);
		for (Path parent = sourcePath; parent != null; parent = parent.getParent()) {
			for (FileInfo iFileInfo : this.files) {
				if (Paths.get(iFileInfo.getRelFilepath()).equals(parent)) {
					parents.add(iFileInfo);
					break;
				}
			}
		}
		Collections.reverse(parents);
		return parents;
	}
	
	public String getRecordFriendlySize() {
		return Util.byteCountToDisplaySize(this.recordSize);
	}
	
	public String getDirFriendlySize() {
		return Util.byteCountToDisplaySize(this.dirSize);
	}
}