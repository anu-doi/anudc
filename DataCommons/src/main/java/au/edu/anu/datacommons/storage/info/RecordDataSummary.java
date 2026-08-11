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

import java.util.Collection;

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
public class RecordDataSummary {
	private String pid;
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
	

	public String getRecordFriendlySize() {
		return Util.byteCountToDisplaySize(this.recordSize);
	}
	
	public String getDirFriendlySize() {
		return Util.byteCountToDisplaySize(this.dirSize);
	}
}