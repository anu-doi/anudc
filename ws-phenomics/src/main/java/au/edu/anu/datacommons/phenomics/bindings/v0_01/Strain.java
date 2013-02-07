/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.phenomics.bindings.v0_01;

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

	@XmlAttribute(name = "phid")
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
