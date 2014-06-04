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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.model.ReportAuto;
import au.edu.anu.datacommons.data.db.model.ReportAutoParam;
import au.edu.anu.datacommons.data.db.model.ReportAutoParamPK;
import au.edu.anu.datacommons.report.schedule.ReportRunnable;
import au.edu.anu.datacommons.security.service.GroupService;

/**
 * ReportServiceTest
 *
 * Australian National University Data Commons
 * 
 * Tests the ReportService class
 *
 * JUnit coverage:
 * ReportService
 * ReportServiceImpl
 * 
 * @author Genevieve Turner
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportRunnable.class);

	@InjectMocks
	ReportService reportService = new ReportServiceImpl();
	
	@Mock
	GroupService groupService;
	
	@Before
	public void setUp() {
	}

	//@Ignore
	@Test
	public void generateCronStringTest() {
		String dayOfWeek = "FRI";
		String hour = "3";
		String minute = "47";
		
		String cron = reportService.generateCronString(dayOfWeek, hour, minute);
		assertEquals("0 47 3 * * FRI", cron);
	}
	
	//@Ignore
	@Test
	public void testScheduling() {
		Long reportId = new Long(9);
		String email = "genevieve.turner@anu.edu.au";
		String cron = "0 47 3 * * FRI";
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] values = {"1"};
		parameterMap.put("param1", values);
		reportService.schedule(reportId, email, cron, "pdf", parameterMap);
	}

	//@Ignore
	@Test
	public void processCronStringTest() {
		String cron = "0 47 3 * * FRI";
		Map<String, String> cronMap = reportService.mapCronString(cron);
		assertNotNull(cronMap);
		assertEquals(4, cronMap.size());
		assertEquals("0", cronMap.get("second"));
		assertEquals("47", cronMap.get("minute"));
		assertEquals("3", cronMap.get("hour"));
		assertEquals("Friday", cronMap.get("dayOfWeek"));
	}
	
	//@Ignore
	@Test
	public void generateReportTest() {
		//Seems to not populate the contents of the report, just the report template
		String reportLocation = "C:/WorkSpace/software/jasperreports/test";
		ReportGenerator.reloadReports(reportLocation + "/WEB-INF/reports");
		ReportAuto reportAuto = new ReportAuto();
		reportAuto.setId(new Long(1));
		reportAuto.setEmail("genevieve.turner@anu.edu.au");
		reportAuto.setReportId(new Long(9));
		reportAuto.setCron("0 28 13 * * TUE");
		
		ReportAutoParam param = new ReportAutoParam();
		ReportAutoParamPK pk = new ReportAutoParamPK();
		pk.setReportAutoId(new Long(1));
		pk.setSeqNum(0);
		param.setId(pk);
		param.setParam("param1");
		param.setParam("1");
		ReportGenerator generator = new ReportGenerator(reportAuto, reportLocation);
		try {
			//byte[] bytes = generator.generateReportXLSX();
			byte[] bytes = generator.generateReportForEmail("xlsx");
			
			OutputStream os = new FileOutputStream("C:/WorkSpace/Testing/output.xlsx");
			//OutputStream os = new FileOutputStream("C:/WorkSpace/Testing/output.pdf");
			os.write(bytes);
			os.close();
		}
		catch (Exception e) {
			LOGGER.error("Excpetion error", e);
		}
		LOGGER.info("Report Location: {}", reportLocation);
	}
}
