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
package au.edu.anu.datacommons.embargo;

import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * EmbargoManager
 *
 * Australian National University Data Commons
 * 
 * Manager class for scheduling embargo features such as reminder emails
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class EmbargoManager {
	static final Logger LOGGER = LoggerFactory.getLogger(EmbargoManager.class);
	
	private static EmbargoManager embargoManager = new EmbargoManager();
	
	ScheduledFuture<EmbargoEmailer> scheduledEmailer;
	ScheduledFuture<EmbargoLifter> scheduledLifter;
	
	/**
	 * Returns the instance of the EmbargoManager
	 * 
	 * @return The embargo manager
	 */
	public static EmbargoManager getInstance() {
		return embargoManager;
	}
	
	/**
	 * Start the embargo runnables
	 */
	public void start() {
		scheduleEmbargoEmailer();
		scheduleEmbargoLifter();
	}
	
	/**
	 * Stop the embargo runnables
	 */
	public void shutdown() {
		if (scheduledEmailer != null) {
			scheduledEmailer.cancel(false);
		}
		if (scheduledLifter != null) {
			scheduledLifter.cancel(false);
		}
	}
	
	/**
	 * Schedule emails that make notifications about an embargo lifting soon.
	 */
	@SuppressWarnings("unchecked")
	public void scheduleEmbargoEmailer() {
		LOGGER.info("Scheduling emails to be sent close to embargo lift date");
		TimeZone tz = TimeZone.getDefault();
		String cronString = getCronString("embargo.reminder.time", "lift email reminder");
		if (cronString == null) {
			LOGGER.debug("No time defined for the emargo email scheduler so not scheduling the emails");
			return;
		}
		CronTrigger trigger = new CronTrigger(cronString, tz);

		ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
		
		EmbargoEmailer emailer = new EmbargoEmailer();
		scheduledEmailer = scheduler.schedule(emailer, trigger);
	}

	/**
	 * Schedule the embargo lifter
	 */
	@SuppressWarnings("unchecked")
	public void scheduleEmbargoLifter() {
		LOGGER.info("Scheduling embargo lifter");
		TimeZone tz = TimeZone.getDefault();
		String cronString = getCronString("embargo.lifter.time", "lifter");
		if (cronString == null) {
			LOGGER.debug("No time defined for the embargo lifter so scheduling it to 7 minutes past midnight");
			cronString = "0 7 0 * * *";
		}
		CronTrigger trigger = new CronTrigger(cronString, tz);

		ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
		
		EmbargoLifter lifter = new EmbargoLifter();
		scheduledLifter = scheduler.schedule(lifter, trigger);
		
	}
	
	/**
	 * Get the cron string for the times
	 * 
	 * @param property The property that holds the time of day for the cron to run
	 * @param aspect The string that identifies what type of item is being scheduled
	 * @return The cron string
	 */
	private String getCronString(String property, String aspect) {
		String cronTime = GlobalProps.getProperty(property);
		if (cronTime == null) {
			return null;
		}
		String[] timeParts = cronTime.split(":");
		if (timeParts.length != 2) {
			LOGGER.error("Embargo " + aspect + " time not set correctly");
			return null;
		}
		String cronString = "0 " + timeParts[1] + " " + timeParts[0] + " * * *";
		return cronString;
	}
}
