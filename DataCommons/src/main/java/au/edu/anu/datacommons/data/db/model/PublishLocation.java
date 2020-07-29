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

package au.edu.anu.datacommons.data.db.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * PublishLocation
 * 
 * Australian National University Data Commons
 * 
 * A publish location
 *
 * JUnit Coverage:
 * PublishLocationDAOTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		XX/XX/XXXX	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="publish_location")
public class PublishLocation {
	private Long id;
	private String code;
	private String name;
	private String execute_class;
	private Long requires;
	private List<FedoraObject> fedoraObjects;
	private List<Template> templates;
	
	/**
	 * Get the id
	 * 
	 * @return The id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * Set the id
	 * 
	 * @param id  The id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the code
	 * 
	 * @return The code
	 */
	@Column(name="code")
	public String getCode() {
		return code;
	}

	/**
	 * Set the code
	 * 
	 * @param code The code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Get the name
	 * 
	 * @return The name
	 */
	@Column(name="name")
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the execution class
	 * 
	 * @return The execution class
	 */
	@Column(name="execute_class")
	public String getExecute_class() {
		return execute_class;
	}

	/**
	 * Set the execution class
	 * 
	 * @param execute_class The execution class
	 */
	public void setExecute_class(String execute_class) {
		this.execute_class = execute_class;
	}
	
	/**
	 * Get the id of another publish location that is required for this object (unused)
	 * 
	 * @return The required publish location id
	 */
	@Column(name="requires")
	public Long getRequires() {
		return requires;
	}
	
	/**
	 * Set the id of another publish location that is required for this object (unused)
	 * 
	 * @param requires The required publish location id
	 */
	public void setRequires(Long requires) {
		this.requires = requires;
	}
	
	/**
	 * Get the fedora objects that have been published to this location
	 * 
	 * @return The fedora objects
	 */
	@ManyToMany(mappedBy="publishedLocations", fetch=FetchType.LAZY)
	public List<FedoraObject> getFedoraObjects() {
		return fedoraObjects;
	}

	/**
	 * Set the fedora objects that have been published to this location
	 * 
	 * @param fedoraObjects The fedora objects
	 */
	public void setFedoraObjects(List<FedoraObject> fedoraObjects) {
		this.fedoraObjects = fedoraObjects;
	}

	/**
	 * Get the templates that can be published to this location
	 * 
	 * @return The templates
	 */
	@ManyToMany(mappedBy="publishLocations", fetch=FetchType.LAZY)
	public List<Template> getTemplates() {
		return templates;
	}

	/**
	 * Set the templates that can be published to this location
	 * 
	 * @param templates The templates
	 */
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		if (getId() != null) {
			result = 37 * result + getId().hashCode();
		}
		if (getCode() != null) {
			result = 37 * result + getCode().hashCode();
		}
		if (getName() != null) {
			result = 37 * result + getName().hashCode();
		}
		if (getExecute_class() != null) {
			result = 37 * result + getExecute_class().hashCode();
		}
		if (getRequires() != null) {
			result = 37 * result + getRequires().hashCode();
		}
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof PublishLocation)) {
			return false;
		}
		PublishLocation castOther = (PublishLocation) other;
		
		//return (this.getCode().equals(castOther.getCode()))
		return (
				((getId() == null && castOther.getId() == null) 
						|| (getId() != null && getId().equals(castOther.getId()))) &&
				((getCode() == null && castOther.getCode() == null) 
						|| (getCode() != null && getCode().equals(castOther.getCode()))) &&
				((getName() == null && castOther.getName() == null) 
						|| (getName() != null && getName().equals(castOther.getName()))) &&
				((getExecute_class() == null && castOther.getExecute_class() == null) 
						|| (getExecute_class() != null && getExecute_class().equals(castOther.getExecute_class()))) &&
				((getRequires() == null && castOther.getRequires() == null) 
						|| (getRequires() != null && getRequires().equals(castOther)))
				);
	}
}
