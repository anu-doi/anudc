package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Activity
 * 
 * Australian National University Data Commons
 * 
 * Activitiy class that extends the ObjectType
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 */
@XmlRootElement(name="activity")
public class Activity extends ObjectType {
	private List<ExistenceDates> existenceDates;

	public Activity() {
		existenceDates = new ArrayList<ExistenceDates>();
	}
	
	/**
	 * getExistenceDates
	 *
	 * Gets the existence dates for the activity
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the existenceDates
	 */
	@Size(min=1, message="Quality Level 3 - At least one set of existence dates")
	@XmlElement(name="existenceDates", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ExistenceDates> getExistenceDates() {
		return existenceDates;
	}

	/**
	 * setExistenceDates
	 *
	 * Sets the existence dates for the activity
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param existenceDates the existenceDates to set
	 */
	public void setExistenceDates(List<ExistenceDates> existenceDates) {
		this.existenceDates = existenceDates;
	}
}
