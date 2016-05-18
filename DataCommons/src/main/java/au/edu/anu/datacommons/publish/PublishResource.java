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

package au.edu.anu.datacommons.publish;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.HashMap;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.PublishLocation;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.publish.service.LocationValidationMessage;
import au.edu.anu.datacommons.publish.service.PublishService;
import au.edu.anu.datacommons.security.acl.CustomACLPermission;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.services.ListResource;
import au.edu.anu.datacommons.util.Util;

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
 * 0.7		10/12/2012	Genevieve Turner (GT)	Added mass publication and validation functionality, also moved some functions from the fedora object service to the publish service
 * 0.8		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
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
	
	@Resource(name="publishServiceImpl")
	private PublishService publishService;
	
	@Resource(name="permissionService")
	private PermissionService permissionService;
	
	/**
	 * getPublishers
	 * 
	 * Returns a list of publishers.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial
	 * 0.3		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
	 * 0.7		10/12/2012	Genevieve Turner (GT)	Updated to use publishService rather htan fedoraObjectService
	 * </pre>
	 * 
	 * @return The web page for publishers
	 */
	@GET
	@Path("{item}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getPublishers(@PathParam("item") String pid) {
		LOGGER.info("User {} requested publish location selection page.", getCurUsername());
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		
		List<PublishLocation> publishLocations = publishService.getPublishers(fedoraObject);
		
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
	 * 0.7		10/12/2012	Genevieve Turner (GT)	Updated to use publishService rather than fedoraObjectService
	 * 0.8		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
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
	public Response publishItem(@PathParam("item") String item, @QueryParam("layout") String layout, 
			@QueryParam("tmplt") String tmplt, @Context HttpServletRequest request) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
		
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> publishers = form.get("publish");
		LOGGER.debug("Locations to publish to: {}", publishers);
		
		if (publishers != null && publishers.size() > 0) {
			publishService.publish(fedoraObject, publishers);
		}
		
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
		UriBuilder redirUri = UriBuilder.fromPath("/display").path(pid).queryParam("layout", "def:display").queryParam("tmplt", tmplt);
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		if (!permissionService.checkPermission(fedoraObject, CustomACLPermission.PUBLISH)) {
			throw new AccessDeniedException(format("User does not have Publish permissions for {0}.", pid));
		}

		try
		{
			LOGGER.info("User {} requested a DOI to be minted for {}", getCurUsername(), pid);
			fedoraObjectService.generateDoi(pid, tmplt, null);
			resp = Response.seeOther(redirUri.build()).build();
		}
		catch (Exception e)
		{
			LOGGER.error("DOI Minting failed for " + pid, e);
			resp = Response.seeOther(redirUri.queryParam("emsg", e.getMessage()).build()).build();
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
	 * 0.7		11/12/2012	Genevieve Turner (GT)	Updated to use the publishing service
	 * </pre>
	 * 
	 * @param item The item to check validation on
	 * @return The validation page
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("validate/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getValidationCheckScreen(@PathParam("item") String item) {
		List<PublishLocation> publishLocations = publishService.getPublishers();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("publishLocations", publishLocations);
		model.put("item", item);
		
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
	 * 0.7		10/12/2012	Genevieve Turner (GT)	Updated to use publishService rather htan fedoraObjectService
	 * 0.8		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling and fixed a null pointer exception if no publishers had been selected
	 * </pre>
	 * 
	 * @param item The item to check validation on
	 * @param request The request information
	 * @return The validation reponse page
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Path("validate/{item}")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response validateItem(@PathParam("item") String item, @Context HttpServletRequest request) {
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> publishers = form.get("publish");

		Map<String, Object> model = new HashMap<String, Object>();
		
		if (publishers != null && publishers.size() > 0) {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(item);
			List<String> messages = publishService.validatePublishLocation(fedoraObject, publishers);
			model.put("validateMessages", messages);
		}
		else {
			throw new ValidateException("No publish location specified");
		}
		
		List<PublishLocation> publishLocations = publishService.getPublishers();
		
		model.put("publishLocations", publishLocations);
		model.put("item", item);
		
		Viewable viewable = new Viewable("/validate.jsp", model);
		
		return Response.ok(viewable).build();
	}
	
	/**
	 * getMultipleItemValidationScreen
	 *
	 * Retrieve the page to select items and locations to validate against
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groupId The group to find records for
	 * @param page The page number to get records for
	 * @return A page that contains groups that the user can validate for, also locations and items that they can validate
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("validate/multiple")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getMultipleItemValidationScreen(@QueryParam("group") Long groupId, @QueryParam("page") Integer page) {
		List<Groups> groups = publishService.getValidationGroups();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("groups", groups);
		
		if (groupId != null) {
			List<PublishLocation> publishers = publishService.getPublishers();
			model.put("publishers", publishers);
			try {
				SolrSearchResult results = publishService.getGroupObjects(groupId, page);
				model.put("results", results);
			}
			catch (SolrServerException | IOException e) {
				LOGGER.error("Exception querying solr", e);
			}
		}
		
		return Response.ok(new Viewable("/validate_multiple.jsp", model)).build();
	}
	
	/**
	 * executeMultipleItemValidation
	 *
	 * Perform validation against multiple records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.8		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
	 * </pre>
	 * 
	 * @param request The http request information
	 * @return A page containing the validated records
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("validate/multiple")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response executeMultipleItemValidation(@Context HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		String[] ids = request.getParameterValues("ids");
		String[] publishLocations = request.getParameterValues("publishLocation");

		if (publishLocations == null || publishLocations.length == 0 || ids == null || ids.length == 0) {
			throw new ValidateException("You must select at least one publish location and one record");
		}
		
		try {
			Map<String, List<LocationValidationMessage>> messages = publishService.validateMultiple(publishLocations, ids);
			model.put("validationMessages", messages);
		}
		catch (Exception e) {
			LOGGER.error("Exception validating messages", e);
			throw new ValidateException("Exception performing validation on the selected records");
		}
		
		try {
			SolrSearchResult information = publishService.getItemInformation(ids);
			model.put("information", information);
		}
		catch (SolrServerException | IOException e) {
			LOGGER.error("Error searching solr", e);
		}
		
		return Response.ok(new Viewable("/validate_display.jsp", model)).build();
	}
	
	/**
	 * getMultipleItemPublishScreen
	 *
	 * Retrieves the screen for records available publishing records
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groupId The group to find items to publish for
	 * @param page The page number of the items
	 * @return A page that contains the groups, locations and items the user can publish
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("multiple")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getMultipleItemPublishScreen(@QueryParam("group") Long groupId, @QueryParam("page") Integer page) {
		List<Groups> groups = publishService.getMultiplePublishGroups();
		LOGGER.info("Number of groups: {}", groups.size());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("groups", groups);
		
		if (groupId != null) {
			List<PublishLocation> publishers = publishService.getPublishers();
			model.put("publishers", publishers);
			try {
				SolrSearchResult results = publishService.getGroupObjects(groupId, page);
				model.put("results", results);
			}
			catch (SolrServerException | IOException e) {
				LOGGER.error("Exception querying solr", e);
			}
		}
		
		return Response.ok(new Viewable("/publish_multiple.jsp", model)).build();
	}
	
	/**
	 * executeMultiplePublish
	 *
	 * Publish the selected items to the given locations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		10/12/2012	Genevieve Turner(GT)	Initial
	 * 0.8		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
	 * </pre>
	 * 
	 * @param request The http request
	 * @return A page indicating whether the items have been successfully published
	 */
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("multiple")
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response executeMultiplePublish(@Context HttpServletRequest request, @QueryParam("groupId") Long id) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		String[] ids = request.getParameterValues("ids");
		String[] publishLocations = request.getParameterValues("publishLocation");

		if (publishLocations == null || publishLocations.length == 0 || ids == null || ids.length == 0) {
			throw new ValidateException("At least one publish location and record must be selected");
		}
		
		Map<String, String> published = publishService.publishMultiple(ids, publishLocations);
		if (published == null) {
			return Response.status(400).entity(new Viewable("/publish_multiple_validation_error.jsp", model)).build();
		}
		model.put("published", published);
		try {
			SolrSearchResult information = publishService.getItemInformation(ids);
			model.put("information", information);
		}
		catch (SolrServerException | IOException e) {
			LOGGER.error("Error searching solr", e);
		}
		
		return Response.ok(new Viewable("/publish_multiple_display.jsp", model)).build();
	}
	
	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
