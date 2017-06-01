/*
 * UpdateProjectDetails utility class has one method
 * 	->	updateProjectPushProfileDetails(Statement stmt, String appTitle, String profilePath): Takes care of updating the PROJECTS table with latest Push profile path
 */

package com.nike.util;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateProjectDetails {
	
	static Logger logger =  LogManager.getLogger(UpdateProjectDetails.class.getName());

	public static boolean updateProjectPushProfileDetails(Statement stmt,
			String appTitle, String profilePath) {
		if (stmt == null || appTitle == null && profilePath == null) {
			return false;
		} else {
			String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
					+ TableConstants.PUSH_PROFILE_PATH + "='" + profilePath
					+ "' WHERE " + TableConstants.APP_TITLE + "='" + appTitle
					+ "';";
			int result;
			try {
				result = stmt.executeUpdate(query);
				if (result > 0) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				logger.error("SQLException in performing the update on Projects table " + ErrorUtils.getStackTrace(e));
				return false;
			}

		}
	}
	
	public static boolean updateProjectBusinessOwnerDetails(Statement stmt,
			String appTitle, String userIDs) throws SQLException {
		if (stmt == null || appTitle == null && userIDs == null) {
			return false;
		} else {
			String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
					+ TableConstants.BO_DEPT + "='" + userIDs + "' WHERE "
					+ TableConstants.APP_TITLE + "='" + appTitle + "';";
			int result;
			result = stmt.executeUpdate(query);
			if (result > 0) {
				return true;
			} else {
				return false;
			}

		}
	}
}
