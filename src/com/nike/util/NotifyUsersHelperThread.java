package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotifyUsersHelperThread extends Thread {

	private static boolean isActive = false;
	private ResultSet resultSet;
	private String date;
	static Logger logger = LogManager.getLogger(NotifyUsersHelperThread.class.getName());

	public NotifyUsersHelperThread(ResultSet result, String date) {
		this.resultSet = result;
		this.date = date;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		if (date == null || resultSet == null) {
			isActive = false;
			logger.error("Unable to notify the users.");
			return;
		}
		try {
			String projects = "";
			String subject = "Application Profile Expires! (Nike Tech Store)";
			while (resultSet.next()) {
				String projectSignedDate = resultSet
						.getString(TableConstants.DATE);
				if (!Dates.isDateGreater(projectSignedDate, date)) { // Email
																		// are
																		// sent
																		// to
																		// the
																		// applications
																		// whose
																		// sign
																		// date
																		// wasn't
																		// greater
																		// than
																		// the
																		// condition
																		// date
					String projectName = resultSet
							.getString(TableConstants.APP_TITLE);
					projects += "<p>" + projectName + "</p>";
					String body = "<p>Hello</p>"
							+ createParagraph("The application, " + projectName
									+ " profile will be expired soon.")
							+ createParagraph("We recommend you to send us an updated application.")
							+ createParagraph("If you don't have an update then please increment the version number and send us the application for deployment.")
							+ createParagraph("Please let us know if you are planning to update your application on Tech Store or not.")
							+ createParagraph("If you are planning to remove the application from Nike Tech Store then please do inform us via email to "
									+ TableConstants.ADMIN_EMAIL_IDS)
							+ createParagraph("If you have any questions, then please feel free to contact us.")
							+ createParagraph("We look forward to hear from you.")
							+ createParagraph("If you have already taken care of it, then please ignore this email.")
							
							+ createParagraph("P.S: If you are not the Application Owner any more, then please forward this email to the respective person OR Please let us know.")
							+ createParagraph("") + createParagraph("Thanks!")
							+ createParagraph("MOBILE APPS.");
					String toEmailAddress = resultSet
							.getString(TableConstants.TSC_EMAIL);
					try {
						EmailNotification notify = new EmailNotification(
								toEmailAddress, subject + " " + projectName,
								body, GetImageDetails.getProjectIcon(resultSet.getString(TableConstants.APP_TITLE)));
						notify.start();
						Thread.sleep(100);
					} catch (Exception e) {
						logger.error(ErrorUtils.getStackTrace(e));
					}
				}
			}
			if (!projects.equals("")) {
				String body = createParagraph("An informative email is sent to the business owners about the Profile Expiration. The following projects have to be resigned with the new Generic profile: ");
				body += projects;
				EmailNotification notify; // Email notification to Administrator.
				try {
					notify = new EmailNotification(
							TableConstants.ADMIN_EMAIL_IDS, subject, body, TableConstants.APPLICATION_ICON);
					notify.start();
				} catch (Exception e) {
					logger.error(ErrorUtils.getStackTrace(e));
				}
			}
		} catch (SQLException e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
		isActive = false;
	}

	private String createParagraph(String content) {
		return "<p>" + content + "</p>";
	}

	@Override
	public synchronized void start() {
		if (!isActive) {
			isActive = true;
			super.start();
		}
	}

	public boolean isActive() {
		return isActive;
	}

}
