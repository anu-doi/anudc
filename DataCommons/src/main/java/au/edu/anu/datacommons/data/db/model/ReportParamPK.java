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
 * ReportParamPK
 * 
 * Australian National University Data Commons
 * 
 * Class for the primary key for ReportParams'.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Embeddable
public class ReportParamPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Integer seqNum;
	
	/**
	 * getId
	 *
	 * Get the id of the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Column(name="id")
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 *
	 * Set the id of the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getSeqNum
	 *
	 * Get the sequence number of the parameter
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the seqNum
	 */
	@Column(name="seq_num")
	public Integer getSeqNum() {
		return seqNum;
	}
	
	/**
	 * setSeqNum
	 *
	 * Set the sequence number of the parameter
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param seqNum the seqNum to set
	 */
	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}
	
	/**
	 * hashCode
	 * 
	 * Generate the hash code value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The hash code
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) id.hashCode() + seqNum.hashCode();
	}
	
	/**
	 * equals
	 * 
	 * Check if an object is equal to another 
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return Whether the value equals the other
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ReportParamPK)) {
			return false;
		}
		ReportParamPK pk = (ReportParamPK) obj;
		return pk.getId().equals(id) && pk.getSeqNum().equals(seqNum);
	}
}
