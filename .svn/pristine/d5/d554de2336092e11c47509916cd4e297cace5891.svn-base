/*
 * AppDevelopmentRequest is a post call Servlet which is extended from PostSuperClass and Override the following methods.
 * 	->	createQuery(JSONObject jsObject): This method is used to create the insert query and returns it
 * 	->	callFailure(String error): This method is use to send the failure response whenever there is exception or failure during the post
 * 									call execution
 * 	->	callSuccess(ServletContext servletContext):	This method is a part of Post call execution which will be called when it is successful query
 * This Servlet allows the users to submit a request for sharing their interest to request service for developing their app.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.EmailValidator;
import com.nike.util.FieldValidator;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class AppDevelopmentRequest
 */
public class AppDevelopmentRequest extends SuperPostMethodsServlet {
	
	static Logger logger =  LogManager.getLogger(AppDevelopmentRequest.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppDevelopmentRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected UserSessionObject isSessionValid(String boDept,
			HttpSession session, BuildJSON parentJSON) {
		UserSessionObject userSession;
		if (session != null) {
			synchronized (session) {
				String id = session.getId();
				getLogger().entry(id);
				try {
					userSession = (UserSessionObject) session
							.getAttribute(TableConstants.BO_DEPT);
				} catch (Exception e) {
					getLogger().info("Guest user submitting the App Development request.");
					userSession = new UserSessionObject("", id, 0, "");
				}
				if (userSession != null) {
					setLoggerEntry(userSession.getUserID() + " " + id);
					return userSession;
				}
			}
		}
		getLogger()
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		parentJSON = callFailure(TableConstants.PLEASE_LOGIN_MSG, parentJSON);
		setLoggerEntry("Unknown user or Session expired user!");
		return null;
	}
	
	@Override
	protected boolean isAdmin(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON) {
		return true;
	}
	
	@Override
	protected HttpSession processSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null) {
			return request.getSession(true);
		}
		return session;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String error = "Data is not provided to submit your app request.";
		super.doPost(request, response);
		logger.exit(false);
	}

	@Override
	protected String createQuery(JSONObject jsObject) {
		String query = "INSERT INTO "
				+ TableConstants.TABLE_DEVELOPMENT_REQUEST + " ("
				+ TableConstants.FIRST_NAME + ", " + TableConstants.LAST_NAME
				+ ", " + TableConstants.EMAIL_ID + ", "
				+ TableConstants.PHONE_NO + ", " + TableConstants.DEPT + ", "
				+ TableConstants.WHEN_DATE + ", " + TableConstants.DESCRIPTION
				+ ", " + TableConstants.DATE + ") " + "VALUES ('"
				+ jsObject.getString(TableConstants.FIRST_NAME) + "', '"
				+ jsObject.getString(TableConstants.LAST_NAME) + "', '"
				+ jsObject.getString(TableConstants.EMAIL_ID) + "', '"
				+ jsObject.getString(TableConstants.PHONE_NO) + "', '"
				+ jsObject.getString(TableConstants.DEPT) + "', '"
				+ jsObject.getString(TableConstants.WHEN_DATE) + "', '"
				+ jsObject.getString(TableConstants.DESCRIPTION) + "', '"
				+ Dates.getCurrentDate() + "');";
		return query;
	}


	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	

	@Override
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject,
			UserSessionObject userSessionObject) {
		parentJSON.setValid(false);
		boolean isValidSession = super.isValidCheck(parentJSON, jsonObject, userSessionObject);
		if(jsonObject == null) {
			callFailure("The request does not have any details to save. please submit the request along with the details.", parentJSON);
		} else if(FieldValidator.isAValidName(jsonObject.getString(TableConstants.FIRST_NAME))) {
			callFailure("The request does not have any First name details to save. please submit the request along with the details.", parentJSON);
		} else if(FieldValidator.isAValidName(jsonObject.getString(TableConstants.LAST_NAME))) {
			callFailure("The request does not have any Last name details to save. please submit the request along with the details.", parentJSON);
		} else if(!EmailValidator.validate(jsonObject.getString(TableConstants.EMAIL_ID))) {
			callFailure("The request does not have any Email ID details to save. please submit the request along with the details.", parentJSON);
		} else if(!FieldValidator.validatePhoneNumber(jsonObject.getString(TableConstants.PHONE_NO))) {
			callFailure("The request does not have any phone number details to save. please submit the request along with the details.", parentJSON);
		} else if(FieldValidator.isAValidName(jsonObject.getString(TableConstants.DEPT))) {
			callFailure("The request does not have any Department details to save. please submit the request along with the details.", parentJSON);
		} else if(jsonObject.getString(TableConstants.WHEN_DATE) == null) {
			callFailure("The request does not have any date details to save. please submit the request along with the details.", parentJSON);
		} else {
			if(jsonObject.getString(TableConstants.DESCRIPTION) == null) {
				jsonObject.put(TableConstants.DESCRIPTION, "");
			}
			return isValidSession;
		}
		return false;
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		try {
			if (stmt.executeUpdate(createQuery(jsonObject)) == 1) {
				parentJSON.addElement(TableConstants.SUCCESS,
						"Successful Submission!");
				logger.info("Successful Submission!");
			} else {
				callFailure("Failed to submit your request. Please try again after some time.", parentJSON);
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				callFailure(e.getMessage(), e, parentJSON);
			}
		}
	}
}
