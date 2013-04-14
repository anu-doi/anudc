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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import au.edu.anu.datacommons.data.db.model.Domains;
import au.edu.anu.datacommons.data.db.model.Groups;

@Entity
@Table(name = "question_map", uniqueConstraints = @UniqueConstraint(columnNames =
{ "pid", "question_fk", "group_fk", "domain_fk" }))
public class QuestionMap
{
	private Long id;
	private String pid;
	private Question question;
	private Groups group;
	private Domains domain;
	private Boolean required;

	protected QuestionMap()
	{
	}

	public QuestionMap(String pid, Question question, Boolean required)
	{
		this.pid = pid;
		this.question = question;
	}
	
	public QuestionMap(Groups group, Question question, Boolean required) {
		this.group = group;
		this.question = question;
	}
	
	public QuestionMap(Domains domain, Question question, Boolean required) {
		this.domain = domain;
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

	@Column(name = "pid")
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

	/**
	 * getGroup
	 *
	 * Get the group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the group
	 */
	@ManyToOne
	@JoinColumn(name = "group_fk", referencedColumnName="id")
	public Groups getGroup() {
		return group;
	}

	/**
	 * setGroup
	 *
	 * Set the group
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param group the group to set
	 */
	public void setGroup(Groups group) {
		this.group = group;
	}

	/**
	 * getDomain
	 *
	 * Get the domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the domain
	 */
	@ManyToOne
	@JoinColumn(name = "domain_fk", referencedColumnName="id")
	public Domains getDomain() {
		return domain;
	}

	/**
	 * setDomain
	 *
	 * Set the domain
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param domain the domain to set
	 */
	public void setDomain(Domains domain) {
		this.domain = domain;
	}

	/**
	 * getRequired
	 *
	 * Get whether it is a required question
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		05/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the required
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * setRequired
	 *
	 * Set whether it is a required question
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		05/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

}
