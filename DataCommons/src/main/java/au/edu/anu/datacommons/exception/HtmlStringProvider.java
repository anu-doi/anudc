/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.exception;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;

/**
 * HtmlStringProvider
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
 * 0.1		21/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Provider
@Produces("text/html")
public class HtmlStringProvider implements MessageBodyWriter<String> {
	@Context TemplateContext templateContext;
	
	/**
	 * getSize
	 * 
	 * Attempts to determine the length of the content returned
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages The messages to write a html page for
	 * @param clazz The class of the messages
	 * @param type The type of the messages
	 * @param annotations Any annotations associated
	 * @param mediaType The media type for the resolver
	 * @return
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(String message, Class<?> clazz, Type type,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/**
	 * isWriteable
	 * 
	 * Checks if this class can be used to write the html page
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/12/2012	Genevieve Turner(GT)	Initial
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
		return clazz == String.class;
	}

	/**
	 * writeTo
	 * 
	 * Create the page to return
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages Messages to write
	 * @param clazz The class
	 * @param type The type
	 * @param annotations Associated annotations
	 * @param mediaType The media type to resolve
	 * @param map Parameter map
	 * @param outputStream The output stream to write to
	 * @throws IOException
	 * @throws WebApplicationException
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	@Override
	public void writeTo(String message, Class<?> clazz, Type type,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> map, OutputStream outputStream)
			throws IOException, WebApplicationException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", message);
		
		Viewable viewable = new Viewable("/error.jsp", model);
		
		final ResolvedViewable rv = resolve(viewable);
		
		if (rv == null) {
			throw new IOException("Unable to resolve html page for string");
		}
		rv.writeTo(outputStream);
	}
	
	/**
	 * resolve
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		21/12/2012	Genevieve Turner(GT)	Initial
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
