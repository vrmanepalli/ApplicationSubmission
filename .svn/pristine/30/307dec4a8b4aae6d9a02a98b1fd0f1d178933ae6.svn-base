package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

public class UserCheckUtil {

	public static BuildJSON doUserExist(Statement stmt, String emailID,
			BuildJSON parentJSON, Logger logger) throws SQLException {
		if (stmt != null && emailID != null && EmailValidator.validate(emailID)
				&& parentJSON != null) {
			String query = "select COUNT(*) as " + TableConstants.COUNT + ", "
					+ TableConstants.FIRST_NAME + ", " + TableConstants.USER_ID
					+ ", " + TableConstants.LAST_NAME + ", "
					+ TableConstants.EMAIL_ID + ", " + TableConstants.PHONE_NO
					+ ", " + TableConstants.DEPT + ", "
					+ TableConstants.BO_DEPT + ", "
					+ TableConstants.LAST_APP_REQ_TITLE + ", "
					+ TableConstants.IS_ADMIN + ", "
					+ TableConstants.IPHONE_TOKEN + ", "
					+ TableConstants.IPAD_TOKEN + ", "
					+ TableConstants.IPOD_TOKEN + ", " + TableConstants.IPHONE
					+ ", " + TableConstants.IPAD + ", " + TableConstants.IPOD
					+ " FROM " + TableConstants.TABLE_BUSINESS_OWNER
					+ " where " + TableConstants.EMAIL_ID + "='" + emailID
					+ "';";
			ResultSet res = stmt.executeQuery(query);
			parentJSON = getBuildJSONObject(res, parentJSON, logger);
			if (parentJSON != null) {
				parentJSON.addElement(TableConstants.USER_ID,
						res.getString(TableConstants.USER_ID));
				if (res != null) {
					try {
						res.close();
					} catch (SQLException e) {
						logger.error(ErrorUtils.getStackTrace(e));
					}
				}
				return parentJSON;
			}
		}
		return null;
	}
	
	public static boolean updateUserDetails(Statement stmt, String emailID,
			Logger logger, int isAdminValue)
			throws SQLException {
		if (stmt != null && emailID != null && EmailValidator.validate(emailID)) {
			String query;
			query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET "
					+ TableConstants.IS_ADMIN + "=" + isAdminValue + " WHERE "
					+ TableConstants.EMAIL_ID + "='" + emailID + "';";
			int result = stmt.executeUpdate(query);
			if (result > -1) {
				return true;
			}
		}
		return false;
	}

	public static BuildJSON doUserExist(Statement stmt, String userID,
			String pwd, BuildJSON parentJSON, Logger logger)
			throws SQLException {
		if (stmt != null && userID != null && pwd != null && parentJSON != null) {
			String query = "select COUNT(*) as " + TableConstants.COUNT + ", "
					+ TableConstants.FIRST_NAME + ", "
					+ TableConstants.LAST_NAME + ", " + TableConstants.EMAIL_ID
					+ ", " + TableConstants.PHONE_NO + ", "
					+ TableConstants.DEPT + ", " + TableConstants.BO_DEPT
					+ ", " + TableConstants.LAST_APP_REQ_TITLE + ", "
					+ TableConstants.IS_ADMIN + ", "
					+ TableConstants.IPHONE_TOKEN + ", "
					+ TableConstants.IPAD_TOKEN + ", "
					+ TableConstants.IPOD_TOKEN + ", " + TableConstants.IPHONE
					+ ", " + TableConstants.IPAD + ", " + TableConstants.IPOD
					+ " FROM " + TableConstants.TABLE_BUSINESS_OWNER
					+ " where " + TableConstants.USER_ID + "='" + userID
					+ "' and " + TableConstants.PWD + "=AES_ENCRYPT('" + pwd
					+ "', '" + pwd + "');";

			ResultSet res = stmt.executeQuery(query);
			parentJSON = getBuildJSONObject(res, parentJSON, logger);
			if (parentJSON != null) {
				parentJSON.addElement(TableConstants.USER_ID, userID);
				if (res != null) {
					try {
						res.close();
					} catch (SQLException e) {
						logger.error(ErrorUtils.getStackTrace(e));
					}
				}
				return parentJSON;
			}
		}
		return null;
	}
	
	public static BuildJSON addUserToBusinessOwnerTable(Statement stmt,
			String dept, String userID, String firstName, String lastName,
			String pwd, String emailID, String phoneNo, String question,
			String answer, BuildJSON parentJSON, Logger logger, String query)
			throws Exception {
		String boDept = dept.replace(" ", "_").hashCode() + "_" + userID + "_"
				+ dept.replace(" ", "_").hashCode();
		if (query == null) {
			query = "INSERT INTO " + TableConstants.TABLE_BUSINESS_OWNER + " ("
					+ TableConstants.FIRST_NAME + ", "
					+ TableConstants.LAST_NAME + ", " + TableConstants.USER_ID
					+ ", " + TableConstants.PWD + ", "
					+ TableConstants.EMAIL_ID + ", " + TableConstants.PHONE_NO
					+ ", " + TableConstants.DEPT + ", "
					+ TableConstants.BO_DEPT + ", " + TableConstants.IS_ADMIN
					+ ", " + TableConstants.QUESTION + ", "
					+ TableConstants.ANSWER + ", " + TableConstants.DATE + ") "
					+ "VALUES ('" + firstName + "', '" + lastName + "', '"
					+ userID + "', " + "AES_ENCRYPT('" + pwd + "', '" + pwd
					+ "'), '" + emailID + "', '" + phoneNo + "', '" + dept
					+ "', '" + boDept + "', " + "0, '" + question + "', '"
					+ answer + "', '" + Dates.getCurrentDate() + "');";
		}
		if (stmt.executeUpdate(query) == 1) {
			parentJSON.addElement(TableConstants.SUCCESS,
					"Successful Registration!");
			parentJSON.addElement(TableConstants.IS_USER, "true");
			parentJSON.addElement(TableConstants.FIRST_NAME, firstName);
			parentJSON.addElement(TableConstants.LAST_NAME, lastName);
			parentJSON.addElement(TableConstants.EMAIL_ID, emailID);
			parentJSON.addElement(TableConstants.PHONE_NO, phoneNo);
			parentJSON.addElement(TableConstants.DEPT, dept);
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			parentJSON.addElement(TableConstants.ERROR, "");
			new EmailNotification(emailID, TableConstants.REGISTRATION_SUBJECT,
					TableConstants.REGISTRATION_MSG, TableConstants.APPLICATION_ICON).start();
			logger.info("Successfully registration by user: " + userID);
			return parentJSON;
		} else {
			logger.error("Failed to Register!");
		}
		return null;
	}

	public static BuildJSON getBuildJSONObject(ResultSet res,
			BuildJSON parentJSON, Logger logger) {
		try {
			if (res.next() && res.getInt(TableConstants.COUNT) > 0) {
				parentJSON.addElement(TableConstants.IS_USER, "true");
				parentJSON.addElement(TableConstants.BO_DEPT,
						res.getString(TableConstants.BO_DEPT));
				parentJSON.addElement(TableConstants.FIRST_NAME,
						res.getString(TableConstants.FIRST_NAME));
				parentJSON.addElement(TableConstants.LAST_NAME,
						res.getString(TableConstants.LAST_NAME));
				parentJSON.addElement(TableConstants.EMAIL_ID,
						res.getString(TableConstants.EMAIL_ID));
				parentJSON.addElement(TableConstants.PHONE_NO,
						res.getString(TableConstants.PHONE_NO));
				parentJSON.addElement(TableConstants.DEPT,
						res.getString(TableConstants.DEPT));
				parentJSON.addElement(TableConstants.LAST_APP_REQ_TITLE,
						res.getString(TableConstants.LAST_APP_REQ_TITLE));
				parentJSON.addElement(TableConstants.ERROR, "");
				int isAdmin = res.getInt(TableConstants.IS_ADMIN);
				parentJSON.addElement(TableConstants.IS_ADMIN,
						isAdmin);
				return parentJSON;
			}
		} catch (SQLException e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
		return null;
	}
}
