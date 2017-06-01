/*
 * PushProfilesValidityCheckJob is a utility class extended from Quartz Job class
 * This class is used to create a job which can be scheduled to trigger this job once in every month i.e. 4th around 6 and 7 am 
 * The logic inside the execute method is intended to go through each Push Notification Profile details that are available on Database and 
 * 	calculate the number of months still available before expiration.
 * If the calculated number of months is less than 3 then it sends an email notification to mobileapps@nike.com
 */

package com.nike.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PushProfilesValidityCheckJob implements Job {

	static Logger logger =  LogManager.getLogger(PushProfilesValidityCheckJob.class.getName());
			
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection con = null;
		Statement stmt = null;
		try {
			logger.info("A Push Notification Profile Check happened at " + Dates.getCurrentDate());
			con = getBDConnection(arg0);
			stmt = con.createStatement();
			ResultSet set = PushProfileDetails.getPushProfileDetails(stmt);
			boolean doesAnyProfileExpire = false;
			String content = "<ul>";
			if (set != null) {
				while (set.next()) {
					String expDate = set
							.getString(TableConstants.EXPIRATION_DATE);
					String appTitle = set
							.getString(TableConstants.APP_TITLE);
					int months = Dates.getNumberOfMonthDiff(expDate, appTitle);
					if (months <= 3) {
						doesAnyProfileExpire = true;
						content += "<li>"
								+ set.getString(TableConstants.APP_TITLE)
								+ " mobile application whose ApplicationID is "
								+ set.getString(TableConstants.APPLICATION_IDENTIFIER)
								+ ", expires on "
								+ expDate
								+ ". UrbanAirship account details are as follows: "
								+ "UserID: "
								+ set.getString(TableConstants.UA_USER_NAME)
								+ " Password: "
								+ set.getString(TableConstants.UA_PWD)
								+ "</li>";
						logger.info("Push Notification Profile/Profiles expires! "
								+ set.getString(TableConstants.APP_TITLE)
								+ " mobile application whose ApplicationID is "
								+ set.getString(TableConstants.APPLICATION_IDENTIFIER)
								+ ", expires on "
								+ expDate
								+ ". UrbanAirship account details are as follows: "
								+ "UserID: "
								+ set.getString(TableConstants.UA_USER_NAME)
								+ " Password: "
								+ set.getString(TableConstants.UA_PWD));
					}
				}
				if (doesAnyProfileExpire) {
					content += "</ul>";
//					System.out.println(content);
					try {
						new EmailNotification("mobileapps@nike.com",
								"Push Notification Profile/Profiles expires!",
								content, TableConstants.APPLICATION_ICON).start();
					} catch (Exception e) {
						logger.error("Exception in sending an email notification regarding expiration of Push Profile Notification. " + ErrorUtils.getStackTrace(e));
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException in sending an email notification regarding expiration of Push Profile Notification. " + ErrorUtils.getStackTrace(e));
		} catch (Exception e1) {
			logger.error("Exception in sending an email notification regarding expiration of Push Profile Notification. " + ErrorUtils.getStackTrace(e1));
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {
				logger.error("SQLException in closing the statement. " + ErrorUtils.getStackTrace(e2));
			}
		}
	}

	private Connection getBDConnection(JobExecutionContext context)
			throws Exception {
		return (Connection) context.getMergedJobDataMap().get("DBConnection");
	}

}
