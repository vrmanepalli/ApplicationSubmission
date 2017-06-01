package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.nike.util.ErrorUtils;
import com.nike.util.FieldValidator;
import com.nike.util.GetProjectDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.TriggerJenkinsBuild;
import com.nike.util.UserSessionObject;
import com.nike.util.GetProjectDetails.AppDetailsForJenkinsCall;

/**
 * Servlet implementation class TriggerBuild
 */
public class TriggerBuild extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(TriggerBuild.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TriggerBuild() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected UserSessionObject isSessionValid(String boDept, HttpServletRequest request, HttpSession session, BuildJSON parentJSON, PrintWriter writer) {

		if (boDept != null && session != null) {
			synchronized (session) {
				UserSessionObject userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null && session.getId().equals(boDept)) {
					logger.entry(userSession.getUserID());
					session.setAttribute(TableConstants.BO_DEPT, userSession);
					return userSession;
				}
			}

		}
		logger
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		callFailure("Please login!", writer, parentJSON);
		return null;
	}

	protected BuildJSON callFailure(String error, PrintWriter writer,
			BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		writer.println(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		logger.error(error);
		return parentJSON;
	}

	protected BuildJSON callFailure(String error, Exception e,
			BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}

	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}

	
	protected String getBoDept(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON) throws SQLException {
		String query = "SELECT * FROM " + TableConstants.TABLE_BUSINESS_OWNER + " WHERE " + TableConstants.EMAIL_ID + 
				"='" + userSession.getEmailID() + "';";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if (result != null && result.next()) {
				return result.getString(TableConstants.BO_DEPT);
			}
		} catch (SQLException e) {
			callFailure("Unable to process isAdmin or Nor.", e,
					parentJSON);
		} finally {
			if(result != null) {
				result.close();
			}
		}
		return null;
	}
	
	/**
	 * @param parentJSON
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON)
			throws ServletException, IOException {
		JSONObject jsonObject = MySqlDatabaseConnection.getJSONObject(request,
				parentJSON);
		HttpSession session = request.getSession(false);
		PrintWriter writer = response.getWriter();
		String boDept = jsonObject.getString(TableConstants.BO_DEPT);
		final String appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		boolean isValidAppTitle = FieldValidator.isAValidAppTitle(appTitle);
		final UserSessionObject userSession = isSessionValid(boDept, request, session, parentJSON, writer);
		if (userSession != null && isValidAppTitle) {
			Connection con = MySqlDatabaseConnection.getConnection(request
					.getServletContext());
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				parentJSON.addElement(TableConstants.BO_DEPT,
						userSession.getUniqueID());
				final String finalFileName = userSession.getUniqueID() + "___"
						+ appTitle + ".zip";
				if (userSession.isAdmin()
						&& GetProjectDetails.doZipFileExists(appTitle)) {
					final AppDetailsForJenkinsCall appDetails = GetProjectDetails
							.getUserProjectAppOldVersionAlongWithShotString(
									stmt, appTitle);
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							TriggerJenkinsBuild.addAppToPipeLine(appTitle,
									finalFileName, "true", "New App", appDetails!=null?appDetails.getVersion():"0.0.0^0.0.0",
									getServletContext(), appDetails!=null?appDetails.getBundleID():TableConstants.NEW_APP);
							logger.info("Successful trigger of Jenkins Build signing of app: "
									+ appTitle
									+ " by user: "
									+ userSession.getUserID());
						}
					};
					new Thread(runnable).start();
					writer.println(BuildJSON
							.getStaticJSONParent("<div><label class='error'>Build process triggered. Success.</label><div>"));
				} else {
					writer.println(BuildJSON
							.getStaticJSONParent("<div><label class='error'>Sorry, Build process is not triggered. Failure. May not be admin request or Zip file doesn't exist.</label><div>"));
					logger.error("Sorry, Build process is not triggered. Failure. May not be admin request or Zip file doesn't exist. App: "
							+ appTitle + " by User: " + userSession.getUserID());
				}
			} catch (SQLException e) {
				logger.entry(session.getId());
				parentJSON = callFailure(
						"Invalid App Title. Please try with a proper application title by user: "
								+ userSession.getUserID(), writer, parentJSON);
			}
		} else if (!isValidAppTitle) {
			if (session != null && userSession != null) {
				logger.entry(session.getId());
				parentJSON = callFailure(
						"Invalid App Title. Please try with a proper application title by user: "
								+ userSession.getUserID(), writer, parentJSON);
			} else {
				parentJSON = callFailure(
						"Invalid App Title and session. Please try with a proper application title.",
						writer, parentJSON);
			}

		}
		writer.flush();
		writer.close();
		logger.exit(false);
	}

	protected boolean doRoutine(BuildJSON parentJSON, Statement stmt,
			Connection con) {
		try {
			parentJSON = new BuildJSON();
			stmt = con.createStatement();
			return true;
		} catch (SQLException e) {
			parentJSON.addElement(
					TableConstants.MESSAGE,
					"<div><label class='error'>There was an error: "
							+ e.getMessage() + "</label><div>");
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
			return false;
		}
	}
}
