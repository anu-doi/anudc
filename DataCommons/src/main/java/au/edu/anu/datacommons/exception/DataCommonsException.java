package au.edu.anu.datacommons.exception;

import javax.ws.rs.core.Response.Status;

/**
 * DataCommonsException
 * 
 * Australian National University Data Commons
 * 
 * General Exception class for the Data Commons.  It contains the http status reponse code and a message for an indication of what the error is
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		02/01/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DataCommonsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	int status;
	String message;
	
	/**
	 * Constructor
	 * 
	 * Construct a new instance with the given HTTP status code and message
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status The status of the error reponse
	 * @param message The error message text
	 */
	public DataCommonsException(int status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
	
	/**
	 * Constructor
	 * 
	 * Construct a new instance with the given HTTP status code and message
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status The status of the error reponse
	 * @param message The error message text
	 */
	public DataCommonsException(Status status, String message) {
		this(status.getStatusCode(), message);
	}
	
	/**
	 * getStatus
	 *
	 * Get the status code of the response
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * getErrorMessage
	 *
	 * Get the error message
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the message
	 */
	public String getErrorMessage() {
		return message;
	}
}
