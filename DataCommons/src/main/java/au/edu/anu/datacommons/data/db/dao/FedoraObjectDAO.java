package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.FedoraObject;

/**
 * FedoraObjectDAO
 * 
 * Interface for retrieving fedora objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param <T> The type of the object that is instantiated
 * @param <PK> The type of the primary key that is instantiated
 */
public interface FedoraObjectDAO extends GenericDAO<FedoraObject, Long> {
	/**
	 * getSingleByName
	 * 
	 * Retrieves an object with the given name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param name The name of the object to retrieve
	 */
	FedoraObject getSingleByName(String name);
}
