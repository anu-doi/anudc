package au.edu.anu.datacommons.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.view.Viewable;

/**
 * ReloadResource
 * 
 * Australian National University Data Commons
 * 
 * Reload resources.  Currently just retrieves a page for reloading information
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("/reload")
public class ReloadResource {
	/**
	 * getReloadPage
	 *
	 * Get the web page for reloading resources
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getReloadPage() {
		return Response.ok(new Viewable("/reload.jsp")).build();
	}
}
