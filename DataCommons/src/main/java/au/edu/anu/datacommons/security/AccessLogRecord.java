package au.edu.anu.datacommons.security;

import java.net.URI;
import java.util.Date;

import javax.persistence.CascadeType;
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
	
	public enum Operation
	{
		CREATE, READ, UPDATE, DELETE
	};

	protected AccessLogRecord()
	{
	}
	
	public AccessLogRecord(URI uri, Users user, String ipAddr, Operation op)
	{
		this(uri.toString(), user, ipAddr, op);
	}
	
	public AccessLogRecord(String uri, Users user, String ipAddr, Operation op)
	{
		this.uri = uri;
		this.user = user;
		this.ipAddr = ipAddr;
		this.op = op;
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

	@PrePersist
	protected void onCreate()
	{
		this.timestamp = new Date();
	}
}
