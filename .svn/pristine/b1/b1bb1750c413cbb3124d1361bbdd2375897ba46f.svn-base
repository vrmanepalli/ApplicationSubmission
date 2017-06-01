package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.GetUserDetails;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetUsersOfAProjectServlet
 */
@WebServlet("/Private/GetUsersOfAProjectServlet")
public class GetUsersOfAProjectServlet extends SuperGetMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(GetUsersOfAProjectServlet.class
			.getName());
	static String error = "Failed to process: ";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUsersOfAProjectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		logger.exit(false);
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.createNewJChild();
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE)
					.toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			getLogger().error(
					"Failed to write back to user. "
							+ ErrorUtils.getStackTrace(e));
		}
	}

	@Override
	protected boolean isValidCheck(BuildJSON parentJSON,
			HttpServletRequest request, UserSessionObject userSessionObject) {
		parentJSON.setLocalVariableString(request
				.getParameter(TableConstants.APP_TITLE));
		return userSessionObject.isAdmin();
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		try {
			parentJSON.setValid(true);
			ArrayList<String> userIDs = new ArrayList<String>();
			while (res.next()) {
				try {
					parentJSON = addElementsToJSON(res, parentJSON, userIDs);
				} catch (JSONException e) {
					parentJSON = callFailure(
							"Unable to connect to Database. Please try again later!"
									+ e.getMessage(), parentJSON);
				}
			}
			try {
				res.close();
			} catch (Exception e) {
				parentJSON = callFailure(error, e, parentJSON);
			}
			parentJSON = addUsers(localStmt, res, userIDs, parentJSON, boDept);
			try {
				res.close();
			} catch (Exception e) {
				parentJSON = callFailure(error, e, parentJSON);
			}
		} catch (SQLException e) {
			parentJSON = callFailure(error, e, parentJSON);
		}
	}

	private BuildJSON addUsers(Statement stmt, ResultSet res,
			ArrayList<String> userIDs, BuildJSON parentJSON, String boDept) {
		try {
			res = GetUserDetails.getSpecificUsers(stmt, userIDs);
			parentJSON.createNewJSONArray();
			if (res != null) {
				while (res.next()) {
					try {
						parentJSON = addUsersToJSON(parentJSON, res, userIDs);
					} catch (JSONException e) {
						parentJSON = callFailure(
								"Unable to connect to Database. Please try again later!"
										+ e.getMessage(), parentJSON);
					}
				}
				parentJSON.getParentWithArray(TableConstants.USERS);
				parentJSON.createNewJChild();
				parentJSON.addElement("Success",
						"Successfully retreived the user ids.");
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			}
		} catch (SQLException e) {
			parentJSON = callFailure(error, e, parentJSON);
		}
		return parentJSON;
	}

	private BuildJSON addUsersToJSON(BuildJSON parentJSON, ResultSet res,
			ArrayList<String> userIDs) {
		parentJSON.createNewJChild();
		try {
			String boDeptString = res.getString(TableConstants.BO_DEPT);
			parentJSON.addElement(TableConstants.BO_DEPT, boDeptString);
			parentJSON.addElement(TableConstants.FIRST_NAME,
					res.getString(TableConstants.FIRST_NAME));
			parentJSON.addElement(TableConstants.LAST_NAME,
					res.getString(TableConstants.LAST_NAME));
			parentJSON.addElement(TableConstants.USER_ID,
					res.getString(TableConstants.USER_ID));
			parentJSON.addElement(TableConstants.PHONE_NO,
					res.getString(TableConstants.PHONE_NO));
			parentJSON.addElement(TableConstants.EMAIL_ID,
					res.getString(TableConstants.EMAIL_ID));
			parentJSON.addElement(TableConstants.DEPT,
					res.getString(TableConstants.DEPT));

			if (userIDs.size() > 0 && userIDs.contains(boDeptString)) {
				parentJSON.addJObjectToJArray();
			}
		} catch (SQLException e) {
			parentJSON = callFailure(error, e, parentJSON);
		}
		return parentJSON;
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJSON, String userID) {
		String query = "SELECT " + TableConstants.BO_DEPT + " AS "
				+ TableConstants.USER_ID + " FROM "
				+ TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='"
				+ parentJSON.getLocalVariableString() + "';";
		return query;
	}

	protected BuildJSON addElementsToJSON(ResultSet res, BuildJSON parentJSON,
			ArrayList<String> userIDs) {
		try {
			processEmailIDs(res.getString(TableConstants.USER_ID), userIDs);
		} catch (SQLException e) {
			parentJSON = callFailure(
					"Unable to retreive the userID from Database.", e,
					parentJSON);
		}
		return parentJSON;
	}

	protected void processEmailIDs(String boDepts, ArrayList<String> userIDs) {
		if (boDepts.contains(TableConstants.COMMA_SPACE_SPERATOR)) {
			String[] boDeptsArray = boDepts
					.split(TableConstants.COMMA_SPACE_SPERATOR);
			for (String boDept : boDeptsArray) {
				userIDs.add(boDept);
			}
		} else {
			userIDs.add(boDepts);
		}
	}
}
