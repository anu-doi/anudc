package au.edu.anu.datacommons.connection.db.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * AccessGroup
 * 
 * Australian National University Data Commons
 * 
 * Entity object for the 'access_group' table.
 * 
 * JUnit coverage:
 * AccessGroupTest
 * AccessUserTest
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 * 
 */
@Entity
@Table(name="access_group")
public class AccessGroup
{
	private Long id;
	private String name;
	private Long owner;
	private List<AccessUser> accessUsers;
	
	/**
	 * setId
	 * 
	 * Empty Constructor for the access group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	public AccessGroup()
	{
		
	}
	
	/**
	 * AccessGroup
	 * 
	 * Constructor for access group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param id The id of the group
	 * @param name The name of the group
	 * @param owner The id of the owner of the group
	 */
	public AccessGroup(Long id, String name, Long owner)
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
	}
	
	/**
	 * getId
	 * 
	 * Returns the id of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The id of the group
	 */
	@Id
	@Column(name="group_id")
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param id The id of the group
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * getName
	 * 
	 * Gets the name of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The name of the group
	 */
	@Column(name="group_name")
	public String getName()
	{
		return name;
	}
	
	/**
	 * setName
	 * 
	 * Gets the name of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param name The name of the group
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * getOwner
	 * 
	 * Gets the id of the owner of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The id of the owner of the group
	 */
	@Column(name="group_owner")
	public Long getOwner()
	{
		return owner;
	}
	
	/**
	 * setOwner
	 * 
	 * Sets the id of the owner of the group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param owner The id of the owner of the group
	 */
	public void setOwner(Long owner)
	{
		this.owner = owner;
	}
	
	/**
	 * getAccessUsers
	 * 
	 * Gets the users that are members of this group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The memebers of this group
	 */
	@ManyToMany
	@JoinTable(name = "user_group",
			joinColumns = @JoinColumn(name="group_id", referencedColumnName="group_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName="user_id"))
	public List<AccessUser> getAccessUsers()
	{
		return accessUsers;
	}
	
	/**
	 * setAccessUsers
	 * 
	 * Sets the users that are members of this group
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param accessUsers The memebers of this group
	 */
	public void setAccessUsers(List<AccessUser> accessUsers)
	{
		this.accessUsers = accessUsers;
	}
}
