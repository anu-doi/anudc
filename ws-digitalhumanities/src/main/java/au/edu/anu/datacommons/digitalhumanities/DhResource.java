package au.edu.anu.datacommons.digitalhumanities;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class DhResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DhResource.class);
	
	@Context
	private UriInfo uriInfo;
	@Context
	private Request request;
	@Context
	private HttpHeaders httpHeaders;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetRequest()
	{
		Response resp = null;
		StringBuilder respStr = new StringBuilder();
		respStr.append("Test - Digital Humanities");
		resp = Response.ok(respStr.toString()).build();
		return resp;
	}
}
