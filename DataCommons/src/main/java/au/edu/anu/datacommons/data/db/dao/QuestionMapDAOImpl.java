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
import au.edu.anu.datacommons.collectionrequest.QuestionMap;
import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.AclObjectIdentity;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

/**
 * QuestionMapDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class QuestionMapDAOImpl extends GenericDAOImpl<QuestionMap, Long> implements
		QuestionMapDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(QuestionMapDAOImpl.class);
	
	private static final String fedoraObjectQueryStr = "SELECT qm FROM QuestionMap qm WHERE qm.pid = :pid AND qm.question = :question";
	private static final String groupQueryStr = "SELECT qm FROM QuestionMap qm WHERE qm.group.id = :groupId AND qm.question = :question";
	private static final String domainQueryStr = "SELECT qm FROM QuestionMap qm WHERE qm.domain.id = :domainId AND qm.question = :question";

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
	public QuestionMapDAOImpl() {
		super(QuestionMap.class);
	}
	
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
	 * @see au.edu.anu.datacommons.data.db.dao.QuestionMapDAO#getSingleByPidAndQuestion(java.lang.String, au.edu.anu.datacommons.collectionrequest.Question)
	 */
	public QuestionMap getSingleByPidAndQuestion(String pid, Question question) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		
		//Query query = entityManager.createQuery("SELECT qm FROM QuestionMap qm WHERE qm.pid = :pid AND qm.question = :question", QuestionMap.class);
		Query query = entityManager.createQuery(fedoraObjectQueryStr, QuestionMap.class);
		query.setParameter("pid", pid);
		query.setParameter("question", question);
		
		QuestionMap questionMap = (QuestionMap) query.getSingleResult();
		
		return questionMap;
	}
	
	/**
	 * getSingleByObjectAndQuestion
	 * 
	 * Retrieve the QuestionMap with the given pid, group or domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		04/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param question The question to retrieve the map for
	 * @param pid The pid to potentially retrieve the map for
	 * @param groupId The group to potentially retrieve the map for
	 * @param domainId The domain to potentially retrieve the map for
	 * @return The question map for the given information
	 * @see au.edu.anu.datacommons.data.db.dao.QuestionMapDAO#getSingleByObjectAndQuestion(au.edu.anu.datacommons.collectionrequest.Question, java.lang.String, java.lang.Long, java.lang.Long)
	 */
	public QuestionMap getSingleByObjectAndQuestion(Question question, String pid, Long groupId, Long domainId) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		QuestionMap questionMap = null;
		try {
			if (pid != null && pid.trim().length() > 0) {
				Query query = entityManager.createQuery(fedoraObjectQueryStr, QuestionMap.class);
				query.setParameter("pid", pid);
				query.setParameter("question", question);
				questionMap = (QuestionMap) query.getSingleResult();
			}
			else if (groupId != null) {
				Query query = entityManager.createQuery(groupQueryStr, QuestionMap.class);
				query.setParameter("groupId", groupId);
				query.setParameter("question", question);
				questionMap = (QuestionMap) query.getSingleResult();
			}
			else if (domainId != null) {
				Query query = entityManager.createQuery(domainQueryStr);
				query.setParameter("domainId", domainId);
				query.setParameter("question", question);
				questionMap = (QuestionMap) query.getSingleResult();
			}
		}
		finally {
			entityManager.close();
		}
		
		return questionMap;
	}
	
	public List<QuestionMap> getListByPid(String pid) {
		List<QuestionMap> questions = null;
		
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		Query query = entityManager.createQuery("from QuestionMap where pid = :pid");
		query.setParameter("pid", pid);
		try {
			questions = query.getResultList();
			if (questions == null || questions.size() == 0) {
				FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
			}
		}
		finally {
			entityManager.close();
		}
		
		return questions;
	}
	
	@Override
	public List<QuestionMap> getListByItem(FedoraObject fedoraObject, boolean useParent) {
		List<QuestionMap> questions = null;
		
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		Query query = entityManager.createQuery("from QuestionMap where pid = :pid");
		query.setParameter("pid", fedoraObject.getObject_id());
		try {
			questions = query.getResultList();
			if (useParent && (questions == null || questions.size() == 0)) {
				LOGGER.debug("No questions found so far");
				questions = getListByParent(entityManager, new Long(3), fedoraObject.getId());
			}
			for (QuestionMap question : questions) {
				question.getQuestion().getQuestionOptions().size();
			}
		}
		finally {
			entityManager.close();
		}
		
		return questions;
	}
	
	private List<QuestionMap> getListByParent(EntityManager entityManager, Long objectClassId, Long id) {
		Query aclQuery = entityManager.createQuery("from AclObjectIdentity where object_id_class = :idClass and object_id_identity = :id");
		aclQuery.setParameter("idClass",  objectClassId);
		aclQuery.setParameter("id", id);
		
		AclObjectIdentity aclObject = (AclObjectIdentity) aclQuery.getSingleResult();
		
		List<QuestionMap> questions = null;
		while ((questions == null || questions.size() == 0) && aclObject.getParent_object() != null) {
			LOGGER.debug("Object ID: {}, Parent ID: {}", aclObject.getId(), aclObject.getParent_object());
			aclObject = entityManager.find(AclObjectIdentity.class, aclObject.getParent_object());
			
			if (aclObject.getObject_id_class().longValue() == 2) {
				questions = getListByGroup(entityManager, aclObject.getObject_id_identity());
			}
			else if (aclObject.getObject_id_class().longValue() == 1){
				questions = getListByDomain(entityManager, aclObject.getObject_id_identity());
			}
		}
		
		return questions;
	}
	
	@Override
	public List<QuestionMap> getListByGroup(Long id, boolean useParent) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		try {
			List<QuestionMap> questions = getListByGroup(entityManager, id);
			if (useParent && (questions == null || questions.size() == 0)) {
				questions = getListByParent(entityManager, new Long(2), id);
			}
			for (QuestionMap question : questions) {
				question.getQuestion().getQuestionOptions().size();
			}
			return questions;
		}
		finally {
			entityManager.close();
		}
		
	}
	
	private List<QuestionMap> getListByGroup(EntityManager entityManager, Long id) {
		Query query = entityManager.createQuery("from QuestionMap where group.id = :groupId");
		query.setParameter("groupId", id);
		
		List<QuestionMap> questions = query.getResultList();
		
		return questions;
	}
	
	@Override
	public List<QuestionMap> getListByDomain(Long id, boolean useParent) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		try {
			List<QuestionMap> questions = getListByDomain(entityManager, id);
			if (useParent && (questions == null || questions.size() == 0)) {
				questions = getListByParent(entityManager, new Long(2), id);
			}
			for (QuestionMap question : questions) {
				question.getQuestion().getQuestionOptions().size();
			}
			return questions;
		}
		finally {
			entityManager.close();
		}
	}
	
	private List<QuestionMap> getListByDomain(EntityManager entityManager, Long id) {
		Query query = entityManager.createQuery("from QuestionMap where domain.id = :domainId");
		query.setParameter("domainId", id);
		
		List<QuestionMap> questions = query.getResultList();
		
		return questions;
	}
}
