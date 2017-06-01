package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.ErrorUtils;
import com.nike.util.FieldValidator;
import com.nike.util.NotifyUsersHelperThread;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class NotifyUsersServlet
 */
public class NotifyUsersServlet extends SuperPostMethodsServlet {
	static Logger logger =  LogManager.getLogger(NotifyUsersServlet.class.getName());
	private static final long serialVersionUID = 1L;
	final String from = "mobileapps@nike.com";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NotifyUsersServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		logger.exit(false);
	}

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}
	
	@Override
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject,
			UserSessionObject userSessionObject) {
		String appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		return super.isValidCheck(parentJSON, jsonObject, userSessionObject) && FieldValidator.isAValidAppTitle(appTitle);
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		try {
			Statement localStmt = con.createStatement();
			String appTitle;
			String date;
			try {
				appTitle = jsonObject.getString(TableConstants.APP_TITLE);
				date = getGenericProfileExpOrCreatDate(localStmt, appTitle, parentJSON);
			} catch (Exception e) {
				callFailure("Unable to process,  because of missing appTitle.", e, parentJSON);
				return;
			}
			// This will process the request only if the user is Admin and there
			// is a date which is less <= 3 months
			if (userSession.isAdmin() && date != null) {
				ResultSet result = stmt.executeQuery(createQuery(jsonObject));
				if (result != null) {
					NotifyUsersHelperThread helper = new NotifyUsersHelperThread(
							result, date);
					if (!helper.isActive()) {
						helper.start();
						parentJSON
								.addElement(
										TableConstants.SUCCESS,
										"Your request is submitted and it is in process. Please do not submit multiple requests.");
						logger.info("Successful notification to users by user: " + userSession.getUserID());
					} else {
						parentJSON
								.addElement(TableConstants.SUCCESS,
										"Your request was already in process. Please do not submit multiple requests.");
						logger.info("Multiple notification to users by user: " + userSession.getUserID());
					}
				}
			}
		} catch (Exception e) {
			callFailure(e.getLocalizedMessage(), e, parentJSON);
		}
	}
	
	@Override
	protected String createQuery(JSONObject jsonObject) {
		String query;
		String appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		if (appTitle.equals(TableConstants.GENERIC_NIKE)) {
			query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
					+ " WHERE " + TableConstants.IS_PUSH + "=0" + " ORDER BY "
					+ TableConstants.DATE + " DESC;";
		} else {
			query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
					+ " WHERE " + TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
					+ TableConstants.DATE + " DESC;";
		}
		return query;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	// This method sends either Generic Profile Creation date or Generic Profile
	// Expiration Date based on whose difference with respect to current date is
	// <=3 months
	private String getGenericProfileExpOrCreatDate(Statement localStmt, String appTitle, BuildJSON parentJSON) {
		String sql = "SELECT * FROM "
				+ TableConstants.TABLE_PUSH_PROFILE_DETAILS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "';";
		ResultSet result = null;
		try {
			result = localStmt.executeQuery(sql);
			if (result != null && result.next()) {
				String profileExpirationdate = result
						.getString(TableConstants.EXPIRATION_DATE);
				String profileCreationDate = result
						.getString(TableConstants.CREATION_DATE);
				if (Dates.getNumberOfMonthDiff(profileExpirationdate, appTitle) <= 3) {
					return profileExpirationdate;
				} else if (Dates
						.getNumberOfMonthDiffWithPreviousDate(profileCreationDate) <= 3) {
					return profileCreationDate;
				} else {
					parentJSON
							.addElement(
									TableConstants.SUCCESS,
									"Your request cannot be processed now. The expiration date of the Generic Profile is not within 3 months.");
				}

			}
		} catch (SQLException e) {
			callFailure("Uanble to get generic profile.", e, parentJSON);
		} finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					getLogger().error("Failed to close the result set of Generic Nike Profile search." + ErrorUtils.getStackTrace(e));
				}
			}
			if(localStmt != null) {
				try {
					localStmt.close();
				} catch (SQLException e) {
					getLogger().error("Failed to close the local statment of Generic Nike Profile search." + ErrorUtils.getStackTrace(e));
				}
			}
		}
		return null;
	}
	
	
	

}
