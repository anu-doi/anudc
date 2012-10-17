package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import javax.xml.bind.annotation.XmlElement;

public class Barcode
{
	private String title;
	private String briefDesc;
	
	@XmlElement(name = "title")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlElement(name = "briefDesc")
	public String getBriefDesc()
	{
		return briefDesc;
	}

	public void setBriefDesc(String briefDesc)
	{
		this.briefDesc = briefDesc;
	}
}
