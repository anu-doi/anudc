package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PublishIris
 * 
 * Australian National University Data Commons
 * 
 * Entity class for the publish_iris database table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/11/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="publish_iris")
public class PublishIris {
	private PublishIrisPK id;
	private String status;
	private String iris_network;
	
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	public PublishIrisPK getId() {
		return id;
	}

	/**
	 * setId
	 *
	 * Set the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(PublishIrisPK id) {
		this.id = id;
	}

	/**
	 * getStatus
	 *
	 * Get the status
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the status
	 */
	@Column(name="status")
	public String getStatus() {
		return status;
	}
	
	/**
	 * setStatus
	 *
	 * Set the status
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * getIris_network
	 *
	 * Get the iris network
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the iris_network
	 */
	@Column(name="iris_network")
	public String getIris_network() {
		return iris_network;
	}
	
	/**
	 * setIris_network
	 *
	 * Set the iris network
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param iris_network the iris_network to set
	 */
	public void setIris_network(String iris_network) {
		this.iris_network = iris_network;
	}
}