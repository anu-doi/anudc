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

import java.util.List;

import au.edu.anu.datacommons.collectionrequest.Question;

/**
 * QuestionDAO
 * 
 * Australian National University Data Commons
 * 
 * DAO for the Question class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/04/2013	Genevieve Turner (GT)	Updated to allow for questions also potentially having a group or domain
 * </pre>
 *
 */
public interface QuestionDAO extends GenericDAO<Question, Long> {
	/**
	 * getQuestionsByPid
	 *
	 * Retrieves a list of questions associated with the pid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid of the fedora object to retireve questions for
	 * @return A list of questsions associated with the pid
	 */
	public List<Question> getQuestionsByPid(String pid, Boolean required);
	
	/**
	 * getQuestionsByGroup
	 *
	 * Retrieves a list of questions associated with the group id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groupId The group id
	 * @return The list of associated questions
	 */
	public List<Question> getQuestionsByGroup(Long groupId, Boolean required);
	
	/**
	 * getQuestionsByDomain
	 *
	 * Retrieves a list of questions associated with the domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param domainId The domain id
	 * @return The list of associated questions
	 */
	public List<Question> getQuestionsByDomain(Long domainId, Boolean required);
	
	/**
	 * getQuestionsForObject
	 *
	 * Retrieves a list of questions with the given pid, group or domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid
	 * @param groupId The group id
	 * @param domainId The domain id
	 * @return A list of associated questions
	 */
	public List<Question> getQuestionsForObject(String pid, Long groupId, Long domainId, Boolean required);
}
