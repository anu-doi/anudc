package au.edu.anu.datacommons.services;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * FedoraObjectService
 * 
 * Australian National University Data Commons
 * 
 * Resource for publishing objects to various sources such as ANU and ANDS
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
@Component
@Path("publish")
public class PublishResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ListResource.class);

	@Resource(name="fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The web page for publishers
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getPublishers() {
		LOGGER.info("In get publishers");
		Viewable viewable = fedoraObjectService.getPublishers();
		return Response.ok(viewable).build();
	}
	
	/**
	 * savePublishers
	 * 
	 * Publishes the object to the given list of publishers
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param item The item to publish
	 * @param request The http request that is occuring
	 * @return The web page for publishers.
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response savePublishers(@QueryParam("item") String item, @Context HttpServletRequest request) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> publishers = form.get("publish");
		LOGGER.info("Publisher stuff: {}", publishers);
		
		fedoraObjectService.publish(fedoraObject, publishers);
		Viewable viewable = fedoraObjectService.getPublishers();
		return Response.ok(viewable).build();
	}
}
