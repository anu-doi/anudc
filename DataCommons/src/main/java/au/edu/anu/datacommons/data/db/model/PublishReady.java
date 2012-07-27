package au.edu.anu.datacommons.data.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PublishReady
 * 
 * Australian National University Data Commons
 * 
 * A class that indicates that the object is ready for publish
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="publish_ready")
public class PublishReady {
	private Long id;
	private Date date_submitted;
	
	/**
	 * getId
	 *
	 * Gets the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 *
	 * Sets the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getDate_submitted
	 *
	 * Gets the date submitted
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the date_submitted
	 */
	@Column(name="date_submitted")
	public Date getDate_submitted() {
		return date_submitted;
	}
	
	/**
	 * setDate_submitted
	 *
	 * Sets the date submitted
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param date_submitted the date_submitted to set
	 */
	public void setDate_submitted(Date date_submitted) {
		this.date_submitted = date_submitted;
	}
}
