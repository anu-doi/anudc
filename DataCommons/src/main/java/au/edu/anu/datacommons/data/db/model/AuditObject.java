/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.data.db.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AuditObject
 * 
 * Australian National University Data Commons
 * 
 * Object for the audit_object table that contains auditing rows for objects
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		09/11/2012	Genevieve Turner (GT)	Updated for request id
 * </pre>
 *
 */
@Entity
@Table(name="audit_object")
public class AuditObject {
	Long id_;
	Date log_date_;
	String log_type_;
	Long object_id_;
	Long user_id_;
	String before_;
	String after_;
	private Long rid_;
	
	/**
	 * getId
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
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
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id
	 */
	public void setId(Long id) {
		this.id_ = id;
	}
	
	/**
	 * getLog_date
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the log date
	 */
	@Column(name="log_date")
	public Date getLog_date() {
		return log_date_;
	}

	/**
	 * setLog_date
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param log_date the log date to set
	 */
	public void setLog_date(Date log_date) {
		this.log_date_ = log_date;
	}

	/**
	 * getLog_type
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the log type
	 */
	@Column(name="log_type")
	public String getLog_type() {
		return log_type_;
	}

	/**
	 * setLog_type
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param log_type the log type to set
	 */
	public void setLog_type(String log_type) {
		this.log_type_ = log_type;
	}

	/**
	 * getObject_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the object id
	 */
	@Column(name="object_id")
	public Long getObject_id() {
		return object_id_;
	}

	/**
	 * setObject_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param object_id the object id to set
	 */
	public void setObject_id(Long object_id) {
		this.object_id_ = object_id;
	}

	/**
	 * getUser_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the user id
	 */
	@Column(name="user_id")
	public Long getUser_id() {
		return user_id_;
	}

	/**
	 * setUser_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user_id the user id to set
	 */
	public void setUser_id(Long user_id) {
		this.user_id_ = user_id;
	}

	/**
	 * getBefore
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value before
	 */
	@Column(name="before")
	public String getBefore() {
		return before_;
	}

	/**
	 * setBefore
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param before the value before to set
	 */
	public void setBefore(String before) {
		this.before_ = before;
	}

	/**
	 * getAfter
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value after
	 */
	@Column(name="after")
	public String getAfter() {
		return after_;
	}

	/**
	 * setAfter
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param after the value after to set
	 */
	public void setAfter(String after) {
		this.after_ = after;
	}

	/**
	 * getRid
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		09/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the rid
	 */
	@Column(name="rid")
	public Long getRid() {
		return rid_;
	}

	/**
	 * setRid
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		09/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid_ = rid;
	}
}
