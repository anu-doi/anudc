package au.edu.anu.datacommons.xslt.db.model;

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
 * Class for the link_type table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		18/02/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="link_type")
public class LinkType {
	private Long id;
	private String code;
	private String description;
	private String reverse;
	
	/**
	 * getId
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		18/02/2013	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * getReverse
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reverse
	 */
	@Column(name="reverse")
	public String getReverse() {
		return reverse;
	}
	
	/**
	 * setReverse
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		18/02/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reverse the reverse to set
	 */
	public void setReverse(String reverse) {
		this.reverse = reverse;
	}
}
