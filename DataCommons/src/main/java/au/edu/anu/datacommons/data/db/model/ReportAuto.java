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
package au.edu.anu.datacommons.data.db.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ReportAuto
 *
 * Australian National University Data Commons
 * 
 * Entity class for the 'report_auto' table.  This class is utilised in automatically generating and emailing a report.
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Entity
@Table(name="report_auto")
public class ReportAuto {
	Long id;
	Long reportId;
	String email;
	String cron;
	List<ReportAutoParam> reportAutoParams = new ArrayList<ReportAutoParam>();

	/**
	 * Get the id
	 * 
	 * @return The id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * Set the id
	 * 
	 * @param id The id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Get the report id
	 * 
	 * @return The report id
	 */
	@Column(name="report_id")
	public Long getReportId() {
		return reportId;
	}
	
	/**
	 * Set the report id
	 * 
	 * @param reportId The report id
	 */
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	/**
	 * Get the email to send to
	 * 
	 * @return The email address
	 */
	@Column(name="email")
	public String getEmail() {
		return email;
	}
	
	/**
	 * Set the email to send to
	 * 
	 * @param email The email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get the cron string
	 * 
	 * @return The cron string
	 */
	@Column(name="cron")
	public String getCron() {
		return cron;
	}

	/**
	 * Set the cron string
	 * 
	 * @param cron  The cron string
	 */
	public void setCron(String cron) {
		this.cron = cron;
	}

	/**
	 * Get the parameters that have been set for the automated generation
	 * 
	 * @return The parameters
	 */
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="id")
	public List<ReportAutoParam> getReportAutoParams() {
		return reportAutoParams;
	}

	/**
	 * Get the parameters that have been set for the automated generation
	 * 
	 * @param reportAutoParam The parameters
	 */
	public void setReportAutoParams(List<ReportAutoParam> reportAutoParams) {
		this.reportAutoParams = reportAutoParams;
	}
}
