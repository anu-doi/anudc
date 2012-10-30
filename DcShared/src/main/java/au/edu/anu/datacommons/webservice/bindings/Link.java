package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class Link
{
	private String url;
	private String filename;
	private Boolean refOnly;

	@XmlValue
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
	
	@XmlAttribute(name = "filename")
	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	@XmlAttribute(name = "reference-only")
	public Boolean isRefOnly()
	{
		return refOnly;
	}

	public void setRefOnly(Boolean refOnly)
	{
		this.refOnly = refOnly;
	}
}
