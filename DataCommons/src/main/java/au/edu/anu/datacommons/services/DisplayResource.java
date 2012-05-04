package au.edu.anu.datacommons.services;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * DisplayResource
 * 
 * Australian National University Data Comons
 * 
 * Displays a page given given the specified data
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/03/2012	Genevieve Turner (GT)	Updating to return a page
 * 0.3		23/03/2012	Genevieve Turner (GT)	Updated to include saving of new records and a side page
 * 0.4		29/03/2012	Genevieve Turner (GT)	Updated to include editing
 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
 * </pre>
 */
@Component
@Path("/display")
public class DisplayResource {
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context UriInfo uriInfo;

	@Resource(name="fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;
	
	/**
	 * getItem
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		19/03/2012	Genevieve Turner (GT)	Updating to return a page
	 * 0.3		23/03/2012	Genevieve Turner (GT)	Updated to contain information about the side page
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		Viewable viewable = fedoraObjectService.getViewPage(fedoraObject, layout, tmplt);

		return Response.ok(viewable).build();
	}
	
	/**
	 * newItemPage
	 * 
	 * Gets a page for creating a new fedora object
	 * 
	 * <pre>
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@GET
	@Path("/new")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public Response newItemPage(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item)
	{
		Viewable viewable = fedoraObjectService.getNewPage(layout, tmplt);
		return Response.ok(viewable).build();
	}
	
	/**
	 * postItem
	 * 
	 * Saves a new fedora object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		20/03/2012	Genevieve Turner (GT)	Added as a placeholder
	 * 0.3		23/03/2012	Genevieve Turner (GT)	Modified to save data
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/new")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response postItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item, @Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		Viewable viewable = fedoraObjectService.saveNew(layout, tmplt, form);
		return Response.ok(viewable).build();
	}
	
	/**
	 * editItem
	 * 
	 * Return a transformed page for a single field type.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit/{fieldName}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public String editItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item, @PathParam("fieldName") String fieldName)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		String fields = fedoraObjectService.getEditItem(fedoraObject, layout, tmplt, fieldName);
		return fields;
	}

	/**
	 * editItem
	 * 
	 * Gets a page given the specified parameters, that is enabled for editing.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public Response editItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		Viewable viewable = fedoraObjectService.getEditPage(fedoraObject, layout, tmplt);

		return Response.ok(viewable).build();
	}
	
	/**
	 * editChangeItem
	 * 
	 * Updates the object given the specified parameters, and form values.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/edit")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response editChangeItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item, @Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		
		Viewable viewable = fedoraObjectService.saveEdit(fedoraObject, layout, tmplt, form);
		
		return Response.ok(viewable).build();
	}
	
	/**
	 * addLink
	 * 
	 * Add a link to the specified object.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		29/03/2012	Genevieve Turner (GT)	Added
	 * 0.5		26/04/2012	Genevieve Turner (GT)	Updated for security
	 * </pre>
	 * 
	 * @param item The item to retrieve data for
	 * @param form Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/addLink")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public String addLink(@QueryParam("item") String item, @Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject = fedoraObjectService.getItemByName(item);
		String value = fedoraObjectService.addLink(fedoraObject, form);
		return value;
	}
	
}
