package au.edu.anu.datacommons.exception;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;

/**
 * HtmlListProvider
 * 
 * Australian National University Data Commons
 * 
 * Class to convert the type List<String> to a html web page.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Provider
@Produces("text/html")
public class HtmlListProvider implements MessageBodyWriter<List<String>> {
	static final Logger LOGGER = LoggerFactory.getLogger(HtmlListProvider.class);
	
	@Context TemplateContext templateContext;
	
	/**
	 * getSize
	 * 
	 * Attempts to determine the length of the content returned
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages The messages to write a html page for
	 * @param clazz The class of the messages
	 * @param type The type of the messages
	 * @param annotations Any annotations associated
	 * @param mediaType The media type for the resolver
	 * @return length in bytes or -1 if the length cannot be determined in advance
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(List<String> messages, Class<?> clazz, Type type, Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}
	
	/**
	 * verifyGenericType
	 *
	 * Class to verify that the list has the String parameter
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param genericType The generic type to determine if it is appropriate
	 * @return An indication whether it is a List<String> format
	 */
	private boolean verifyGenericType(Type genericType) {
		if (!(genericType instanceof ParameterizedType)) {
			return false;
		}
		ParameterizedType pt = (ParameterizedType) genericType;
		
		if (pt.getActualTypeArguments().length > 1) {
			return false;
		}
		if (!(pt.getActualTypeArguments()[0] instanceof Class)) {
			return false;
		}
		Class listClass = (Class) pt.getActualTypeArguments()[0];
		return listClass == String.class;
	}

	/**
	 * isWriteable
	 * 
	 * Checks if this class can be used to write the html page for the list
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param clazz The class of the messages
	 * @param type The type of the messages
	 * @param annotations Any annotations associated
	 * @param mediaType The media type for the resolver
	 * @return an indication whether this is the MessageBodyWriter for the given type
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotations,
			MediaType mediaType) {
		return verifyGenericType(type);
	}

	/**
	 * writeTo
	 * 
	 * Create the page to return
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages Messages to write
	 * @param clazz The class
	 * @param type The type
	 * @param annotations Associated annotations
	 * @param mediaType The media type to resolve
	 * @param map Parameter map
	 * @param outputStream The stream to write to
	 * @throws IOException
	 * @throws WebApplicationException
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	@Override
	public void writeTo(List<String> messages, Class<?> clazz, Type type, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> map,
			OutputStream outputStream) throws IOException, WebApplicationException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("messages", messages);
		
		Viewable viewable = new Viewable("/error.jsp", model);
		
		final ResolvedViewable rv = resolve(viewable);
		
		if (rv == null) {
			throw new IOException("Unable to resolve html page for list");
		}
		rv.writeTo(outputStream);
	}
	
	/**
	 * resolve
	 *
	 * Resolve the given viewable
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param viewable The viewable to resolve
	 * @return The resolved viewable
	 */
	private ResolvedViewable resolve(Viewable viewable) {
		if (viewable instanceof ResolvedViewable) {
			return (ResolvedViewable) viewable;
		}
		else {
			return templateContext.resolveViewable(viewable);
		}
	}
}
