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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.CollectionRequestStatus.ReqStatus;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * CollectionRequest
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		25/05/2012	Rahul Khanna (GT)		Initial
 * 0.2		25/06/2012	Genevieve Turner (GT)	Added fedoraObject to class
 * 0.3		28/06/2012	Genevieve Turner (GT)	Fixed a timestamp is null issue 
 * </pre>
 *
 */
@Entity
@Table(name = "collection_requests")
public class CollectionRequest
{
	static final Logger LOGGER = LoggerFactory.getLogger(CollectionRequest.class);
	
	private Long id;
	private String pid;
	private Users requestor;
	private String requestorIp;
	private Set<CollectionRequestStatus> statusHistory = new HashSet<CollectionRequestStatus>();
	private Date timestamp;
	private Set<CollectionRequestItem> items = new HashSet<CollectionRequestItem>();
	private Set<CollectionRequestAnswer> answers = new HashSet<CollectionRequestAnswer>();
	private CollectionDropbox dropbox;
	private FedoraObject fedoraObject;

	protected CollectionRequest()
	{
	}

	public CollectionRequest(String pid, Users requestor, String requestorIp, FedoraObject fedoraObject)
	{
		this.pid = pid;
		this.requestor = requestor;
		this.requestorIp = requestorIp;
		this.statusHistory.add(new CollectionRequestStatus(this, ReqStatus.SUBMITTED, "Submitted by User", this.requestor));
		this.fedoraObject = fedoraObject;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId()
	{
		return this.id;
	}

	protected void setId(Long id)
	{
		this.id = id;
	}

	@Column(name = "pid", nullable = false)
	public String getPid()
	{
		return this.pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name="requestor_fk")
	public Users getRequestor()
	{
		return requestor;
	}

	public void setRequestor(Users requestor)
	{
		this.requestor = requestor;
	}

	@Column(name = "requestor_ip")
	public String getRequestorIp()
	{
		return this.requestorIp;
	}

	public void setRequestorIp(String requestorIp)
	{
		this.requestorIp = requestorIp;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "request_fk")
	public Set<CollectionRequestStatus> getStatus()
	{
		return statusHistory;
	}

	public void setStatus(Set<CollectionRequestStatus> status)
	{
		this.statusHistory = status;
	}

	@Column(name = "timestamp", nullable = false)
	public Date getTimestamp()
	{
		return this.timestamp;
	}

	protected void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "request_fk")
	// Not working. Ref: http://stackoverflow.com/questions/7501723/ordering-in-jpa-not-working. Workaround - include clause in SQL.
	@OrderBy("item")
	public Set<CollectionRequestItem> getItems()
	{
		return items;
	}

	public void setItems(Set<CollectionRequestItem> items)
	{
		this.items = items;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "request_fk")
	public Set<CollectionRequestAnswer> getAnswers()
	{
		return answers;
	}

	public void setAnswers(Set<CollectionRequestAnswer> answers)
	{
		this.answers = answers;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public CollectionDropbox getDropbox()
	{
		return dropbox;
	}

	public void setDropbox(CollectionDropbox dropbox)
	{
		this.dropbox = dropbox;
	}

	/**
	 * getFedoraObject
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		25/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the fedoraObject
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="object_fk", referencedColumnName="id")
	public FedoraObject getFedoraObject() {
		return fedoraObject;
	}

	/**
	 * setFedoraObject
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		25/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject the fedoraObject to set
	 */
	public void setFedoraObject(FedoraObject fedoraObject) {
		this.fedoraObject = fedoraObject;
	}

	@PrePersist
	protected void onCreate()
	{
		timestamp = new Date();
	}

	/**
	 * getLastStatus
	 *
	 * Gets the last status record for the request
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/05/2012	Rahul Khanna (RK)		Initial
	 * 0.3		29/06/2012	Genevieve Turner(GT)	Updated to fix a bug where the iStatus timestamp may be null
	 * </pre>
	 * 
	 * @return
	 */
	@Transient
	public CollectionRequestStatus getLastStatus()
	{
		CollectionRequestStatus lastStatus = null;
		for (CollectionRequestStatus iStatus : this.statusHistory)
		{
			if (lastStatus != null)
			{
				if (iStatus == null) {
					LOGGER.info("Why is iStatus null?");
				}
				else {
					if (iStatus.getTimestamp() == null) {
						LOGGER.debug("iStatus timestamp is null");
					}
					else if (lastStatus.getTimestamp() == null) {
						LOGGER.debug("Timestamp is null");
					}
					else if (lastStatus.getTimestamp().before(iStatus.getTimestamp())) {
						lastStatus = iStatus;
					}
				}
			}
			else
			{
				lastStatus = iStatus;
			}
		}

		return lastStatus;
	}

	public void addStatus(CollectionRequestStatus collReqStatus)
	{
		this.statusHistory.add(collReqStatus);
		collReqStatus.setCollectionRequest(this);
	}

	public void addItem(CollectionRequestItem item)
	{
		this.items.add(item);
		item.setCollectionRequest(this);
	}

	public void addAnswer(CollectionRequestAnswer answer)
	{
		this.answers.add(answer);
		answer.setCollectionRequest(this);
	}
}
