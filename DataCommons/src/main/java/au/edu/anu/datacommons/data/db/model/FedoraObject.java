package au.edu.anu.datacommons.data.db.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * Domains
 * 
 * Australian National University Data Comons
 * 
 * Domain object
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
@Table(name="fedora_object")
public class FedoraObject {
	private Long id;
	private String object_id;
	private Long group_id;
	private Boolean published;
	private List<PublishLocation> publishedLocations;
	
	public FedoraObject() {
		publishedLocations = new ArrayList<PublishLocation>();
	}
	
	/**
	 * getId
	 * 
	 * Gets the id of the object
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
	 * getObject_id
	 * 
	 * Gets the pid of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The pid of the object
	 */
	@Column(name="pid")
	public String getObject_id() {
		return object_id;
	}
	
	/**
	 * setObject_id
	 * 
	 * Sets the pid of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param object_id  The pid of the object
	 */
	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}
	
	/**
	 * getGroup_id
	 * 
	 * Gets the group id of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The group id of the object
	 */
	@Column(name="group_id")
	public Long getGroup_id() {
		return group_id;
	}
	
	/**
	 * setGroup_id
	 * 
	 * Sets the group id of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param group_id The group id of the object
	 */
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	/**
	 * getPublished
	 * 
	 * Gets the published flag
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The published state
	 */
	public Boolean getPublished() {
		return published;
	}

	/**
	 * setPublished
	 * 
	 * Sets the published flag
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param published The published state
	 */
	public void setPublished(Boolean published) {
		this.published = published;
	}

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="published",
			joinColumns=@JoinColumn(name="fedora_id", referencedColumnName="id")
			,inverseJoinColumns=@JoinColumn(name="location_id", referencedColumnName="id")
	)
	public List<PublishLocation> getPublishedLocations() {
		return publishedLocations;
	}

	public void setPublishedLocations(List<PublishLocation> publishedLocations) {
		this.publishedLocations = publishedLocations;
	}
}
