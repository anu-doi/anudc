package au.edu.anu.datacommons.storage.info;

import java.nio.file.Paths;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import au.edu.anu.datacommons.util.Util;

@XmlType
public class FileInfo implements Comparable<FileInfo> {
	public enum Type {
		DIR, FILE
	};

	private String filename;
	private String relFilepath;
	private FileInfo.Type type;
	private long size;
	private Date lastModified;

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
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
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