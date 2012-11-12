package au.edu.anu.datacommons.publish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.services.ListResource;
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
 * 0.2		08/06/2012	Genevieve Turner (GT)	Fixed issue with an exception when publish button is clicked and no optiosn have been selected
 * 0.3		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
 * 0.4		19/09/2012	Genevieve Turner (GT)	Updated to redirect to the display page after publishing
 * 0.5		27/09/2012	Genevieve Turner (GT)	Updated to redirect to display page rather than edit
 * 0.6		15/10/2012	Genevieve Turner (GT)	Added the availablity of validation checks
 * </pre>
 * 
 */
@Component
@Scope("request")
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
	 * 0.3		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * </pre>
	 * 
	 * @return The web page for publishers
	 */
	@GET
	@Path("{item}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getPublishers() {
		LOGGER.info("In get publishers");
		
		List<PublishLocation> publishLocations = fedoraObjectService.getPublishers();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("publishLocations", publishLocations);
		
		Viewable viewable = new Viewable("/publish.jsp", model);
		
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
	 * 0.2		08/06/2012	Genevieve Turner (GT)	Fixed issue with an exception when publish button is clicked and no optiosn have been selected
	 * 0.3		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * 0.4		19/09/2012	Genevieve Turner (GT)	Updated to redirect to the display page
	 * 0.5		27/09/2012	Genevieve Turner (GT)	Updated to redirect to display page rather than edit
	 * </pre>
	 * 
	 * @param item The item to publish
	 * @param request The http request that is occuring
	 * @return The web page for publishers.
	 */
	@POST
	@Path("{item}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response savePublishers(@PathParam("item") String item, @QueryParam("layout") String layout, 
			@QueryParam("tmplt") String tmplt, @Context HttpServletRequest request) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> publishers = form.get("publish");
		LOGGER.debug("Locations to publish to: {}", publishers);
		String message = "No publishers selected";
		if (publishers != null && publishers.size() > 0) {
			message = fedoraObjectService.publish(fedoraObject, publishers);
		}
		
		List<PublishLocation> publishLocations = fedoraObjectService.getPublishers();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("publishLocations", publishLocations);
		model.put("message", message);
		
		UriBuilder uriBuilder = UriBuilder.fromPath("/display").path(item).queryParam("layout", layout);
		if (Util.isNotEmpty(tmplt)) {
			uriBuilder = uriBuilder.queryParam("tmplt", tmplt);
		}
		return Response.seeOther(uriBuilder.build()).build();
	}
	
	@GET
	@Path("/mintdoi/{pid}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response generateDoi(@PathParam("pid") String pid, @QueryParam("tmplt") String tmplt, @Context UriInfo uriInfo)
	{
		Response resp = null;

		try
		{
			fedoraObjectService.generateDoi(pid, tmplt, null);
			UriBuilder redirUri = UriBuilder.fromPath("/display").path(pid).queryParam("layout", "def:display").queryParam("tmplt", tmplt);
			resp = Response.seeOther(redirUri.build()).build();
		}
		catch (Exception e)
		{
			LOGGER.error("DOI Minting failed.", e);
			resp = Response.status(500).entity(e.getMessage()).build();
		}

		return resp;
	}
	
	/**
	 * getValidationCheckScreen
	 *
	 * Retrieve the validation page.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param item The item to check validation on
	 * @return The validation page
	 */
	@GET
	@Path("validate/{item}")
	public Response getValidationCheckScreen(@PathParam("item") String item) {
		List<PublishLocation> publishLocations = fedoraObjectService.getPublishers();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("publishLocations", publishLocations);
		
		Viewable viewable = new Viewable("/validate.jsp", model);
		
		return Response.ok(viewable).build();
	}
	
	/**
	 * validateItem
	 *
	 * Check the  validation and verify the page
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.6		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param item The item to check validation on
	 * @param request The request information
	 * @return The validation reponse page
	 */
	@POST
	@Path("validate/{item}")
	public Response validateItem(@PathParam("item") String item, @Context HttpServletRequest request) {
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> publishers = form.get("publish");

		Map<String, Object> model = new HashMap<String, Object>();
		
		if (publishers.size() > 0) {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
			List<String> messages = fedoraObjectService.validatePublishLocation(fedoraObject, publishers);
			model.put("validateMessages", messages);
		}
		else {
			throw new WebApplicationException(Response.status(400).entity("No publish location specified").build());
		}
		
		List<PublishLocation> publishLocations = fedoraObjectService.getPublishers();
		
		model.put("publishLocations", publishLocations);
		
		Viewable viewable = new Viewable("/validate.jsp", model);
		
		return Response.ok(viewable).build();
	}
}
