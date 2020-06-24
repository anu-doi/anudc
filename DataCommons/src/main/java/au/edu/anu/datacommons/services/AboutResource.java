package au.edu.anu.datacommons.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;


@Path("/about")
@Component
public class AboutResource {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAbout() {
		return Response.ok(new Viewable("/about/about.jsp")).build();
	}
}

