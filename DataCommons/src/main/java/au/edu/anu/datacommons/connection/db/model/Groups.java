package au.edu.anu.datacommons.connection.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Groups
 * 
 * Australian National University Data Comons
 * 
 * Entity class for the groups database table
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
@Table(name="groups")
public class Groups {
	private Long id;
	private String group_name;
	
	/**
	 * setId
	 * 
	 * Sets the id of the group
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the domain
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id of the group
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the group
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getGroup_name
	 * 
	 * Gets the name of the group
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The name of the group
	 */
	@Column(name="group_name")
	public String getGroup_name() {
		return group_name;
	}
	
	/**
	 * setGroup_name
	 * 
	 * Sets the name of the group
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param group_name The name of the group
	 */
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
}
