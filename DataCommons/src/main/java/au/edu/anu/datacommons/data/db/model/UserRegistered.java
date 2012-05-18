package au.edu.anu.datacommons.data.db.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * UserRegistered
 * 
 * Australian National University Data Comons
 * 
 * Entity class for the user_registered database table
 * 
 * JUnit Coverage:
 * UsersTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		17/05/2012	Genevieve Turner (GT)	Added user type column
 * </pre>
 * 
 */
@Entity
@Table(name="user_registered")
//@GenericGenerator(name="users-primarykey", strategy="foreign", parameters={@Parameter(name="property", value="users")})
public class UserRegistered {
	private Long id;
	private String last_name;
	private String given_name;
	private Users user;
	
	/**
	 * getId
	 * 
	 * Gets the id of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return the id of the user
	 */
	@Id
	//@GeneratedValue(generator = "users-primarykey")
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the user
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getLast_name
	 * 
	 * Gets the last name of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The surname of the user
	 */
	@Column(name="last_name")
	public String getLast_name() {
		return last_name;
	}
	
	/**
	 * setLast_name
	 * 
	 * Sets the last name of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param last_name The surname of the user
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	/**
	 * getGiven_name
	 * 
	 * Gets the given name of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The given name of the user
	 */
	@Column(name="given_name")
	public String getGiven_name() {
		return given_name;
	}
	
	/**
	 * setGiven_name
	 * 
	 * Sets the given name of the user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param given_name The given name of the user
	 */
	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	/**
	 * getUser
	 * 
	 * Gets the user associated with the registered user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param user The user for the registered information
	 */
	@OneToOne (cascade=CascadeType.ALL, mappedBy="user_registered")//()
	@PrimaryKeyJoinColumn
	public Users getUser() {
		return user;
	}

	/**
	 * setUser
	 * 
	 * Gets the user associated with the registered user
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param user The user for the registered information
	 */
	public void setUser(Users user) {
		this.user = user;
	}
}
