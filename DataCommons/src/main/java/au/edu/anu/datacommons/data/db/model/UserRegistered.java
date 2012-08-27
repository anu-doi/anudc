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
 * 0.3		21/08/2012	Genevieve Turner (GT)	Added institution, phone and address columns
 * </pre>
 * 
 */
@Entity
@Table(name="user_registered")
//@GenericGenerator(name="users-primarykey", strategy="foreign", parameters={@Parameter(name="property", value="users")})
public class UserRegistered {
	private Long id;
	private String last_name_;
	private String given_name_;
	private String institution_;
	private String phone_;
	private String address_;
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
		return last_name_;
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
		this.last_name_ = last_name;
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
		return given_name_;
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
		this.given_name_ = given_name;
	}

	/**
	 * getInstitution
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the institution
	 */
	@Column(name="institution")
	public String getInstitution() {
		return institution_;
	}

	/**
	 * setInstitution
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param institution the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution_ = institution;
	}

	/**
	 * getPhone
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the phone
	 */
	@Column(name="phone")
	public String getPhone() {
		return phone_;
	}

	/**
	 * setPhone
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone_ = phone;
	}

	/**
	 * getAddress
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the address
	 */
	@Column(name="address")
	public String getAddress() {
		return address_;
	}

	/**
	 * setAddress
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		21/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address_ = address;
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
