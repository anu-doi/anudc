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
package au.edu.anu.datacommons.report.schedule;

import java.util.Map;

/**
 * ScheduledReport
 *
 * Australian National University Data Commons
 * 
 * Object to show the scheduled report infomration
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class ScheduledReport {
	private Long reportAutoId;
	private String reportName;
	private String email;
	private String format;
	private String daysOfYear;
	private String daysOfWeek;
	private String daysOfMonth;
	private String hours;
	private String minutes;
	private String seconds;
	private String month;
	
	/**
	 * Constructor
	 * 
	 * @param reportAutoId The automated report id
	 * @param reportName The scheduled report name
	 * @param email The email address to send to
	 * @param cronMap The deconstructed cron string
	 */
	public ScheduledReport(Long reportAutoId, String reportName, String email, String format, Map<String, String> cronMap) {
		this.reportAutoId = reportAutoId;
		this.reportName = reportName;
		this.email = email;
		this.format = format;
		this.seconds = cronMap.get("second");
		this.minutes = cronMap.get("minute");
		this.hours = cronMap.get("hour");
		this.daysOfMonth = cronMap.get("dayOfMonth");
		this.daysOfYear = cronMap.get("dayOfYear");
		this.daysOfWeek = cronMap.get("dayOfWeek");
	}

	/**
	 * Get the automated report id
	 * 
	 * @return The id
	 */
	public Long getReportAutoId() {
		return reportAutoId;
	}

	/**
	 * SEt the automated report id
	 * 
	 * @param reportAutoId The id
	 */
	public void setReportAutoId(Long reportAutoId) {
		this.reportAutoId = reportAutoId;
	}

	/**
	 * Get the report name
	 * 
	 * @return The report name
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * Set the report name
	 * 
	 * @param reportName The report name
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * Get the email address
	 * 
	 * @return The email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set the email address
	 * 
	 * @param email  The email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get the format
	 * 
	 * @return The format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Set the format
	 * 
	 * @param format The format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Get the days of the year
	 * 
	 * @return The days of the year
	 */
	public String getDaysOfYear() {
		return daysOfYear;
	}

	/**
	 * Set the days of the year
	 * 
	 * @param daysOfYear The days of the year
	 */
	public void setDaysOfYear(String daysOfYear) {
		this.daysOfYear = daysOfYear;
	}

	/**
	 * Get the days of the week
	 * 
	 * @return  The days of the week
	 */
	public String getDaysOfWeek() {
		return daysOfWeek;
	}

	/**
	 * Set the days of the week
	 * 
	 * @param daysOfWeek The days of the week
	 */
	public void setDaysOfWeek(String daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * Get the days of the month
	 * 
	 * @return The days of the month
	 */
	public String getDaysOfMonth() {
		return daysOfMonth;
	}

	/**
	 * Set the days of the month
	 * \
	 * @param daysOfMonth The days of the month
	 */
	public void setDaysOfMonth(String daysOfMonth) {
		this.daysOfMonth = daysOfMonth;
	}

	/**
	 * Get the hours
	 * 
	 * @return The hours
	 */
	public String getHours() {
		return hours;
	}

	/**
	 * Set the hours
	 * 
	 * @param hours The hours
	 */
	public void setHours(String hours) {
		this.hours = hours;
	}

	/**
	 * Get the minutes
	 * 
	 * @return The minutes
	 */
	public String getMinutes() {
		return minutes;
	}

	/**
	 * Set the minutes
	 * 
	 * @param minutes The minutes
	 */
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	/**
	 * Get the seconds
	 * 
	 * @return The seconds
	 */
	public String getSeconds() {
		return seconds;
	}

	/**
	 * Set the seconds
	 * 
	 * @param seconds The seconds
	 */
	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	/**
	 * Get the month
	 * 
	 * @return The month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * Set the month
	 * 
	 * @param month The month
	 */
	public void setMonth(String month) {
		this.month = month;
	}
}
