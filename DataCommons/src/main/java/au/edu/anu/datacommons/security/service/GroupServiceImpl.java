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
		LOGGER.info("Returning groups");
		return groups;
	}
}
