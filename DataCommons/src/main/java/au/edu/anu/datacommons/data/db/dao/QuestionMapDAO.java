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

package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.collectionrequest.Question;
import au.edu.anu.datacommons.collectionrequest.QuestionMap;

/**
 * QuestionMapDAO
 * 
 * Australian National University Data Commons
 * 
 * DAO for the QuestionMap class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/04/2012	Genevieve Turner (GT)	Updated to allow for questions against groups and domains
 * </pre>
 *
 */
public interface QuestionMapDAO extends GenericDAO<QuestionMap, Long> {
	/**
	 * getSingleByPidAndQuestion
	 *
	 * Retrieves a QuestionMap with the given pid and question
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid of the fedora object to retrieve the QuestionMap
	 * @param question The question to retrieve the question map for
	 * @return The question map
	 */
	public QuestionMap getSingleByPidAndQuestion(String pid, Question question);
	
	/**
	 * getSingleByObjectAndQuestion
	 *
	 * Retrieve the QuestionMap with the given pid, group or domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param question The question to retrieve the map for
	 * @param pid The pid to potentially retrieve the map for
	 * @param groupId The group to potentially retrieve the map for
	 * @param domainId The domain to potentially retrieve the map for
	 * @return The question map for the given information
	 */
	public QuestionMap getSingleByObjectAndQuestion(Question question, String pid, Long groupId, Long domainId);
}
