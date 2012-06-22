package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;

/**
 * SelectCodeDAO
 * 
 * Australian National University Data Commons
 * 
 * Performs actions with the SelectCodes in the database
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface SelectCodeDAO  extends GenericDAO<SelectCode, SelectCodePK> {
	/**
	 * getOptionsByNames
	 *
	 * Gets a list of options with the given names
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param names
	 * @return
	 */
	List<SelectCode> getOptionsByNames(List<String> names);
}
