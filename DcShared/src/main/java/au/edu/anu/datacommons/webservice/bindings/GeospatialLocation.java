package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlElement;

public class GeospatialLocation
{
	private String covAreaType;
	private String covAreaValue;

	@XmlElement(name = "covAreaType")
	public String getCovAreaType()
	{
		return covAreaType;
	}

	public void setCovAreaType(String covAreaType)
	{
		this.covAreaType = covAreaType;
	}

	@XmlElement(name = "covAreaValue")
	public String getCovAreaValue()
	{
		return covAreaValue;
	}

	public void setCovAreaValue(String covAreaValue)
	{
		this.covAreaValue = covAreaValue;
	}

}
