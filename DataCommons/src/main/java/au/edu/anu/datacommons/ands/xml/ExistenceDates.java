package au.edu.anu.datacommons.ands.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * ExistenceDates
 * 
 * Australian National University Data Commons
 * 
 * Class for the existenceDates element in the ANDS RIF-CS schema
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ExistenceDates {
	private ANDSDate startDate;
	private ANDSDate endDate;
	
	/**
	 * getStartDate
	 *
	 * Get the start date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the startDate
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getStartDate() {
		return startDate;
	}
	
	/**
	 * setStartDate
	 *
	 * Set the start date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(ANDSDate startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * getEndDate
	 *
	 * Get the end date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the endDate
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getEndDate() {
		return endDate;
	}
	
	/**
	 * setEndDate
	 *
	 * Set the end date for the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(ANDSDate endDate) {
		this.endDate = endDate;
	}
}
