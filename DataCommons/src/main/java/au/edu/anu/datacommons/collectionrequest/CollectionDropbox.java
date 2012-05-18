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

import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;

@Entity
@Table(name = "collection_dropboxes")
public class CollectionDropbox
{
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

	protected CollectionDropbox()
	{
	}

	public CollectionDropbox(CollectionRequest request, Users user, boolean notifyOnPickup)
	{
		this.collectionRequest = request;
		this.creator = user;
		this.notifyOnPickup = notifyOnPickup;
		this.isActive = true;		// Default value.
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId()
	{
		return id;
	}

	protected void setId(Long id)
	{
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "request_fk")
	public CollectionRequest getCollectionRequest()
	{
		return collectionRequest;
	}

	public void setCollectionRequest(CollectionRequest collectionRequest)
	{
		this.collectionRequest = collectionRequest;
	}

	@Column(name = "access_code", nullable = false, unique = true)
	public Long getAccessCode()
	{
		return accessCode;
	}

	public void setAccessCode(Long accessCode)
	{
		this.accessCode = accessCode;
	}

	@Column(name = "access_password", nullable = false)
	public String getAccessPassword()
	{
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword)
	{
		this.accessPassword = accessPassword;
	}

	@Column(name = "created", nullable = false)
	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	@Column(name = "expiry", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getExpiry()
	{
		return expiry;
	}

	public void setExpiry(Date expiry)
	{
		this.expiry = expiry;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name="creator_fk")
	public Users getCreator()
	{
		return creator;
	}

	public void setCreator(Users creator)
	{
		this.creator = creator;
	}

	@Column(name = "notifyOnPickup")
	public boolean isNotifyOnPickup()
	{
		return notifyOnPickup;
	}

	public void setNotifyOnPickup(boolean notifyOnPickup)
	{
		this.notifyOnPickup = notifyOnPickup;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "dropbox_fk")
	public Set<CollectionDropboxAccessLog> getDropboxAccessLog()
	{
		return accessLog;
	}

	public void setDropboxAccessLog(Set<CollectionDropboxAccessLog> dropboxAccessLog)
	{
		this.accessLog = dropboxAccessLog;
	}

	@PrePersist
	protected void onCreate()
	{
		this.timestamp = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		calendar.add(Calendar.DATE, 30);
		this.expiry = calendar.getTime();
		this.accessCode = Math.round(Math.random() * (double) Long.MAX_VALUE);
		this.accessPassword = Util.generatePassword(Integer.parseInt(GlobalProps.getProperty(GlobalProps.PROP_DROPBOX_PASSWORDLENGTH)));
	}

	public void addAccessLogEntry(CollectionDropboxAccessLog accessLogEntry)
	{
		accessLogEntry.setDropbox(this);
		this.accessLog.add(accessLogEntry);
	}
}
