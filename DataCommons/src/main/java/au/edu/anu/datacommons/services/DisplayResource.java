/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.services;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;
import com.yourmediashelf.fedora.client.FedoraClientException;

import au.edu.anu.datacommons.comparator.TemplateAttributeSortByDisplay;
import au.edu.anu.datacommons.comparator.TemplateAttributeSortByTab;
import au.edu.anu.datacommons.comparator.TemplateTabSortByTabOrder;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Template;
import au.edu.anu.datacommons.exception.DataCommonsException;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.freemarker.GroupOptions;
import au.edu.anu.datacommons.freemarker.SecurityCheck;
import au.edu.anu.datacommons.freemarker.SelectOptions;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.sparql.Result;
import au.edu.anu.datacommons.xml.sparql.ResultItem;

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
 * 0.11		22/10/2012	Genevieve Turner (GT)	Updates to allow deletion/edit of relationships
 * 0.12		12/11/2012	Genevieve Turner (GT)	Updated to with the request id fields of null
 * 0.13		13/11/2012	Genevieve Turner (GT)	Updated the retrieval of the edit page with the mode.
 * 0.14		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
 * 0.15		06/03/2013	Genevieve Turner (GT)	Updated to remove then add links in editLink
 * </pre>
 */
@Component
@Scope("request")
@Path("/display")
public class DisplayResource
{
	static final Logger LOGGER = LoggerFactory.getLogger(DisplayResource.class);

	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest request;

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
		LOGGER.info("User {} ({}) requested record page of {}", getCurUsername(), request.getRemoteAddr(), item);

		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		if (fedoraObject == null) {
			return Response.status(Status.NOT_FOUND).entity("Item not found").build();
		}
		try {
			Template template = fedoraObjectService.getTemplateByTemplateId(fedoraObject.getTmplt_id());
			
			template.getTemplateAttributes().sort(new TemplateAttributeSortByDisplay());
			
			Data editData = null;
			Data publishData = null;
			try {
				editData = fedoraObjectService.getEditData(fedoraObject);
			}
			catch (Exception e) {
				LOGGER.error("Exception retrieving edit data", e);
			}
			try {
				publishData = fedoraObjectService.getPublishData(fedoraObject);
			}
			catch (Exception e) {
				LOGGER.error("Exception retrieving publish data: {}", e.getMessage());
			}
			if (editData == null && publishData == null) {
				throw new AccessDeniedException("You do not have access to this resource");
			}
			Map<String, Object> values = new HashMap<String, Object>();
			if (publishData == null) {
				values.put("data", editData);
				
			}
			else {
				values.put("data", publishData);
				Data differences = fedoraObjectService.getDataDifferences(template, editData, publishData);
				values.put("differenceData", differences);
				
			}
			RecordDataSummary rdi = fedoraObjectService.getRecordDataSummary(fedoraObject);
			List<Result> links = fedoraObjectService.getLinks(fedoraObject);
			
			values.put("tmplt", template);
			values.put("item", fedoraObject);
			values.put("rdi", rdi);
			values.put("links", links);
			values.put("options", new SelectOptions());
			values.put("groups", new GroupOptions());
			values.put("security",new SecurityCheck(fedoraObject));
			
			Viewable viewable = new Viewable("/display/display.ftl", values);
			return Response.ok(viewable).build();
		}
		catch (JAXBException | FedoraClientException e) {
			LOGGER.error("Exception retrieing template", e);
		}
		return Response.serverError().build();
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
			@QueryParam("error") String error, @QueryParam("type") String type)
	{
		LOGGER.info("In GET method for new");
		try {
			Template template = fedoraObjectService.getTemplateByTemplateId(tmplt);
			Data data = fedoraObjectService.getInitialData(template);
			
			Collections.sort(template.getTemplateAttributes(), new TemplateAttributeSortByTab());
			template.getTemplateTabs().sort(new TemplateTabSortByTabOrder());
			
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("tmplt", template);
			values.put("options", new SelectOptions());
			values.put("groups", new GroupOptions());
			values.put("data", data);
			values.put("security",new SecurityCheck());
			Viewable viewable = new Viewable("/form/new.ftl", values);
			return Response.ok(viewable).build();
		}
		catch (JAXBException | FedoraClientException e) {
			LOGGER.error("Exception retrieving template", e);
			return Response.serverError().build();
		}
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
	 * 0.12		12/11/2012	Genevieve Turner (GT)	Updated to with the request id fields of null
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
		LOGGER.info("In post method for new");
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject;
		Response resp;
		try
		{
			fedoraObject = fedoraObjectService.saveNew(tmplt, form, null);
			LOGGER.info("User {} created record {} of group {} through Web UI.", getCurUsername(),
					fedoraObject.getObject_id(), fedoraObject.getGroup_id());
			UriBuilder redirUri = UriBuilder.fromPath("/display").path(fedoraObject.getObject_id()).queryParam("layout", layout).queryParam("tmplt", tmplt);
			resp = Response.seeOther(redirUri.build()).build();
		}
		catch (FedoraClientException | JAXBException | IOException e)
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
	 * 0.12		12/11/2012	Genevieve Turner (GT)	Updated to with the request id fields of null
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
	public Response postItemAsText(@Context UriInfo uriInfo, @QueryParam("layout") String layout,
			@QueryParam("tmplt") String tmplt, @Context HttpServletRequest request) {
		LOGGER.info("In POST method for new");
		
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject;
		Response resp = null;
		try {
			fedoraObject = fedoraObjectService.saveNew(tmplt, form, null);
			LOGGER.info("User {} created record {} of group {} through Web Service. Returning HTTP 201 response.", getCurUsername(),
					fedoraObject.getObject_id(), fedoraObject.getGroup_id());
			URI createdUri = UriBuilder.fromUri(uriInfo.getBaseUri()).path(DisplayResource.class)
					.path(DisplayResource.class, "getItem").build(fedoraObject.getObject_id());
			resp = Response.created(createdUri).entity(fedoraObject.getObject_id()).build();
		} catch (FedoraClientException | JAXBException | IOException e) {
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
	 * 0.14		21/12/2012	Genevieve Turner (GT)	Updated so that text/plain is produced rather than text/html so that a page is not rendered
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
	@Produces(MediaType.TEXT_PLAIN)
	@Deprecated
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
	 * 0.13		13/11/2012	Genevieve Turner (GT)	Updated with whether the page should be found via edit mode to fix a bug with full page edits
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
		LOGGER.debug("In editItem get");
		try {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
			
			Template template = fedoraObjectService.getTemplateByTemplateId(fedoraObject.getTmplt_id());
			
			template.getTemplateAttributes().sort(new TemplateAttributeSortByTab());
			template.getTemplateTabs().sort(new TemplateTabSortByTabOrder());
			
			Data itemData = fedoraObjectService.getEditData(fedoraObject);
			
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("tmplt", template);
			values.put("item", fedoraObject);
			values.put("data", itemData);
			values.put("options", new SelectOptions());
			values.put("groups", new GroupOptions());
			values.put("security",new SecurityCheck(fedoraObject));
			Viewable viewable = new Viewable("/form/edit.ftl", values);
			return Response.ok(viewable).build();
		}
		catch (JAXBException | FedoraClientException e) {
			LOGGER.error("Exception retrieving template", e);
			return Response.serverError().build();
		}
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
	 * 0.12		12/11/2012	Genevieve Turner (GT)	Updated to with the request id fields of null
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
		LOGGER.debug("In editChangeItem post");
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);

		LOGGER.info("User {} edited record {}", getCurUsername(), pid);
		
		try {
			fedoraObjectService.saveEdit(fedoraObject, tmplt, form, null);
			UriBuilder uriBuilder = UriBuilder.fromPath("/display/edit").path(pid);
			
			if (Util.isNotEmpty(tmplt))
			{
				uriBuilder = uriBuilder.queryParam("tmplt", tmplt);
			}
			if (Util.isNotEmpty(style)) {
				uriBuilder = uriBuilder.queryParam("style", style);
			}
			return Response.seeOther(uriBuilder.build()).build();
		}
		catch (JAXBException | FedoraClientException | IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * <p>Delete the item with the given pid.</p>
	 * <p>Note: a post method is required as not all browsers support the delete http method for buttons on html forms</p>
	 * 
	 * @param pid The pid of the item to delete
	 * @param layout THe layout
	 * @return The response
	 */
	@POST
	@Path("/delete/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response deleteItemPost(@PathParam("item") String pid, 
			@QueryParam("layout") @DefaultValue("def:display") String layout) {
		return deleteItem(pid, layout);
	}
	
	/**
	 * <p>Delete the item with the given pid.</p>
	 * 
	 * @param pid The pid of the item to delete
	 * @param layout THe layout
	 * @return The response
	 */
	@DELETE
	@Path("/delete/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response deleteItemDelete(@PathParam("item") String pid, 
			@QueryParam("layout") @DefaultValue("def:display") String layout) {
		return deleteItem(pid, layout);
	}
	
	/**
	 * Delete the item with the given pid
	 * 
	 * @param pid The pid of the item to delete
	 * @param layout The layout to return the user to
	 * @return
	 */
	private Response deleteItem(String pid, String layout) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		LOGGER.info("Requested to delete item: {}", fedoraObject.getObject_id());
		try {
			fedoraObjectService.delete(fedoraObject);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception deleting object", e);
			throw new DataCommonsException(Status.INTERNAL_SERVER_ERROR, "Exception deleting object");
		}
		UriBuilder uriBuilder = UriBuilder.fromPath("/display/").path(pid).queryParam("layout", layout);
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
	 * 0.11		XX/XX/2012	Rahul Khanna (RK)		Initial
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
		
//		String itemPrefix = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE);
		
		try
		{
			if (fedoraObject == null)
				throw new IllegalArgumentException(MessageFormat.format("No object exists for the pid {0}", relPid));
			if (linkType == null || linkType.length() == 0)
				throw new IllegalArgumentException("linkType not provided.");
			if (itemId == null || itemId.length() == 0)
				throw new IllegalArgumentException("itemId not provided.");
//			String nsPrefix = "info:fedora/";
//			if (itemId.toLowerCase().startsWith(itemPrefix)) {
//				itemId = MessageFormat.format("{0}{1}", nsPrefix, itemId);
//			}
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
	
	/**
	 * editLink
	 *
	 * Edits the link assocated associated with records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.11		22/10/2012	Genevieve Turner(GT)	Initial
	 * 0.14		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * 0.15		06/03/2013	Genevieve Turner (GT)	Updated to remove then add links in editLink
	 * </pre>
	 * 
	 * @param item The item to edit the links for
	 * @param linkType The link type to set
	 * @param removeLinkType The link type to remove
	 * @param itemId The item to link to (this may be another item in fedora or an external identifying link)
	 * @return The response
	 */
	@POST
	@Path("/editLink/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String editLink(@PathParam("item") String item, @FormParam("linkType") String linkType, 
			@FormParam("removeLinkType") String removeLinkType, @FormParam("itemId") String itemId) {
		if (linkType == null) {
			throw new ValidateException("The relationship type is missing");
		}
		if (itemId == null) {
			throw new ValidateException("The item to relate to is missing");
		}
		
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		
		try {
			fedoraObjectService.removeLink(fedoraObject, removeLinkType, itemId);
			fedoraObjectService.addLink(fedoraObject, linkType, itemId);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception removing link", e);
			throw new DataCommonsException(500, "Exception editing the relationship");
		}
		
		return "Referenced Changed";
	}

	/**
	 * removeLink
	 *
	 * Remove the link associated with an item
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.11		22/10/2012	Genevieve Turner(GT)	Initial
	 * 0.14		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param item The item to edit the links for
	 * @param linkType The link type to remove
	 * @param itemId The item to remove the link/relationship with
	 * @return The response
	 */
	@POST
	@Path("/removeLink/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeLink(@PathParam("item") String item, @FormParam("linkType") String linkType, 
			@FormParam("itemId") String itemId) {
		if (linkType == null) {
			throw new ValidateException("The relationship type is missing");
		}
		if (itemId == null) {
			throw new ValidateException("The item to relate to is missing");
		}
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		
		try {
			fedoraObjectService.removeLink(fedoraObject, linkType, itemId);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception removing link", e);

			throw new DataCommonsException(500, "Exception editing the relationship");
		}
		
		return "Reference Removed";
	}
	
	/**
	 * getLinks
	 *
	 * Retrieves the links
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.11		22/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param item The item to get the links for
	 * @param request The request called
	 * @return The response
	 */
	@GET
	@Path("/getLinks/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLinks(@PathParam("item") String item, @Context HttpServletRequest request) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		List<Result> resultList = fedoraObjectService.getLinks(fedoraObject);
		
		JSONArray resultArray = new JSONArray();
		JSONObject results = new JSONObject();
		try {
			for (Result result : resultList) {
				JSONObject resultObject = new JSONObject();
				for (Entry<String, ResultItem> entry : result.getFields().entrySet()) {
					resultObject.put(entry.getKey(), entry.getValue().getValue());
				}
				resultArray.put(resultObject);
			}
			results.put("results", resultArray);
		}
		catch (JSONException e) {
			throw new WebApplicationException(Response.status(400).entity("Error creating return").build());
		}
		return Response.ok(results).build();
	}

	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
