package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

public interface AuditObjectDAO extends GenericDAO<AuditObject, Long> {
	
	/**
	 * Get the first record of the specified audit type for the given object
	 * 
	 * @param fedoraObject
	 * @param type
	 * @return
	 */
	public AuditObject getFirstRecord(FedoraObject fedoraObject, String type);
	
	/**
	 * Get the last record of the specified audit type for the given object
	 * 
	 * @param fedoraObject
	 * @param type
	 * @return
	 */
	public AuditObject getLastRecord(FedoraObject fedoraObject, String type);
}
