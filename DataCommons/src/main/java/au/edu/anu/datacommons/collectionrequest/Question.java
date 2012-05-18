package au.edu.anu.datacommons.collectionrequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "question_bank")
public class Question
{
	private Long id;
	private String questionText;

	protected Question()
	{
	}

	public Question(String questionText)
	{
		this.questionText = questionText;
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

	@Column(name = "question_text", nullable = false, unique = true)
	public String getQuestionText()
	{
		return questionText;
	}

	public void setQuestionText(String question)
	{
		this.questionText = question;
	}
}
