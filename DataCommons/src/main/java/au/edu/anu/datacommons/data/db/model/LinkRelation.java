package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * LinkRelation
 * 
 * Australian National University Data Commons
 * 
 * Entity object for the link_relation table.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="link_relation")
public class LinkRelation {
	LinkRelationPK id;

	/**
	 * getId
	 *
	 * Retrieves the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	public LinkRelationPK getId() {
		return id;
	}

	/**
	 * setId
	 *
	 * Sets the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(LinkRelationPK id) {
		this.id = id;
	}
}
