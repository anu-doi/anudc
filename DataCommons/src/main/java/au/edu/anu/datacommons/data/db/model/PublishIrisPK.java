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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PublishIrisPK
 * 
 * Australian National University Data Commons
 * 
 * Primary Key for the publish_iris table
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
@Embeddable
public class PublishIrisPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String pid;
	private Date publishDate;
	
	/**
	 * getId
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Column(name="pid")
	public String getPid() {
		return pid;
	}
	
	/**
	 * setId
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	/**
	 * getPublishDate
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the publishDate
	 */
	@Column(name="publish_date")
	public Date getPublishDate() {
		return publishDate;
	}
	
	/**
	 * setPublishDate
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	
	/**
	 * hashCode
	 * 
	 * Overrides the hashCode method
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The hash code
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) pid.hashCode() + publishDate.hashCode();
	}
	
	/**
	 * equals
	 * 
	 * Overrides the equals method
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param obj The object to compare
	 * @return Whether the objects are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PublishIrisPK)) {
			return false;
		}
		PublishIrisPK pk = (PublishIrisPK) obj;
		return pk.getPid().equals(pid) && pk.getPublishDate().equals(publishDate);
	}
}
