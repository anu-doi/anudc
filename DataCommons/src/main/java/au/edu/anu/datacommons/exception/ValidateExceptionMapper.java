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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;

import com.sun.jersey.api.view.Viewable;

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
		Response resp;
		if (headers.getAcceptableMediaTypes().contains(MediaType.TEXT_HTML_TYPE)) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("messages", e.getMessages());
			Viewable viewable = new Viewable("/error.jsp", model);
			resp = Response.status(Status.BAD_REQUEST).entity(viewable).build();
		} else {
			resp = Response.status(Status.BAD_REQUEST).entity(convertListToString(e.getMessages())).build();
		}
		return resp;
	}
	
	private String convertListToString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
			sb.append(Config.NEWLINE);
		}
		return sb.toString();
	}
}
