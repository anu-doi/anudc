package au.edu.anu.datacommons.collectionrequest;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import au.edu.anu.datacommons.collectionrequest.CollectionRequestStatus.ReqStatus;

@Entity
@Table(name = "collection_requests")
public class CollectionRequest
{
	private Long id;
	private String pid;
	private Long requestorId;
	private String requestorIp;
	private Set<CollectionRequestStatus> statusHistory = new HashSet<CollectionRequestStatus>();
	private Date timestamp;
	private Set<CollectionRequestItem> items = new HashSet<CollectionRequestItem>();
	private Set<CollectionRequestAnswer> answers = new HashSet<CollectionRequestAnswer>();
	private CollectionDropbox dropbox;

	protected CollectionRequest()
	{
	}

	public CollectionRequest(String pid, Long requestorId, String requestorIp)
	{
		this.pid = pid;
		this.requestorId = requestorId;
		this.requestorIp = requestorIp;
		this.statusHistory.add(new CollectionRequestStatus(ReqStatus.SUBMITTED, "Submitted by User", requestorId));
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

	@Column(name = "requestor_id", nullable = false)
	public long getRequestorId()
	{
		return this.requestorId;
	}

	public void setRequestorId(Long requestorId)
	{
		this.requestorId = requestorId;
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

	@PrePersist
	protected void onCreate()
	{
		timestamp = new Date();
	}

	public void addStatus(CollectionRequestStatus collReqStatus)
	{
		this.statusHistory.add(collReqStatus);
		collReqStatus.setCollectionRequest(this);

		if (collReqStatus.getStatus() == ReqStatus.ACCEPTED)
		{
			this.dropbox = new CollectionDropbox(this, collReqStatus.getUserId(), true);
		}
	}

	@Transient
	public CollectionRequestStatus getLastStatus()
	{
		CollectionRequestStatus lastStatus = null;
		for (CollectionRequestStatus iStatus : this.statusHistory)
		{
			if (lastStatus != null)
			{
				if (lastStatus.getTimestamp().before(iStatus.getTimestamp()))
					lastStatus = iStatus;
			}
			else
			{
				lastStatus = iStatus;
			}
		}

		return lastStatus;
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

	@Override
	public String toString()
	{
		StringBuilder collReqStr = new StringBuilder();

		collReqStr.append("[");
		collReqStr.append(this.id);
		collReqStr.append("] ");
		collReqStr.append(this.timestamp);
		collReqStr.append(": Pid: ");
		collReqStr.append(this.pid);
		collReqStr.append(", Requestor Id: ");
		collReqStr.append(this.requestorId);
		collReqStr.append(", Requestor IP: ");
		collReqStr.append(this.requestorIp);
		collReqStr.append("\r\nStatus: ");
		if (this.statusHistory.size() > 0)
		{
			for (CollectionRequestStatus iStatus : this.statusHistory)
			{
				collReqStr.append("\r\n\t[");
				collReqStr.append(iStatus.getId());
				collReqStr.append("] ");
				collReqStr.append(iStatus.getTimestamp());
				collReqStr.append(": ");
				collReqStr.append(iStatus.getStatus().name());
				collReqStr.append(", ");
				collReqStr.append(iStatus.getReason());
			}
		}
		else
		{
			collReqStr.append("\r\n\tNo status history.");
		}

		collReqStr.append("\r\nRequested Items:");
		for (CollectionRequestItem item : this.items)
		{
			collReqStr.append("\r\n\t");
			collReqStr.append(item.getItem());
		}

		collReqStr.append("\r\nAnswers:");
		for (CollectionRequestAnswer answer : this.answers)
		{
			collReqStr.append("\r\n\t");
			collReqStr.append(answer.getQuestion().getQuestion());
			collReqStr.append(": ");
			collReqStr.append(answer.getAnswer());
		}

		if (this.dropbox != null)
		{
			collReqStr.append("\r\nDropbox:");
			collReqStr.append("\r\n\t");
			collReqStr.append(this.dropbox.getAccessCode());
			collReqStr.append(", ");
			collReqStr.append(this.dropbox.getAccessPassword());
		}

		return collReqStr.toString();
	}
}
