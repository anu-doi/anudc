package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import au.edu.anu.datacommons.collectionrequest.CollectionRequest;
import au.edu.anu.datacommons.data.db.model.Groups;

/**
 * CollectionRequestDAO
 * 
 * Australian National University Data Commons
 * 
 * Interface for the CollectionRequest class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface CollectionRequestDAO extends GenericDAO<CollectionRequest, Long> {
	/**
	 * getPermittedRequests
	 *
	 * Retrieves a list of requests that the user has permission to view.
	 * i.e. it retrieves the users own requests, and if they are a reviewere for any
	 * of the groups they are able to view those requests.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0,1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param userId  The user id to retrieve collection requests for
	 * @param groups A set of groups to retrieve collection requests for
	 * @return A list of collection requests
	 */
	public List<CollectionRequest> getPermittedRequests(Long userId, List<Groups> groups);
	
	/**
	 * getSingleByIdEager
	 *
	 * Eagerly retrieves the class (and sub classes) of an object by the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the object ot retrieve
	 * @return The collection request associated with the id
	 */
	public CollectionRequest getSingleByIdEager(Long id);
}
