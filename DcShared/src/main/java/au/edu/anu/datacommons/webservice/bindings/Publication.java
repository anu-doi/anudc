package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlElement;

public class Publication
{
	private String idType;
	private String id;
	private String title;
	
	@XmlElement(name = "pubType")
	public String getIdType()
	{
		return idType;
	}
	public void setIdType(String idType)
	{
		this.idType = idType;
	}
	
	@XmlElement(name = "pubValue")
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	@XmlElement(name = "pubTitle")
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
}
