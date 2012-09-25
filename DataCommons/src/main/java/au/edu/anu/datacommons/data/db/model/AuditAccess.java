package au.edu.anu.datacommons.data.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="audit_access")
public class AuditAccess {
	private Long id;
	private Date accessDate;
	private String ipAddress;
	private String url;
	private String method;
	private String pid;
	private String username;
	
	/**
	 * getId
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
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
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getAccessDate
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the accessDate
	 */
	@Column(name="access_date")
	public Date getAccessDate() {
		return accessDate;
	}
	
	/**
	 * setAccessDate
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param accessDate the accessDate to set
	 */
	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}
	
	/**
	 * getIpAddress
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the ipAddress
	 */
	@Column(name="ip_address")
	public String getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * setIpAddress
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * getUrl
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the url
	 */
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	
	/**
	 * setUrl
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * getMethod
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the method
	 */
	@Column(name="method")
	public String getMethod() {
		return method;
	}
	
	/**
	 * setMethod
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * getPid
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the pid
	 */
	@Column(name="pid")
	public String getPid() {
		return pid;
	}
	
	/**
	 * setPid
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	/**
	 * getUsername
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the username
	 */
	@Column(name="username")
	public String getUsername() {
		return username;
	}
	
	/**
	 * setUsername
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		24/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
