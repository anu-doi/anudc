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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.Question;
import au.edu.anu.datacommons.collectionrequest.QuestionMap;
import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.AclObjectIdentity;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

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
 * 0.2		04/04/2013	Genevieve Turner (GT)	Updated to allow for questions also potentially having a group or domain
 * </pre>
 *
 */
public class QuestionDAOImpl extends GenericDAOImpl<Question, Long> implements
		QuestionDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(QuestionDAOImpl.class);
	
	private static final String fedoraObjectStr = "SELECT q, qm FROM Question q, QuestionMap qm WHERE qm.pid=:pid AND q=qm.question";
	private static final String groupQueryStr = "SELECT q, qm FROM Question q, QuestionMap qm WHERE qm.group.id = :groupId AND q=qm.question";
	private static final String domainQueryStr = "SELECT q, qm FROM Question q, QuestionMap qm WHERE qm.domain.id = :domainId AND q=qm.question";
	private static final String aclQueryStr = "FROM AclObjectIdentity WHERE object_id_class = :idClass and object_id_identity = :id";
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public QuestionDAOImpl() {
		super(Question.class);
	}
	
	/**
	 * getQuestionsByPid
	 * 
	 * Retrieves a list of questions associated with the pid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		04/04/2013	Genevieve Turner (GT)	Updated to find questions up the hierarchy if there are no questions so far
	 * </pre>
	 * 
	 * @param pid The pid of the fedora object to retireve questions for
	 * @return A list of questions associated with the pid
	 * @see au.edu.anu.datacommons.data.db.dao.QuestionDAO#getQuestionsByPid(java.lang.String)
	 */
	@Override
	public List<Question> getQuestionsByPid(String pid, Boolean required) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		List<Question> questions = null;
		try {
			Query query = entityManager.createQuery(fedoraObjectStr);
			query.setParameter("pid", pid);
			
			List<Object[]> results = query.getResultList();
			LOGGER.info("Number of results: {}", results.size());
			
			//if (questions == null || questions.size() == 0) {
			if (results == null || results.size() == 0) {
				FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
				FedoraObject fedoraObject = fedoraObjectDAO.getSingleByName(pid);
				
				questions = getParentQuestions(entityManager, new Long(3), fedoraObject.getId(), required);
			}
			else {
				questions = filterQuestionRequirement(results, required);
			}
		}
		finally {
			entityManager.close();
		}
		
		return questions;
	}

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
	 * @see au.edu.anu.datacommons.data.db.dao.QuestionDAO#getQuestionsByGroup(java.lang.Long)
	 */
	public List<Question> getQuestionsByGroup(Long groupId, Boolean required) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		List<Question> questions = null;
		try {
			Query groupQuery = entityManager.createQuery(groupQueryStr);
			groupQuery.setParameter("groupId", groupId);

			List<Object[]> results = groupQuery.getResultList();
			
			if (results == null || results.size() == 0) {
				questions = getParentQuestions(entityManager, new Long(2), groupId, required);
			}
			else {
				questions = filterQuestionRequirement(results, required);
			}
		}
		finally {
			entityManager.close();
		}
		return questions;
	}

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
	public List<Question> getQuestionsByDomain(Long domainId, Boolean required) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		List<Question> questions = null;
		try {
			Query domainQuery = entityManager.createQuery(domainQueryStr);
			domainQuery.setParameter("domainId", domainId);

			List<Object[]> results = domainQuery.getResultList();
			
			if (results == null || results.size() == 0) {
				questions = getParentQuestions(entityManager, new Long(1), domainId, required);
			}
			else {
				questions = filterQuestionRequirement(results, required);
			}
		}
		finally {
			entityManager.close();
		}
		return questions;
	}
	
	/**
	 * getParentQuestions
	 *
	 * Traverses up the acl object tree to find questions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param entityManager The entity manager to retrieve questions for
	 * @param objectClassId The id of the object_id_class for the acl_object_identity
	 * @param id The id of the object to retrieve
	 * @return A list of questions
	 */
	private List<Question> getParentQuestions(EntityManager entityManager, Long objectClassId, Long id, Boolean required) {
		Query groupQuery = entityManager.createQuery(groupQueryStr);
		Query domainQuery = entityManager.createQuery(domainQueryStr);
		
		List<Question> questions = null;
		LOGGER.debug("Retrieving parent questions for Class Id: {}, Identity: {}", objectClassId, id);
		//Find the acl object for the item
		Query aclQuery = entityManager.createQuery(aclQueryStr, AclObjectIdentity.class);
		aclQuery.setParameter("idClass", objectClassId);
		aclQuery.setParameter("id", id);
		AclObjectIdentity aclObject = (AclObjectIdentity) aclQuery.getSingleResult();
		
		//Keep looping through until either questions are found or there is no parent object
		while ((questions == null || questions.size() == 0) &&aclObject.getParent_object() != null) {
			aclObject = entityManager.find(AclObjectIdentity.class, aclObject.getParent_object());
			if (aclObject.getObject_id_class().longValue() == 2) {
				groupQuery.setParameter("groupId", aclObject.getObject_id_identity());

				List<Object[]> results = groupQuery.getResultList();
				questions = filterQuestionRequirement(results, required);
			}
			else if (aclObject.getObject_id_class().longValue() == 1) {
				domainQuery.setParameter("domainId", aclObject.getObject_id_identity());
				
				List<Object[]> results = domainQuery.getResultList();
				questions = filterQuestionRequirement(results, required);
			}
		}
		
		return questions;
	}
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
	public List<Question> getQuestionsForObject(String pid, Long groupId, Long domainId, Boolean required) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		List<Question> questions = null;
		try {
			if (pid != null && pid.trim().length() > 0) {
				Query query = entityManager.createQuery(fedoraObjectStr);
				query.setParameter("pid", pid);
				List<Object[]> results = query.getResultList();
				questions = filterQuestionRequirement(results, required);
			}
			else if (groupId != null) {
				Query query = entityManager.createQuery(groupQueryStr);
				query.setParameter("groupId", groupId);
				List<Object[]> results = query.getResultList();
				questions = filterQuestionRequirement(results, required);
			}
			else if (domainId != null) {
				Query query = entityManager.createQuery(domainQueryStr);
				query.setParameter("domainId", domainId);
				List<Object[]> results = query.getResultList();
				questions = filterQuestionRequirement(results, required);
			}
		}
		finally {
			entityManager.close();
		}
		
		return questions;
	}
	
	/**
	 * filterQuestionRequirement
	 *
	 * Filters the list of questions by whether they are required or not
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		08/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param results The list of results to filter
	 * @param required Indicates whether to return required or optional questions
	 * @return The list of filtered questions
	 */
	public List<Question> filterQuestionRequirement(List<Object[]> results, Boolean required) {
		List<Question> questions = new ArrayList<Question>();
		QuestionMap questionMap = null;
		Question question = null;
		for (Object[] result : results) {
			questionMap = (QuestionMap) result[1];
			if (questionMap.getRequired().equals(required)) {
				question = (Question) result[0];
				questions.add(question);
			}
		}
		
		return questions;
	}
}
