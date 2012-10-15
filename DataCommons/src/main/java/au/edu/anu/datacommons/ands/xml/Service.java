package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Service
 * 
 * Australian National University Data Commons
 * 
 * Class for the service element in the ANDS RIF-CS schema
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
@XmlRootElement(name="service")
public class Service extends ObjectType {
	private List<ExistenceDates> existenceDates;
	private List<String> accessPolicies;

	/**
	 * Constructor
	 * 
	 * Constructor for the Service class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public Service() {
		existenceDates = new ArrayList<ExistenceDates>();
	}
	
	/**
	 * getExistenceDates
	 *
	 * Get the existence dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the existenceDates
	 */
	@XmlElement(name="existenceDates", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ExistenceDates> getExistenceDates() {
		return existenceDates;
	}

	/**
	 * setExistenceDates
	 *
	 * Set the existencd dates
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

	/**
	 * getAccessPolicy
	 *
	 * Get the access policy
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the accessPolicy
	 */
	@XmlElement(name="accessPolicy", namespace=Constants.ANDS_RIF_CS_NS)
	public List<String> getAccessPolicies() {
		return accessPolicies;
	}

	/**
	 * setAccessPolicy
	 *
	 * Set the access policy
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param accessPolicy the accessPolicy to set
	 */
	public void setAccessPolicies(List<String> accessPolicies) {
		this.accessPolicies = accessPolicies;
	}
}
