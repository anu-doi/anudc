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

package au.edu.anu.datacommons.data.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SolrUtils
 * 
 * Australian National University Data Commons
 * 
 * Utlities to use with Solr
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		23/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrUtils.class);
	
	/**
	 * escapeSpecialCharacters
	 *
	 * Escapes some of the special characters used in Solr.  This is used rather than
	 * ClientUtils as ClientUtils escapes characters that we do not want escaped.<br />
	 * Currently escapes: ':'
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param query The query to escape special characters from
	 * @return Returns the modified string
	 */
	public static String escapeSpecialCharacters(String query) {
		query = query.replace(":", "\\:");
		return query;
	}
}
