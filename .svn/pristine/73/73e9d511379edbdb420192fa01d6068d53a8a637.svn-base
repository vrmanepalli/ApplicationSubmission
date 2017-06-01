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
import com.nike.util.NotifyAllUsersHelperThread;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class EmailToBusinessOwnersServlet
 */
public class EmailToBusinessOwnersServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(EmailToBusinessOwnersServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EmailToBusinessOwnersServlet() {
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
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected String createQuery(JSONObject jsonObject) {
		return "SELECT * FROM " + TableConstants.TABLE_PROJECTS
				+ " ORDER BY " + TableConstants.DATE + " DESC;";
	}
	
	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON, JSONObject jsonObject) {
		super.doRoutine(request, response, parentJSON, jsonObject);
		logger.exit(false);
	}
	
	

	@Override
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject,
			UserSessionObject userSessionObject) {
		try {
			String body = jsonObject.getString(TableConstants.BODY);
			String appendString = jsonObject.getString("DoAppend");
			String reason = jsonObject.getString("Reason");
			String subject = jsonObject.getString(TableConstants.SUBJECT);
			if(body != null && appendString != null && reason != null && subject != null) {
				return true && super.isValidCheck(parentJSON, jsonObject, userSessionObject);
			} 
		} catch (Exception e) {
			callFailure("Failed to retreive the strings from request. ", e, parentJSON);
		}
		return false;
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		try {
			String genericProfileDate = "2012-07-15 22:32:45";
			if (userSession.isAdmin()) {
				boolean doAppend = false;
				String appendString = jsonObject.getString("DoAppend");
				if (appendString.equalsIgnoreCase("YES")) {
					doAppend = true;
				} else {
					doAppend = false;
				}
				String query = createQuery(jsonObject);
				String  reason = jsonObject.getString("Reason");
				if (reason.equals(TableConstants.GENERAL)) {
					query = "SELECT DISTINCT(" + TableConstants.TSC_EMAIL
							+ ") AS " + TableConstants.TSC_EMAIL + " FROM "
							+ TableConstants.TABLE_PROJECTS + " WHERE "
							+ TableConstants.IS_PUSH + "=0 ORDER BY "
							+ TableConstants.TSC_EMAIL + " ASC;";
					genericProfileDate = TableConstants.GENERAL;
				} else if(reason.equals("PROFILE_RENEWAL")) {
					query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
							+ " WHERE " + TableConstants.IS_PUSH + "=0 ORDER BY " + TableConstants.DATE + " DESC;";
					Statement localStmt = con.createStatement();
					genericProfileDate = PushProfileDetails.getCreationDate(localStmt , TableConstants.GENERIC_NIKE);
				} else if(reason.equals(TableConstants.SPECIFIC_USER)) {
					String emailID = jsonObject.getString(TableConstants.EMAIL_ID);
					query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
							+ " WHERE " + TableConstants.TSC_EMAIL + "='" + emailID + "' ORDER BY " + TableConstants.DATE + " DESC;";
					genericProfileDate = TableConstants.SPECIFIC_USER;
				}
				ResultSet result = stmt.executeQuery(query);
				if (result != null) {
					NotifyAllUsersHelperThread helper = new NotifyAllUsersHelperThread(
							result, jsonObject.getString(TableConstants.SUBJECT), jsonObject.getString(TableConstants.BODY), doAppend, genericProfileDate);
					if (!helper.isActive()) {
						helper.start();
						parentJSON
								.addElement(
										TableConstants.SUCCESS,
										"Your request is submitted and it is in process. Please do not submit multiple requests.");
					} else {
						parentJSON
								.addElement(TableConstants.SUCCESS,
										"Your request was already in process. Please do not submit multiple requests.");
					}
					logger.info("Successful submission of email by user: " + userSession.getUserID());
				}
			} else {
				callFailure("You does not have the permission to send email to all Business Owners", parentJSON);
			}
		} catch (SQLException e) {
			callFailure(e.getLocalizedMessage(), e, parentJSON);
		}

	}
}
