package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * PhysicalAddress
 * 
 * Australian National University Data Commons
 * 
 * Class for the physical element in the ANDS RIF-CS schema
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
@XmlRootElement(name="physical")
public class PhysicalAddress {
	private String type;
	private String lang;
	private List<AddressPart> addressParts;
	
	/**
	 * Constructor
	 * 
	 * The constructor for the PhysicalAddress class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public PhysicalAddress() {
		addressParts = new ArrayList<AddressPart>();
	}
	
	/**
	 * getType
	 *
	 * Get the physical address type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@XmlAttribute
	public String getType() {
		return type;
	}

	/**
	 * setType
	 *
	 * Set the physical address type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getLang
	 *
	 * Get the physical address language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the lang
	 */
	@XmlAttribute
	public String getLang() {
		return lang;
	}
	
	/**
	 * setLang
	 *
	 * Set the physical address language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	/**
	 * getAddressParts
	 *
	 * Set the addressParts
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the addressParts
	 */
	@Valid
	@Size(min=1, message="Each physical address must have an address part")
	@XmlElement(name="addressPart", namespace=Constants.ANDS_RIF_CS_NS)
	public List<AddressPart> getAddressParts() {
		return addressParts;
	}
	
	/**
	 * setAddressParts
	 *
	 * Get the addressParts
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param addressParts the addressParts to set
	 */
	public void setAddressParts(List<AddressPart> addressParts) {
		this.addressParts = addressParts;
	}
}
