package au.edu.anu.datacommons.connection.db;

/**
 * GenericDAO
 * 
 * Australian National University Data Comons
 * 
 * Generic Data Access Object implementation
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 
 */
public interface GenericDAO <T, PK>{
	//T create(T t);
	
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
	T getSingleByName(String name);
	//T getSingleById(PK id);
	//T update(T t);
	//void delete(T t);
}
