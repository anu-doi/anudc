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
}
