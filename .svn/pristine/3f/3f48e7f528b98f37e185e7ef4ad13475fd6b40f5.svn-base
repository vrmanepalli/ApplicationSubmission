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
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.EmailNotification;
import com.nike.util.EmailValidator;
import com.nike.util.FieldValidator;
import com.nike.util.GetProjectDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class SubmitProjectToMI
 */
public class SubmitProjectToMI extends SuperGetMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(SubmitProjectToMI.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitProjectToMI() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = processSession(request);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		ServletOutputStream out = response.getOutputStream();
		BuildJSON parentJSON = new BuildJSON();
		String error = "Failure!";
		String boDept = null;
		boolean inProgress = false;
		int index = -1;
		int inProgressIndex = -1;
		String result = "";
		JSONObject jsonObject = MySqlDatabaseConnection.getJSONObjectFromGetCall(request, parentJSON);
		parentJSON.setValid(false);
		try {
			boDept = jsonObject.getString(TableConstants.BO_DEPT);
		} catch (Exception e) {
			getLogger().error(getStackTrace(e));
		}

		UserSessionObject userSession = isSessionValid(boDept, session);

		processRequest(userSession, boDept, parentJSON, error, request,
				session, out, result, inProgress, inProgressIndex, jsonObject);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error, HttpServletRequest request,
			HttpSession session, ServletOutputStream out, String result,
			boolean inProgress, int inProgressIndex, JSONObject jsonObject) {
		String currentVersionNumber = "1.0.0";
		String currentVersionShortStringNumber = "1.0.0";
		String mobileIronInstance = "Corporate";
		String appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		parentJSON.setLocalVariableString("__" + appTitle + "__");
		parentJSON.addElement(TableConstants.BO_DEPT, boDept);
		boolean isAValidAppTitle = FieldValidator.isAValidAppTitle(appTitle);
		if (isAValidAppTitle && userSession != null) {
			Statement stmt = null;
			Statement localStmt = null;
			try {
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				Connection con = MySqlDatabaseConnection.getConnection(getServletContext());
				stmt = con .createStatement();
				localStmt = con.createStatement();
				boolean isAdmin = userSession.isAdmin();
				if (isAdmin) {
					UpdateRequestCount.updateRecordOfRequestCount(localStmt,
							appTitle, userSession.getUniqueID(), userSession.isAdmin());
					ResultSet res = GetProjectDetails
							.getUserProjectAppCurrentVersionAndBoDept(stmt,
									appTitle);
					if (res != null) {
						currentVersionNumber = res
								.getString(TableConstants.APP_CURRENT_VERSION);
						currentVersionShortStringNumber = res
								.getString(TableConstants.APP_CURRENT_VERSION_SHORT_STRING);
						mobileIronInstance = res
								.getString(TableConstants.MI_INSTANCE);
						String projectBoDept = res.getString(TableConstants.BO_DEPT);
						String appADGLabel = res.getString(TableConstants.APP_ADG_LEVEL);
						parentJSON.setLocalVariableString(parentJSON.getLocalVariableString() + " Version: " + currentVersionShortStringNumber + " is submitted to AirWatch only, with label: " + appADGLabel + ", by user " + GetProjectDetails.getUserID(stmt, res.getString(TableConstants.SUBMITTED_BY)));
						int resultInt = stmt.executeUpdate(createQuery(currentVersionNumber, currentVersionShortStringNumber, appTitle));
						if (resultInt > 0) {
							callSuccess(session, parentJSON, res, userSession, projectBoDept, stmt, localStmt, request, out);;
						} else {
							callFailure("Sorry, unable to submit/update the project, "
									+ appTitle + " now, by user: " + userSession.getUserID(), parentJSON);
						}
					} else {
						callFailure("Sorry, failed to submit/update the project, "
								+ appTitle + " now by user: " + userSession.getUserID(), parentJSON);
					}
				} else {
					callFailure("InCorrect AppTitle or You are not authorized to submit the project by user: " + userSession.getUserID(), parentJSON);
				}
			} catch (SQLException e) {
				callFailure(TableConstants.DATABASE_ERROR_MSG, e, parentJSON);
			} finally {
				try {
					writeFinalOP(parentJSON, out, boDept, result, inProgress, inProgressIndex);
					if (stmt != null) {
						stmt.close();
					}
					if (localStmt != null) {
						localStmt.close();
					}
				} catch (SQLException e) {
					callFailure(TableConstants.DATABASE_ERROR_MSG, e, parentJSON);
					writeFinalOP(parentJSON, out, boDept, result, inProgress, inProgressIndex);
				}
			}
			logger.info(parentJSON.toString());
		} else if (!isAValidAppTitle) {
			callFailure("InCorrect AppTitle: " + appTitle + " by user: " + userSession.getUniqueID(), parentJSON);
		}
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		String emailID = GetUserDetails.getUserEmailID(stmt, boDept);
		String appTitle = parentJSON.getLocalVariableString().split("__")[1];
		parentJSON.setLocalVariableString(parentJSON.getLocalVariableString().replaceAll("__", "**"));
		if(EmailValidator.isAValidApprovedEmailIDs(emailID)) {
			try {
				EmailNotification emailNotification = new EmailNotification(emailID, appTitle + " status update", parentJSON.getLocalVariableString(), TableConstants.APPLICATION_ICON);
				emailNotification.start();
				logger.info("Successful submission of the application:  " + appTitle
						+ " by user: " + userSession.getUserID());
			} catch (Exception e) {
				callFailure("Failed to submit the application:  " + appTitle
						+ " by user: " + userSession.getUserID(), e, parentJSON);
			}
		} else {
			callFailure("Failed to submit the application to Mobile Iron because of invalid email ID: "
					+ emailID + " by user:" + userSession.getUserID(), parentJSON);
		}
		parentJSON.addElement(TableConstants.SUCCESS,
				parentJSON.getLocalVariableString());
	}

	protected String createQuery(String currentVersionNumber, String currentVersionShortStringNumber, String appTitle) {
		String query = "UPDATE " + TableConstants.TABLE_PROJECTS
				+ " SET " + TableConstants.APP_OLD_VERSION + "='" + currentVersionNumber + "', "
				+ TableConstants.APP_OLD_VERSION_SHORT_STRING + "='" + currentVersionShortStringNumber + "', "
				+ TableConstants.CURRENT_STATUS + "='" + TableConstants.COMPLETE 
				+ "' WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + "';";
		return query;
	}


	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}

}
