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

package au.edu.anu.dcbag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.util.TreeMap;

/**
 * Represents a Map of FileSummary for each BagFile in a specified Bag.
 */
public class FileSummaryMap extends TreeMap<String, FileSummary>
{
	private static final long serialVersionUID = 1L;

	protected FileSummaryMap() {
		super();
	}
	
	/**
	 * Instantiates a new file summary map.
	 * 
	 * @param bag
	 *            the bag containing files whose summary will be read
	 */
	public FileSummaryMap(Bag bag)
	{
		for (BagFile iBagFile : bag.getPayload())
			this.put(iBagFile.getFilepath(), new FileSummary(bag, iBagFile));
	}
	
	/**
	 * Gets the file summary.
	 * 
	 * @param bagFilePath
	 *            the bag file path
	 * @return the file summary
	 */
	public FileSummary getFileSummary(String bagFilePath)
	{
		for (String iFilepath : this.keySet())
			if (iFilepath.equals(bagFilePath))
				return this.get(iFilepath);
		return null;
	}
}
