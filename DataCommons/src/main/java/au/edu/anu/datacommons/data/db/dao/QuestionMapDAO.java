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
}
