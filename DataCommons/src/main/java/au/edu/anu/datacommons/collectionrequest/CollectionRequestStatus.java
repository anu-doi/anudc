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
@Table(name = "collection_request_status")
public class CollectionRequestStatus
{
	public enum ReqStatus
	{
		SUBMITTED, ACCEPTED, REJECTED, PENDING
	};
	
	private Long id;
	private CollectionRequest collectionRequest;
	private ReqStatus status;
	private String reason;
	private Long userId;
	private Date timestamp;
	
	public CollectionRequestStatus()
	{
	}
	
	public CollectionRequestStatus(ReqStatus status, String reason, Long userId)
	{
		this.status = status;
		this.reason = reason;
		this.userId = userId;
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
	@JoinColumn(name = "request_fk")
	public CollectionRequest getCollectionRequest()
	{
		return collectionRequest;
	}

	public void setCollectionRequest(CollectionRequest collectionRequest)
	{
		this.collectionRequest = collectionRequest;
	}

	@Column(name = "status", nullable = false)
	public ReqStatus getStatus()
	{
		return status;
	}

	public void setStatus(ReqStatus status)
	{
		this.status = status;
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

	@Column(name = "reason", nullable = false)
	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	@PrePersist
	protected void onCreate()
	{
		timestamp = new Date();
	}
}