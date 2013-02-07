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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.Question;
import au.edu.anu.datacommons.data.db.PersistenceManager;

/**
 * QuestionDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * An implementation for QuestionDAO
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class QuestionDAOImpl extends GenericDAOImpl<Question, Long> implements
		QuestionDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(QuestionDAOImpl.class);

	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public QuestionDAOImpl(Class<Question> type) {
		super(type);
	}
	
	/**
	 * getQuestionsByPid
	 * 
	 * Retrieves a list of questsions associated with the pid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid of the fedora object to retireve questions for
	 * @return A list of questsions associated with the pid
	 * @see au.edu.anu.datacommons.data.db.dao.QuestionDAO#getQuestionsByPid(java.lang.String)
	 */
	@Override
	public List<Question> getQuestionsByPid(String pid) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Query query = entityManager.createQuery("SELECT q FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question", Question.class);
		query.setParameter("pid", pid);
		
		List<Question> questions = query.getResultList();
		
		return questions;
	}

}
