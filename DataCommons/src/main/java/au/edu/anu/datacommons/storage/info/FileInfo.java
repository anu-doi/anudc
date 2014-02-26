package au.edu.anu.datacommons.storage.info;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import au.edu.anu.datacommons.util.Util;

@XmlType
public class FileInfo implements Comparable<FileInfo> {
	public enum Type {
		DIR, FILE
	};

	private String filename;
	private String relFilepath;
	private String dirpath;
	private FileInfo.Type type;
	private long size;
	private Date lastModified;
	
	private Map<String, String> messageDigests;
	private PronomFormat pronomFormat;
	private Map<String, String[]> metadata;
	private String scanResult;
	private String presvPath;


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
	public String getDirpath() {
		return dirpath;
	}

	public void setDirpath(String dirpath) {
		this.dirpath = dirpath;
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