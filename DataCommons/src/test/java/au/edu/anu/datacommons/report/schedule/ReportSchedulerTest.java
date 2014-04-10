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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockServletContext;

import au.edu.anu.datacommons.report.ReportGenerator;

public class ReportSchedulerTest {
	//ServletContext context = new MockServletContext("C:/WorkSpace/git/anudc/DataCommons/target/DataCommons");
	//ServletContext context = new MockServletContext("C:/WorkSpace/git/anudc/DataCommons/src/main/webapp/WEB-INF/reports");
	//ServletContext context = new MockServletContext("C:/WorkSpace/eclipse_workspaces/datacommons/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/DataCommons");
	
	/*static final Logger LOGGER = LoggerFactory.getLogger(ReportRunnable.class);
	@Test
	public void test() {
		//ReportGenerator.reloadReports(context);
		ReportGenerator.reloadReports("C:/WorkSpace/eclipse_workspaces/datacommons/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/DataCommons/WEB-INF/reports");
		//"C:/WorkSpace/git/anudc/DataCommons/target/DataCommons/";
		
		LOGGER.info("Create scheduler");
		ReportScheduler scheduler = new ReportScheduler();
		scheduler.schedule(new Long(1));
		
		LOGGER.info("before sleep");
		
		try {
			Thread.sleep(100000);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		LOGGER.info("Done!");
	}*/
}
