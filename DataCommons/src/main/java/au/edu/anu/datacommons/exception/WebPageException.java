package au.edu.anu.datacommons.exception;

import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;

/**
 * WebPageException
 * 
 * Australian National University Data Commons
 * 
 * An exception that displays the message.jsp page
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class WebPageException extends WebApplicationException {
	static final Logger LOGGER = LoggerFactory.getLogger(WebPageException.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * Constructs the message.jsp
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status The HTTP status code
	 * @param model A model containing the message
	 */
	public WebPageException(int status, Map<String, Object> model) {
		super(Response.status(400).entity(new Viewable("/message.jsp", model)).type(MediaType.TEXT_HTML).build());
	}
}
