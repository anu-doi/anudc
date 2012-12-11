package au.edu.anu.datacommons.publish;

/**
 * ValidateException
 * 
 * Australian National University Data Commons
 * 
 * Exception that occurs on validation
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ValidateException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * Exception for validation
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param message The message behind the error
	 */
	public ValidateException(String message) {
		super(message);
	}
}
