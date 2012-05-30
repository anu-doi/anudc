package au.edu.anu.datacommons.xml.other;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import au.edu.anu.datacommons.data.db.model.Groups;

@XmlRootElement(name="options")
@XmlSeeAlso(Groups.class)
public class OptionList {
	List<Groups> groups;
	
	public OptionList() {
		groups = new ArrayList<Groups>();
	}
	
	@XmlElement(name="ownerGroup")
	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}
	
}
