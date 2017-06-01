/*
 * GetProjectDetails utility class provides the functionality to do queries against the database to find out the 
 * 	-> App old version number: getUserProjectAppOldVersion -> returns String
 * 	-> App Current Version Number: getUserProjectAppCurrentVersionAndBoDept -> returns ResultSet
 * 	-> Get the incremented request number, this is to keep track of the analytics: getIncrementedRequestNumber -> returns integer
 * 	-> Get the count of successful builds which is incremented: getIncrementedSuccessfulBuildNumber -> returns integer
 * 	-> Check if the project exists with respect to the project name provided: doProjectExist -> returns boolean
 * 	->	Check if the zip file of the project exists with respect to the project name: doZipFileExists -> returns boolean
 * 
 * */
package com.nike.util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetProjectDetails {
	
	static Logger logger =  LogManager.getLogger(GetProjectDetails.class.getName());

	public static String getUserProjectAppOldVersion(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return null;
		}
		String query = "SELECT " + TableConstants.APP_OLD_VERSION + " FROM "
				+ TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next()) {
				return res.getString(TableConstants.APP_OLD_VERSION);
			} else {
				return "0.0.0";
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			return "0.0.0";
		}

	}
	
	public static AppDetailsForJenkinsCall getUserProjectAppOldVersionAlongWithShotString(Statement stmt,
			String appTitle) throws SQLException {
		if(stmt == null || appTitle == null) {
			return null;
		}
		AppDetailsForJenkinsCall appDetails = new AppDetailsForJenkinsCall();
		String query = "SELECT " + TableConstants.APP_OLD_VERSION + ", " + TableConstants.APP_OLD_VERSION_SHORT_STRING + ", " + TableConstants.APP_BUNDLE_ID + " FROM "
				+ TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		ResultSet res = null;
		try {
			res = stmt.executeQuery(query);
			if (res.next()) {
				appDetails.setVersion(res.getString(TableConstants.APP_OLD_VERSION) + "^" + res.getString(TableConstants.APP_OLD_VERSION_SHORT_STRING));
				appDetails.setBundleID(res.getString(TableConstants.APP_BUNDLE_ID) != null ? res.getString(TableConstants.APP_BUNDLE_ID) : TableConstants.NEW_APP);
			} else {
				appDetails.setVersion("0.0.0^0.0.0");
				appDetails.setBundleID(TableConstants.NEW_APP);
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			appDetails.setVersion("0.0.0^0.0.0");
			appDetails.setBundleID(TableConstants.NEW_APP);
		} finally {
			if(res != null) {
				res.close();
			}
		}
		return appDetails;
	}
	
	public static class AppDetailsForJenkinsCall {
		private String version;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
		
		private String bundleID;

		public String getBundleID() {
			return bundleID;
		}

		public void setBundleID(String bundleID) {
			this.bundleID = bundleID;
		}
	}

	public static ResultSet getUserProjectAppCurrentVersionAndBoDept(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return null;
		}
		String query = "SELECT " + TableConstants.APP_CURRENT_VERSION
				+ ", " + TableConstants.BO_DEPT + ", " + TableConstants.APP_CURRENT_VERSION_SHORT_STRING
				+ ", " + TableConstants.APP_ADG_LEVEL
				+ ", " + TableConstants.MI_INSTANCE
				+ ", " + TableConstants.SUBMITTED_BY
				+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next()) {
				return res;
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			return null;
		}

	}
	
	
	public static String getUserID(Statement stmt, String boDept) {
		String query;
		query = "SELECT " + TableConstants.USER_ID + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER
				+ " WHERE " + TableConstants.BO_DEPT + "='" + boDept
				+ "';";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next()) {
				return res.getString(TableConstants.USER_ID);
			} else {
				return null;
			}
		} catch (SQLException e) {
			logger.error("SQLException in retrieving the UserID "
					+ ErrorUtils.getStackTrace(e));
			return null;
		}
	}
	
	public static int getIncrementedRequestNumber(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return 1;
		}
		String query = "SELECT " + TableConstants.REQUEST_NUMBER
				+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next()) {
				return res.getInt(TableConstants.REQUEST_NUMBER) + 1;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			return 1;
		}
	}
	
	public static int getIncrementedSuccessfulBuildNumber(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return 1;
		}
		String query = "SELECT " + TableConstants.SUCCESSFUL_BUILDS
				+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next()) {
				return res.getInt(TableConstants.SUCCESSFUL_BUILDS) + 1;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			return 1;
		}
	}
	
	public static boolean doProjectExist(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return false;
		}
		String query = "SELECT COUNT(*) as " + TableConstants.COUNT
				+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next() && res.getInt(TableConstants.COUNT) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in performing the execute query statement " + ErrorUtils.getStackTrace(e));
			return false;
		}
	}

	
	public static boolean doZipFileExists(String appName) {
		if(appName == null || appName == "") {
			return false;
		}
		File sourceDir = new File(TableConstants.UPLOAD_FINAL_DIRECTORY);
		File[] children = sourceDir.listFiles();
		for (File sourceChild : children) {
			if(sourceChild.getName().contains(appName)) {
				return true;
			}
		}
		return false;
	}
	
}
