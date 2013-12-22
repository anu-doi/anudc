package au.edu.anu.dcclient.shibboleth.idp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ShibbolethIdp {
	private String entityID;
	private List<ShibbolethDisplayName> displayNames = new ArrayList<ShibbolethDisplayName>();
	
	public ShibbolethIdp() {
		
	}

	@XmlElement(name="entityID")
	public String getEntityID() {
		return entityID;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	@XmlElement(name="DisplayNames")
	public List<ShibbolethDisplayName> getDisplayNames() {
		return displayNames;
	}

	public void setDisplayNames(List<ShibbolethDisplayName> displayNames) {
		this.displayNames = displayNames;
	}
	
	@Override
	public String toString() {
		if (displayNames != null && displayNames.size() > 0) {
			for (ShibbolethDisplayName displayName : displayNames) {
				if ("en".equals(displayName.getLanguage())) {
					return displayName.getValue();
				}
			}
			return displayNames.get(0).getValue();
		}
		return entityID;
	}
}
