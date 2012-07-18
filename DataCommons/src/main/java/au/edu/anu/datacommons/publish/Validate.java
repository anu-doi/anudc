package au.edu.anu.datacommons.publish;

import java.util.List;

/**
 * Validate
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
 * 0.1		17/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface Validate {
	/**
	 * isValid
	 *
	 * Verifies that the object with the given pid is valid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid
	 * @return
	 */
	public boolean isValid(String pid);
	
	/**
	 * getErrorMessages
	 *
	 * Retrieves a list of error messages from the validation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public List<String> getErrorMessages();
}
