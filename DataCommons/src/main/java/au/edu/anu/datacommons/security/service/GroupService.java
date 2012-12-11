package au.edu.anu.datacommons.security.service;

import java.util.List;

import org.springframework.security.access.prepost.PostFilter;

import au.edu.anu.datacommons.data.db.model.Groups;

/**
 * GroupService
 * 
 * Australian National University Data Commons
 * 
 * Service for retrieving groups
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated filter permissions
 * 0.3		20/06/2012	Genevieve Turner (GT)	Created a select all groups that filters out those for which the user has create permissions
 * 0.4		20/08/2012	Genevieve Turner (GT)	Added a method that determines whether the user has permissions to modify groups
 * 0.5		11/09/2012	Genevieve turner (GT)	Updated edit permissions for retrieving the create groups
 * 0.6		14/11/2012	Genevieve Turner (GT)	Updated to allow a user with administration permissions to update a users group permissions
 * 0.7		11/12/2022	Genevieve Turner (GT)	Added validation and mass publication methods
 * </pre>
 * 
 */	
public interface GroupService {
	/**
	 * getAll
	 * 
	 * Gets all the groups the user has permission to read or write to
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/05/2012	Genevieve Turner (GT)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated filter permissions
	 * </pre>
	 * 
	 * @return Returns a list of groups
	 */
	@PostFilter("hasPermission(filterObject,'READ') or hasPermission(filterObject,'WRITE') or hasPermission(filterObject,'ADMINISTRATION') or hasPermission(filterObject,'CREATE')")
	public List<Groups> getAll();
	
	/**
	 * 
	 * getCreateGroups
	 *
	 * Gets a list of users who have permission to create objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		20/06/2012	Genevieve Turner (GT)	Initial
	 * 0.5		11/09/2012	Genevieve Turner (GT)	Updated permissions
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to create objects in
	 */
	@PostFilter("hasPermission(filterObject,'CREATE') or hasPermission(filterObject,'WRITE') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<Groups> getCreateGroups();
	
	/**
	 * getReviewGroups
	 *
	 * Gets a list of users who have permissions to review objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to review
	 */
	@PostFilter("hasPermission(filterObject,'REVIEW')")
	public List<Groups> getReviewGroups();
	
	/**
	 * getAllowModifyGroups
	 *
	 * A list of users who have permissions to modify other users permissions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		20/08/2012	Genevieve Turner(GT)	Initial
	 * 0.6		14/11/2012	Genevieve Turner (GT)	Updated to allow a user with administration permissions on a group access
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to modify the groups for
	 */
	@PostFilter("hasRole('ROLE_ADMIN') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<Groups> getAllowModifyGroups();
	
	/**
	 * getValidationGroups
	 *
	 * Get the groups that the user is allowed to validate
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@PostFilter("hasPermission(filterObject,'CREATE') or hasPermission(filterObject,'WRITE') or hasPermission(filterObject,'REVIEW') or hasPermission(filterObject,'PUBLISH') or hasPermission(filterObject,'ADMINISTRATION')")
	public List<Groups> getValidationGroups();
	
	/**
	 * getMultiplePublishGroups
	 *
	 * Get the groups that the user is allowed to perform mass publication on
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@PostFilter("hasPermission(filterObject,'PUBLISH_MULTI')")
	public List<Groups> getMultiplePublishGroups();
	
}
