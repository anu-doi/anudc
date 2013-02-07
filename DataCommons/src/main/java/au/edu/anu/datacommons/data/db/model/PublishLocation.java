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

@Entity
@Table(name="publish_location")
public class PublishLocation {
	private Long id;
	private String code;
	private String name;
	private String execute_class;
	private Long requires;
	private List<FedoraObject> fedoraObjects;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="execute_class")
	public String getExecute_class() {
		return execute_class;
	}

	public void setExecute_class(String execute_class) {
		this.execute_class = execute_class;
	}
	
	@Column(name="requires")
	public Long getRequires() {
		return requires;
	}
	
	public void setRequires(Long requires) {
		this.requires = requires;
	}
	
	@ManyToMany(mappedBy="publishedLocations", fetch=FetchType.LAZY)
	public List<FedoraObject> getFedoraObjects() {
		return fedoraObjects;
	}

	public void setFedoraObjects(List<FedoraObject> fedoraObjects) {
		this.fedoraObjects = fedoraObjects;
	}
}
