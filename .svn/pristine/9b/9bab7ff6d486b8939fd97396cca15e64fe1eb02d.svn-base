/*
 * PushProfileDetails utility class provides the details related to the Push Notification Profile details and methods related to updating/inserting records into the database.
 *	-> putRow(Statement stmt, String appTitle,
			String creationDate, String expirationDate, String appId) : Inserts or updates the Profile record details in the database.
 *	-> putUADetails(Statement stmt, String appTitle,
			String UAUserName, String UAPwd) : Inserts or updates the Profile record details in the database.
 *	-> doRecodExist(Statement stmt, String appTitle) : Helper method to check whether the record exists or not.
 *	-> getExpDate(Statement stmt, String appTitle)
 *	-> getPushProfileDetails(Statement stmt)
 *	-> isPushCertificateValid(Statement stmt, String appTitle)
 */
package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PushProfileDetails {
	
	static Logger logger =  LogManager.getLogger(PushProfileDetails.class.getName());

	public static boolean putRow(Statement stmt, String appTitle,
			String creationDate, String expirationDate, String appId) {
		if (stmt == null || appTitle == null || creationDate == null
				|| expirationDate == null || appId == null) {
			return false;
		}
		String query;
		if (doRecodExist(stmt, appTitle)) {
			query = "UPDATE " + TableConstants.TABLE_PUSH_PROFILE_DETAILS
					+ " SET " + TableConstants.CREATION_DATE + "='"
					+ creationDate + "', " + TableConstants.EXPIRATION_DATE
					+ "='" + expirationDate + "', "
					+ TableConstants.APPLICATION_IDENTIFIER + "='" + appId
					+ "' WHERE " + TableConstants.APP_TITLE + "='" + appTitle
					+ "';";
		} else {
			query = "INSERT INTO " + TableConstants.TABLE_PUSH_PROFILE_DETAILS
					+ "(" + TableConstants.APP_TITLE + ", "
					+ TableConstants.CREATION_DATE + ", "
					+ TableConstants.EXPIRATION_DATE + ", "
					+ TableConstants.APPLICATION_IDENTIFIER + ") VALUES ('"
					+ appTitle + "', '" + creationDate + "', '"
					+ expirationDate + "', '" + appId + "');";
		}

		try {
			int result = stmt.executeUpdate(query);
			if (result > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in executing the update of query. " + query);
			logger.error(ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean putUADetails(Statement stmt, String appTitle,
			String UAUserName, String UAPwd) {
		if (stmt == null || appTitle == null || UAUserName.equals("__")
				|| UAPwd == null || UAUserName == ""
				|| UAPwd.equals("__") ) {
			return true;
		}
		String query;
		if (doRecodExist(stmt, appTitle)) {
			query = "UPDATE " + TableConstants.TABLE_PUSH_PROFILE_DETAILS
					+ " SET " + TableConstants.UA_USER_NAME + "='"
					+ UAUserName + "', " + TableConstants.UA_PWD
					+ "='" + UAPwd 
					+ "' WHERE " + TableConstants.APP_TITLE + "='" + appTitle
					+ "';";
		} else {
			query = "INSERT INTO " + TableConstants.TABLE_PUSH_PROFILE_DETAILS
					+ "(" + TableConstants.APP_TITLE + ", "
					+ TableConstants.UA_USER_NAME + ", "
					+ TableConstants.UA_PWD + ") VALUES ('"
					+ appTitle + "', '" + UAUserName + "', '"
					+ UAPwd + "');";
		}
		logger.info("Query " + query);
		try {
			int result = stmt.executeUpdate(query);
			if (result > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in executing the update of query. " + query);
			logger.error(e.getMessage());
			return false;
		}
	}

	public static boolean doRecodExist(Statement stmt, String appTitle) {
		String queryCheck = "SELECT COUNT(*) as " + TableConstants.COUNT + ", "
				+ TableConstants.EXPIRATION_DATE + " FROM "
				+ TableConstants.TABLE_PUSH_PROFILE_DETAILS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.EXPIRATION_DATE + " ASC;";
		try {
			ResultSet res = stmt.executeQuery(queryCheck);
			if (res.next() && res.getInt(TableConstants.COUNT) == 1) {
				res.close();
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in executing the update of query. " + queryCheck);
			logger.error(e.getMessage());
			return false;
		}
	}
	
	public static String getExpDate(Statement stmt, String appTitle) {
		String queryCheck = "SELECT COUNT(*) as " + TableConstants.COUNT + ", "
				+ TableConstants.EXPIRATION_DATE + " FROM "
				+ TableConstants.TABLE_PUSH_PROFILE_DETAILS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.EXPIRATION_DATE + " ASC;";
		try {
			ResultSet res = stmt.executeQuery(queryCheck);
			if (res.next() && res.getInt(TableConstants.COUNT) == 1) {
				String dateString = res.getString(TableConstants.EXPIRATION_DATE);
				res.close();
				return dateString;
			} else {
				logger.info("Either ResultSet no next or count not > 1");
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in executing the update of query. " + queryCheck);
			logger.error(ErrorUtils.getStackTrace(e));
			return null;
		}
	}
	
	public static String getCreationDate(Statement stmt, String appTitle) {
		String queryCheck = "SELECT COUNT(*) as " + TableConstants.COUNT + ", "
				+ TableConstants.CREATION_DATE + " FROM "
				+ TableConstants.TABLE_PUSH_PROFILE_DETAILS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.EXPIRATION_DATE + " ASC;";
		try {
			ResultSet res = stmt.executeQuery(queryCheck);
			if (res.next() && res.getInt(TableConstants.COUNT) == 1) {
				String dateString = res.getString(TableConstants.CREATION_DATE);
				res.close();
//				stmt.close();
				return dateString;
			} else {
				logger.info("Either ResultSet no next or count not > 1");
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in executing the update of query. " + queryCheck);
			logger.error(ErrorUtils.getStackTrace(e));
			return null;
		}
	}

	public static ResultSet getPushProfileDetails(Statement stmt) {
		if (stmt == null) {
			return null;
		} else {
			String query = "SELECT * FROM "
					+ TableConstants.TABLE_PUSH_PROFILE_DETAILS + " ORDER BY "
					+ TableConstants.EXPIRATION_DATE + " ASC;";
			try {
				ResultSet result = stmt.executeQuery(query);
				return result;
			} catch (SQLException e) {
				logger.error("SQLException in executing the update of query. " + query);
				logger.error(ErrorUtils.getStackTrace(e));
				return null;
			}
		}
	}

	public static boolean isPushCertificateValid(Statement stmt, String appTitle) {
		if (stmt == null || appTitle == null) {
			return false;
		}
		String expDate = getExpDate(stmt, appTitle);
		logger.info("Expired date: " + expDate);
		if (expDate != null
				&& Dates.getNumberOfMonthDiff(expDate, appTitle) > 3) {
			return true;
		} else {
			return false;
		}
	}
}
