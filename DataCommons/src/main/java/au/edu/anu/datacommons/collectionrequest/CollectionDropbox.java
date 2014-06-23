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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

/**
 * Entity class for a Collection Dropbox that gets created when a collection request is accepted.
 * 
 * @author Rahul Khanna
 * 
 */
@Entity
@Table(name = "collection_dropboxes")
public class CollectionDropbox {
	private Long id;
	private CollectionRequest collectionRequest;
	private Long accessCode;
	private String accessPassword;
	private Date timestamp;
	private Date expiry;
	private Users creator;
	private boolean notifyOnPickup;
	private boolean isActive;
	private Set<CollectionDropboxAccessLog> accessLog = new HashSet<CollectionDropboxAccessLog>();

	protected CollectionDropbox() {
	}

	/**
	 * Creates a new instance of Collection Dropbox
	 * 
	 * @param request
	 *            Collection Request to which this Dropbox will have a one-to-one relationship with
	 * @param user
	 *            User who accepted the associated collection request resulting in the creation of this dropbox.
	 * @param notifyOnPickup
	 *            true if the creator should be notified when the requestor downloads the files from the dropbox.
	 */
	public CollectionDropbox(CollectionRequest request, Users user, boolean notifyOnPickup) {
		this.collectionRequest = request;
		this.creator = user;
		this.notifyOnPickup = notifyOnPickup;
		this.isActive = true; // Default value.
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

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "request_fk")
	public CollectionRequest getCollectionRequest() {
		return collectionRequest;
	}

	public void setCollectionRequest(CollectionRequest collectionRequest) {
		this.collectionRequest = collectionRequest;
	}

	@Column(name = "access_code", nullable = false, unique = true)
	public Long getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(Long accessCode) {
		this.accessCode = accessCode;
	}

	@Column(name = "access_password", nullable = false)
	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	@Column(name = "created", nullable = false)
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "expiry", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_fk")
	public Users getCreator() {
		return creator;
	}

	public void setCreator(Users creator) {
		this.creator = creator;
	}

	@Column(name = "notifyOnPickup")
	public boolean isNotifyOnPickup() {
		return notifyOnPickup;
	}

	public void setNotifyOnPickup(boolean notifyOnPickup) {
		this.notifyOnPickup = notifyOnPickup;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "dropbox_fk")
	public Set<CollectionDropboxAccessLog> getDropboxAccessLog() {
		return accessLog;
	}

	public void setDropboxAccessLog(Set<CollectionDropboxAccessLog> dropboxAccessLog) {
		this.accessLog = dropboxAccessLog;
	}

	/**
	 * Called before this instance is persisted. Sets the expiry date, generates an access code and random password
	 * that must be entered by the user to access the files in this dropbox.
	 */
	@PrePersist
	protected void onCreate() {
		this.timestamp = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		calendar.add(Calendar.DATE, 30);
		this.expiry = calendar.getTime();
		this.accessCode = Math.round(Math.random() * (double) Long.MAX_VALUE);
		this.accessPassword = Util.generateRandomString(Integer.parseInt(GlobalProps
				.getProperty(GlobalProps.PROP_DROPBOX_PASSWORDLENGTH)));
	}

	public void addAccessLogEntry(CollectionDropboxAccessLog accessLogEntry) {
		accessLogEntry.setDropbox(this);
		this.accessLog.add(accessLogEntry);
	}

	/**
	 * Returns if the dropbox is valid. Checks:
	 * <p>
	 * <ul>
	 * <li>If the provided password matches the one in the dropbox
	 * <li>If the dropbox expiry date has passed
	 * <li>If the dropbox has been specifically flagged as inactive
	 * </ul>
	 * 
	 * @param password
	 *            Password to match
	 * @return true if the dropbox is valid, false otherwise
	 */
	@Transient
	public boolean isValid(String password) {
		// Check if active.
		if (!this.isActive)
			return false;

		// Check if expired.
		if (new Date().after(this.getExpiry()))
			return false;

		// Check if password matches.
		if (!password.equals(this.getAccessPassword()))
			return false;

		return true;
	}
}
