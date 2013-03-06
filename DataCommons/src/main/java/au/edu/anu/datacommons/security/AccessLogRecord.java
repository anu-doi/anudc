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

package au.edu.anu.datacommons.security;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import au.edu.anu.datacommons.data.db.model.Users;

@Entity
@Table(name = "access_logs")
public class AccessLogRecord
{
	private Long id;
	private String uri;
	private Users user;
	private Date timestamp;
	private String ipAddr;
	private Operation op;
	private String userAgent;
	
	public enum Operation
	{
		CREATE, READ, UPDATE, DELETE
	};

	protected AccessLogRecord()
	{
	}
	
	public AccessLogRecord(String uri, Users user, String ipAddr, String userAgent, Operation op)
	{
		this.uri = uri;
		this.user = user;
		this.ipAddr = ipAddr;
		this.op = op;
		this.userAgent = userAgent;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Long getId()
	{
		return id;
	}

	protected void setId(Long id)
	{
		this.id = id;
	}

	@Column(name = "uri", nullable = false)
	public String getUri()
	{
		return uri;
	}

	protected void setUri(String uri)
	{
		this.uri = uri;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name="user_fk")
	public Users getUser()
	{
		return user;
	}

	protected void setUser(Users user)
	{
		this.user = user;
	}

	@Column(name = "timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimestamp()
	{
		return timestamp;
	}

	protected void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	@Column(name = "ip_address", nullable = false)
	public String getIpAddr()
	{
		return ipAddr;
	}

	protected void setIpAddr(String ipAddr)
	{
		this.ipAddr = ipAddr;
	}

	@Column(name = "operation", nullable = false)
	public Operation getOp()
	{
		return op;
	}

	protected void setOp(Operation op)
	{
		this.op = op;
	}

	@Column(name = "user_agent")
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@PrePersist
	protected void onCreate()
	{
		this.timestamp = new Date();
	}
}
