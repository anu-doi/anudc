package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * LinkType
 * 
 * Australian National University Data Commons
 * 
 * Entity class for the link_type table
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
@Table(name="link_type")
public class LinkType {
	private Long id;
	private String code;
	private String description;
	
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 *
	 * Set the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getCode
	 *
	 * Get the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the code
	 */
	@Column(name="code")
	public String getCode() {
		return code;
	}
	
	/**
	 * setCode
	 *
	 * Set the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * getDescription
	 *
	 * Get the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the description
	 */
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	
	/**
	 * setDescription
	 *
	 * Set the description
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
