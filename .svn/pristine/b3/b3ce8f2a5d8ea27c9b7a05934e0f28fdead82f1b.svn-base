package com.nike.util;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Authentication {
	
	Logger logger = LogManager.getLogger(Authentication.class
			.getName());
	protected String userID;
	protected String localPasswordString;
	protected String lastName;
	protected String firstName;
	protected String emailID;
	protected String question;
	protected String answer;
	protected String phoneNo;
	protected String department;
	
	public  boolean doUserBelongsToAGroup(MultiMap data, String userID, Statement stmt) {
		Collection collection = (Collection) data.get("Groups");
		for (Object val : collection) {
			String groupLabel = (String) val;
			// Group:
			// CN=Shareddata.Projects.WHQ.Owner,OU=SharedData,OU=Groups,DC=ad,DC=nike,DC=com
			if(doExist(groupLabel, stmt)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doExist(String groupLabel, Statement stmt) {
		if (groupLabel.contains(",")) {
			String CNString = groupLabel.split(",")[0];
			CNString = CNString.replace("CN=", "");
			logger.info("Group: " + CNString + " value");
			if (CNString.equalsIgnoreCase(TableConstants.MOBILE_APPS_PROD_GROUP_ADMINS)) {
//				try {
//					UserCheckUtil.updateUserDetails(stmt, emailID, logger, TableConstants.IS_PROD_ADMIN_VALUE);
					logger.info(String.format(userID + " is a member of group: "
							+ CNString));
//				} catch (SQLException e) {
//					logger.error("Failed to update database with prviliges value " + ErrorUtils.getStackTrace(e));
//				}
				return true;
			} else if(CNString.equalsIgnoreCase(TableConstants.MOBILE_APPS_DEV_GROUP_ADMINS)) {
//				try {
//					UserCheckUtil.updateUserDetails(stmt, emailID, logger, TableConstants.IS_DEV_ADMIN_VALUE);
					logger.info(String.format(userID + " is a member of group: "
							+ CNString));
//				} catch (SQLException e) {
//					logger.error("Failed to update database with prviliges value " + ErrorUtils.getStackTrace(e));
//				}
				return true;
			} else if(CNString.equalsIgnoreCase(TableConstants.MOBILE_APPS_DEV_GROUP_DEVELOPERS)) {
//				try {
//					UserCheckUtil.updateUserDetails(stmt, emailID, logger, TableConstants.IS_DEV_USER_VALUE);
					logger.info(String.format(userID + " is a member of group: "
							+ CNString));
//				} catch (SQLException e) {
//					logger.error("Failed to update database with prviliges value " + ErrorUtils.getStackTrace(e));
//				}
				return true;
			} else if(CNString.equalsIgnoreCase(TableConstants.MOBILE_APPS_PROD_GROUP_USERS)) {
//				try {
//					UserCheckUtil.updateUserDetails(stmt, emailID, logger, TableConstants.IS_PROD_USER_VALUE);
					logger.info(String.format(userID + " is a member of group: "
							+ CNString));
//				} catch (SQLException e) {
//					logger.error("Failed to update database with prviliges value " + ErrorUtils.getStackTrace(e));
//				}
				return true;
			} else {
				logger.info(String.format(userID + " does not have access to any privliged group."
						+ CNString));
			}
		}
		return false;
	}
	
	public BuildJSON addUserToLocalDatabase(HttpServletRequest request,
			HttpServletResponse response, Statement stmt, BuildJSON parentJSON) throws Exception {
		// Uncomment when the user needs to be added
		parentJSON = new BuildJSON();
		parentJSON = UserCheckUtil.addUserToBusinessOwnerTable(
				// Adding the new user to the database.
				stmt, "", userID, firstName, lastName, "Password1@", emailID,
				phoneNo, question, answer, parentJSON, logger, null);
		if (parentJSON == null) {
			logger.error("Failed to register user in local database: " + userID);
		} else {
			int isAdmin = Integer.parseInt(parentJSON
					.getElement(TableConstants.IS_ADMIN));
			UserSessionObject userSession = new UserSessionObject(userID,
					parentJSON.getElement(TableConstants.BO_DEPT), isAdmin,
					emailID);
			HttpSession session = request.getSession(true);
			String boDept = session.getId();
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			synchronized (session) {
				session.setAttribute(TableConstants.BO_DEPT, userSession);
				redirectResponse(response, boDept);
			}
			logger.info("Successful addition of new user: " + userID);
			return parentJSON;
		}
		return null;
	}
	
	public void redirectResponse(HttpServletResponse response, String boDept) throws IOException {
		response.sendRedirect(TableConstants.WELCOME_PAGE_URL
				+ "?BoDept=" + boDept);
	}
	
	public BuildJSON processUserDetailsFromLocalDatabase(
			HttpServletRequest request, HttpServletResponse response, BuildJSON parentJSON)
			throws IOException {
		int isAdmin = Integer.parseInt(parentJSON
				.getElement(TableConstants.IS_ADMIN));
		HttpSession session = request.getSession(true);
		String boDept = session.getId();
		parentJSON.addElement(TableConstants.BO_DEPT, boDept);
		synchronized (session) {
			UserSessionObject userSession = new UserSessionObject(userID,
					parentJSON.getElement(TableConstants.BO_DEPT), isAdmin,
					emailID);
			session.setAttribute(TableConstants.BO_DEPT, userSession);
			redirectResponse(response, boDept);
		}
		logger.info("Successful user login validation of user: " + userID);
		return parentJSON;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPassword() {
		return localPasswordString;
	}

	public void setPassword(String password) {
		this.localPasswordString = password;
	}
}
