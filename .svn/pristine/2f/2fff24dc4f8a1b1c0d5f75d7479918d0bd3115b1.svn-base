package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NotifyAllUsersHelperThread extends Thread {

	private static boolean isActive = false;
	private ResultSet resultSet;
	private String adminSubject;
	private String adminBody;
	private boolean doAppend;
	private String profileExpiryDate;
	static Logger logger = LogManager.getLogger(NotifyAllUsersHelperThread.class.getName());

	public NotifyAllUsersHelperThread(ResultSet result, String adminSubject, String adminBody, boolean doAppend, String profileExpiryDate) {
		this.resultSet = result;
		this.adminSubject = adminSubject;
		this.adminBody = adminBody;
		this.doAppend = doAppend;
		this.profileExpiryDate = profileExpiryDate;
	}

	@Override
	public void run() {
		logger.entry();
		super.run();
		if (resultSet == null) {
			isActive = false;
			logger.error("Unable to notify the users.");
			return;
		}
		try {
			String projects = " ";
			boolean isProfileRenewal = false;
			if(profileExpiryDate.equals(TableConstants.GENERAL)){
				isProfileRenewal = false;
			} else {
				isProfileRenewal = true;
			}
			while (resultSet.next()) {
				String projectDate = null;
				boolean doSendEmail;
				if (!isProfileRenewal || profileExpiryDate.equals(TableConstants.SPECIFIC_USER)) {
					doSendEmail = true;
				} else {
					projectDate = resultSet.getString(TableConstants.DATE);
					doSendEmail = Dates.getNumberOfDaysDiffBtwTwoDates(
							projectDate, profileExpiryDate) > 0 ? true : false;
				}
				if(doSendEmail) {
					String toEmailAddress = resultSet
							.getString(TableConstants.TSC_EMAIL);
					logger.entry(toEmailAddress);
					if (EmailValidator.validate(toEmailAddress)) { // Email
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
						String projectName = "";
						String subject = adminSubject;
						if (isProfileRenewal) {
							projectName = resultSet
									.getString(TableConstants.APP_TITLE);
							subject = adminSubject + " Project: " + projectName;
							projects += "<p>" + projectName + "</p>";
						}
						String body = createParagraph(adminBody);
						if(doAppend) {
							if (isProfileRenewal) {
								body += createParagraph("The application, " + projectName
										+ " has to be updated.");
							}
							body += createParagraph("We recommend you to send us an updated application.")
							+ createParagraph("Please do increment the version number of your updated application and send us the application for deployment.")
							+ createParagraph("Please let us know if you are planning to update your application on Tech Store or not.")
							+ createParagraph("If you are planning to remove the application from Nike Tech Store then please do inform us via email to "
									+ TableConstants.ADMIN_EMAIL_IDS)
							+ createParagraph("If you have any questions, then please feel free to contact us.")
							+ createParagraph("We look forward to hear from you.")
							+ createParagraph("If you have already taken care of it, then please ignore this email.")
							
							+ createParagraph("P.S: If you are not the Application Owner any more, then please forward this email to the respective person OR Please let us know.")
							+ createParagraph("") + createParagraph("Thanks!")
							+ createParagraph("MOBILE APPS.");
						} else {
//							body += createParagraph("If you have any questions, then please feel free to contact us.")
//									+ createParagraph("We look forward to hear from you.")
//									+ createParagraph("If you have already taken care of it, then please ignore this email.")
//									
//									+ createParagraph("P.S: If you are not the Application Owner any more, then please forward this email to the respective person OR Please let us know.")
//									+ createParagraph("") + createParagraph("Thanks!")
//									+ createParagraph("MOBILE APPS.");
//									+ createParagraph(TableConstants.ADMIN_EMAIL_IDS);
						}
						try {
							String imageURL = TableConstants.APPLICATION_ICON;
							if (isProfileRenewal) {
								imageURL = GetImageDetails.getProjectIcon(resultSet.getString(TableConstants.APP_TITLE));
							}
							EmailNotification notify = new EmailNotification(
									toEmailAddress, subject,
									body, imageURL);
							logger.info("ToEmail: " + toEmailAddress + " subject: " + subject + " Project Name: " + projectName);
							notify.start();
							Thread.sleep(100);
						} catch (Exception e) {
							logger.error(ErrorUtils.getStackTrace(e));
						}
					}
				}
			}
			if (!projects.equals("")) {
				String body = createParagraph("An informative email is sent to the business owners about the Profile Expiration. The following projects have to be resigned with the new Generic profile: ") + createParagraph(adminBody);
				body += projects;
				EmailNotification notify; // Email notification to Administrator.
				try {
					notify = new EmailNotification(
							TableConstants.ADMIN_EMAIL_IDS, adminSubject, body, TableConstants.APPLICATION_ICON);
					logger.info("Body: " + body + " subject: " + adminSubject + " Projects: " + projects);
					notify.start();
				} catch (Exception e) {
					logger.error(ErrorUtils.getStackTrace(e));
				}
			}
		} catch (SQLException e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
		isActive = false;
		logger.exit(false);
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
