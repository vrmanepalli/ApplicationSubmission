/*
 * Register Servlet allows guests to register with this website.
 * It doesn't allow duplicate userIds or emailIds.
 * Extends PostSuperClass which only allows Post call.
 * ->callSuccess(): returns none. doPost method calls this method when the intended data processing is successful. This is abstract method
 * ->writeFinalOP(): returns none.This is the method which prints JSON object or JSON Array based on the repsonse and Servlet  that is extending this class.
 * ->reset(HttpServletRequest request): returns none. Resets the variables to default values for every call.
 * ->callFailure(String error): returns none, abstract method. Intended to create a failure JSON response.
 * ->createQuery(): returns String, abstract method. It constructs the query string to insert the record into the BusinessOwners table.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.EmailNotification;
import com.nike.util.EmailValidator;
import com.nike.util.FieldValidator;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserCheckUtil;

/**
 * Servlet implementation class Register
 */
public class Register extends PostSuperClass {
	static Logger logger =  LogManager.getLogger(Register.class.getName());
	private static final long serialVersionUID = 1L;
	private String boDept;
	private String firstName;
	private String lastName;
	private String emailID;
	private String phoneNo;
	private String dept;
	private String userID;
	private String pwd;
	private String question;
	private String answer;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		error = "Data is not provided to register.";
		super.doPost(request, response);
		logger.exit(false);
	}

	@Override
	protected void processSession(HttpServletRequest request) {
		session = request.getSession(true);
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
	protected void reset() {
		super.reset();
		boDept = null;
		try {
			firstName = jsonObject.getString(TableConstants.FIRST_NAME);
			lastName = jsonObject.getString(TableConstants.LAST_NAME);
			emailID = jsonObject.getString(TableConstants.EMAIL_ID);
			phoneNo = jsonObject.getString(TableConstants.PHONE_NO);
			dept = jsonObject.getString(TableConstants.DEPT);
			userID = jsonObject.getString(TableConstants.USER_ID);
			pwd = jsonObject.getString(TableConstants.PWD);
			question = jsonObject.getString(TableConstants.QUESTION);
			answer = jsonObject.getString(TableConstants.ANSWER);
		} catch (Exception e) {
			callFailure("Cannot process your request null values.", e);
		}
	}

	@Override
	protected void callFailure(String error) {
		logger.error(className + " Json is null." + error);
		parentJSON.addElement(TableConstants.ERROR, error);
	}

	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response) {
		boolean doDetailsValid = true;
		if (!FieldValidator.isAValidFirstName(firstName)) {
			callFailure("Invalid first name. Please fill in the first name");
			doDetailsValid = false;
		} else if (!FieldValidator.isAValidLastName(lastName)) {
			callFailure("Invalid last name. Please fill in the last name");
			doDetailsValid = false;
		} else if (!EmailValidator.validate(emailID)) {
			callFailure("Invalid email id. Please fill in the email id");
			doDetailsValid = false;
		} else if (!FieldValidator.validatePhoneNumber(phoneNo)) {
			callFailure("Invalid phone number. Please fill in the phone number");
			doDetailsValid = false;
		} else if (!FieldValidator.isAValidFirstName(dept)) {
			callFailure("Invalid department. Please fill in the department");
			doDetailsValid = false;
		}
		try {
			out = response.getWriter();
			if (doDetailsValid) {
				con = MySqlDatabaseConnection.getConnection(getServletContext());
				callSuccess(request);
			} else {
				callFailure(error);
			}
			writeFinalOP();
		} catch (IOException e1) {
			callFailure("Error in getting the writer from response: "
					+ e1.getLocalizedMessage(), e1);
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request) {
		try {
			stmt = con.createStatement();
			parentJSON = UserCheckUtil.addUserToBusinessOwnerTable(stmt,
					dept, userID, firstName, lastName, pwd, emailID, phoneNo,
					question, answer, parentJSON, logger,
					createQuery(jsonObject));
			if(parentJSON == null) {
				parentJSON = new BuildJSON();
				callFailure("Failed to Register!");
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e);
		} catch (Exception e) {
			callFailure(e.getMessage(), e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				callFailure(e.getMessage(), e);
			}
		}
	}

	@Override
	protected String createQuery(JSONObject jsObject) {
		boDept = dept.replace(" ", "_").hashCode() + "_" + userID + "_"
				+ dept.replace(" ", "_").hashCode();
		String query = "INSERT INTO " + TableConstants.TABLE_BUSINESS_OWNER
				+ " (" + TableConstants.FIRST_NAME + ", "
				+ TableConstants.LAST_NAME + ", " + TableConstants.USER_ID
				+ ", " + TableConstants.PWD + ", " + TableConstants.EMAIL_ID
				+ ", " + TableConstants.PHONE_NO + ", " + TableConstants.DEPT
				+ ", " + TableConstants.BO_DEPT + ", "
				+ TableConstants.IS_ADMIN + ", " + TableConstants.QUESTION
				+ ", " + TableConstants.ANSWER + ", " + TableConstants.DATE
				+ ") " + "VALUES ('"
				+ firstName + "', '"
				+ lastName + "', '"
				+ userID + "', "
				+ "AES_ENCRYPT('" + pwd
				+ "', '" + pwd + "'), '"
				+ emailID + "', '"
				+ phoneNo + "', '"
				+ dept + "', '" + boDept
				+ "', " + "0, '" + question
				+ "', '" + answer + "', '"
				+ Dates.getCurrentDate() + "');";
		return query;
	}
}
