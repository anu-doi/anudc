package au.edu.anu.datacommons.data.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UserRequestPassword
 * 
 * Australian National University Data Commons
 * 
 * Contains user request password information
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="user_request_pwd")
public class UserRequestPassword {
	private Long id_;
	private Users user_;
	private Date request_date_;
	private String ip_address_;
	private String link_id_;
	private Boolean used_;
	
	/**
	 * getId
	 *
	 * Gets the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id_;
	}
	
	/**
	 * setId
	 *
	 * Sets the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id_ = id;
	}
	
	/**
	 * getUser_id
	 *
	 * Gets the user id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the user id
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	public Users getUser() {
		return user_;
	}
	
	/**
	 * setUser_id
	 *
	 * Sets the user id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user id the user id to set
	 */
	public void setUser(Users user) {
		this.user_ = user;
	}
	
	/**
	 * getRequest_date
	 *
	 * Gets the request date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the request date
	 */
	@Column(name="request_date")
	public Date getRequest_date() {
		return request_date_;
	}
	
	/**
	 * setRequest_date
	 *
	 * Sets the request date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param request date the request date to set
	 */
	public void setRequest_date(Date request_date) {
		this.request_date_ = request_date;
	}
	
	/**
	 * getIp_address
	 *
	 * Gets the ip address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the ip address
	 */
	@Column(name="ip_address")
	public String getIp_address() {
		return ip_address_;
	}
	
	/**
	 * setIp_address
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ip_address the ip_address to set
	 */
	public void setIp_address(String ip_address) {
		this.ip_address_ = ip_address;
	}
	
	/**
	 * getLink_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the link_id
	 */
	@Column(name="link_id")
	public String getLink_id() {
		return link_id_;
	}
	
	/**
	 * setLink_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link_id the link_id to set
	 */
	public void setLink_id(String link_id) {
		this.link_id_ = link_id;
	}
	
	/**
	 * getUsed
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the used
	 */
	@Column(name="used")
	public Boolean getUsed() {
		return used_;
	}
	
	/**
	 * setUsed
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param used the used to set
	 */
	public void setUsed(Boolean used) {
		this.used_ = used;
	}
}
