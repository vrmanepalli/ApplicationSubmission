package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationCheck {
	static Logger logger =  LogManager.getLogger(ApplicationCheck.class.getName());
	private static ResultSet res;
	
	public static boolean doAppTitleExist(Statement stmt, String appTitle, Logger logger) {
		if (appTitle != null && stmt != null) {
			String query = "SELECT COUNT(*) AS " + TableConstants.COUNT
					+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
					+ TableConstants.APP_TITLE + "='" + appTitle + "' AND "
					+ TableConstants.CURRENT_STATUS + "!='"
					+ TableConstants.REMOVE + "';";
			try {
				ResultSet set = stmt.executeQuery(query);
				if(set != null && set.next()) {
					return set.getInt(TableConstants.COUNT) > 0;
				}
			} catch (SQLException e) {
				logger.error("Failed to execute the query to check the application exist or not: " + appTitle);
			}
		}
		return false;
	}
	
	public static BuildJSON processRetrieveUsers(Statement stmt,
			String appTitle, Logger logger, BuildJSON buildJSON)
			throws SQLException {
		String query = "SELECT " + TableConstants.BO_DEPT + " AS "
				+ TableConstants.USER_ID + " FROM "
				+ TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "';";
		ArrayList<String> userIDs = new ArrayList<String>();
		;

		res = stmt.executeQuery(query);
		while (res.next()) {
			String boDepts = res.getString(TableConstants.USER_ID);
			try {
				if (boDepts.contains(TableConstants.COMMA_SPACE_SPERATOR)) {
					String[] boDeptsArray = boDepts
							.split(TableConstants.COMMA_SPACE_SPERATOR);
					for (String boDept : boDeptsArray) {
						userIDs.add(boDept);
					}
				} else {
					userIDs.add(boDepts);
				}
			} catch (JSONException e) {
				logger.error("Unable to connect to Database. Please try again later!"
						+ ErrorUtils.getStackTrace(e));
			}
		}
		try {
			res.close();
		} catch (Exception e) {
			logger.error("Unable to connect to Database. Please try again later!"
					+ ErrorUtils.getStackTrace(e));
		}
		res = GetUserDetails.getSpecificUsers(stmt, userIDs);
		buildJSON.createNewJSONArray();
		if (res != null) {
			while (res.next()) {
				try {
					buildJSON.createNewJChild();
					try {
						String boDeptString = res
								.getString(TableConstants.BO_DEPT);
						buildJSON.addElement(TableConstants.BO_DEPT,
								boDeptString);
						buildJSON.addElement(TableConstants.FIRST_NAME,
								res.getString(TableConstants.FIRST_NAME));
						buildJSON.addElement(TableConstants.LAST_NAME,
								res.getString(TableConstants.LAST_NAME));
						buildJSON.addElement(TableConstants.USER_ID,
								res.getString(TableConstants.USER_ID));
						buildJSON.addElement(TableConstants.PHONE_NO,
								res.getString(TableConstants.PHONE_NO));
						buildJSON.addElement(TableConstants.EMAIL_ID,
								res.getString(TableConstants.EMAIL_ID));
						buildJSON.addElement(TableConstants.DEPT,
								res.getString(TableConstants.DEPT));

						if (userIDs.size() > 0
								&& userIDs.contains(boDeptString)) {
							buildJSON.addJObjectToJArray();
						}
					} catch (SQLException e) {
						logger.error("Unable to connect to Database. Please try again later!"
								+ ErrorUtils.getStackTrace(e));
					}
				} catch (JSONException e) {
					logger.error("Unable to connect to Database. Please try again later!"
							+ ErrorUtils.getStackTrace(e));
				}
			}
			buildJSON.getParentWithArray(TableConstants.USERS);
			buildJSON.createNewJChild();
			buildJSON.addElement("Success",
					"Successfully retreived the user ids.");
		}
		return buildJSON;
	}


}
