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
