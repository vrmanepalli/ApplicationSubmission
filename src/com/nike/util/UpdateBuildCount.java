/*
 * UpdateBuildCount utility class provides the methods to multiple servlets to update BuildNumber on Database
 * 	->	updateBuildNumber(String lastAppReqTitle,String date, int buildNumber, Statement stmt): Updates the build number
 * 	->	getCurrentBuildNumber(Statement stmt): Gets the latest build number from database by making a query
 */

package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateBuildCount {

	private static String error;
	public static int CURRENT_BUILD_NUMBER = -1;
	static Logger logger =  LogManager.getLogger(UpdateBuildCount.class.getName());

	public static boolean updateBuildNumber(String lastAppReqTitle,
			String date, int buildNumber, Statement stmt) {
		String query = "UPDATE " + TableConstants.TABLE_BUILD_COUNT + " SET "
				+ TableConstants.BUILD_NUMBER + "='" + buildNumber + "', "
				+ TableConstants.APP_TITLE + "='" + lastAppReqTitle + "', "
				+ TableConstants.DATE + "='" + date + "' WHERE "
				+ TableConstants.ID + "='" + 1 + "';";
		CURRENT_BUILD_NUMBER = buildNumber;
		int res;
		try {
			res = stmt.executeUpdate(query);
			if (res == 1) {
				return true;
			} else {
				error = null;
				logger.error("Failed to update the builder number of Table BuildCount");
				return false;
			}
		} catch (SQLException e) {
			error = e.getMessage();
			logger.error("Failed to update the builder number of Table BuildCount "
					+ error + " " + ErrorUtils.getStackTrace(e));
			return false;
		}
	}

	public static int getCurrentBuildNumber(Statement stmt) {
		if(stmt == null) {
			return -1;
		}
		else if (CURRENT_BUILD_NUMBER == -1) {
			String query = "SELECT * FROM " + TableConstants.TABLE_BUILD_COUNT
					+ " WHERE " + TableConstants.ID + "='" + 1 + "';";
			ResultSet res = null;
			try {
				res = stmt.executeQuery(query);
				if (res.next()) {
					CURRENT_BUILD_NUMBER = res.getInt(TableConstants.BUILD_NUMBER);
				} else {
					CURRENT_BUILD_NUMBER = -1;
					error = null;
					logger.error("Failed to update the builder number of Table BuildCount");
				}
			} catch (SQLException e) {
				CURRENT_BUILD_NUMBER = -1;
				error = e.getMessage();
				logger.error("Failed to update the builder number of Table BuildCount "
						+ error + " " + ErrorUtils.getStackTrace(e));
			} 
		}
		return CURRENT_BUILD_NUMBER;
	}
}
