package au.edu.anu.datacommons.security.acl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.AclSidDAO;
import au.edu.anu.datacommons.data.db.dao.AclSidDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.AclSid;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.util.Util;

/**
 * PermissionService
 * 
 * Australian National University Data Commons
 * 
 * A service to retrieve and update permissions
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/08/2012	Genevieve Turner (GT)	Initial
 * 0.2		28/08/2012	Genevieve Turner (GT)	Updates to fix an exception if there is no acl_sid row for the user
 * 0.3		17/09/2012	Genevieve Turner (GT)	Added methods around getting a list of groups the user has create permissions on 
 * </pre>
 *
 */
@Service("permissionService")
public class PermissionService {
	static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);
	
	@Resource(name="aclService")
	MutableAclService aclService;
	
	/**
	 * getListOfPermission
	 *
	 * Retrieves a list of permissions.  If the user name is null then it gets a list
	 * of permissions for the currently logged in user
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * 0.3		17/09/2012	Genevieve Turner (GT)	Updated to use the getAuthenticatedSidList method
	 * </pre>
	 * 
	 * @param clazz The class type to get permissions for
	 * @param id The id to get permissions for
	 * @param username The username to get permissions for
	 * @return A list of permissions given the constraints
	 */
	public List<Permission> getListOfPermission(Class clazz, Long id, String username) {
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(clazz, id);
		
		// Get the uers permissions
		List<Sid> sidList = new ArrayList<Sid>();
		if (Util.isNotEmpty(username)) {
			LOGGER.info("Username {}", username);
			Sid sid = new PrincipalSid(username.toLowerCase());
			sidList.add(sid);
		}
		else {
			sidList.addAll(getAuthenticatedSidList());
		}
		List<Permission> permissionList = new ArrayList<Permission>();
		Acl acl = null;
		
		try {
			acl = aclService.readAclById(objectIdentity);
		}
		catch (NotFoundException e) {
			LOGGER.error("Issue with permissions", e);
		}
		
		// Would have liked to have had this in the previous try/catch but it appears
		// to throw an exception when the permission is not found.
		if (acl != null) {
			for (Permission permission : CustomACLPermission.getPermissionList()) {
				try {
					if (checkSinglePermission(acl,permission, sidList)) {
						permissionList.add(permission);
					}
					else {
						LOGGER.info("Does not have permission {}", permission.getMask());
					}
				}
				catch(NotFoundException e) {
					LOGGER.debug("No ACE found");
				}
			}
		}
		
		return permissionList;
	}
	
	/**
	 * checkSinglePermission
	 *
	 * Check if the permission is granted
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param acl The acl to check against
	 * @param permission The permission to check
	 * @param sidList The list of sids to check
	 * @return Whether the sids have this permission
	 */
	private boolean checkSinglePermission(Acl acl, Permission permission, List<Sid> sidList) {
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(permission);
		return acl.isGranted(permissionList, sidList, false);
	}
	
	/**
	 * checkViewPermission
	 *
	 * Check if the user has permissions to view an object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject Check the fedora object permissions
	 * @return Whether the user has permissions
	 */
	public boolean checkViewPermission(FedoraObject fedoraObject) {
		boolean hasPermission = false;
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(FedoraObject.class, fedoraObject.getId());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<Sid> sidList = new ArrayList<Sid>();
		// Put the user into a sid list
		Sid sid = new PrincipalSid(authentication.getName());
		sidList.add(sid);
		
		Iterator<GrantedAuthority> it = authentication.getAuthorities().iterator();
		while (it.hasNext()) {
			GrantedAuthority auth = it.next();
			Sid authSid = new GrantedAuthoritySid(auth.getAuthority());
			sidList.add(authSid);
		}
		
		Acl acl = null;
		try {
			acl = aclService.readAclById(objectIdentity, sidList);
			
			hasPermission = acl.isGranted(CustomACLPermission.getPermissionList(), sidList, false);
		}
		catch (NotFoundException e) {
			LOGGER.debug("User doesn't have permissions");
		}
		return hasPermission;
	}
	
	/**
	 * getCreatePermissions
	 *
	 * This method has been created as the Spring Security post filters do not appear to work
	 * when they are not accessed via Servlet/REST.  We need to see where the user can create groups
	 * for the XSL transformation which is not being called via the Servlet.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of groups the logged in user has create permissions for
	 */
	public List<Groups> getCreatePermissions() {
		GenericDAO<Groups, Long> groupsDAO = new GenericDAOImpl<Groups, Long>(Groups.class);
		List<Groups> groups = groupsDAO.getAll();
		
		List<Sid> sidList = getAuthenticatedSidList();
		
		List<Groups> createGroups = new ArrayList<Groups>();

		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(CustomACLPermission.WRITE);
		permissionList.add(CustomACLPermission.ADMINISTRATION);
		for (Groups group : groups) {
			if (checkGroup(group, permissionList, sidList)) {
				createGroups.add(group);
			}
		}
		
		return createGroups;
	}

	/**
	 * checkGroup
	 *
	 * Check the permissions on a group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param group The group to check the permission for
	 * @param permission The list of permissions to check
	 * @param sidList The list of sids to check
	 * @return
	 */
	private boolean checkGroup(Groups group, List<Permission> permissionList, List<Sid> sidList) {
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(Groups.class, group.getId());
		
		boolean hasPermission = false;
		Acl acl = null;
		
		try {
			acl = aclService.readAclById(objectIdentity);
			hasPermission = acl.isGranted(permissionList, sidList, false);
		}
		catch(NotFoundException e) {
			LOGGER.info("User does not have permissions for {}", group.getGroup_name());
		}
		
		return hasPermission;
	}
	
	/**
	 * getAuthenticatedSidList
	 *
	 * Get a list of sids associated with the authenticated user.  i.e. The sid for the user
	 * and the sid(s) for the users granted authorities.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	private List<Sid> getAuthenticatedSidList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		List<Sid> sidList = new ArrayList<Sid>();
		Sid sid = new PrincipalSid(authentication.getName());
		sidList.add(sid);
		
		Iterator<GrantedAuthority> it = authentication.getAuthorities().iterator();
		while (it.hasNext()) {
			GrantedAuthority auth = it.next();
			Sid authSid = new GrantedAuthoritySid(auth.getAuthority());
			sidList.add(authSid);
		}
		
		return sidList;
	}
	
	/**
	 * saveUserPermissions
	 *
	 * Update the permissions of the given user for the particular group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * 0.2		28/08/2012	Genevieve Turner (GT)	Updates to fix an exception if there is no acl_sid row for the user
	 * </pre>
	 * 
	 * @param id The id to save permissions for
	 * @param username The username to save permissions for
	 * @param masks The masks of the permissions to save
	 */
	public void saveUserPermissions(Long id, String username, List<Integer> masks) {
		// This code is here due to a exception with Transaction must be running
		// On the updateAcl action if the acl_sid row does not exist
		// There may be something in the spring security framework that can be used instead
		// however at this point in time I am unsure as to what it is.
		AclSidDAO aclSidDAO = new AclSidDAOImpl(AclSid.class);
		AclSid aclSid = aclSidDAO.getAclSidByUsername(username);
		if (aclSid == null) {
			aclSid = new AclSid();
			aclSid.setPrincipal(Boolean.TRUE);
			aclSid.setSid(username);
			aclSidDAO.create(aclSid);
		}
		
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(Groups.class, id);
		Sid sid = new PrincipalSid(username);
		MutableAcl groupAcl = null;
		try {
			groupAcl = (MutableAcl)aclService.readAclById(objectIdentity);
			List<AccessControlEntry> entries = groupAcl.getEntries();
			
			// Delete all the permissions for the specified user
			for (int i = entries.size()-1; i >= 0; i--) {
				AccessControlEntry entry = entries.get(i);
				if (entry.getSid().equals(sid)) {
					LOGGER.info("Is same sid");
					groupAcl.deleteAce(i);
				}
			}
		}
		catch (NotFoundException nfe) {
			groupAcl = aclService.createAcl(objectIdentity);
		}
		for(Permission permission : CustomACLPermission.getPermissionList()) {
			if (masks.contains(permission.getMask())) {
				// Add all the permissions in the list
				groupAcl.insertAce(groupAcl.getEntries().size(), permission, sid, true);
			}
		}
		aclService.updateAcl(groupAcl);
	}
	
	/**
	 * saveObjectPermissions
	 *
	 * Save the permissions for a fedora object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to save permissions for
	 */
	public void saveObjectPermissions(FedoraObject fedoraObject) {
		LOGGER.info("Id: {}", fedoraObject.getId());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Sid sid = new PrincipalSid(authentication.getName());
		
		ObjectIdentity group_oi = new ObjectIdentityImpl(Groups.class, fedoraObject.getGroup_id());
		MutableAcl groupAcl = null;
		try {
			groupAcl = (MutableAcl) aclService.readAclById(group_oi);
		}
		catch (NotFoundException nfe) {
			groupAcl = aclService.createAcl(group_oi);
		}
		
		ObjectIdentity fedora_oi = new ObjectIdentityImpl(FedoraObject.class, fedoraObject.getId());
		MutableAcl fedoraAcl = null;
		try {
			fedoraAcl = (MutableAcl) aclService.readAclById(fedora_oi);
		}
		catch (NotFoundException nfe) {
			fedoraAcl = aclService.createAcl(fedora_oi);
		}
		
		fedoraAcl.setParent(groupAcl);
		fedoraAcl.setEntriesInheriting(Boolean.TRUE);
		fedoraAcl.setOwner(sid);
		
		aclService.updateAcl(fedoraAcl);
	}
}
