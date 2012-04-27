package au.edu.anu.datacommons.connection.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Domains
 * 
 * Australian National University Data Comons
 * 
 * Entity class for the domains database table
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
@Entity
@Table(name="domains")
public class Domains {
	private Long id;
	private String domain_name;

	/**
	 * getId
	 * 
	 * Gets the id of the domain
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the object
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id of the domain
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the domain
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getDomain_name
	 * 
	 * Gets the name of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The name of the domain
	 */
	@Column(name="domain_name")
	public String getDomain_name() {
		return domain_name;
	}
	
	/**
	 * setDomain_name
	 * 
	 * Sets the name of the domain
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param domain_name The name of the domain
	 */
	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}
}
