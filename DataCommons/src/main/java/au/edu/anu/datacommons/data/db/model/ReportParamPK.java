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
