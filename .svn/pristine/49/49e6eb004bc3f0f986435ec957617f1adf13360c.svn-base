package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.EmailValidator;
import com.nike.util.FieldValidator;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UpdateOtherDetails
 */
public class UpdateOtherDetails extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UpdateOtherDetails.class.getName());

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String error = "Data is not provided to update other profile details.";
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
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON, JSONObject jsonObject) {
		boolean doDetailsValid = true;
		if(!FieldValidator.isAValidFirstName(jsonObject.getString(TableConstants.FIRST_NAME))) {
			callFailure("Invalid First Name. Please try again with valid first name.", parentJSON);
			doDetailsValid = false;
		} else if(!FieldValidator.isAValidLastName(jsonObject.getString(TableConstants.LAST_NAME))) {
			callFailure("Invalid Last Name. Please try again with valid last name.", parentJSON);
			doDetailsValid = false;
		} else if(!EmailValidator.validate(jsonObject.getString(TableConstants.EMAIL_ID))) {
			callFailure("Invalid Email ID. Please try again with valid Email ID.", parentJSON);
			doDetailsValid = false;
		} else if(!FieldValidator.validatePhoneNumber(jsonObject.getString(TableConstants.PHONE_NO))) {
			callFailure("Invalid Phone Number. Please try again with valid phone number.", parentJSON);
			doDetailsValid = false;
		} 
		if (doDetailsValid) {
			super.doRoutine(request, response, parentJSON, jsonObject);
		} else {
			try {
				callFailure("Unable to process your request. Please try again later.", parentJSON);
				ServletOutputStream out = response.getOutputStream();
				writeFinalOP(parentJSON, out);
			} catch (IOException e) {
				callFailure("Error in getting the writer from response: "
						+ e.getLocalizedMessage(), e, parentJSON);
			}
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		try {
			stmt = con.createStatement();
			String boDeptString = getBoDept(userSession, stmt, parentJSON);
			ResultSet res = stmt.executeQuery(checkQuery(jsonObject, boDeptString));
			if (res.next() && res.getInt(TableConstants.COUNT) == 0) {
				int isUpdated = stmt.executeUpdate(createQuery(jsonObject, boDeptString));
				if (isUpdated > -1) {
					parentJSON.addElement(TableConstants.SUCCESS,
							"Update of other profile details is successful!");
					logger.info("Update of other profile details of User: " + boDeptString + " is successful!");
					parentJSON.setValid(false);
				} else {
					callFailure("Sorry Unable to update other profile details of User: " + boDeptString + "! Please try again later!", parentJSON);
				}
			} else {
				callFailure("Sorry Unable to update other profile details of User: " + boDeptString + "! Email ID that was provided was already in use!", parentJSON);
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
	
	protected String createQuery(JSONObject jsonObject, String uniqueID) {
		String query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET "
				+ TableConstants.FIRST_NAME + "='"
				+ jsonObject.getString(TableConstants.FIRST_NAME) + "', "
				+ TableConstants.LAST_NAME + "='"
				+ jsonObject.getString(TableConstants.LAST_NAME) + "', "
				+ TableConstants.EMAIL_ID + "='"
				+ jsonObject.getString(TableConstants.EMAIL_ID) + "', "
				+ TableConstants.PHONE_NO + "='"
				+ jsonObject.getString(TableConstants.PHONE_NO) + "' WHERE "
				+ TableConstants.BO_DEPT + "='"
				+ uniqueID + "';";
		return query;
	}

	private String checkQuery(JSONObject jsonObject, String uniqueID) {
		return "SELECT COUNT(*) as "+TableConstants.COUNT+" FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.EMAIL_ID + "='"
				+ jsonObject.getString(TableConstants.EMAIL_ID) + "' AND "+TableConstants.BO_DEPT+"!='"+uniqueID+"';";
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		logger.error(error);
		parentJSON.setValid(false);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.addElement(TableConstants.IS_USER, "false");
		return parentJSON;
	}
	
}
