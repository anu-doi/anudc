package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class PhenProject
{
	private String title;
	private String briefDesc;
	private List<Strain> strains;

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

	@XmlElementWrapper(name = "strains")
	@XmlElement(name = "strain")
	public List<Strain> getStrains()
	{
		return strains;
	}

	public void setStrains(List<Strain> strains)
	{
		this.strains = strains;
	}
}
