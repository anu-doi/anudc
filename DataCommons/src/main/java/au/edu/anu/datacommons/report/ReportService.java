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
package au.edu.anu.datacommons.report;

import java.util.List;
import java.util.Map;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Report;
import au.edu.anu.datacommons.data.db.model.ReportAuto;
import au.edu.anu.datacommons.data.db.model.ReportParam;
import au.edu.anu.datacommons.report.schedule.ScheduledReport;

/**
 * ReportService
 *
 * Australian National University Data Commons
 * 
 * Provides the report services
 *
 * JUnit coverage:
 * ReportServiceTest
 * 
 * @author Genevieve Turner
 *
 */
public interface ReportService {
	/**
	 * Get all the reports
	 * 
	 * @return The list of reports
	 */
	public List<Report> getAllReports();
	
	/**
	 * Get the parameters associated wit the given report
	 * 
	 * @param reportId The report identifier
	 * @return The report parametesr
	 */
	public List<ReportParam> getReportParameters(Long reportId);
	
	/**
	 * Get the available groups to associate with a report
	 * 
	 * @return The list of groups
	 */
	public List<Groups> getGroups();
	
	/**
	 * Generate a cron string
	 * 
	 * @param dayOfWeek The day of week
	 * @param hour The hour to run
	 * @param minute The minute to run
	 * @return A cron string generated from the provided information
	 */
	public String generateCronString(String dayOfWeek, String hour, String minute);
	
	/**
	 * Generate a map based on the provided cron string
	 * 
	 * @param cron The cron string
	 * @return A map with the elements of the crno string broken down
	 */
	public Map<String, String> mapCronString(String cron);
	
	/**
	 * Schedule a report to run with the given information
	 * 
	 * @param reportId The id of the report to run
	 * @param email The email address to send the report to
	 * @param cron The cron string that defines when to run the report
	 * @param parameterMap The map of parameters to provide associated report parameters
	 * @return The ReportAuto object
	 */
	public ReportAuto schedule(Long reportId, String email, String cron, Map<String, String[]> parameterMap);
	
	/**
	 * Get the list of scheduled reports
	 * 
	 * @return The scheduled reports
	 */
	public List<ScheduledReport> getScheduledReports();
	
	/**
	 * Delete the given scheduled report
	 * 
	 * @param reportAutoId The id of the automated report to delete
	 */
	public void deleteScheduledReport(Long reportAutoId);
}
