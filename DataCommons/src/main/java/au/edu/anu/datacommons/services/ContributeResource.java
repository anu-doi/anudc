package au.edu.anu.datacommons.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;

@Path("/contribute")
@Component
public class ContributeResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getContribute() {
		return Response.ok(new Viewable("/contribute/contribute_home.jsp")).build();
	}
	
	@GET
	@Path("/data")
	@Produces(MediaType.TEXT_HTML)
	public Response getContributeData() {
		return Response.ok(new Viewable("/contribute/contribute_data.jsp")).build();
	}
}
