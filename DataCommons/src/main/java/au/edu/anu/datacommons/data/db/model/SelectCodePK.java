package au.edu.anu.datacommons.data.db.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * SelectCodePK
 * 
 * Australian National University Data Commons
 * 
 * Primary key type for the SelectCode table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Embeddable
public class SelectCodePK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String select_name_;
	private String code_;
	
	/**
	 * getSelect_name
	 *
	 * Gets field name to which the select code is associated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the select_name
	 */
	public String getSelect_name() {
		return select_name_;
	}
	
	/**
	 * setSelect_name
	 *
	 * Sets field name to which the select code is associated
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param select_name the select_name to set
	 */
	public void setSelect_name(String select_name) {
		this.select_name_ = select_name;
	}
	
	/**
	 * getCode
	 *
	 * Gets the code for the select
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code_;
	}
	
	/**
	 * setCode
	 *
	 * Sets the code for the select
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code_ = code;
	}
	
	/**
	 * hashCode
	 * 
	 * Method to override hashCode
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) select_name_.hashCode() + code_.hashCode();
	}
	
	/**
	 * equals
	 * 
	 * Method to override equals
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SelectCodePK)) {
			return false;
		}
		SelectCodePK pk = (SelectCodePK) obj;
		return pk.getSelect_name().equals(select_name_) && pk.getCode().equals(code_);
	}
}
