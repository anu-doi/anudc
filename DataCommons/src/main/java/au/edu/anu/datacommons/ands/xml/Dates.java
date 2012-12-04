package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Dates
 * 
 * Australian National University Data Commons
 * 
 * Class for the dates element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Dates {
	private String type;
	private List<ANDSDate> date;
	
	public Dates() {
		date = new ArrayList<ANDSDate>();
	}
	
	/**
	 * getType
	 *
	 * Get the dates type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@XmlAttribute
	@NotNull(message="The dates type must not be null")
	@Pattern(regexp="^available|created|dateAccepted|dateSubmitted|issues|valid$", 
			message="The dates field should be one of type available, created, dateAccepted, dateSubmitted, issued, or valid")
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the dates type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getDate
	 *
	 * Get the dates dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the date
	 */
	@Valid
	@Size(min=1, message="At least one dates element is recommended for the collection (For example the date of data creation)")
	@XmlElement(name="date", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ANDSDate> getDate() {
		return date;
	}
	
	/**
	 * setDate
	 *
	 * Set the dates dates
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param date the date to set
	 */
	public void setDate(List<ANDSDate> date) {
		this.date = date;
	}
}
