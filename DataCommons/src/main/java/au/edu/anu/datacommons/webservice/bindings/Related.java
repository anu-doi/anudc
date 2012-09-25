package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

public class Related
{
	private String relatedWebTitle;
	private String relatedWebUrl;
	
	@XmlElement(name = "relatedWebTitle")
	public String getRelatedWebTitle()
	{
		return relatedWebTitle;
	}
	
	public void setRelatedWebTitle(String relatedWebTitle)
	{
		this.relatedWebTitle = relatedWebTitle;
	}
	
	@XmlElement(name = "relatedWebURL")
	public String getRelatedWebUrl()
	{
		return relatedWebUrl;
	}
	
	public void setRelatedWebUrl(String relatedWebUrl)
	{
		this.relatedWebUrl = relatedWebUrl;
	}
}
