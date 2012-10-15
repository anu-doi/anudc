package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RegistryObjects
 * 
 * Australian National University Data Commons
 * 
 * Class for the registryObjects element in the ANDS RIF-CS schema
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
@XmlRootElement(name="registryObjects")
public class RegistryObjects {
	private List<RegistryObject> registryObjects;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the RegistryObjects class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public RegistryObjects() {
		registryObjects = new ArrayList<RegistryObject>();
	}

	/**
	 * getRegistryObjects
	 *
	 * Get the registry objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the registryObjects
	 */
	@Size(min=1, message="Quality Level 1 - At least one registry object is required")
	@Valid
	@XmlElement(name="registryObject", namespace=Constants.ANDS_RIF_CS_NS)
	public List<RegistryObject> getRegistryObjects() {
		return registryObjects;
	}

	/**
	 * setRegistryObjects
	 *
	 * Set the registry objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param registryObjects the registryObjects to set
	 */
	public void setRegistryObjects(List<RegistryObject> registryObjects) {
		this.registryObjects = registryObjects;
	}
}
