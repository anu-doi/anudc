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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;



/**
 * Domains
 * 
 * Australian National University Data Comons
 * 
 * Domain object
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 0.2		25/07/2012	Genevieve Turner (GT)	Added functions for review processing
 * </pre>
 * 
 */
@Entity
@Table(name="fedora_object")
public class FedoraObject {
	private Long id;
	private String object_id;
	private Long group_id;
	private Boolean published;
	private String tmplt_id;
	private List<PublishLocation> publishedLocations;
	private ReviewReady reviewReady;
	private PublishReady publishReady;
	private ReviewReject reviewReject;
	
	public FedoraObject() {
		publishedLocations = new ArrayList<PublishLocation>();
	}
	
	/**
	 * getId
	 * 
	 * Gets the id of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the object
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the id of the domain
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the domain
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getObject_id
	 * 
	 * Gets the pid of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The pid of the object
	 */
	@Column(name="pid")
	public String getObject_id() {
		return object_id;
	}
	
	/**
	 * setObject_id
	 * 
	 * Sets the pid of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param object_id  The pid of the object
	 */
	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}
	
	/**
	 * getGroup_id
	 * 
	 * Gets the group id of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The group id of the object
	 */
	@Column(name="group_id")
	public Long getGroup_id() {
		return group_id;
	}
	
	/**
	 * setGroup_id
	 * 
	 * Sets the group id of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param group_id The group id of the object
	 */
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	/**
	 * getPublished
	 * 
	 * Gets the published flag
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The published state
	 */
	public Boolean getPublished() {
		return published;
	}

	/**
	 * setPublished
	 * 
	 * Sets the published flag
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param published The published state
	 */
	public void setPublished(Boolean published) {
		this.published = published;
	}

	/**
	 * getTmplt_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		13/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the tmplt_id
	 */
	public String getTmplt_id() {
		return tmplt_id;
	}

	/**
	 * setTmplt_id
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		13/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param tmplt_id the tmplt_id to set
	 */
	public void setTmplt_id(String tmplt_id) {
		this.tmplt_id = tmplt_id;
	}

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="published",
			joinColumns=@JoinColumn(name="fedora_id", referencedColumnName="id")
			,inverseJoinColumns=@JoinColumn(name="location_id", referencedColumnName="id")
	)
	public List<PublishLocation> getPublishedLocations() {
		return publishedLocations;
	}

	public void setPublishedLocations(List<PublishLocation> publishedLocations) {
		this.publishedLocations = publishedLocations;
	}

	/**
	 * getReviewReady
	 *
	 * Get the associated ready for review information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reviewReady
	 */
	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public ReviewReady getReviewReady() {
		return reviewReady;
	}

	/**
	 * setReviewReady
	 *
	 * Set the associated ready for review information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reviewReady the reviewReady to set
	 */
	public void setReviewReady(ReviewReady reviewReady) {
		this.reviewReady = reviewReady;
	}

	/**
	 * getPublishReady
	 *
	 * Get the associated ready for publish information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the publishReady
	 */
	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public PublishReady getPublishReady() {
		return publishReady;
	}

	/**
	 * setPublishReady
	 *
	 * Set the associated ready for publish information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publishReady the publishReady to set
	 */
	public void setPublishReady(PublishReady publishReady) {
		this.publishReady = publishReady;
	}

	/**
	 * getReviewReject
	 *
	 * Get the associated rejection information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reviewReject
	 */
	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public ReviewReject getReviewReject() {
		return reviewReject;
	}

	/**
	 * setReviewReject
	 *
	 * Set the associated rejection information
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reviewReject the reviewReject to set
	 */
	public void setReviewReject(ReviewReject reviewReject) {
		this.reviewReject = reviewReject;
	}
}
