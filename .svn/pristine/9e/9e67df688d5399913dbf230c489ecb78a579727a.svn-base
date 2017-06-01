package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
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
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UpdatePassword
 */
public class UpdatePassword extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UpdatePassword.class.getName());

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String error = "Data is not provided to Login.";
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
		String pwd = jsonObject.getString(TableConstants.PWD);
		if(pwd == null && pwd.length() < 8) {
			try {
				ServletOutputStream out = response.getOutputStream();
				callFailure("Invalid Password format. Please try again with valid password", parentJSON);
				writeFinalOP(parentJSON, out);
			} catch (IOException e) {
				callFailure("Error in getting the writer from response: "
						+ e.getLocalizedMessage(), e, parentJSON);
			}
		} else {
			super.doRoutine(request, response, parentJSON, jsonObject);
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		try {
			stmt = con.createStatement();
			int result = stmt.executeUpdate(createQuery(jsonObject, getBoDept(userSession, stmt, parentJSON)));
			if (result > -1) {
				parentJSON.addElement(TableConstants.SUCCESS,
						"Change of your password is successful!");
				logger.info("Change of your password is successful for user: " + userSession.getUserID() + "!");
			} else {
				callFailure("Sorry Unable to change password for user: " + userSession.getUserID() + "! Please try again later!", parentJSON);
			}
		} catch (SQLException e) {
			callFailure("Failed to complete the transaction.", e, parentJSON);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				callFailure("Failed to complete the transaction.", e, parentJSON);
			}
		}
	}

	protected String createQuery(JSONObject jsonObject, String boDeptString) {
		return "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET "+TableConstants.PWD+"=AES_ENCRYPT('"
				+ jsonObject.getString(TableConstants.PWD) + "', '" + jsonObject.getString(TableConstants.PWD) + "') WHERE "
				+ TableConstants.BO_DEPT + "='"
				+ boDeptString + "';";
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		logger.error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.addElement(TableConstants.IS_USER, "false");
		parentJSON.setValid(false);
		return parentJSON;
	}
	
}
