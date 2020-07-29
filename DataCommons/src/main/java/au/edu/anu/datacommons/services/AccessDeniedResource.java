package au.edu.anu.datacommons.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;

@Component
@Scope("request")
@Path("/access-denied")
public class AccessDeniedResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAccessDeniedPageHtml() {
		return Response.status(Status.UNAUTHORIZED).entity(new Viewable("/accessdenied.jsp")).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccessDeniedPageJson() {
		return Response.status(Status.UNAUTHORIZED).entity("{\"error\":\"You do not have permission to access this feature\"}").build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response getAccessDeniedPageXml() {
		return Response.status(Status.UNAUTHORIZED).entity("{<error>You do not have permission to access this feature</error>").build();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAccessDeniedPageText() {
		return Response.status(Status.UNAUTHORIZED).entity("You do not have permission to access this feature").build();
	}
}
