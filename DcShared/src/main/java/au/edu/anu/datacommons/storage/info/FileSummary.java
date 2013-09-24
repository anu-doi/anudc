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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * Represents the summary of a single file in a bag associated with a record. Provides the following details of each
 * file in a bag:
 * <ul>
 * <li>Path of file relative to root of bag. For example, data/abc.txt</li>
 * <li>Size of file in bytes</li>
 * <li>Pronom format name of file. For example, "Acrobat PDF 1.6 - Portable Document Format"</li>
 * <li>Pronom format ID of file. For example, "fmt/20"</li>
 * <li>MD5 hash value of the file contents</li>
 * <li>Technical metadata of the file.</li>
 * <li>Virus scan result of the file</li>
 * </ul>
 * 
 * @author Rahul Khanna
 */
public class FileSummary {
	private String filepath;
	
	private String filename;
	private long lastModified;
	private long sizeInBytes;
	private String friendlySize;
	private String presvFilepath;
	
	private Map<String, String> messageDigests;
	private PronomFormat pronomFormat;
	private Map<String, String[]> metadata;
	private String scanResult;
	
	public FileSummary() {
	}
	
	public FileSummary(String filepath, File file) {
		this.filepath = filepath;
		this.filename = file.getName();
		this.sizeInBytes = file.length();
		this.friendlySize = FileUtils.byteCountToDisplaySize(getSizeInBytes());
		this.lastModified = file.lastModified();
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Gets the size in bytes.
	 * 
	 * @return the size in bytes
	 */
	public long getSizeInBytes() {
		return sizeInBytes;
	}
	
	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
	/**
	 * Gets the friendly size.
	 * 
	 * @return the friendly size
	 */
	public String getFriendlySize() {
		return this.friendlySize;
	}
	
	public void setFriendlySize(String friendlySize) {
		this.friendlySize = friendlySize;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public Map<String, String> getMessageDigests() {
		if (this.messageDigests == null) {
			this.messageDigests = new HashMap<String, String>();
		}
		return messageDigests;
	}
	
	public PronomFormat getPronomFormat() {
		return pronomFormat;
	}
	
	public void setPronomFormat(PronomFormat pronomFormat) {
		this.pronomFormat = pronomFormat;
	}
	
	/**
	 * Gets the metadata.
	 * 
	 * @return the metadata
	 */
	public Map<String, String[]> getMetadata() {
		return metadata;
	}
	
	public void setMetadata(Map<String, String[]> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Gets the scan result.
	 * 
	 * @return the scan result
	 */
	public String getScanResult() {
		return scanResult;
	}
	
	public void setScanResult(String scanResult) {
		this.scanResult = scanResult;
	}

	public String getPresvFilepath() {
		return presvFilepath;
	}

	public void setPresvFilepath(String presvFilepath) {
		this.presvFilepath = presvFilepath;
	}

}
