package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.security.action.GetLongAction;
import net.sf.json.JSONObject;

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
 * Servlet implementation class TriggerAPNSBuildServlet
 */
@WebServlet("/Private/TriggerAPNSBuildServlet")
public class TriggerAPNSBuildServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(TriggerAPNSBuildServlet.class.getName());  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TriggerAPNSBuildServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Initialize local variables.
		BuildJSON parentJSON = new BuildJSON();
		JSONObject jsonObject = MySqlDatabaseConnection.getJSONObject(request,
				parentJSON );
		HttpSession session = request.getSession(false);
		PrintWriter writer = response.getWriter();
		String boDept = null;
		UserSessionObject userSession = null;
		try {
			boDept = jsonObject.getString(TableConstants.BO_DEPT);
			userSession = isSessionValid(boDept, request, session, parentJSON, writer);
		} catch (Exception e1) {
			parentJSON = callFailure("Session invalid. Please refresh your screen and login.", writer, parentJSON);
		}
		String appTitle = null;
		boolean isValidAppTitle = false;
		try {
			appTitle = jsonObject.getString(TableConstants.APP_TITLE);
			isValidAppTitle = FieldValidator.isAValidAppTitle(appTitle);
		} catch (Exception e1) {
			parentJSON = callFailure("Invalid AppTitle. Please refresh your screen and try again.", writer, parentJSON);
		}
		if (userSession != null && isValidAppTitle) {
			Connection con = MySqlDatabaseConnection.getConnection(request
					.getServletContext());
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				parentJSON.addElement(TableConstants.BO_DEPT,
						userSession.getUniqueID());
				final String finalFileName = getBoDept(stmt, parentJSON, appTitle, writer) + "___"
						+ appTitle;
				logger.info("File Name: " + finalFileName);
				if (userSession.isAdmin()
						&& GetProjectDetails.doZipFileExists(appTitle)) {
					final AppDetailsForJenkinsCall appDetails = GetProjectDetails
							.getUserProjectAppOldVersionAlongWithShotString(
									stmt, appTitle);
					final String localAppTitleValue = appTitle;
					final UserSessionObject localUserSessionValue = userSession;
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							TriggerJenkinsBuild.addAppToPipeLine(localAppTitleValue,
									finalFileName, "true", "New App", appDetails!=null?appDetails.getVersion():"0.0.0^0.0.0",
									getServletContext(), appDetails!=null?appDetails.getBundleID():TableConstants.NEW_APP);
							logger.info("Successful trigger of Jenkins Build signing of app: "
									+ localAppTitleValue
									+ " by user: "
									+ localUserSessionValue.getUserID());
						}
					};
					new Thread(runnable).start();
					parentJSON.addElement(TableConstants.SUCCESS,
							"<div><label class='error'>Build process triggered. Success.</label><div>");
				} else {
					parentJSON.addElement(TableConstants.ERROR,
							"<div><label class='error'>Build process triggered. Success.</label><div>");
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
		writer.println(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		writer.flush();
		writer.close();
		logger.exit(false);
	}
	
	protected String getBoDept(Statement stmt,
			BuildJSON parentJSON, String appTitle, PrintWriter writer) throws SQLException {
		String query = "SELECT " + TableConstants.SUBMITTED_BY + " FROM " + TableConstants.TABLE_PROJECTS + " WHERE " + TableConstants.APP_TITLE + 
				"='" + appTitle + "';";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if (result != null && result.next()) {
				return result.getString(TableConstants.SUBMITTED_BY);
			}
		} catch (SQLException e) {
			callFailure("Unable to process isAdmin or Nor.", writer, parentJSON);
		} finally {
			if(result != null) {
				result.close();
			}
		}
		return null;
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

}
