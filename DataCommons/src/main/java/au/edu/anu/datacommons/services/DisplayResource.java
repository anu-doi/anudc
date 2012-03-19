package au.edu.anu.datacommons.services;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DisplayResource
 * 
 * Australian National University Data Comons
 * 
 * Displays a page given given the specified data
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 */
@Path("/display")
public class DisplayResource {
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context UriInfo uriInfo;

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_XML)
	public String getItem(@PathParam("id") String objectId, @QueryParam("streamId") String streamId) {
		StringWriter writer = new StringWriter();
		//TODO This class is nowhere near final
		writer.append("<doc>");
		if(objectId != null){
			writer.append("<id>");
			writer.append(objectId);
			writer.append("</id>");
		}
		if (streamId != null) {
			writer.append("<streamId>");
			writer.append(streamId);
			writer.append("</streamId>");
		}
		writer.append("</doc>");
		
		return writer.toString();
	}
	
}
