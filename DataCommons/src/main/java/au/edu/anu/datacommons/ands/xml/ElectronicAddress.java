package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.edu.anu.datacommons.ands.check.ActivityCheck;
import au.edu.anu.datacommons.ands.check.CollectionCheck;
import au.edu.anu.datacommons.ands.check.PartyCheck;

/**
 * ElectronicAddress
 * 
 * Australian National University Data Commons
 * 
 * Class for the electronic element in the ANDS RIF-CS schema
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
@XmlRootElement(name="electronic")
public class ElectronicAddress {
	private String type;
	private String value;
	private List<ElectronicAddressArgument> args;
	//TODO filter out types for electronic addresses
	public ElectronicAddress() {
		args = new ArrayList<ElectronicAddressArgument>();
	}
	
	/**
	 * getType
	 *
	 * Get the type of electronic address
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
	 * Set the type of electronic address
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
	 * getValue
	 *
	 * Get the value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@NotNull(message="Electronic Address value may not be null")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getValue() {
		return value;
	}
	
	/**
	 * setValue
	 *
	 * Set the value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * getArgs
	 *
	 * Get the arguments
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the args
	 */
	//TODO make sure that these can only be there for services
	@Valid
	@Size(max=0, message="There should be no electronic address arguments", groups={CollectionCheck.class, ActivityCheck.class, PartyCheck.class})
	@XmlElement(name="arg", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ElectronicAddressArgument> getArgs() {
		return args;
	}
	
	/**
	 * setArgs
	 *
	 * Set the arguments
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param args the args to set
	 */
	public void setArgs(List<ElectronicAddressArgument> args) {
		this.args = args;
	}
}
