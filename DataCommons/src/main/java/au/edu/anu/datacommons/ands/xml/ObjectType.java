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

package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.ands.check.ActivityCheck;
import au.edu.anu.datacommons.ands.check.CollectionCheck;
import au.edu.anu.datacommons.ands.check.PartyCheck;
import au.edu.anu.datacommons.ands.check.ServiceCheck;
import au.edu.anu.datacommons.validator.RequiredProperty;

/**
 * ObjectType
 * 
 * Australian National University Data Commons
 * 
 * Abstract class for the Activity, Collection, Party and Service objects
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public abstract class ObjectType {
	private String type;
	private List<Identifier> identifiers;
	private List<Name> names;
	private List<Location> locations;
	private List<RelatedObject> relatedObjects;
	private List<Subject> subjects;
	private List<Description> descriptions;
	private List<Coverage> coverage;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the ObjectType class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public ObjectType() {
		identifiers = new ArrayList<Identifier>();
		names = new ArrayList<Name>();
		locations = new ArrayList<Location>();
		relatedObjects = new ArrayList<RelatedObject>();
		subjects = new ArrayList<Subject>();
		descriptions = new ArrayList<Description>();
		coverage = new ArrayList<Coverage>();
	}
	
	/**
	 * getType
	 *
	 * Get the type for the object. e.g. for a Collection it could be a dataset, for an
	 * Activity it could be a project.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@NotNull(message="Quality Level 1 - A sub type is required")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the type for the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
		
	}
	
	/**
	 * getIdentifiers
	 *
	 * Get the objects identifiers
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifiers
	 */
	@Valid
	@Size(min=1, message="Quality Level 3 - At least one identifier", groups={CollectionCheck.class, PartyCheck.class})
	@XmlElement(name="identifier", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Identifier> getIdentifiers() {
		return identifiers;
	}
	
	/**
	 * setIdentifiers
	 *
	 * Set the objects identifiers
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifiers the identifiers to set
	 */
	public void setIdentifiers(List<Identifier> identifiers) {
		this.identifiers = identifiers;
	}
	
	/**
	 * getNames
	 *
	 * Get the objects names
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the names
	 */
	//TODO make sure that there is a primary name
	@Valid
	@RequiredProperty(subFieldName="type",expectedValues={"primary"}, message="Quality Level 2 - A primary name is required")
	//@Size(min=1, message="Quality Level 2 - A primary name is required")
	@XmlElement(name="name", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Name> getNames() {
		return names;
	}
	
	/**
	 * setNames
	 *
	 * Set the objects names
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param names the names to set
	 */
	public void setNames(List<Name> names) {
		this.names = names;
	}

	/**
	 * getLocations
	 *
	 * Get the objects locations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the locations
	 */
	//TODO make sure there is an address location
	@Valid
	@Size(min=1, message="Quality Level 2 - A location is required")
	@XmlElement(name="location", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Location> getLocations() {
		return locations;
	}

	/**
	 * setLocations
	 *
	 * Set the objects locations
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param locations the locations to set
	 */
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	/**
	 * getRelatedObjects
	 *
	 * Get the related objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the relatedObjects
	 */
	@Valid
	@XmlElement(name="relatedObject", namespace=Constants.ANDS_RIF_CS_NS)
	public List<RelatedObject> getRelatedObjects() {
		return relatedObjects;
	}

	/**
	 * setRelatedObjects
	 *
	 * Set the related objects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param relatedObjects the relatedObjects to set
	 */
	public void setRelatedObjects(List<RelatedObject> relatedObjects) {
		this.relatedObjects = relatedObjects;
	}

	/**
	 * getSubjects
	 *
	 * Get objects the subjects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the subjects
	 */
	//TODO make sure that it has subjects non services, and that it has at least one anzsrc-for subject
	@Valid
	@Size(min=1, message="Quality Level 3 - An ANZSRC-FOR subject is required",groups={ActivityCheck.class,CollectionCheck.class,Party.class})
	@XmlElement(name="subject", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Subject> getSubjects() {
		return subjects;
	}

	/**
	 * setSubjects
	 *
	 * Set objects the subjects
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param subjects the subjects to set
	 */
	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	/**
	 * getDescriptions
	 *
	 * Get objects the descriptions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the descriptions
	 */
	@Valid
	@RequiredProperty.List(
		{
			@RequiredProperty(subFieldName="type",expectedValues={"brief","full"}, message="Quality Level 2 - A brief or full description is required",groups={CollectionCheck.class,ActivityCheck.class})
			,@RequiredProperty(subFieldName="type",expectedValues={"brief","full"}, message="Quality Level 3 - A brief or full description is required",groups={PartyCheck.class, ServiceCheck.class})
		}
	)
	@XmlElement(name="description", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Description> getDescriptions() {
		return descriptions;
	}

	/**
	 * setDescriptions
	 *
	 * Set objects the descriptions
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * getCoverage
	 *
	 * Get the objects coverage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the coverage
	 */
	@Valid
	//TODO add checking to ensure that both a temporal and spatial coverage exists - unsure as to how to do this at this point
	@Size(min=1, message="Quality Level 3 - At least one temporal and spatial coverage",groups={CollectionCheck.class})
	@XmlElement(name="coverage", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Coverage> getCoverage() {
		return coverage;
	}

	/**
	 * setCoverage
	 *
	 * Set the objects coverage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param coverage the coverage to set
	 */
	public void setCoverage(List<Coverage> coverage) {
		this.coverage = coverage;
	}
}
