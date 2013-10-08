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

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sun.jersey.api.view.Viewable;

/**
 * @author Rahul Khanna
 *
 */
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedExceptionMapper.class);
	
	@Context
	private HttpHeaders headers;
	
	@Override
	public Response toResponse(AccessDeniedException exception) {
		Response resp;
		LOGGER.warn("User {} requested a resource to which they don't have access: {}", getCurUsername(), exception.getMessage());
		List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
		if (acceptableMediaTypes.contains(MediaType.TEXT_HTML_TYPE)) {
			resp = Response.status(Status.UNAUTHORIZED).entity(new Viewable("/login_select.jsp")).build();
		} else {
			resp = Response.status(Status.UNAUTHORIZED).entity(exception.getMessage()).build();
		}
		return resp;
	}

	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
