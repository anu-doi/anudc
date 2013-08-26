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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * Represents the summary of a bag's contents. This class contains the following information about a bag:
 * 
 * <li>
 * <ul>
 * FileSummaryMap - FileSummary for each BagFile in the Bag this BagSummary represents
 * </ul>
 * <ul>
 * Bag Properties - Contains Data Commons specific attributes of a Bag
 * </ul>
 * <ul>
 * Bag Info - Contains the Pid this bag belongs to, the bagging date and bag size
 * </ul>
 * </li>
 */
public class BagSummary {
	private FileSummaryMap fsMap = null;
	private Map<String, String> bagInfoTxt = null;
	private Map<String, String> extRefsTxt = null;
	private long bagSize;
	private String friendlySize;

	public BagSummary() {
	}

	public BagSummary(FileSummaryMap fsMap) {
		this.fsMap = fsMap;
		friendlySize = FileUtils.byteCountToDisplaySize(bagSize);
	}

	/**
	 * Gets the friendly size of this bag. For example, <code>2 MB</code>, <code>257 KB</code>
	 * 
	 * @return the friendly size
	 */
	public String getFriendlySize() {
		return friendlySize;
	}
	
	public void setFriendlySize(String friendlySize) {
		this.friendlySize = friendlySize;
	}

	/**
	 * Gets the number of payload files in this bag.
	 * 
	 * @return the number of payload files as long
	 */
	public long getNumFiles() {
		return fsMap.size();
	}

	/**
	 * Gets the pid of the record this bag belongs to.
	 * 
	 * @return the pid of record
	 */
	public String getPid() {
		return bagInfoTxt.get("External-Identifier");
	}

	/**
	 * Gets the FileSummaryMap containing FileSummary for each BagFile in this bag.
	 * 
	 * @return FileSummaryMap
	 */
	public FileSummaryMap getFileSummaryMap() {
		return fsMap;
	}

	public void setFileSummaryMap(FileSummaryMap fsMap) {
		this.fsMap = fsMap;
	}

	/**
	 * Gets the bag info txt.
	 * 
	 * @return the bag info txt
	 */
	public Map<String, String> getBagInfoTxt() {
		return Collections.unmodifiableMap(bagInfoTxt);
	}
	
	public void setBagInfoTxt(Map<String, String> bagInfoTxt) {
		this.bagInfoTxt = bagInfoTxt;
	}

	/**
	 * Gets the external references tag file containing links to external resources.
	 * 
	 * @return ExtRefsTxt
	 */
	public Map<String, String> getExtRefsTxt() {
		return Collections.unmodifiableMap(extRefsTxt);
	}
	
	public void setExtRefsTxt(Map<String, String> extRefsTxt) {
		this.extRefsTxt = extRefsTxt;
	}
	
	public long getBagSize() {
		return bagSize;
	}

	public void setBagSize(long bagSize) {
		this.bagSize = bagSize;
	}
}
