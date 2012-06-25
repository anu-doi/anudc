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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.security.service.GroupService#getCreateGroups()
	 */
	public List<Groups> getCreateGroups() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}
	
	public List<Groups> getReviewGroups() {
		GenericDAOImpl genericDAO = new GenericDAOImpl(Groups.class);
		List<Groups> groups = genericDAO.getAll();
		return groups;
	}
}
