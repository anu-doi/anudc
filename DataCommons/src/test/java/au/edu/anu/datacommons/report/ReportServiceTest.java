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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
	@InjectMocks
	ReportService reportService = new ReportServiceImpl();
	
	@Mock
	GroupService groupService;
	
	@Before
	public void setUp() {
	}

	@Ignore
	@Test
	public void generateCronStringTest() {
		String dayOfWeek = "FRI";
		String hour = "3";
		String minute = "47";
		
		String cron = reportService.generateCronString(dayOfWeek, hour, minute);
		assertEquals("0 47 3 * * FRI", cron);
	}
	
	@Ignore
	@Test
	public void testScheduling() {
		Long reportId = new Long(9);
		String email = "genevieve.turner@anu.edu.au";
		String cron = "0 47 3 * * FRI";
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] values = {"1"};
		parameterMap.put("param1", values);
		reportService.schedule(reportId, email, cron, parameterMap);
	}
	
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
}
