/*
 * UpdateRequestCount utility class is used by multiple servlets for updating the Request Count and getting the request count
 * 	->	addRecordOfRequestCount(Statement stmt, String appTitle, String boDept): Adds a record with the new App details and 1 as request count
 * 	->	updateRecordOfRequestCount(Statement stmt, String appTitle, String boDept):	Updates the request count for the app with appTitle
 * 	->	removeRecordFromRequestCount(Statement stmt, String appTitle): Removes the app record from the table when user requests to remove the app
 * 	->	getAppFinalRequestCount(Statement stmt, String appTitle, boolean doExist): Gets the latest request count from database.
 */

package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateRequestCount {
	
	static Logger logger = LogManager.getLogger(UpdateRequestCount.class.getName());

	public static boolean addRecordOfRequestCount(Statement stmt, String appTitle, String boDept) {
		String query = "INSERT INTO " + TableConstants.TABLE_REQUEST_COUNT
				+ " (" 
				+ TableConstants.APP_TITLE + ", "
				+ TableConstants.IN_YEAR + ", " 
				+ Dates.getCurrentMonth() + ", " 
				+ TableConstants.BO_DEPT 
				+ ") VALUES ('"
				+ appTitle + "', "
				+ Dates.getCurrentYear() + ", " 
				+ 1 + ", "
				+ "'" + boDept +"'"
				+ ");";
		try {
			if (stmt.executeUpdate(query) == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in adding the new app record " +ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean updateRecordOfRequestCount(Statement stmt,
			String appTitle, String boDept, boolean isAdmin) {
		int requestCount = getAppFinalRequestCount(stmt, appTitle, true) + 1;
		if(requestCount > 0) {
			String query;
			if (isAdmin) {
				query = "UPDATE " + TableConstants.TABLE_REQUEST_COUNT
						+ " SET " + Dates.getCurrentMonth() + "="
						+ requestCount + " " + " WHERE " + TableConstants.APP_TITLE
						+ "='" + appTitle + "' AND " + TableConstants.IN_YEAR
						+ "=" + Dates.getCurrentYear() + ";";
			} else {
				query = "UPDATE " + TableConstants.TABLE_REQUEST_COUNT
						+ " SET " + Dates.getCurrentMonth() + "="
						+ requestCount + ", " + TableConstants.BO_DEPT + "='"
						+ boDept + "'" + " WHERE " + TableConstants.APP_TITLE
						+ "='" + appTitle + "' AND " + TableConstants.IN_YEAR
						+ "=" + Dates.getCurrentYear() + ";";
			}
			try {
				if(stmt.executeUpdate(query) == 1) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				logger.error("SQLException in updating the existing app record " +ErrorUtils.getStackTrace(e));
				return false;
			}
		} else {
			return addRecordOfRequestCount(stmt, appTitle, boDept);
		}
	}

	public static boolean updateBusinessOwnersOfRequestCount(Statement stmt,
			String appTitle, String boDepts, boolean isAdmin) throws SQLException {
		int requestCount = getAppFinalRequestCount(stmt, appTitle, true) + 1;
		if (requestCount > 0 && isAdmin) {
			String query;
			query = "UPDATE " + TableConstants.TABLE_REQUEST_COUNT + " SET "
					+ TableConstants.BO_DEPT + "='" + boDepts + "'" + " WHERE "
					+ TableConstants.APP_TITLE + "='" + appTitle + "';";
			if (stmt.executeUpdate(query) == 1) {
				return true;
			} else {
				return false;
			}
		} else if (isAdmin){
			return addRecordOfRequestCount(stmt, appTitle, boDepts);
		}
		logger.error("Unauthorized access to update the RequestCount table Business Owner.");
		return false;
	}
	
	public static boolean removeRecordFromRequestCount(Statement stmt,
			String appTitle, boolean isAdmin) {
		String query;
		if (isAdmin) {
			query = "DELETE FROM " + TableConstants.TABLE_REQUEST_COUNT
					+ " WHERE " + TableConstants.APP_TITLE + "='" + appTitle
					+ "';";
			try {
				if(stmt.executeUpdate(query) == 1) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				logger.error("SQLException in deleting the existing app record " +ErrorUtils.getStackTrace(e));
				return false;
			}
		} 
		return false;
	}
	
	public static boolean doExists(Statement stmt, String appTitle) {
		String query = "SELECT * FROM " + TableConstants.TABLE_REQUEST_COUNT
				+ " WHERE " + TableConstants.APP_TITLE + "='" + appTitle
				+ "' AND " + TableConstants.IN_YEAR + "="
				+ Dates.getCurrentYear() + ";";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the existing app record latest request count "
					+ ErrorUtils.getStackTrace(e));
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("SQLException in closing the statement "
							+ ErrorUtils.getStackTrace(e));
				}
			}
		}
		return false;
	}
	
	public static int getAppMaxReqCount(Statement stmt, String appTitle) {
		String query = "SELECT * FROM "
				+ TableConstants.TABLE_REQUEST_COUNT + " WHERE "
				+ TableConstants.APP_TITLE +"='"
				+ appTitle + "' AND "
				+ TableConstants.IN_YEAR + "="
				+ Dates.getCurrentYear() 
				+ ";";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if(result.next()) {
				int count = 0;
				for(int i = 0; i < 12; i++) {
					count += result.getInt(Dates.months[i]);
				}
				return count;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the existing app record latest request count " +ErrorUtils.getStackTrace(e));
			return 0;
		} finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("SQLException in closing the statement " +ErrorUtils.getStackTrace(e));
				}
			}
		}
	}
	
	public static int getAppFinalRequestCount(Statement stmt, String appTitle, boolean doExist) {
		String query = "SELECT * FROM "
				+ TableConstants.TABLE_REQUEST_COUNT + " WHERE "
				+ TableConstants.APP_TITLE +"='"
				+ appTitle + "' AND "
				+ TableConstants.IN_YEAR + "="
				+ Dates.getCurrentYear() 
				+ ";";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if(result.next()) {
				return result.getInt(Dates.getCurrentMonth());
			} else {
				return -1;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the existing app record latest request count " +ErrorUtils.getStackTrace(e));
			return -1;
		} finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					logger.error("SQLException in closing the statement " +ErrorUtils.getStackTrace(e));
				}
			}
		}
		
	}
	
}
