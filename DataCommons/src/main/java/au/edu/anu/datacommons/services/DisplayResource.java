package au.edu.anu.datacommons.services;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;

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
 * 0.6		28/06/2012	Rahul Khanna (RK)		Fixed failure condition
 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
 * 0.8		11/09/2012	Genevieve Turner (GT)	Updated to reject creation of groups when the user does not have permissions
 * 0.9		16/10/2012	Genevieve Turner(GT)	Fixed an issue with info:fedora being appended in linkItemAsText
 * 0.10		17/10/2012	Genevieve Turner (GT)	Updated to support a full page edit
 * </pre>
 */
@Component
@Scope("request")
@Path("/display")
public class DisplayResource
{
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context
	UriInfo uriInfo;

	@Resource(name = "fedoraObjectServiceImpl")
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
	 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * </pre>
	 * 
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param template
	 *            The template that determines the fields on the screen
	 * @param item
	 *            The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@GET
	@Path("{item}")
	@Produces(MediaType.TEXT_HTML)
	public Response getItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @PathParam("item") String item)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		Map<String, Object> values = fedoraObjectService.getViewPage(fedoraObject, layout, tmplt);

		Viewable viewable = new Viewable((String) values.remove("topage"), values);
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
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt
	 *            The template that determines the fields on the screen
	 * @param item
	 *            The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up
	 */
	@GET
	@Path("/new")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public Response newItemPage(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item,
			@QueryParam("error") String error)
	{
		Map<String, Object> values = fedoraObjectService.getNewPage(layout, tmplt);
		if (Util.isNotEmpty(error))
		{
			values.put("error", "Error saving item");
		}
		Viewable viewable = new Viewable((String) values.remove("topage"), values);
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
	 * 0.6		28/06/2012	Rahul Khanna (RK)		Fixed failure condition
	 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * 0.8		11/09/2012	Genevieve Turner (GT)	Updated to reject creation of groups when the user does not have permissions
	 * </pre>
	 * 
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt
	 *            The template that determines the fields on the screen
	 * @param item
	 *            The item to retrieve data for
	 * @param form
	 *            Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/new")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response postItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("item") String item,
			@Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject;
		Response resp;
		try
		{
			fedoraObject = fedoraObjectService.saveNew(tmplt, form);
			UriBuilder redirUri = UriBuilder.fromPath("/display").path(fedoraObject.getObject_id()).queryParam("layout", layout).queryParam("tmplt", tmplt);
			resp = Response.seeOther(redirUri.build()).build();
		}
		catch (FedoraClientException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return resp;
	}

	/**
	 * postItemAsText
	 * 
	 * Australian National University Data Commons
	 * 
	 * Creates a new Fedora Object. Returns a text response with the Pid of the created object.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		31/07/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param uriInfo
	 *            URI context
	 * @param layout
	 *            Layout
	 * @param tmplt
	 *            Template Id
	 * @param request
	 *            HTTPRequest context
	 * @return HTTP Response
	 */
	@POST
	@Path("new")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postItemAsText(@Context UriInfo uriInfo, @QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt,
			@Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		LOGGER.debug("Saving Fedora Object... layout: {}, tmplt: {}, form: {}", new Object[] { layout, tmplt, form.toString() });
		FedoraObject fedoraObject;
		Response resp = null;
		try
		{
			fedoraObject = fedoraObjectService.saveNew(tmplt, form);
			LOGGER.info("Created fedora object {}. Returning HTTP 301 response.", fedoraObject.getObject_id());
			URI createdUri = UriBuilder.fromUri(uriInfo.getBaseUri()).path(DisplayResource.class).path(DisplayResource.class, "getItem")
					.build(fedoraObject.getObject_id());
			resp = Response.created(createdUri).entity(fedoraObject.getObject_id()).build();
		}
		catch (FedoraClientException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return resp;
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
	 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * </pre>
	 * 
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt
	 *            The template that determines the fields on the screen
	 * @param item
	 *            The item to retrieve data for
	 * @param form
	 *            Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit/{item}/{fieldName}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public String getEditItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @PathParam("item") String item,
			@PathParam("fieldName") String fieldName)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
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
	 * 0.10		17/10/2012	Genevieve Turner (GT)	Updated to support a full page edit
	 * </pre>
	 * 
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt
	 *            The template that determines the fields on the screen
	 * @param item
	 *            The item to retrieve data for
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@GET
	@Path("/edit/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public Response editItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @QueryParam("style") String style, @PathParam("item") String item)
	{
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		Map<String, Object> values = null;
		if ("full".equals(style)) {
			values = fedoraObjectService.getEditPage(fedoraObject, "def:new", tmplt);
			values.remove("sidepage");
		}
		else {
			values = fedoraObjectService.getEditPage(fedoraObject, layout, tmplt);
		}
		
		//Map<String, Object> values = fedoraObjectService.getEditPage(fedoraObject, layout, tmplt);
		Viewable viewable = new Viewable((String) values.remove("topage"), values);

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
	 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * 0.10		17/10/2012	Genevieve Turner (GT)	Updated to support a full page edit
	 * </pre>
	 * 
	 * @param layout
	 *            The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt
	 *            The template that determines the fields on the screen
	 * @param pid
	 *            The item to retrieve data for
	 * @param form
	 *            Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/edit/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response editChangeItem(@QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, 
			@QueryParam("style") String style, @PathParam("item") String pid,
			@Context HttpServletRequest request)
	{
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);

		LOGGER.info("tmplt: {}", tmplt);

		Map<String, Object> values = fedoraObjectService.saveEdit(fedoraObject, tmplt, form);
		UriBuilder uriBuilder = null;
		if (values.containsKey("error"))
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		else
		{
			uriBuilder = UriBuilder.fromPath("/display/edit").path(pid).queryParam("layout", layout);
			if (Util.isNotEmpty(tmplt))
			{
				uriBuilder = uriBuilder.queryParam("tmplt", tmplt);
			}
			if (Util.isNotEmpty(style)) {
				uriBuilder = uriBuilder.queryParam("style", style);
			}
		}
		return Response.seeOther(uriBuilder.build()).build();
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
	 * 0.7		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * </pre>
	 * 
	 * @param item
	 *            The item to retrieve data for
	 * @param form
	 *            Form data that has been posted
	 * @return Returns the viewable for the jsp file to pick up.
	 */
	@POST
	@Path("/addLink/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	public String addLink(@PathParam("item") String item, @Context HttpServletRequest request)
	{
		String value;
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		try
		{
			fedoraObjectService.addLink(fedoraObject, form.get("linkType").get(0), form.get("itemId").get(0));
			value = "<html><body>Reference added</body></html>";
		}
		catch (FedoraClientException e)
		{
			LOGGER.error(e.getMessage(), e);
			value = "<html><body>Exception adding reference</body></html>";
		}
		return value;
	}

	/**
	 * addLinkAsText
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		XX/XX/2012	Rahul Khanna (RK)		Initial
	 * 0.9		16/10/2012	Genevieve Turner(GT)	Fixed an issue with info:fedora being appended
	 * </pre>
	 * 
	 * @param relPid Related pid
	 * @param linkType The relation type
	 * @param itemId The related item id
	 * @return
	 */
	@POST
	@Path("/addLink/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addLinkAsText(@PathParam("item") String relPid, @FormParam("linkType") String linkType, @FormParam("itemId") String itemId)
	{
		Response resp = null;
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(relPid);
		try
		{
			if (fedoraObject == null)
				throw new IllegalArgumentException(MessageFormat.format("No object exists for the pid {0}", relPid));
			if (linkType == null || linkType.length() == 0)
				throw new IllegalArgumentException("linkType not provided.");
			if (itemId == null || itemId.length() == 0)
				throw new IllegalArgumentException("itemId not provided.");
			String nsPrefix = "info:fedora/";
			if (!itemId.toLowerCase().startsWith("http://") && !itemId.toLowerCase().startsWith(nsPrefix)) {
				itemId = MessageFormat.format("{0}{1}", nsPrefix, itemId);
			}
			fedoraObjectService.addLink(fedoraObject, linkType, itemId);
			resp = Response.ok("OK", MediaType.TEXT_PLAIN_TYPE).build();
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		catch (FedoraClientException e)
		{
			LOGGER.error(e.getMessage(), e);
			resp = Response.serverError().build();
		}
		return resp;
	}
}
