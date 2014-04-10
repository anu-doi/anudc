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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ReportAutoParam
 *
 * Australian National University Data Commons
 * 
 * Entity class for the 'report_auto_param' table.  This class is utilised in automatically generating and emailing a report.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Entity
@Table(name="report_auto_param")
public class ReportAutoParam {
	private ReportAutoParamPK id;
	private String param;
	private String paramVal;
	
	/**
	 * Get the id
	 * 
	 * @return The id
	 */
	@Id
	public ReportAutoParamPK getId() {
		return id;
	}
	
	/**
	 * Set the id
	 * 
	 * @param id The id
	 */
	public void setId(ReportAutoParamPK id) {
		this.id = id;
	}
	
	/**
	 * Get the parameter name
	 * 
	 * @return The parameter name
	 */
	@Column(name="param")
	public String getParam() {
		return param;
	}
	
	/**
	 * Set the parameter name
	 * 
	 * @param param The parameter name
	 */
	public void setParam(String param) {
		this.param = param;
	}
	
	/**
	 * Get the parameter value
	 * 
	 * @return The parameter value
	 */
	@Column(name="param_val")
	public String getParamVal() {
		return paramVal;
	}
	
	/**
	 * Set the parameter value
	 * 
	 * @param paramVal The parameter value
	 */
	public void setParamVal(String paramVal) {
		this.paramVal = paramVal;
	}
}
