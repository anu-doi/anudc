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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

/**
 * RegistryObject
 * 
 * Australian National University Data Commons
 * 
 * Class for the registryObject element in the ANDS RIF-CS schema
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
public class RegistryObject {
	private String group;
	private String key;
	private String originatingSource;
	private ObjectType objectType;
	
	/**
	 * getGroup
	 *
	 * Get the registry object group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the group
	 */
	@NotNull(message = "Quality Level 1 - A Group is required")
	@XmlAttribute
	public String getGroup() {
		return group;
	}
	
	/**
	 * setGroup
	 *
	 * Set the registry object group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * getKey
	 *
	 * Get the registry object key
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the key
	 */
	@NotNull(message="Quality Level 1 - A valid key is required")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getKey() {
		return key;
	}
	
	/**
	 * setKey
	 *
	 * Set the registry object key
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * getOriginatingSource
	 *
	 * Get the originating source
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the originatingSource
	 */
	@NotNull(message="Quality Level 1 - An originating source is required")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getOriginatingSource() {
		return originatingSource;
	}
	
	/**
	 * setOriginatingSource
	 *
	 * Set the originating source
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param originatingSource the originatingSource to set
	 */
	public void setOriginatingSource(String originatingSource) {
		this.originatingSource = originatingSource;
	}

	/**
	 * getObjectType
	 *
	 * Get the object type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the objectType
	 */
	@NotNull(message="Quality Level 1 - An object type is required (i.e. activity, collection, party, or service)")
	@Valid
	@XmlElementRefs(
			{
			@XmlElementRef(type=Collection.class, name="collection", namespace=Constants.ANDS_RIF_CS_NS),
			@XmlElementRef(type=Activity.class, name="activity", namespace=Constants.ANDS_RIF_CS_NS),
			@XmlElementRef(type=Party.class, name="party", namespace=Constants.ANDS_RIF_CS_NS),
			@XmlElementRef(type=Service.class, name="service", namespace=Constants.ANDS_RIF_CS_NS)
			}
		)
	public ObjectType getObjectType() {
		return objectType;
	}

	/**
	 * setObjectType
	 *
	 * Set the object type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param objectType the objectType to set
	 */
	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
}
