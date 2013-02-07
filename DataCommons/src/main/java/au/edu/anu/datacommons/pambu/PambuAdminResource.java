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

package au.edu.anu.datacommons.pambu;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.view.Viewable;

/**
 * PambuAdminResource
 * 
 * Australian National University Data Commons
 * 
 * Resource for PAMBU administration functions.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		14/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("/pambu/admin")
@Component
@Scope("request")
public class PambuAdminResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PambuAdminResource.class);
	
	/**
	 * getPambuAdminPage
	 *
	 * Displays the pambu administration page.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		14/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response getPambuAdminPage() {
		Response response = null;
		
		response = Response.ok(new Viewable("/pambu/pambuadmin.jsp")).build();
		
		return response;
	}
	
	/**
	 * generateIndex
	 *
	 * Currently this only displays a list of links for the PAMBU web crawler.
	 * It is intended that eventually this will generate a page and place it on
	 * the PAMBU website
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		14/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param context Uri information
	 * @return Response from the action
	 */
	@GET
	@Path("/index")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response generateIndex(@Context UriInfo context) {
		Response response = null;
		
		//context.getBaseUriBuilder();
		//LOGGER.info("Base URI: {}", context.getBaseUri().getRawPath());
		//LOGGER.info("Base URI: {}", context.getAbsolutePath().getRawPath());
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uri = context.getBaseUriBuilder().path(PambuSearchResource.class);
		uri.path("/published");
		
		WebResource webResource = client.resource(uri.build());
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML).get(ClientResponse.class);
		response = Response.ok(clientResponse.getEntityInputStream()).build();
		
		return response;
	}
}
