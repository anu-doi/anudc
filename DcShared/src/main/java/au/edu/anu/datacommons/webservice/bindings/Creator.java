/**
 * 
 */
package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Rahul Khanna
 *
 */
public class Creator {
	private String citCreatorGiven;
	private String citCreatorSurname;

	@XmlElement(name = "citCreatorGiven")
	public String getCitCreatorGiven() {
		return citCreatorGiven;
	}

	public void setCitCreatorGiven(String citCreatorGiven) {
		this.citCreatorGiven = citCreatorGiven;
	}

	@XmlElement(name = "citCreatorSurname")
	public String getCitCreatorSurname() {
		return citCreatorSurname;
	}

	public void setCitCreatorSurname(String citCreatorSurname) {
		this.citCreatorSurname = citCreatorSurname;
	}

}
