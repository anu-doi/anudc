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
 * Report
 * 
 * Australian National University Data Commons
 * 
 * Entity class for the report table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Entity
@Table(name="report")
public class Report {
	private Long id;
	private String reportName;
	private String reportTemplate;
	private String subReport;
	private List<ReportParam> reportParams;
	
	/**
	 * Constructor
	 * 
	 * Constructor class that initialises lists
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 */
	public Report() {
		reportParams = new ArrayList<ReportParam>();
	}
	
	/**
	 * getId
	 *
	 * Get the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 *
	 * Set the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getReportName
	 *
	 * Get the report name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reportName
	 */
	@Column(name="report_name")
	public String getReportName() {
		return reportName;
	}
	
	/**
	 * setReportName
	 *
	 * Set the report name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	/**
	 * getReportTemplate
	 *
	 * Get the name of the report template
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reportTemplate
	 */
	@Column(name="report_template")
	public String getReportTemplate() {
		return reportTemplate;
	}
	
	/**
	 * setReportTemplate
	 *
	 * Set the name of the report template
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reportTemplate the reportTemplate to set
	 */
	public void setReportTemplate(String reportTemplate) {
		this.reportTemplate = reportTemplate;
	}
	
	/**
	 * getSubReport
	 *
	 * Get the name of the subreport
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the subReport
	 */
	@Column(name="sub_report")
	public String getSubReport() {
		return subReport;
	}
	
	/**
	 * setSubReport
	 *
	 * Set the name of the sub report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param subReport the subReport to set
	 */
	public void setSubReport(String subReport) {
		this.subReport = subReport;
	}

	/**
	 * getReportParams
	 *
	 * Get the report parameters for the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the reportParams
	 */
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="id")
	public List<ReportParam> getReportParams() {
		return reportParams;
	}

	/**
	 * setReportParams
	 *
	 * Set the report parameters for the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param reportParams the reportParams to set
	 */
	public void setReportParams(List<ReportParam> reportParams) {
		this.reportParams = reportParams;
	}
}
