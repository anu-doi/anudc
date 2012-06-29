package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.Question;
import au.edu.anu.datacommons.collectionrequest.QuestionMap;
import au.edu.anu.datacommons.data.db.PersistenceManager;

public class QuestionMapDAOImpl extends GenericDAOImpl<QuestionMap, Long> implements
		QuestionMapDAO {
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
	public QuestionMapDAOImpl(Class<QuestionMap> type) {
		super(type);
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
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Query query = entityManager.createQuery("SELECT qm FROM QuestionMap qm WHERE qm.pid = :pid AND qm.question = :question", QuestionMap.class);
		query.setParameter("pid", pid);
		query.setParameter("question", question);
		
		QuestionMap questionMap = (QuestionMap) query.getSingleResult();
		
		return questionMap;
	}
}
