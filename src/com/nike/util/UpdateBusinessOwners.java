/*
 * UpdateBusinessOwners just has one method
 * 	->	updateLastAppReqTitle(String lastAppReqTitle, String boDept, Statement stmt): Which updates the value of the Last App request title to 
 * 		keep track of the last request sumbitted by that user.
 */


package com.nike.util;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateBusinessOwners {

	private static String error;
	static Logger logger =  LogManager.getLogger(UpdateBusinessOwners.class.getName());
	
	public static boolean updateLastAppReqTitle(String lastAppReqTitle, String emailIDString, Statement stmt) {
		String query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET " 
				+ TableConstants.LAST_APP_REQ_TITLE + "='"+ lastAppReqTitle +
				"' WHERE " 
				+ TableConstants.EMAIL_ID + "='" + emailIDString + "';";
		int res;
		try {
			res = stmt.executeUpdate(query);
			if (res == 1) {
				return true;
			} else {
				error = null;
				return false;
			}
		} catch (SQLException e) {
			error = e.getMessage();
			logger.error("SQLException in performing the select query " + error
					+ " " + ErrorUtils.getStackTrace(e));
			return false;
		}
		
	}
}
