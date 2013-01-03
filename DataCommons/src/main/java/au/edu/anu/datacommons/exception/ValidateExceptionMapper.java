package au.edu.anu.datacommons.exception;

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ValidateExceptionMapper
 * 
 * Australian National University Data Commons
 * 
 * Maps the ValidateException to a response
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
@Provider
public class ValidateExceptionMapper implements ExceptionMapper<ValidateException> {
	static final Logger LOGGER = LoggerFactory.getLogger(ValidateExceptionMapper.class);
	
	@Context HttpHeaders headers;
	
	/**
	 * toResponse
	 * 
	 * Map the exception to a response
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		02/01/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param e The exception to map to a response
	 * @return The response for the Data Commons Exception
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(ValidateException e) {
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		
		// Ensure that the List<String> is held through the entity so that the appropriate MessageBodyWriter is assigned
		GenericEntity<List<String>> entity = new GenericEntity<List<String>>(e.getMessages()) {};
		
		ResponseBuilder responseBuilder = Response.status(400).entity(entity);
		
		LOGGER.debug("Header Media Type: {}", headers.getMediaType());
		
		if (accepts != null && accepts.size() > 0) {
			LOGGER.debug("There is an acceptable media type set");
			MediaType m = accepts.get(0);
			LOGGER.debug("Media Type: {}", m);
			
			responseBuilder = responseBuilder.type(m);
		}
		else {
			responseBuilder = responseBuilder.type(headers.getMediaType());
			LOGGER.debug("Using header media type");
		}
		
		return responseBuilder.build();
	}
	
}
