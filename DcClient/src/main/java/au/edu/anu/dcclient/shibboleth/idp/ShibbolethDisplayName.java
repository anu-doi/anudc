package au.edu.anu.dcclient.shibboleth.idp;

import javax.xml.bind.annotation.XmlElement;

public class ShibbolethDisplayName {
	private String value;
	private String language;
	
	public ShibbolethDisplayName() 
	{
		
	}

	@XmlElement(name="value")
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@XmlElement(name="lang")
	public String getLanguage() 
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}
}
