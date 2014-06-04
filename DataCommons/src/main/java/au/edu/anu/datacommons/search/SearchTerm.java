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
package au.edu.anu.datacommons.search;

/**
 * SearchTerm
 *
 * Australian National University Data Commons
 * 
 * Search terms for solr queries
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class SearchTerm {
	private String key;
	private String value;
	
	/**
	 * Constructor
	 * 
	 * @param key The search term key
	 * @param value The search term value
	 */
	public SearchTerm(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Get the key
	 * 
	 * @return The key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the key
	 * 
	 * @param key The key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get the value
	 * 
	 * @return The value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value
	 * 
	 * @param value The value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
