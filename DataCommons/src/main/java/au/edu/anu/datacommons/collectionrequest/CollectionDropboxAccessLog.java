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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "collection_dropbox_access_logs")
public class CollectionDropboxAccessLog
{
	private Long id;
	private CollectionDropbox dropbox;
	private String ipAddress;
	private Date timestamp;

	protected CollectionDropboxAccessLog()
	{
	}

	public CollectionDropboxAccessLog(CollectionDropbox dropbox, String ipAddress)
	{
		this.dropbox = dropbox;
		this.ipAddress = ipAddress;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "dropbox_fk")
	public CollectionDropbox getDropbox()
	{
		return dropbox;
	}

	public void setDropbox(CollectionDropbox dropbox)
	{
		this.dropbox = dropbox;
	}

	// Nullable to accommodate for situations (if possible) where the client IP is unobtainable.
	@Column(name = "ip_address", nullable = false)
	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	@Column(name = "timestamp", nullable = false)
	public Date getTimestamp()
	{
		return timestamp;
	}

	protected void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	@PrePersist
	protected void onCreate()
	{
		this.timestamp = new Date();
	}
}
