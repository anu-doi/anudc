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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity class representing an answer for a collection request.
 * 
 * @author Rahul Khanna
 *
 */
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
