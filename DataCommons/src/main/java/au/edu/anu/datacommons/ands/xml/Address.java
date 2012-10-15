package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * Address
 * 
 * Australian National University Data Commons
 * 
 * Relates to the address object in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@AtLeastOneOf(fieldNames={"electronicAddresses", "physicalAddresses"}, message="Each location must have an address")
public class Address {
	private List<ElectronicAddress> electronicAddresses;
	private List<PhysicalAddress> physicalAddresses;
	
	/**
	 * Constructor
	 * 
	 * Constructor class that initialises lists
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		05/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public Address() {
		electronicAddresses = new ArrayList<ElectronicAddress>();
		physicalAddresses = new ArrayList<PhysicalAddress>();
	}

	/**
	 * getElectronicAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the electronicAddresses
	 */
	@Valid
	@XmlElement(name="electronic", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ElectronicAddress> getElectronicAddresses() {
		return electronicAddresses;
	}

	/**
	 * setElectronicAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param electronicAddresses the electronicAddresses to set
	 */
	public void setElectronicAddresses(List<ElectronicAddress> electronicAddresses) {
		this.electronicAddresses = electronicAddresses;
	}

	/**
	 * getPhysicalAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the physicalAddresses
	 */
	@Valid
	@XmlElement(name="physical", namespace=Constants.ANDS_RIF_CS_NS)
	public List<PhysicalAddress> getPhysicalAddresses() {
		return physicalAddresses;
	}

	/**
	 * setPhysicalAddresses
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param physicalAddresses the physicalAddresses to set
	 */
	public void setPhysicalAddresses(List<PhysicalAddress> physicalAddresses) {
		this.physicalAddresses = physicalAddresses;
	}

}
