package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import au.edu.anu.datacommons.data.db.model.LinkRelation;

public interface LinkRelationDAO extends GenericDAO<LinkRelation, Long> {
	public List<LinkRelation> getRelations(String category1, String category2);
}
