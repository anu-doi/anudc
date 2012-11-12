package au.edu.anu.datacommons.digitalhumanities.bindings.v0_01;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Strain
{
	private String extId;
	private String anudcId;
	
	private String title;
	private String briefDesc;
	private List<Animal> animals;

	@XmlAttribute(name = "dhid")
	public String getExtId()
	{
		return extId;
	}

	public void setExtId(String extId)
	{
		this.extId = extId;
	}
	
	@XmlAttribute(name = "anudcid")
	public String getAnudcId()
	{
		return anudcId;
	}

	public void setAnudcId(String anudcId)
	{
		this.anudcId = anudcId;
	}

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

	@XmlElement(name = "animal")
	public List<Animal> getAnimals()
	{
		return animals;
	}

	public void setAnimals(List<Animal> animals)
	{
		this.animals = animals;
	}
}
