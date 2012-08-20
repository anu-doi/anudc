package au.edu.anu.datacommons.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.acl.PermissionService;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * 
 * UserResource
 * 
 * Australian National University Data Commons
 * 
 * A set of resources to find and update information about users including
 * their permissions
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Component
@Scope("request")
@Path("user")
public class UserResource {
	static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Resource(name="aclService")
	MutableAclService aclService;
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;
	
	@Resource(name="permissionService")
	PermissionService permissionService;
	
	/**
	 * getUserInformation
	 *
	 * Retrieves the page with the user information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A page with user information
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("isAuthenticated()")
	public Response getUserInformation() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		List<Groups> groups = groupService.getAll();
		model.put("groups", groups);
		
		return Response.ok(new Viewable("/user_info.jsp", model)).build();
	}
	
	/**
	 * getPermissionsPage
	 *
	 * Retrieves the page for updating user permissions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A page for updating permissions
	 */
	@GET
	@Path("permissions")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response getPermissionsPage() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		List<Groups> groups = groupService.getAllowModifyGroups();
		model.put("groups", groups);
		return Response.ok(new Viewable("/user_permissions.jsp", model)).build();
	}
	
	/**
	 * getGroupPermissions
	 *
	 * Gets a list of permissions for the user
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the group to retrieve permissions for
	 * @param username The name of the user to retireve permissions for
	 * @return
	 */
	@GET
	@Path("permissions/{id}")
	@PreAuthorize("isAuthenticated()")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getGroupPermissions(@PathParam("id") Long id, @QueryParam("username") String username) {
		List<Permission> permissionList = permissionService.getListOfPermission(Groups.class, id, username);
		List<Integer> maskList = new ArrayList<Integer>();
		for (Permission permission : permissionList) {
			maskList.add(permission.getMask());
		}
		return maskList;
	}
	
	/**
	 * updateUserPermissions
	 *
	 * Updates the users permissions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the group to update the permissions for
	 * @param request The http request made
	 * @return A successful message
	 */
	@POST
	@Path("permissions/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String updateUserPermissions(@PathParam("id") Long id, @Context HttpServletRequest request) {
		String username = request.getParameter("username");
		if (!Util.isNotEmpty(username)) {
			throw new WebApplicationException(Response.status(400).entity("No username specified").build());
		}
		List<Integer> permissions = new ArrayList<Integer>();
		String[] group_permissions = request.getParameterValues("group_perm[]");
		if (group_permissions != null) {
			for (String mask : group_permissions) {
				permissions.add(Integer.valueOf(mask));
			}
		}
		permissionService.saveUserPermissions(id, username, permissions);
		return "Permission Updated";
	}
	
	/**
	 * findUser
	 *
	 * Find users with the given information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param firstname The firstname of the user to find
	 * @param lastname The last of the user to find
	 * @param uniId The university id of the user to find
	 * @return A list of users with the given criteria
	 */
	@GET
	@Path("find")
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<LdapPerson> findUser(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname, @QueryParam("uniId") String uniId) {
		LdapRequest ldapRequest = new LdapRequest();
		boolean hasInfo = false;
		StringBuilder sb = new StringBuilder();
		sb.append("(&");
		if (Util.isNotEmpty(firstname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME),firstname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(lastname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_FAMILYNAME),lastname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(uniId)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID),uniId));
			hasInfo = true;
		}
		sb.append(")");
		if (!hasInfo) {
			throw new WebApplicationException(Response.status(400).entity("Need a username, lastname or uni id to search for").build());
		}
		ldapRequest.setQuery(sb.toString());
		List<LdapPerson> people = null;
		try {
			people = ldapRequest.search();
			LOGGER.info("Number of people returned: {}", people.size());
		}
		catch (NamingException e) {
			LOGGER.error("Error querying ldap", e);
		}
		return people;
	}
	
	/**
	 * addLdapVariable
	 *
	 * Add a ldap variable
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param variable The variable to add
	 * @param value The value to add
	 * @return The string to add to the ldap search
	 */
	private String addLdapVariable(String variable, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(variable);
		sb.append("=");
		sb.append(value);
		sb.append(")");
		return sb.toString();
	}
}
