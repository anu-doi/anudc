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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ReportAutoParamPK
 *
 * Australian National University Data Commons
 * 
 * Class for the primary key for 'ReportAutoParams'.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Embeddable
public class ReportAutoParamPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Integer seqNum;
	
	/**
	 * Get the report auto id
	 * 
	 * @return The id
	 */
	@Column(name="id")
	public Long getId() {
		return id;
	}
	
	/**
	 * Set the report auto id
	 * 
	 * @param id The id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the parameter sequence number
	 * 
	 * @return The sequence number
	 */
	@Column(name="seq_num")
	public Integer getSeqNum() {
		return seqNum;
	}
	
	/**
	 * Set the parameter sequence number
	 * 
	 * @param seqNum The sequence number
	 */
	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		
		if (id != null) {
			hashCode = 17 * hashCode + id.hashCode();
		}
		if (seqNum != null) {
			hashCode = 17 * hashCode + seqNum.hashCode();
		}
		
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ReportAutoParamPK)) {
			return false;
		}
		ReportParamPK other = (ReportParamPK) obj;
		
		return (
				((this.getId() == other.getId()) || (this.getId() != null && this.getId().equals(other.getId()))) &&
				((this.getSeqNum() == other.getSeqNum()) || (this.getSeqNum() != null && this.getSeqNum().equals(other.getSeqNum())))
				);
	}
}
