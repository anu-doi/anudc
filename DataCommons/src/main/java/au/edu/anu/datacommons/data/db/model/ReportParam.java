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
