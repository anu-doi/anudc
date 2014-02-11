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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.collectionrequest.Email;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UserRequestPasswordDAO;
import au.edu.anu.datacommons.data.db.dao.UserRequestPasswordDAOImpl;
import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Authorities;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.UserRegistered;
import au.edu.anu.datacommons.data.db.model.UserRequestPassword;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.exception.DataCommonsException;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.CustomUser;
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
 * A set of resources to find and update information about users including their permissions
 * 
 * JUnit Coverage: None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/08/2012	Genevieve Turner (GT)	Initial
 * 0.2		27/08/2012	Genevieve Turner (GT)	Updates for adding 
 * 0.3		17/09/2012	Genevieve Turner (GT)	Fixed issue with updateUserPermissions
 * 0.4		14/11/2012	Genevieve Turner (GT)	Added a setting of a password for the user if it is null for the getEncodedPassword method
 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
 * </pre>
 * 
 */
@Component
@Scope("request")
@Path("user")
public class UserResource {
	static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
	private final static long MILLIS_PER_DAY = 24 * 3600 * 1000;

	@Resource(name = "groupServiceImpl")
	GroupService groupService;

	@Resource(name = "permissionService")
	PermissionService permissionService;

	@Resource(name = "saltSource")
	SaltSource saltSource;

	@Resource(name = "mailSender")
	JavaMailSenderImpl mailSender;

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
		CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getSingleById(customUser.getId());

		List<Groups> groups = groupService.getAll();
		model.put("groups", groups);
		model.put("user", user);

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
	 * 0.4		14/11/2012	Genevieve Turner (GT)	Updated to allow administrative role users able to update permissions
	 * </pre>
	 * 
	 * @return A page for updating permissions
	 */
	@GET
	@Path("permissions")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANU_USER')")
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
	 * @param id
	 *            The id of the group to retrieve permissions for
	 * @param username
	 *            The name of the user to retireve permissions for
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
	 * 0.2		17/09/2012	Genevieve Turner (GT)	Fixed an issue with the return result not being in the json format
	 * 0.4		14/11/2012	Genevieve Turner (GT)	Updated to allow administrative role users able to update permissions
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param id
	 *            The id of the group to update the permissions for
	 * @param request
	 *            The http request made
	 * @return A successful message
	 */
	@POST
	@Path("permissions/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANU_USER')")
	public String updateUserPermissions(@PathParam("id") Long id, @Context HttpServletRequest request) {
		String username = request.getParameter("username");
		if (!Util.isNotEmpty(username)) {
			throw new ValidateException("No username specified");
		}

		// Ensure the logged in user has permissions to update this group
		List<Groups> groups = groupService.getAllowModifyGroups();
		boolean hasGroupPermission = false;
		for (int i = 0; !hasGroupPermission && i < groups.size(); i++) {
			if (groups.get(i).getId().equals(id)) {
				hasGroupPermission = true;
			}
		}
		if (!hasGroupPermission) {
			LOGGER.error("{} does not have permissions to update group {}", SecurityContextHolder.getContext()
					.getAuthentication().getName(), id);
			throw new ValidateException("You do not have permissions to update the group");
		}

		List<Integer> permissions = new ArrayList<Integer>();
		String[] group_permissions = request.getParameterValues("group_perm[]");
		if (group_permissions != null) {
			for (String mask : group_permissions) {
				permissions.add(Integer.valueOf(mask));
			}
		}
		permissionService.saveUserPermissions(id, username, permissions);
		return "{\"response\": \"Permission Updated\"}";
	}

	/**
	 * findUser
	 * 
	 * Find users with the given information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * 0.4		14/11/2012	Genevieve Turner (GT)	Updated to allow administrative role users able to update permissions
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param firstname
	 *            The firstname of the user to find
	 * @param lastname
	 *            The last of the user to find
	 * @param uniId
	 *            The university id of the user to find
	 * @return A list of users with the given criteria
	 */
	@GET
	@Path("find")
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ANU_USER')")
	public List<LdapPerson> findUser(@QueryParam("firstname") String firstname,
			@QueryParam("lastname") String lastname, @QueryParam("uniId") String uniId) {

		List<Groups> groups = groupService.getAllowModifyGroups();
		if (groups.size() == 0) {
			LOGGER.error("{} does not have permissions search ldap for other users", SecurityContextHolder.getContext()
					.getAuthentication().getName());
			throw new DataCommonsException(Status.UNAUTHORIZED, "You do not have permissions to search ldap");
		}

		LdapRequest ldapRequest = new LdapRequest();
		boolean hasInfo = false;
		StringBuilder sb = new StringBuilder();
		sb.append("(&");
		if (Util.isNotEmpty(firstname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME), firstname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(lastname)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_FAMILYNAME), lastname));
			hasInfo = true;
		}
		if (Util.isNotEmpty(uniId)) {
			sb.append(addLdapVariable(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID), uniId));
			hasInfo = true;
		}
		sb.append(")");
		if (!hasInfo) {
			throw new ValidateException("A given name, surname or university id is required for the search");
		}
		ldapRequest.setQuery(sb.toString());
		List<LdapPerson> people = null;
		try {
			people = ldapRequest.search();
			LOGGER.trace("Query [fn={}, ln={}, uid={}]. Results: {}", firstname, lastname, uniId, people.size());
		} catch (NamingException e) {
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
	 * @param variable
	 *            The variable to add
	 * @param value
	 *            The value to add
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

	/**
	 * updateUser
	 * 
	 * Retrieves a page for updating user information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param request
	 *            Http request information
	 * @return Response for updating user information
	 */
	@GET
	@Path("update")
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	public Response updateUser(@Context HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getSingleById(customUser.getId());

		if (!user.getUser_type().equals(new Long(2))) {
			throw new DataCommonsException(Status.FORBIDDEN, "Only registered users update their information");
		}

		model.put("user", user);

		return Response.ok(new Viewable("/user_update.jsp", model)).build();
	}

	/**
	 * saveUpdateUser
	 * 
	 * Updates user information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param request
	 *            Http request information
	 * @param uriInfo
	 *            URI information
	 * @return The user page
	 */
	@POST
	@Path("update")
	@PreAuthorize("hasRole('ROLE_REGISTERED')")
	public Response saveUpdateUser(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		String password = request.getParameter("password");
		String newpassword = request.getParameter("newpassword");
		String newpassword2 = request.getParameter("newpassword2");

		Map<String, Object> model = new HashMap<String, Object>();
		CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getSingleById(customUser.getId());

		if (!user.getUser_type().equals(new Long(2))) {
			throw new DataCommonsException(403, "Only registered users update their information");
		}

		// Verify password
		Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		user = setUserDetails(request, user);

		if (!user.getPassword().equals(passwordEncoder.encodePassword(password, saltSource.getSalt(customUser)))) {
			model.put("error", "Incorrect Password");
			model.put("user", user);
			throw new WebApplicationException(Response.status(Status.FORBIDDEN)
					.entity(new Viewable("/user_update.jsp", model)).build());
		}
		if (Util.isNotEmpty(newpassword)) {
			user.setPassword(getEncodedPassword(newpassword, newpassword2, user));
		}

		userDAO.update(user);

		model.put("user", user);
		UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getBaseUri()).path("user");
		return Response.seeOther(uriBuilder.build()).build();
	}

	/**
	 * setUserDetails
	 * 
	 * Set user details information
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param request
	 *            Http request information
	 * @param user
	 *            The user to update user details for
	 * @return An updated user
	 */
	private Users setUserDetails(HttpServletRequest request, Users user) {
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String institution = request.getParameter("institution");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");

		UserRegistered user_registered = user.getUser_registered();
		if (user_registered == null) {
			user_registered = new UserRegistered();
			user_registered.setUser(user);
		}

		user_registered.setId(user.getId());
		user_registered.setGiven_name(firstname);
		user_registered.setLast_name(lastname);
		user_registered.setInstitution(institution);
		user_registered.setPhone(phone);
		user_registered.setAddress(address);
		user.setUser_registered(user_registered);

		return user;
	}

	public UserRegistered createUserRegistered(HttpServletRequest request, Users user) {
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String institution = request.getParameter("institution");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");

		UserRegistered userRegistered = new UserRegistered();
		userRegistered.setId(user.getId());
		userRegistered.setGiven_name(firstname);
		userRegistered.setLast_name(lastname);
		userRegistered.setInstitution(institution);
		userRegistered.setPhone(phone);
		userRegistered.setAddress(address);
		userRegistered.setUser(user);

		return userRegistered;
	}

	/**
	 * getNewUser
	 * 
	 * Gets a new user page
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A create new user page
	 */
	@GET
	@Path("new")
	public Response getNewUser() {
		Viewable viewable = new Viewable("/user_new.jsp");
		return Response.ok(viewable).build();
	}

	/**
	 * createNewUser
	 * 
	 * Create a new registered user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param request
	 *            Http request information
	 * @return A resposne to the create new user
	 */
	@POST
	@Path("new")
	public Response createNewUser(@Context HttpServletRequest request) {
		String emailAddr = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");

		Users user = new Users();
		user.setUsername(emailAddr);

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_REGISTERED"));
		String encodedPassword = getEncodedPassword(password, password2, user);

		user.setPassword(encodedPassword);
		user.setEnabled(Boolean.TRUE);
		user.setUser_type(new Long(2));

		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		UserRegistered ur = createUserRegistered(request, user);
		user.setUser_registered(ur);

		user = userDAO.create(user);

		Authorities authority = new Authorities();
		authority.setUsername(emailAddr);
		authority.setAuthority("ROLE_REGISTERED");
		GenericDAO<Authorities, String> authorityDAO = new GenericDAOImpl<Authorities, String>(Authorities.class);
		authorityDAO.create(authority);

		Email email = new Email(mailSender);
		email.addRecipient(emailAddr, user.getDisplayName());
		email.setSubject("Account Created");

		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put("displayName", user.getDisplayName());
		try {
			email.setBody("mailtmpl/newaccount.txt", varMap);
			email.send();
		} catch (IOException e) {
			LOGGER.error("Exception creating email", e);
			throw new DataCommonsException(500, "Exception creating email");
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "The user has been created and an email has been sent");
		return Response.ok(new Viewable("/message.jsp", model)).build();
	}

	/**
	 * getForgotPassword
	 * 
	 * Gets a forgot password page
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return Returns a page to notify that the password has been forgotten
	 */
	@GET
	@Path("forgotpassword")
	public Response getForgotPassword() {
		return Response.ok(new Viewable("/user_forgotpassword.jsp")).build();
	}

	/**
	 * sendForgotPasswordEmail
	 * 
	 * Sends an email about a forgotten password for the specified user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param request
	 *            Http request information
	 * @param uriInfo
	 *            URI information
	 * @return A response to the email request
	 */
	@POST
	@Path("forgotpassword")
	public Response sendForgotPasswordEmail(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		String username = request.getParameter("email");
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		Users user = userDAO.getUserByName(username);
		Map<String, Object> model = new HashMap<String, Object>();
		if (user == null) {
			model.put("error", "Email address does not exist in the database");
			throw new WebApplicationException(Response.status(400)
					.entity(new Viewable("/user_forgotpassword.jsp", model)).build());
		}

		UserRequestPassword userRequest = new UserRequestPassword();
		userRequest.setUser(user);
		userRequest.setRequest_date(new Date());
		userRequest.setIp_address(request.getRemoteAddr());
		userRequest.setLink_id(UUID.randomUUID().toString());
		UserRequestPasswordDAO userRequestDAO = new UserRequestPasswordDAOImpl(UserRequestPassword.class);
		userRequestDAO.create(userRequest);

		UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getBaseUri()).path("user").path("resetpassword")
				.queryParam("link", userRequest.getLink_id());

		Email email = new Email(mailSender);
		email.addRecipient(username, user.getDisplayName());
		email.setSubject("Forgotten Password");

		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put("displayName", user.getDisplayName());
		varMap.put("link", uriBuilder.build().toString());
		try {
			email.setBody("mailtmpl/forgotpassword.txt", varMap);
			email.send();
		} catch (IOException e) {
			LOGGER.error("Exception creating email", e);
			throw new DataCommonsException(500, "Exception creating email");
		}

		return Response.ok(new Viewable("/user_emailsent.jsp")).build();
	}

	/**
	 * getResetPassword
	 * 
	 * Gets a page to reset the password
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link
	 *            The link to use to get the password reset page
	 * @return A page to reset the password
	 */
	@GET
	@Path("resetpassword")
	public Response getResetPassword(@QueryParam("link") String link) {
		UserRequestPasswordDAO userRequestDAO = new UserRequestPasswordDAOImpl(UserRequestPassword.class);
		UserRequestPassword userRequest = userRequestDAO.getByLink(link);
		isValidReset(userRequest);

		return Response.ok(new Viewable("/user_forgottenreset.jsp")).build();
	}

	/**
	 * resetPassword
	 * 
	 * Resets the password
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param link
	 *            The link of the password reset request
	 * @param request
	 *            The http request performed
	 * @return A response from restting the password
	 */
	@POST
	@Path("resetpassword")
	public Response resetPassword(@QueryParam("link") String link, @Context HttpServletRequest request) {
		UserRequestPasswordDAO userRequestDAO = new UserRequestPasswordDAOImpl(UserRequestPassword.class);
		UserRequestPassword userRequest = userRequestDAO.getByLink(link);

		isValidReset(userRequest);

		Users user = userRequest.getUser();
		user.setPassword(getEncodedPassword(request.getParameter("password"), request.getParameter("password2"), user));
		UsersDAO userDAO = new UsersDAOImpl(Users.class);
		userDAO.update(user);
		userRequest.setUsed(Boolean.TRUE);
		userRequestDAO.update(userRequest);

		Email email = new Email(mailSender);
		email.addRecipient(user.getUsername(), user.getDisplayName());
		email.setSubject("Forgotten Password");

		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put("displayName", user.getDisplayName());
		try {
			email.setBody("mailtmpl/passwordreset.txt", varMap);
			email.send();
		} catch (IOException e) {
			LOGGER.error("Exception creating email", e);
			throw new DataCommonsException(500, "Exception creating email for password reset");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Password Reset and email has been sent");
		return Response.ok(new Viewable("/message.jsp", model)).build();
	}

	/**
	 * isValidReset
	 * 
	 * Verfies that its a valid link for resetting the password. It checks that the link exists in a row and that it has
	 * not expired.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param userRequest
	 *            The user request to validate
	 * @return true if the link is valid
	 */
	private boolean isValidReset(UserRequestPassword userRequest) {
		Date today = new Date();
		if (userRequest == null) {
			throw new DataCommonsException(404, "Either a the password change request was not found or it is invalid");
		} else if (today.getTime() - userRequest.getRequest_date().getTime() - MILLIS_PER_DAY > 0
				|| Boolean.TRUE.equals(userRequest.getUsed())) {
			throw new DataCommonsException(400, "Password change request has expired");
		}

		return true;
	}

	/**
	 * getEncodedPassword
	 * 
	 * Verifies that two passwords are the same and returns an md5 salted password
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		27/08/2012	Genevieve Turner(GT)	Initial
	 * 0.4		14/11/2012	Genevieve Turner (GT)	Added a setting of a password for the user if it is null
	 * 0.5		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @param password
	 *            Password 1 for comparison
	 * @param password2
	 *            Password 2 for comparison
	 * @param user
	 *            User to match password for
	 * @return An md5 salted password
	 */
	private String getEncodedPassword(String password, String password2, Users user) {
		if (password == null || !password.equals(password2)) {
			throw new ValidateException("Passwords do not match");
		}
		Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		// The system appears to have issues if the password is null when retrieving the CustomUser
		if (user.getPassword() == null) {
			user.setPassword("xxx");
		}
		CustomUser customUser = new CustomUser(user, true, true, true, true, new ArrayList<GrantedAuthority>());

		return passwordEncoder.encodePassword(password, saltSource.getSalt(customUser));
	}

	@GET
	@Path("caslogout")
	public Response getCasLogout() {
		Response resp = null;
		resp = Response.seeOther(UriBuilder.fromUri(GlobalProps.getCasServerUri()).path("logout").build()).build();
		return resp;
	}
}
