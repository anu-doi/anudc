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

package au.edu.anu.datacommons.xml.sparql;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

/**
 * Results
 * 
 * Australian National University Data Commons
 * 
 * Defines the 'results' node from a sparql query result.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		01/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Results {
	List<Result> results_;

	public Results() {
		results_ = new ArrayList<Result>();
	}
	
	/**
	 * getResults
	 *
	 * Gets the result nodes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the results
	 */
	@XmlAnyElement
	public List<Result> getResults() {
		return results_;
	}

	/**
	 * setResults
	 *
	 * Sets the result nodes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param results the results to set
	 */
	public void setResults(List<Result> results) {
		this.results_ = results;
	}
}
