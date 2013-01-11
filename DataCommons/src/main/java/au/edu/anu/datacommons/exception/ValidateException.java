package au.edu.anu.datacommons.exception;

import java.util.Arrays;
import java.util.List;

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
public class ValidateException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	List<String> messages;
	
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
		messages = Arrays.asList(message);
	}
	
	/**
	 * Constructor
	 * 
	 * Constructor for a validation exception
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages A list of validation error messages
	 */
	public ValidateException(List<String> messages) {
		super(messages.toString());
		this.messages = messages;
	}
	
	/**
	 * getMessages
	 *
	 * The messages that have been set for the validation exception
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The validation error messages
	 */
	public List<String> getMessages() {
		return messages;
	}
}
