package au.edu.anu.datacommons.xml.other;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.SelectCode;

/**
 * OptionList
 * 
 * Australian National University Data Commons
 * 
 * Creates a list of options available to the system
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@XmlRootElement(name="options")
@XmlSeeAlso(Groups.class)
public class OptionList {
	List<Groups> groups;
	List<SelectCode> selectCodes;
	
	public OptionList() {
		groups = new ArrayList<Groups>();
		selectCodes = new ArrayList<SelectCode>();
	}
	
	@XmlElement(name="ownerGroup")
	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	/**
	 * getSelectCodes
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the selectCodes
	 */
	@XmlAnyElement
	public List<SelectCode> getSelectCodes() {
		return selectCodes;
	}

	/**
	 * setSelectCodes
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param selectCodes the selectCodes to set
	 */
	public void setSelectCodes(List<SelectCode> selectCodes) {
		this.selectCodes = selectCodes;
	}
}
