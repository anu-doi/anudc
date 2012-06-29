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
 * </pre>
 *
 */
public interface QuestionDAO extends GenericDAO<Question, Long> {
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
	 */
	public List<Question> getQuestionsByPid(String pid);
}
