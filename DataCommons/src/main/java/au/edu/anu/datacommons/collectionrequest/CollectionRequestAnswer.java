package au.edu.anu.datacommons.collectionrequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "collection_request_answers", uniqueConstraints = @UniqueConstraint(columnNames =
{ "request_fk", "question_fk" }))
public class CollectionRequestAnswer
{
	private Long id;
	private CollectionRequest collectionRequest;
	private Question question;
	private String answer;

	public CollectionRequestAnswer()
	{
	}

	public CollectionRequestAnswer(Question question, String answer)
	{
		this.question = question;
		this.answer = answer;
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
	// @ForeignKey(name = "request_fk")
	public CollectionRequest getCollectionRequest()
	{
		return collectionRequest;
	}

	public void setCollectionRequest(CollectionRequest collectionRequest)
	{
		this.collectionRequest = collectionRequest;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "question_fk")
	// @ForeignKey(name = "question_fk")
	public Question getQuestion()
	{
		return question;
	}

	public void setQuestion(Question question)
	{
		this.question = question;
	}

	@Column(name = "answer", nullable = false)
	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}
}
