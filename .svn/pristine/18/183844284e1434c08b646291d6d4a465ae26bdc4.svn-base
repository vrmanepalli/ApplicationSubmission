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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.EmailNotification;
import com.nike.util.FieldValidator;
import com.nike.util.GetUserDetails;
import com.nike.util.MoveDirToADir;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class RemoveProject
 */
public class RemoveProject extends SuperGetMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(RemoveProject.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RemoveProject() {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error("Exception inside " + getClass().getName() + ": " + error);
		return parentJSON;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error, HttpServletRequest request,
			HttpSession session, ServletOutputStream out, String result,
			boolean inProgress, int inProgressIndex) throws IOException {
		String appTitle = request.getParameter(TableConstants.APP_TITLE);
		parentJSON.setLocalVariableString(appTitle);
		if (userSession != null && FieldValidator.isAValidAppTitle(appTitle) && userSession.isAdmin()) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			getLogger().info(
					"Valid session of user: " + userSession.getUserID()
							+ ", request for " + getClass().getName());
			Connection con = MySqlDatabaseConnection
					.getConnection(getServletContext());
			Statement stmt = null;
			Statement localStmt = null;
			try {
				stmt = con.createStatement();
				localStmt = con.createStatement();
				int resultInt = stmt.executeUpdate(createQuery(userSession, parentJSON, userSession.getUserID()));
				if(resultInt > 0) {
					String query = "DELETE FROM " + TableConstants.TABLE_PUSH_PROFILE_DETAILS
							+ " WHERE "
							+ TableConstants.APP_TITLE + "='" + appTitle + "';";
					resultInt = stmt.executeUpdate(query);
				}
				String projectDirectory = TableConstants.ROOT_FILE_PATH_PROJECTS
						+ appTitle;
				String imageDirectory = TableConstants.UPLOAD_IMAGE_DIRECTORY + appTitle;
				java.io.File projectFile = new java.io.File(
						projectDirectory);
				java.io.File imageFile = new java.io.File(imageDirectory);
				try {
					boolean doFilesDeleted = true;
					if (projectFile.exists()) {
						doFilesDeleted = MoveDirToADir.delete(projectFile);
					}
					if (imageFile.exists()) {
						doFilesDeleted = doFilesDeleted
								&& MoveDirToADir.delete(imageFile);
					}
					if (resultInt >= 0 && doFilesDeleted) {
						callSuccess(session, parentJSON, null, userSession, boDept, stmt, localStmt, request, out);
					} else {
						parentJSON = callFailure("Sorry, unable to remove the project, "
								+ appTitle + " now. " + resultInt + " " + doFilesDeleted, parentJSON);
					}
				} catch (IOException e) {
					parentJSON = callFailure("Sorry, unable to remove the project, "
							+ appTitle + " now. " + e.getMessage(), e, parentJSON);
				}
			} catch (SQLException e) {
				parentJSON.addElement(TableConstants.ERROR, getStackTrace(e));
				parentJSON = callFailure(e.getMessage(), e, parentJSON);
			} finally {
				try {
					writeFinalOP(parentJSON, out, boDept, result, inProgress,
							inProgressIndex);
					if(stmt != null) {
						stmt.close();
					}
					if(localStmt != null) {
						localStmt.close();
					}
				} catch (SQLException e) {
					parentJSON = callFailure(e.getMessage(), e, parentJSON);
					writeFinalOP(parentJSON, out, boDept, result, inProgress,
							inProgressIndex);
				}
			}
		} else {
			writeFinalOP(parentJSON, out, boDept, result, inProgress,
					inProgressIndex);
		}
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		parentJSON.setValid(false);
		parentJSON.addElement(TableConstants.SUCCESS,
				"Successfully removed the project, " + parentJSON.getLocalVariableString());
		String emailId = GetUserDetails.getUserEmailIDUsingAppTitle(stmt,
				parentJSON.getLocalVariableString());
		if(emailId != null) {
			try {
				EmailNotification emailNotification = new EmailNotification(emailId, parentJSON.getLocalVariableString() + "status update", parentJSON.getLocalVariableString() + " is removed from Mobile Iron.", TableConstants.APPLICATION_ICON);
				emailNotification.start();
				logger.info("Successfule removal request of the application: " + parentJSON.getLocalVariableString() + ", by user: " + userSession.getUserID());
			} catch (Exception e) {
				parentJSON = callFailure(e.getMessage(), e, parentJSON);
			}
		} else {
			logger.error(" Email id is not available or unable to pull from the database.");
		}
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJSON, String userID) {
		String query = "DELETE FROM " + TableConstants.TABLE_PROJECTS
				+ " WHERE "
				+ TableConstants.APP_TITLE + "='" + parentJSON.getLocalVariableString() + "';";
		return query;
	}
}
