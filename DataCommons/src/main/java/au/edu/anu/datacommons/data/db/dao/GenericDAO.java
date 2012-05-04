package au.edu.anu.datacommons.data.db.dao;

import java.io.Serializable;
import java.util.List;

/**
 * GenericDAO
 * 
 * Australian National University Data Commons
 * 
 * Generic Data Access Object implementation
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public interface GenericDAO <T, PK extends Serializable>{
	/**
	 * create
	 * 
	 * Creates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to create in the database
	 * @return
	 */
	T create(T t);
	
	/**
	 * getSingleById
	 * 
	 * Retrieves the object by the primary key
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The primary key of the object to retrieve
	 * @return
	 */
	T getSingleById(PK id);
	
	/**
	 * getAll
	 * 
	 * Retrieves all objects of the approriate type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects of the appropriate type
	 */
	List<T> getAll();
	
	/**
	 * update
	 * 
	 * Updates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to update
	 * @return
	 */
	T update(T t);
	
	/**
	 * delete
	 * 
	 * Removes objects from the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to delete
	 */
	void delete(T t);
}
