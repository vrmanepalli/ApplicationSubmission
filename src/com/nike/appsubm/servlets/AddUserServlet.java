package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.ApplicationCheck;
import com.nike.util.BuildJSON;
import com.nike.util.EmailValidator;
import com.nike.util.FieldValidator;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UpdateProjectDetails;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class AddUserServlet
 */
@WebServlet("/Private/AddUserServlet")
public class AddUserServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(AddUserServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddUserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HttpSession processSession(HttpServletRequest request) {
		return request.getSession(true);
	}

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		logger.error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.addElement(TableConstants.IS_USER, "false");
		return parentJSON;
	}

	@Override
	protected Logger getLogger() {
		return logger;
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
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject, UserSessionObject userSession) {
		String newToAddUserID = null;
		try {
			newToAddUserID = jsonObject.getString(TableConstants.USER_ID);
		} catch (Exception e) {
			callFailure(TableConstants.NEW_TO_ADD_USER_ID + " value cannot be null.", e, parentJSON);
		}
		String appTitle = null;
		try {
			appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		} catch (Exception e) {
			callFailure(TableConstants.APP_TITLE + " value cannot be null.", e, parentJSON);
		}
		String existingUserIDs = null;
		try {
			existingUserIDs = jsonObject.getString(TableConstants.EXISTING_USER_ID);
		} catch (Exception e) {
			callFailure(TableConstants.EXISTING_USER_ID + " value cannot be null.", e, parentJSON);
		}
		boolean isCheck = userSession.isAdmin()
//		&& newToAddUserID != null
		&& FieldValidator.isAValidUserName(newToAddUserID)
		&& appTitle != "" && existingUserIDs != null
		&& EmailValidator.isAValidBoDepts(existingUserIDs);
		return super.isValidCheck(parentJSON, jsonObject, userSession) && isCheck;
	}
	
	

	@Override
	protected void processFailure(String error, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		if (!userSession.isAdmin()) {
			callFailure("You are not authorized to edit the ownership of the project: "
					+ jsonObject.getString(TableConstants.APP_TITLE), parentJSON);
		} else if (jsonObject.getString(TableConstants.USER_ID) == null) {
			callFailure("UserID cannot be null! Please enter User ID and try again. ", parentJSON);
		} else if (jsonObject.getString(TableConstants.APP_TITLE) == null) {
			callFailure(jsonObject.getString(TableConstants.USER_ID)
					+ "'s AppTitle cannot be null! Please enter Password and try again. ", parentJSON);
		} else {
			callFailure("Unable to process your request.", parentJSON);
		}
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out) {
		try {
			out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			callFailure(TableConstants.WRITE_STREAM_ERROR_MSG, e, parentJSON);
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {

		try {
			if (ApplicationCheck.doAppTitleExist(stmt, jsonObject.getString(TableConstants.APP_TITLE), logger)) {
				logger.info("Add User: " + jsonObject.getString(TableConstants.USER_ID)
						+ " attempt to project: " + jsonObject.getString(TableConstants.APP_TITLE));
				boolean doUpdateSuccess = UpdateProjectDetails
						.updateProjectBusinessOwnerDetails(stmt, jsonObject.getString(TableConstants.APP_TITLE),
								jsonObject.getString(TableConstants.EXISTING_USER_ID));
				if (doUpdateSuccess) {
					doUpdateSuccess = UpdateRequestCount
							.updateBusinessOwnersOfRequestCount(stmt, jsonObject.getString(TableConstants.APP_TITLE),
									jsonObject.getString(TableConstants.EXISTING_USER_ID), userSession.isAdmin());
				} else {
					callFailure("Add User: " + jsonObject.getString(TableConstants.USER_ID)
							+ " attempt to project: " + jsonObject.getString(TableConstants.APP_TITLE) + " failed.", parentJSON);
				}
				if (doUpdateSuccess) {
					parentJSON.setValid(true);
					parentJSON = ApplicationCheck.processRetrieveUsers(stmt, jsonObject.getString(TableConstants.APP_TITLE), logger, parentJSON);
					parentJSON.addElement(TableConstants.BO_DEPT, jsonObject.getString(TableConstants.BO_DEPT));
				} else { 
					callFailure("Add User: " + jsonObject.getString(TableConstants.USER_ID)
							+ " attempt to project: " + jsonObject.getString(TableConstants.APP_TITLE) + " failed.", parentJSON);
				}
			} else {
				callFailure("Add User: " + jsonObject.getString(TableConstants.USER_ID)
						+ " attempt to project: " + jsonObject.getString(TableConstants.APP_TITLE)
						+ " failed because application doesn't exist.", parentJSON);
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				callFailure(e.getMessage(), e, parentJSON);
			}
		}
	}

}
