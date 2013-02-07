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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sparql
 * 
 * Australian National University Data Commons
 * 
 * Defines the 'sparql' node of a risearch result. Utilised when transforming the result return
 * to a java class.
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
@XmlRootElement(name="sparql", namespace="http://www.w3.org/2001/sw/DataAccess/rf1/result")
public class Sparql {
	Results results;

	/**
	 * getResults
	 *
	 * Get the results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the results
	 */
	@XmlElement(namespace="http://www.w3.org/2001/sw/DataAccess/rf1/result")
	public Results getResults() {
		return results;
	}

	/**
	 * setResults
	 *
	 * Set the results
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param results the results to set
	 */
	public void setResults(Results results) {
		this.results = results;
	}
	
}
