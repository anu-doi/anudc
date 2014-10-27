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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Transient;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAO;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAOImpl;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;



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
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObject.class);
	
	private Long id;
	private String object_id;
	private Long group_id;
	private Boolean published;
	private String tmplt_id;
	private List<PublishLocation> publishedLocations;
	private ReviewReady reviewReady;
	private PublishReady publishReady;
	private ReviewReject reviewReject;
	private Boolean isFilesPublic;
	private Boolean embargoed;
	private Date embargoDate;
	
	public FedoraObject() {
		publishedLocations = new ArrayList<PublishLocation>();
		isFilesPublic = false;
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

	/**
	 * Returns if files in this record, if any, are public after the record's published.
	 * 
	 * @return true if public, false otherwise
	 */
	@Column(name = "is_files_public", nullable = false)
	public Boolean isFilesPublic() {
		return isFilesPublic;
	}

	/**
	 * Sets if files in this record, if any, are public after the record's published.
	 * 
	 * @param isFilesPublic
	 *            true if public, false otherwise
	 */
	public void setFilesPublic(Boolean isFilesPublic) {
		this.isFilesPublic = isFilesPublic;
	}
	
	/**
	 * Get the embargo date if it exists
	 * 
	 * @return The embargo date
	 */
	@Transient
	public Date getEmbargoDate() {
		if (embargoDate == null) {
			SolrQuery solrQuery = new SolrQuery();
			String q = "id:" + SolrUtils.escapeSpecialCharacters(this.getObject_id());
			solrQuery.setQuery(q);
			solrQuery.setFields("published.embargoDate");
			SolrSearchDAO solrSearchDAO = new SolrSearchDAOImpl();
			try {
				SolrSearchResult result = solrSearchDAO.executeSearch(solrQuery);
				SolrDocumentList documentList = result.getDocumentList();
				long numFound = documentList.getNumFound();
				if (numFound > 0) {
					SolrDocument doc = documentList.get(0);
					String embargoDateStr = (String) doc.getFirstValue("published.embargoDate");
					if (embargoDateStr != null) {
						LOGGER.info("Embargo date string: {}", embargoDateStr);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						embargoDate = sdf.parse(embargoDateStr);
					}
				}
			}
			catch (SolrServerException | ParseException e) {
				LOGGER.info("Exception retrieving embargo date: {}", e);
			}
		}
		return embargoDate;
	}
	
	/**
	 * Get whether or not the embargo date has passed
	 * 
	 * @return Whether the embargo date has passed
	 */
	@Transient
	public Boolean getEmbargoDatePassed() {
		Date embargoDate = getEmbargoDate();
		if (embargoDate != null) {
			Date now = new Date();
			if (embargoDate.compareTo(now) <= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get whether the record is embargoed
	 * 
	 * @return Whether the item is embargoed
	 */
	@Transient
	public Boolean getEmbargoed() {
		if (embargoed == null) {
			embargoed = false;
			boolean passed = getEmbargoDatePassed();
			Date embargoDate = getEmbargoDate();
			if (embargoDate != null) {
				Date now = new Date();
				if (embargoDate.compareTo(now) > 0) {
					embargoed = true;
				}
			}
		}
		return embargoed;
	}
}
