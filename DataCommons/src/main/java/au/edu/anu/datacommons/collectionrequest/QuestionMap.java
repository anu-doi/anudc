package au.edu.anu.datacommons.collectionrequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "question_map", uniqueConstraints = @UniqueConstraint(columnNames =
{ "pid", "question_fk" }))
public class QuestionMap
{
	private Long id;
	private String pid;
	private Question question;

	protected QuestionMap()
	{
	}

	public QuestionMap(String pid, Question question)
	{
		this.pid = pid;
		this.question = question;
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

	@Column(name = "pid", nullable = false)
	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	@OneToOne
	@JoinColumn(name = "question_fk")
	public Question getQuestion()
	{
		return question;
	}

	public void setQuestion(Question question)
	{
		this.question = question;
	}

}
