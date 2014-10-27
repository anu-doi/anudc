/**
 * 
 */
package au.edu.anu.datacommons.external;

/**
 * @author Rahul Khanna
 *
 */
public class ParamInfo {
	private String name;
	private String friendlyName;

	public ParamInfo(String name, String friendlyName) {
		super();
		this.name = name;
		this.friendlyName = friendlyName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
}
