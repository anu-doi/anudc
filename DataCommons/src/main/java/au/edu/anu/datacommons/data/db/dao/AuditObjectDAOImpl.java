package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

public class AuditObjectDAOImpl extends GenericDAOImpl<AuditObject, Long> implements AuditObjectDAO {

	/*@Override
	public AuditObject create(AuditObject t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditObject getSingleById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuditObject> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditObject update(AuditObject t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub

	}*/

	public AuditObjectDAOImpl(Class<AuditObject> type) {
		super(type);
	}
	
	@Override
	public AuditObject getFirstRecord(FedoraObject fedoraObject, String type) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		try {
			Query query = entityManager.createQuery("from AuditObject ao where ao.object_id = :fedoraObjectId AND ao.log_type = :logType order by log_date asc limit 1");
			query.setParameter("fedoraObjectId", fedoraObject.getId());
			query.setParameter("logType", type);
			
			List<AuditObject> auditObjects = query.getResultList();
			if (auditObjects != null && auditObjects.size() > 0) {
				return auditObjects.get(0);
			}
		}
		finally {
			entityManager.close();
		}
		return null;
//		return (AuditObject)query.getSingleResult();
	}
	
	@Override
	public AuditObject getLastRecord(FedoraObject fedoraObject, String type) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		try {
			Query query = entityManager.createQuery("from AuditObject ao where ao.object_id = :fedoraObjectId AND ao.log_type = :logType order by log_date desc limit 1");
			query.setParameter("fedoraObjectId", fedoraObject.getId());
			query.setParameter("logType", type);
			List<AuditObject> auditObjects = query.getResultList();
			if (auditObjects != null && auditObjects.size() > 0) {
				return auditObjects.get(0);
			}
		}
		finally {
			entityManager.close();
		}
		return null;
//		return (AuditObject)query.getSingleResult();
	}

}
