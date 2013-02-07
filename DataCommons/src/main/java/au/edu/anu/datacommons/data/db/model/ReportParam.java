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
 * ReportParam
 * 
 * Australian National University Data Commons
 * 
 * Entity for the report_param table
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
@Entity
@Table(name="report_param")
public class ReportParam {
	private ReportParamPK id;
	private String paramName;
	private String requestParam;
	private String defaultValue;
	
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	public ReportParamPK getId() {
		return id;
	}

	/**
	 * setId
	 *
	 * Set the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(ReportParamPK id) {
		this.id = id;
	}
	
	/**
	 * getParamName
	 *
	 * Get the parameter name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the paramName
	 */
	@Column(name="param_name")
	public String getParamName() {
		return paramName;
	}
	
	/**
	 * setParamName
	 *
	 * Set the parameter name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	/**
	 * getRequestParam
	 *
	 * Get the request parameter name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the requestParam
	 */
	@Column(name="request_param")
	public String getRequestParam() {
		return requestParam;
	}
	
	/**
	 * setRequestParam
	 *
	 * Set the request parameter name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param requestParam the requestParam to set
	 */
	public void setRequestParam(String requestParam) {
		this.requestParam = requestParam;
	}
	
	/**
	 * getDefaultValue
	 *
	 * Get the default value to send to the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the defaultValue
	 */
	@Column(name="default_value")
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * setDefaultValue
	 *
	 * Set the default value to send the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
