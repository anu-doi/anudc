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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.report.schedule.ReportScheduler;
import au.edu.anu.datacommons.report.schedule.ReportSchedulerManager;

/**
 * ReportAppListener
 * 
 * Australian National University Data Commons
 * 
 * Initiates the recompilation and scheduling of reports on server start up.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		02/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ReportAppListener implements ServletContextListener {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	private final boolean isEnabled;

	public ReportAppListener() {
		isEnabled = Boolean.parseBoolean(GlobalProps.getProperty("reporting.enabled", "true"));
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (isEnabled) {
			ReportSchedulerManager reportSchedulerManager = ReportSchedulerManager.getInstance();
			ReportScheduler scheduler = reportSchedulerManager.getReportScheduler();
			scheduler.cancelAll();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (isEnabled) {
			ServletContext context = event.getServletContext();
			ReportGenerator.reloadReports(context);
			ReportSchedulerManager reportSchedulerManager = ReportSchedulerManager.getInstance();
			
			reportSchedulerManager.setServletContext(context);
			reportSchedulerManager.getReportScheduler().scheduleAll();
		}
	}
}
