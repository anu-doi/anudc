package au.edu.anu.datacommons.data.db.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class LinkRelationPK implements Serializable {
	private String category1;
	private String category2;
	private LinkType link_type;
	
	/**
	 * getCategory1
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the category1
	 */
	public String getCategory1() {
		return category1;
	}
	
	/**
	 * setCategory1
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category1 the category1 to set
	 */
	public void setCategory1(String category1) {
		this.category1 = category1;
	}
	
	/**
	 * getCategory2
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the category2
	 */
	public String getCategory2() {
		return category2;
	}
	
	/**
	 * setCategory2
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category2 the category2 to set
	 */
	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	/**
	 * getLink_type
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the link_type
	 */
	@ManyToOne
	@JoinColumn(name="link_type_id", nullable=false, updatable=false)
	public LinkType getLink_type() {
		return link_type;
	}

	/**
	 * setLink_type
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link_type the link_type to set
	 */
	public void setLink_type(LinkType link_type) {
		this.link_type = link_type;
	}
	
	public int hashCode() {
		return (int) category1.hashCode() + category2.hashCode() + link_type.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if(!(obj instanceof LinkRelationPK)) {
			return false;
		}
		LinkRelationPK pk = (LinkRelationPK) obj;
		return pk.getCategory1().equals(category1) && pk.getCategory2().equals(category2) &&
				pk.getLink_type().getId().equals(link_type.getId()) &&
				pk.getLink_type().getCode().equals(link_type.getCode()) &&
				pk.getLink_type().getDescription().equals(link_type.getDescription());
	}
}
