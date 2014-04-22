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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.report.ReportServiceImpl;

/**
 * ReportSchedulerManager
 *
 * Australian National University Data Commons
 * 
 * Manager class for the report scheduler
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class ReportSchedulerManager {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	private static final ReportSchedulerManager inst = new ReportSchedulerManager();
	
	private ServletContext context;
	
	private ReportScheduler reportScheduler;
	
	/**
	 * Get an instance of the ReportSchedulerManager
	 * @return
	 */
	public static ReportSchedulerManager getInstance() {
		return inst;
	}
	
	/**
	 * Constructor
	 */
	private ReportSchedulerManager() {
		
	}
	
	/**
	 * Set the servlet context
	 * 
	 * @param context The context
	 */
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
	
	/**
	 * Get the report scheduler
	 * 
	 * @return The report scheduler
	 */
	public ReportScheduler getReportScheduler() {
		if (reportScheduler == null) {
			reportScheduler = new ReportScheduler(context);
		}
		return reportScheduler;
	}
}
