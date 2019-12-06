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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Template
 *
 * Australian National University Data Commons
 * 
 * Entity class for the 'template' table.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Entity
@Table(name="template")
public class Template {
	private Long id;
	private String templatePid;
	private String name;
	private String description;
	private EntityType entityType;
	private List<TemplateAttribute> templateAttributes;
	private List<TemplateTab> templateTabs;
	private List<PublishLocation> publishLocations = new ArrayList<PublishLocation>();
	
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
	 * @param id The id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Get the pid of the associated object in fedora commons
	 * 
	 * @return The pid
	 */
	@Column(name="template_pid")
	public String getTemplatePid() {
		return templatePid;
	}
	
	/**
	 * Set the pid of the associated object in fedora commons
	 * 
	 * @param templatePid The pid
	 */
	public void setTemplatePid(String templatePid) {
		this.templatePid = templatePid;
	}

	/**
	 * Get the template name
	 * 
	 * @return The template name
	 */
	@Column(name="name")
	public String getName() {
		return name;
	}

	/**
	 * Set the template name
	 * 
	 * @param name  The template name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the template description
	 * 
	 * @return The description
	 */
	@Column(name="description")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the template description
	 * 
	 * @param description The description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="entity_type_id", referencedColumnName="id")
	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	@OneToMany(mappedBy="template", cascade=CascadeType.ALL, orphanRemoval=true)
	public List<TemplateAttribute> getTemplateAttributes() {
		return templateAttributes;
	}

	public void setTemplateAttributes(List<TemplateAttribute> templateAttributes) {
		this.templateAttributes = templateAttributes;
	}

	@OneToMany(mappedBy="template", cascade=CascadeType.ALL, orphanRemoval=true)
	public List<TemplateTab> getTemplateTabs() {
		return templateTabs;
	}

	public void setTemplateTabs(List<TemplateTab> templateTabs) {
		this.templateTabs = templateTabs;
	}

	/**
	 * Get the locations the template can be published to
	 * 
	 * @return The publish locations
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="template_publish_location",
			joinColumns=@JoinColumn(name="template_id", referencedColumnName="id")
			,inverseJoinColumns=@JoinColumn(name="location_id", referencedColumnName="id")
	)
	public List<PublishLocation> getPublishLocations() {
		return publishLocations;
	}

	/**
	 * Set the locations the template can be published to
	 * 
	 * @param publishLocations The publish locations
	 */
	public void setPublishLocations(List<PublishLocation> publishLocations) {
		this.publishLocations = publishLocations;
	}
}
