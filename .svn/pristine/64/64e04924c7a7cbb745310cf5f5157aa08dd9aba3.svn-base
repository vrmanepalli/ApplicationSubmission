/*
  * GetUserDetails utility class provides the functionality to do queries against the database to 
 * 	-> Get users: getUsers -> returns ResultSet
 * 	-> Get the user projects that were by him/her: getUserProjects -> returns ResultSet
 * 	-> Find out if he/she is an Admin or not: isAdmin -> returns boolean
 * 	-> Get the user email id based on boDept: getUserEmailID -> returns String
 * 	-> Get the user email id based on AppTitle: getUserEmailIDUsingAppTitle -> returns String
 * */
package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetUserDetails {
	
	static Logger logger =  LogManager.getLogger(GetUserDetails.class);

	public static ResultSet getUsers(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		String query = "SELECT " + TableConstants.FIRST_NAME + ", "
				+ TableConstants.LAST_NAME + ", " + TableConstants.USER_ID
				+ ", " + TableConstants.BO_DEPT + ", "
				+ TableConstants.PHONE_NO + ", " + TableConstants.EMAIL_ID
				+ ", " + TableConstants.DEPT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + "  ORDER BY "
				+ TableConstants.FIRST_NAME + " ASC;";
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return null;
		}
	}
	
	public static ResultSet getSpecificUsers(Statement stmt, ArrayList<String> usersBoDepts) {
		if(stmt == null) {
			return null;
		}
		String condition = "";
		if(usersBoDepts.size() > 0) {
			int pointer = 0;
			for(String boDept:usersBoDepts) {
				if(pointer == 0) {
					condition += " WHERE " + TableConstants.BO_DEPT + "='" + boDept + "'";
				} else {
					condition += " OR " + TableConstants.BO_DEPT + "='" + boDept + "'";
				}
				pointer++;
			}
		}
		String query = "SELECT " + TableConstants.FIRST_NAME + ", "
				+ TableConstants.LAST_NAME + ", " + TableConstants.USER_ID
				+ ", " + TableConstants.BO_DEPT + ", "
				+ TableConstants.PHONE_NO + ", " + TableConstants.EMAIL_ID
				+ ", " + TableConstants.DEPT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + condition + "  ORDER BY "
				+ TableConstants.FIRST_NAME + " ASC;";
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return null;
		}
	}

	public static ResultSet getUserProjects(Statement stmt, String boDept) {
		if(stmt == null || boDept == null) {
			return null;
		}
		String query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
				+ " WHERE " + TableConstants.BO_DEPT + "='" + boDept + "' AND "
				+ TableConstants.CURRENT_STATUS + "!='" + TableConstants.REMOVE
				+ "' ORDER BY " + TableConstants.DATE + " DESC;";
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return null;
		}
	}
	
	public static ResultSet getUserProjectsWithRange(Statement stmt, String boDept , int start, int limit, boolean isAdmin) {
		if(stmt == null || boDept == null) {
			return null;
		}
		String filterString;
		if(isAdmin) {
			filterString = "";
		} else {
			filterString = "' AND " + TableConstants.BO_DEPT + " LIKE '%" + boDept + "%";
		}
		String query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
				+ " WHERE " + TableConstants.CURRENT_STATUS + "!='" + TableConstants.REMOVE + filterString
				+ "' ORDER BY " + TableConstants.DATE + " DESC LIMIT " + limit + " OFFSET " + start +";";
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return null;
		}
	}

	public static boolean isAdmin(Statement stmt, String boDept) {
		if(stmt == null || boDept == null) {
			return false;
		}
		String query = "SELECT COUNT(*) FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.BO_DEPT + "='" + boDept + "' AND "
				+ TableConstants.IS_ADMIN + "='1';";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result.next() && result.getInt(1) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean isAdminOrUser(Statement stmt, String boDept) {
		if(stmt == null || boDept == null) {
			return false;
		}
		if(isAdmin(stmt, boDept)) {
			return true;
		}
		String query = "SELECT COUNT(*) FROM "
				+ TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.BO_DEPT + "='" + boDept + "';";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result.next() && result.getInt(1) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static String getUserEmailID(Statement stmt, String boDept) {
		if(stmt == null || boDept == null) {
			return null;
		} else {
			if(boDept.contains(", ")) {
				return getUserEmailIDs(stmt, boDept);
			} else {
				String query = "SELECT "+TableConstants.EMAIL_ID+" FROM "
						+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
						+ TableConstants.BO_DEPT + "='" + boDept + "';";
				try {
					ResultSet result = stmt.executeQuery(query);
					if (result.next()) {
						return result.getString(TableConstants.EMAIL_ID);
					} else {
						return null;
					}
				} catch (SQLException e) {
					logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
					return null;
				}
			}
		}
	}
	
	public static String getUserEmailIDs(Statement stmt, String boDept) {
		if(stmt == null || boDept == null) {
			return null;
		} else {
			String[] bodepts = {};
			String emailIDS = TableConstants.ADMIN_EMAIL_IDS;
			if(boDept.contains(", ")) {
				bodepts = boDept.split(", ");
			} else {
				bodepts[0] = boDept;
			}
			for(String boDeptString: bodepts) {
				String query = "SELECT "+TableConstants.EMAIL_ID+" FROM "
						+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
						+ TableConstants.BO_DEPT + "='" + boDeptString + "';";
				try {
					ResultSet result = stmt.executeQuery(query);
					if (result.next()) {
						emailIDS += ", " + result.getString(TableConstants.EMAIL_ID);
					} else {
						return null;
					}
				} catch (SQLException e) {
					logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
					return null;
				}
			}
			return emailIDS;
		}
	}

	public static String getUserEmailIDUsingAppTitle(Statement stmt,
			String appTitle) {
		if(stmt == null || appTitle == null) {
			return null;
		}
		String emailIDs = "";
		ResultSet  res = GetProjectDetails.getUserProjectAppCurrentVersionAndBoDept(stmt, appTitle);
		if(res != null) {
			try {
				String boDepts = res.getString(TableConstants.BO_DEPT);
				if (boDepts != null) {
					if (boDepts.contains(", ")) {
						String[] arrayOfBodepts = boDepts.split(", ");
						for (int pointer = 0; pointer < arrayOfBodepts.length; pointer++) {
							if (pointer == 0) {
								emailIDs = getUserEmailID(stmt,
										arrayOfBodepts[pointer]);
							} else {
								emailIDs += ", "
										+ getUserEmailID(stmt,
												arrayOfBodepts[pointer]);
							}
						}
					} else {
						emailIDs = getUserEmailID(stmt, boDepts);
					}
					return emailIDs;
				} else {
					return null;
				}
			} catch (SQLException e) {
				logger.error("SQLException in running an Execute query statement " + ErrorUtils.getStackTrace(e));
				return null;
			}
		} else {
			return null;
		}
	}

}
