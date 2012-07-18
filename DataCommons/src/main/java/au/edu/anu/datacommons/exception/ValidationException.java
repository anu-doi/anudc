package au.edu.anu.datacommons.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ValidationException
 * 
 * Australian National University Data Commons
 * 
 * Exception that to be thrown when validation fails
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
public class ValidationException extends WebApplicationException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * Throws the validation exception with the given message
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param message Message to return in the exception
	 */
	public ValidationException(String message) {
		super(Response.status(400).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
}
