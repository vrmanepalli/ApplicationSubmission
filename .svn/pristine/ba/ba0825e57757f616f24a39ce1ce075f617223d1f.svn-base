/*
 * GetProjectStatus servlet sends JSON response (which includes Project status, whether in the process of signing or not) of a particular Project.
 * It supports get call
 *  ->callFailure(String error): returns none. Creates a JSON response with reason for failure.
 *  ->callSuccess():	This  method is called to perform the query and create JSON response out of it.
 *  ->createQuery():	returns a String i.e. a query of SELECT against Project table.
 *  ->writeFinalOP():	returns none. Writes the JSON response that is created by the servlet to the output stream.
 *  ->reset(HttpServletRequest request): returns none. Resets all the variables to default values.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetProjectStatus
 */
public class GetProjectStatus extends SuperGetMethodsServlet {
	static Logger logger = LogManager.getLogger(GetProjectStatus.class
			.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetProjectStatus() {
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
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected ResultSet getResultSet(HttpServletRequest request,
			UserSessionObject userSession, Statement stmt, BuildJSON parentJSON)
			throws SQLException {
		try {
			String appTitle = request.getParameter(TableConstants.APP_TITLE);
			parentJSON.setLocalVariableString(appTitle);
			return stmt.executeQuery(createQuery(userSession, parentJSON,
					appTitle));
		} catch (Exception e) {
			parentJSON = callFailure("Unable to find apptitle or check on Database", e,
					parentJSON);
		}
		return null;
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		getLogger().error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		return parentJSON;
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		try {
			if (res.next() && res.getInt(TableConstants.COUNT) > 0) {
				try {
					parentJSON.addElement(TableConstants.CURRENT_STATUS,
							res.getString(TableConstants.CURRENT_STATUS));
					String result = res.getString(TableConstants.RESULT);
					boolean inProgress;
					if (res.getString(TableConstants.CURRENT_STATUS)
							.equalsIgnoreCase(TableConstants.IN_PROGRESS)) {
						inProgress = true;
					} else {
						inProgress = false;
					}
					parentJSON.addElement("InProgress", inProgress);
					parentJSON.addElement(TableConstants.BO_DEPT, boDept);
					try {
						parentJSON.addElement(TableConstants.DATE,
								res.getString(TableConstants.DATE));
						parentJSON.addElement(TableConstants.IS_SIGNING_REQ,
								res.getString(TableConstants.IS_SIGNING_REQ));
						parentJSON.addElement(TableConstants.MI_INSTANCE,
								res.getString(TableConstants.MI_INSTANCE));
						parentJSON
								.addElement(
										TableConstants.APP_CURRENT_VERSION,
										res.getString(TableConstants.APP_CURRENT_VERSION));
						parentJSON
								.addElement(
										"Index",
										inProgress ? res
												.getInt(TableConstants.ID) : -1);
					} catch (SQLException e) {
						parentJSON = callFailure(
								"No such App does exist with title "
										+ parentJSON.getLocalVariableString() + " "
										+ e.getMessage(), e, parentJSON);
					}
					parentJSON.addElement(TableConstants.RESULT, result);
					logger.info("Successful retrieval of project status of "
							+ parentJSON.getLocalVariableString() + " by user: "
							+ userSession.getUserID());
				} catch (SQLException e) {
					parentJSON = callFailure(
							"1. No such App does exist with title "
									+ parentJSON.getLocalVariableString() + " Error: "
									+ e.getMessage(), e, parentJSON);
				}
			} else {
				parentJSON = callFailure("2. No such App does exist with title "
						+ parentJSON.getLocalVariableString(), parentJSON);
			}
		} catch (SQLException e) {
			parentJSON = callFailure(
					"3. No such App does exist with title "
							+ parentJSON.getLocalVariableString() + " " + e.getMessage(),
					e, parentJSON);
		}
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJSON, String appTitle) {
		String query;
		query = "SELECT COUNT(*) as count, " + TableConstants.RESULT + ", "
				+ TableConstants.DATE + ", "
				+ TableConstants.APP_CURRENT_VERSION + ", "
				+ TableConstants.CURRENT_STATUS + ", " + TableConstants.ID + ", " + TableConstants.IS_SIGNING_REQ + ", " + TableConstants.MI_INSTANCE 
				+ " FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "' ORDER BY "
				+ TableConstants.DATE + " DESC;";
		return query;
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE)
					.toString().getBytes());
		} catch (IOException e) {
			parentJSON = callFailure(
					"Failed to write response to user "
							+ parentJSON.getLocalVariableString() + " " + e.getMessage(),
					e, parentJSON);
		}
	}

}
