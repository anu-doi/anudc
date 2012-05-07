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

	public CollectionDropboxAccessLog(String ipAddress)
	{
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

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	@PrePersist
	protected void onCreate()
	{
		this.timestamp = new Date();
	}
}
