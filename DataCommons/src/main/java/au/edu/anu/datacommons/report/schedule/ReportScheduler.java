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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.ReportAuto;

/**
 * ReportScheduler
 *
 * Australian National University Data Commons
 * 
 * Schedules reports to run
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class ReportScheduler {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportScheduler.class);
	
	private static List<ScheduledFuture> scheduledFutures = new ArrayList<ScheduledFuture>();
	ServletContext context;
	
	/**
	 * Constructor
	 * 
	 * @param context The servlet context so the location of the reports can be found
	 */
	public ReportScheduler(ServletContext context) {
		LOGGER.trace("Initializing report scheduler");
		this.context = context;
	}
	
	/**
	 * Schedule all the reports
	 */
	public void scheduleAll() {
		LOGGER.info("Scheduling all reports to run");
		for (ScheduledFuture scheduledFuture : scheduledFutures) {
			scheduledFuture.cancel(true);
		}
		scheduledFutures.clear();
		
		GenericDAO<ReportAuto, Long> reportAutoDAO = new GenericDAOImpl<ReportAuto, Long>(ReportAuto.class);
		
		List<ReportAuto> automaticReports = reportAutoDAO.getAll();
		LOGGER.debug("Number of automated reports: {}", automaticReports.size());
		for (ReportAuto autoReport : automaticReports) {
			schedule(autoReport);
		}
	}
	
	/**
	 * Cancel all the scheduled reports
	 */
	public void cancelAll() {
		LOGGER.info("Cancelling all the scheduled reports");
		for (ScheduledFuture scheduledFuture : scheduledFutures) {
			scheduledFuture.cancel(true);
		}
		scheduledFutures.clear();
	}
	
	/**
	 * Cancel the scheduled report with the given id
	 * 
	 * @param reportAutoId The id of the automated report
	 */
	public void schedule(Long reportAutoId) {
		GenericDAO<ReportAuto, Long> reportAutoDAO = new GenericDAOImpl<ReportAuto, Long>(ReportAuto.class);
		ReportAuto reportAuto = reportAutoDAO.getSingleById(reportAutoId);
		schedule(reportAuto);
	}
	
	/**
	 * Cancel the scheduled report with the given report auto information
	 * 
	 * @param reportAuto The automated report
	 */
	public void schedule(ReportAuto reportAuto) {
		TimeZone tz = TimeZone.getDefault();
		LOGGER.info("Setting up report {} to execute with cron string: '{}'", reportAuto.getReportId(), reportAuto.getCron());
		CronTrigger trigger = new CronTrigger(reportAuto.getCron(), tz);
		
		ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
		
		ReportRunnable runnable = new ReportRunnable(reportAuto, context);
		
		ScheduledFuture<?> future = scheduler.schedule(runnable, trigger);
		scheduledFutures.add(future);
	}
}
