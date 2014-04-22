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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Report;
import au.edu.anu.datacommons.data.db.model.ReportAuto;
import au.edu.anu.datacommons.data.db.model.ReportAutoParam;
import au.edu.anu.datacommons.data.db.model.ReportAutoParamPK;
import au.edu.anu.datacommons.data.db.model.ReportParam;
import au.edu.anu.datacommons.exception.ValidateException;
import au.edu.anu.datacommons.report.schedule.ReportSchedulerManager;
import au.edu.anu.datacommons.report.schedule.ScheduledReport;
import au.edu.anu.datacommons.security.service.GroupService;

/**
 * ReportServiceImpl
 *
 * Australian National University Data Commons
 * 
 * Implementation class for the report service
 *
 * JUnit coverage:
 * ReportServiceTest
 * 
 * @author Genevieve Turner
 *
 */
@Service("reportServiceImpl")
public class ReportServiceImpl implements ReportService {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;
	
	@Context
	ServletContext context;
	
	private static final Map<String, String> dayOfWeekMap;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("MON", "Monday");
		aMap.put("TUE", "Tuesday");
		aMap.put("WED", "Wednesday");
		aMap.put("THU", "Thursday");
		aMap.put("FRI", "Friday");
		aMap.put("SAT", "Saturday");
		aMap.put("SUN", "Sunday");
		dayOfWeekMap = Collections.unmodifiableMap(aMap);
	}

	@Override
	public List<Report> getAllReports() {
		GenericDAO<Report, Long> reportDAO = new GenericDAOImpl<Report, Long>(Report.class);
		List<Report> reports = reportDAO.getAll();
		return reports;
	}

	@Override
	public List<ReportParam> getReportParameters(Long reportId) {
		GenericDAO<Report, Long> reportDAO = new GenericDAOImpl<Report, Long>(Report.class);
		Report report = reportDAO.getSingleById(reportId);
		return report.getReportParams();
	}
	
	@Override
	public List<Groups> getGroups() {
		return groupService.getAll();
	}

	@Override
	public String generateCronString(String dayOfWeek, String hour, String minute) {
		try {
			int hourInt = Integer.parseInt(hour);
			if (hourInt < 0 || hourInt > 23) {
				throw new ValidateException("Hour is not valid");
			}
		}
		catch (NumberFormatException e) {
			throw new ValidateException("Hour is not valid");
		}
		try {
			int minuteInt = Integer.parseInt(minute);
			if (minuteInt < 0 || minuteInt > 59) {
				throw new ValidateException("Minute is not valid");
			}
		}
		catch (NumberFormatException e) {
			throw new ValidateException("Minute is not valid");
		}
		if (!dayOfWeekMap.containsKey(dayOfWeek)) {
			throw new ValidateException("The day of week is not valid");
		}
		
		String cron = "0 " + minute + " " + hour + " * * " + dayOfWeek;
		
		return cron;
	}

	@Override
	public ReportAuto schedule(Long reportId, String email, String cron,
			Map<String, String[]> parameterMap) {
		List<ReportParam> reportParameters = getReportParameters(reportId);
		ReportAuto reportAuto = new ReportAuto();
		reportAuto.setReportId(reportId);
		reportAuto.setEmail(email);
		reportAuto.setCron(cron);

		GenericDAO<ReportAuto, Long> reportAutoDAO = new GenericDAOImpl<ReportAuto, Long>(ReportAuto.class);
		reportAuto = reportAutoDAO.create(reportAuto);
		GenericDAO<ReportAutoParam, ReportAutoParamPK> reportAutoParamDAO = new GenericDAOImpl<ReportAutoParam, ReportAutoParamPK>(ReportAutoParam.class);
		
		int counter = 0;
		for (ReportParam reportParam : reportParameters) {
			LOGGER.info("Report Param: {}", reportParam.getParamName());
			String[] parameterValues = parameterMap.get(reportParam.getParamName());
			if (parameterValues != null && parameterValues.length > 0) {
				for (String value : parameterValues) {
					ReportAutoParam param = new ReportAutoParam();
					ReportAutoParamPK pk = new ReportAutoParamPK();
					pk.setReportAutoId(reportAuto.getId());
					pk.setSeqNum(counter);
					param.setId(pk);
					param.setParam(reportParam.getParamName());
					param.setParamVal(value);
					reportAutoParamDAO.create(param);
				}
			}
		};
		reportAuto = reportAutoDAO.getSingleById(reportAuto.getId());
		
		scheduleAll();
		return reportAuto;
	}
	
	public void scheduleAll() {
		ReportSchedulerManager.getInstance().getReportScheduler().scheduleAll();
	}

	@Override
	public List<ScheduledReport> getScheduledReports() {
		GenericDAO<ReportAuto, Long> reportAutoDAO = new GenericDAOImpl<ReportAuto, Long>(ReportAuto.class);
		List<ReportAuto> autoReports = reportAutoDAO.getAll();
		List<ScheduledReport> scheduledReports = new ArrayList<ScheduledReport>();
		
		GenericDAO<Report, Long> reportDAO = new GenericDAOImpl<Report,Long>(Report.class);
		for (ReportAuto reportAuto : autoReports) {
			Map<String, String> cronMap = mapCronString(reportAuto.getCron());
			Report report = reportDAO.getSingleById(reportAuto.getReportId());
			ScheduledReport scheduledReport = new ScheduledReport(reportAuto.getId(), report.getReportName(), reportAuto.getEmail(), cronMap);
			scheduledReports.add(scheduledReport);
		}
		return scheduledReports;
	}

	@Override
	public void deleteScheduledReport(Long reportAutoId) {
		GenericDAO<ReportAuto, Long> reportAutoDAO = new GenericDAOImpl<ReportAuto, Long>(ReportAuto.class);
		ReportAuto reportAuto = reportAutoDAO.getSingleById(reportAutoId);
		
		if (reportAuto.getReportAutoParams() != null && reportAuto.getReportAutoParams().size() > 0) {
			GenericDAO<ReportAutoParam, ReportAutoParamPK> reportAutoParamDAO = new GenericDAOImpl<ReportAutoParam, ReportAutoParamPK>(ReportAutoParam.class);
			for (ReportAutoParam param : reportAuto.getReportAutoParams()) {
				reportAutoParamDAO.delete(param.getId());
			}
		}
		reportAutoDAO.delete(reportAutoId);
		scheduleAll();
	}
	
	@Override
	public Map<String, String> mapCronString(String cron) {
		Map<String, String> cronMap = new HashMap<String, String>();
		
		String[] splitCron = cron.split(" ");
		if (!splitCron[0].equals("*")) {
			cronMap.put("second", splitCron[0]);
		}
		if (!splitCron[1].equals("*")) {
			cronMap.put("minute", splitCron[1]);
		}
		if (!splitCron[2].equals("*")) {
			cronMap.put("hour", splitCron[2]);
		}
		if (!splitCron[3].equals("*")) {
			cronMap.put("dayOfMonth", splitCron[3]);
		}
		if (!splitCron[4].equals("*")) {
			cronMap.put("dayOfYear", splitCron[4]);
		}
		if (!splitCron[5].equals("*")) {
			String dayOfWeek = getDaysOfWeek(splitCron[5]);
			cronMap.put("dayOfWeek", dayOfWeek);
		}
		
		return cronMap;
	}
	
	/**
	 * Replace the shortened day of week from the cron with a full day of week name. e.g. change 'FRI' to 'Friday'.
	 * 
	 * @param dayOfWeek The day of week
	 * @return The full day of week
	 */
	private String getDaysOfWeek(String dayOfWeek) {
		for (Entry<String, String> entry : dayOfWeekMap.entrySet()) {
			dayOfWeek = dayOfWeek.replace(entry.getKey(), entry.getValue());
		}
		
		return dayOfWeek;
	}

}
