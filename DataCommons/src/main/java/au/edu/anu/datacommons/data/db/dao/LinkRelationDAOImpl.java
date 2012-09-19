package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.LinkRelation;

public class LinkRelationDAOImpl extends GenericDAOImpl<LinkRelation, Long> implements
		LinkRelationDAO {

	public LinkRelationDAOImpl(Class<LinkRelation> type) {
		super(type);
	}

	public List<LinkRelation> getRelations(String category1, String category2) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<LinkRelation> linkRelations = null;
		try {
			Query query = entityManager.createQuery("from LinkRelation lr where lr.id.category1 = :category1 and lr.id.category2 = :category2");
			query.setParameter("category1", category1);
			query.setParameter("category2", category2);
			
			linkRelations = query.getResultList();
		}
		catch (NoResultException e) {
			LOGGER.debug("No entity found for {}, {}", category1, category2);
		}
		finally {
			entityManager.close();
		}
		
		return linkRelations;
	}
}
