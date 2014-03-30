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

package au.edu.anu.datacommons.collectionrequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a requested item in a collection request. A requested item is a relative path to a file or
 * folder within a collection record.
 * 
 * @author Rahul Khanna
 * 
 */
@Entity
@Table(name = "collection_request_items")
public class CollectionRequestItem {
	private Long id;
	private String item;
	private CollectionRequest collectionRequest;

	protected CollectionRequestItem() {
	}

	public CollectionRequestItem(String item) {
		this.item = item;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@Column(name = "item", nullable = false)
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "request_fk")
	public CollectionRequest getCollectionRequest() {
		return collectionRequest;
	}

	public void setCollectionRequest(CollectionRequest parentRequest) {
		this.collectionRequest = parentRequest;
	}
}
