package au.edu.anu.datacommons.security.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;

/**
 * GroupServiceImpl
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
 * 0.2		20/06/2012	Genevieve Turner (GT)	Created a select all groups that filters out those for which the user has create permissions
 * 0.3		20/08/2012	Genevieve Turner (GT)	Added a method that determines whether the user has permissions to modify groups
 * </pre>
 * 
 */	
@Service("groupServiceImpl")
public class GroupServiceImpl implements GroupService {
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectServiceImpl.class);

	/**
	 * getAll
	 * 
	 * Gets all the groups
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return Returns a list of groups
	 */
	public List<Groups> getAll() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}
	
	/**
	 * getCreateGroups
	 * 
	 * Gets a list of users who have permission to create objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to create objects in
	 * @see au.edu.anu.datacommons.security.service.GroupService#getCreateGroups()
	 */
	public List<Groups> getCreateGroups() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}
	
	/**
	 * getReviewGroups
	 * 
	 * Gets a list of users who have permissions to review objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to review
	 * @see au.edu.anu.datacommons.security.service.GroupService#getReviewGroups()
	 */
	public List<Groups> getReviewGroups() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}

	/**
	 * getAllowModifyGroups
	 * 
	 * A list of users who have permissions to modify other users permissions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		20/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of groups the user has permissions to modify the groups for
	 * @see au.edu.anu.datacommons.security.service.GroupService#getAllowModifyGroups()
	 */
	public List<Groups> getAllowModifyGroups() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}
}
