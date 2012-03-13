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
 * AccessUser
 * 
 * Australian National University Data Commons
 * 
 * Entity object for the 'access_user' table.
 * 
 * JUnit coverage:
 * AccessUserTest
 * AccessGroupTest
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 * 
 */
@Entity
@Table(name="access_user")
public class AccessUser
{
	private Long id;
	private String uid;
	private List<AccessGroup> accessGroups;
	
	public AccessUser()
	{
	}
	
	public AccessUser(Long id, String uid)
	{
		this.id = id;
		this.uid = uid;
	}
	
	/**
	 * getId
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return Returns the id for the user_id table;
	 */
	@Id
	@Column(name="user_id")
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id for the row in the user_id table
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param id The id of the table
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * getUid
	 * 
	 * Gets the uid for the row in the user_id table
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The uid of the user
	 */
	@Column(name="user_uid")
	public String getUid()
	{
		return uid;
	}
	
	/**
	 * setUid
	 * 
	 * Sets the uid for the row in the user_id table
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param uid The uid of the user
	 */
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	
	/**
	 * getAccessGroups
	 * 
	 * Gets the access groups associated with the user
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The access groups associated with the user
	 */
	@ManyToMany
	@JoinTable(name = "user_group",
			joinColumns = @JoinColumn(name="user_id", referencedColumnName="user_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName="group_id"))
	public List<AccessGroup> getAccessGroups()
	{
		return accessGroups;
	}
	
	/**
	 * setAccessGroups
	 * 
	 * Sets the access groups associated with the user
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param accessGroups The access groups associated with the user
	 */
	public void setAccessGroups(List<AccessGroup> accessGroups)
	{
		this.accessGroups = accessGroups;
	}
}
