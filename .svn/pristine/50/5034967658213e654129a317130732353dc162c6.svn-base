package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.EmailNotification;
import com.nike.util.ErrorUtils;
import com.nike.util.FieldValidator;
import com.nike.util.GetProjectDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.TriggerJenkinsBuild;

/**
 * Servlet implementation class UpdateJobResult
 */
public class UpdateJobResult extends GetSuperClass {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UpdateJobResult.class.getName());
	private String appTitle;
	private String result;
	private int buildNumber;
	private String currentBuildPath;
	private String previousBuildPath;
	private String currentStatus;
	private String AppCurrentVersion;
	private boolean isFailure = false;
	private boolean isSuccess = false;
	private String appInstallUrl = "";
	private String url;
	private ServletContext servletContext;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateJobResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setLoggerEntry("Jenkins call to update the Job result");
		super.doGet(request, response);
		logger.exit(false);
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
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void processSession(HttpServletRequest request) {
		session = request.getSession(true);
	}

	@Override
	protected void doRoutine(HttpServletRequest request) {
		servletContext = getServletContext();
		if (FieldValidator.isAValidAppTitle(appTitle)) {
			try {
				con = MySqlDatabaseConnection.getConnection(servletContext);
				stmt = con.createStatement();
				res = stmt.executeQuery("SELECT COUNT(*) from Projects WHERE "
						+ TableConstants.APP_TITLE + "='" + appTitle + "';");
				res.next();
				if (res.getInt(1) > 0) {
					res = stmt
							.executeQuery("SELECT Result from Projects WHERE "
									+ TableConstants.APP_TITLE + "='"
									+ appTitle + "';");
					if (res.next()) {
						String addStatus;
						logger.info(result);
						//Update Database Signing is successful
						if (result.contains("Creation of final signed")) {
							addStatus = "";
							currentStatus = TableConstants.SUCCESS;
							startP = "<p  class=\"checkmark\">";
							isSuccess = true;
							TriggerJenkinsBuild.setInProcess(false);
							Runnable runnable = new Runnable() {

								@Override
								public void run() {
									if (TriggerJenkinsBuild.getNextAppTitle() != null) {
										String appTitle = TriggerJenkinsBuild
												.getNextAppTitle();
										TriggerJenkinsBuild
												.startSigningNextApp(appTitle, servletContext);
									}
								}
							};
							Thread t = new Thread(runnable,
									"Jenkins job trigger call.");
							t.start();
						} 
						//Update Database Failed to Sign.
						else if (result.contains("Failed")) {
							addStatus = "";
							currentStatus = TableConstants.FAILED;
							startP = "<p  class=\"cross\">";
							isFailure = true;
							TriggerJenkinsBuild.setInProcess(false);
							Runnable runnable = new Runnable() {

								@Override
								public void run() {
									if (TriggerJenkinsBuild.getNextAppTitle() != null) {
										String appTitle = TriggerJenkinsBuild
												.getNextAppTitle();
										TriggerJenkinsBuild
										.startSigningNextApp(appTitle, servletContext);
									}
								}
							};
							Thread t = new Thread(runnable,
									"Jenkins job trigger call.");
							t.start();
						} 
						//Update Database signing is in Progress
						else {
							startP = "<p  class=\"checkmark\">";
							addStatus = "<p  class=\"checkmark\">In progress...</p>";
							currentStatus = TableConstants.IN_PROGRESS;
						}
						result = res.getString(TableConstants.RESULT).replace(
								"<p  class=\"checkmark\">In progress...</p>",
								"")
								+ startP + processOutput(result) + endP + addStatus;
						if (stmt.executeUpdate(createQuery()) != 1) {
							callFailure(appTitle + " " + error);
						} else {
							callSuccess(session);
						}
					} else {
						callFailure("No such application with name " + appTitle
								+ " does exists in the database.");
					}
				} else {
					callFailure("No such application with name " + appTitle
							+ " does exists in the database.");
				}
			} catch (SQLException e) {
				callFailure(appTitle + " " + e.getMessage(), e);
			} finally {
				try {
					stmt.close();
				} catch (SQLException e) {
					callFailure(appTitle + " " + e.getMessage(), e);
				}
			}
		}
		writeFinalOP();
	}
	
	String startP = "<p>";
	String endP = "</p>";
	private String AppCurrentShortStringVersion;
	private String bundleID;
	
	private String processOutput(String data) {
		if(data.contains("<br>")) {
			String[] details = data.split("<br>");
			for(int i = 0; i < details.length; i++) {
				if(i == 0) {
					result = details[i];
				} else if(i == details.length-1) {
					result += startP + details[i];
				} else {
					result += startP + details[i] + endP;
				}
			}
		}
		return result;
	}

	@Override
	protected void callFailure(String error) {
		parentJSON.addElement(TableConstants.ERROR, error);
		logger.error(error);
		isValid = false;
	}

	@Override
	protected void callSuccess(HttpSession httpSession) {
		isValid = false;
		parentJSON.addElement(TableConstants.RESPONSE, "Successful Update!");
		if (isFailure) {
			try {
				String emailIDs = GetUserDetails.getUserEmailIDUsingAppTitle(stmt,
						appTitle);
				if (emailIDs != null) {
					EmailNotification emailNotification = new EmailNotification(
							emailIDs, result + "<p>" + appTitle
									+ " app status report", appTitle
									+ ", Failed to sign your app. </p>", TableConstants.APPLICATION_ICON);
					emailNotification.start();
					logger.error(appTitle
									+ " app status report", appTitle
									+ ", Failed to sign your app. " + result);
				} else {
					logger.error("Was not able to get the emailIDs from database. Not able to send the notification to users.");
				}
			} catch (Exception e) {
				callFailure(appTitle + "  " + e.getLocalizedMessage(), e);
			}
		} else if (isSuccess) {
			try {
				String emailIDs = GetUserDetails.getUserEmailIDUsingAppTitle(stmt,
						appTitle);
				if (emailIDs != null) {
					EmailNotification emailNotification = new EmailNotification(emailIDs
							, appTitle + " app status report",
							result + "<p>" + appTitle
							+ ",  signing your app is successful. </p>", TableConstants.APPLICATION_ICON);
					emailNotification.start();
					logger.info(appTitle
							+ " app status report", appTitle
							+ ", signing your app is successful.");
				} else {
					logger.error("Was not able to get the emailIDs from database. Not able to send the notification to users.");
				}
			} catch (Exception e) {
				callFailure(appTitle + "  " + e.getLocalizedMessage(), e);
			}
		} else {
			logger.info("Successful update of the job result step for app: " + appTitle);
		}
	}

	@Override
	protected String createQuery() {
		String additionalUpdates = "";
		if (isFailure) {
			additionalUpdates += "', " + TableConstants.CURRENT_BUILD_PATH
					+ "='" + "', " + TableConstants.CURRENT_STATUS + "='"
					+ currentStatus;
		} else if (buildNumber > -1
//				&& UpdateBuildCount.updateBuildNumber(appTitle,
//						Dates.getCurrentDate(), buildNumber,
//						stmt)
						) {
			int successfulBuilds = GetProjectDetails
					.getIncrementedSuccessfulBuildNumber(stmt, appTitle);
			additionalUpdates = "', " + TableConstants.CURRENT_BUILD_PATH
					+ "='" + currentBuildPath;

			if (successfulBuilds > 1) {
				additionalUpdates += "', " + TableConstants.PREVIOUS_BUILD_PATH
						+ "='" + previousBuildPath;
			}
			additionalUpdates += "', " + TableConstants.CURRENT_STATUS + "='"
					+ currentStatus + "', " + TableConstants.SUCCESSFUL_BUILDS
					+ "='" + successfulBuilds;
			if (appInstallUrl != null && appInstallUrl != "" && url != null && url != "") {
				appInstallUrl += "&url="+url;
				additionalUpdates += "', " + TableConstants.APP_INSTALL_URL
						+ "='" + appInstallUrl;
			}
		} else if (currentStatus != null) {
			additionalUpdates = "', " + TableConstants.CURRENT_STATUS + "='"
					+ currentStatus;
		}
		if (AppCurrentVersion != null && AppCurrentVersion != "") {
			additionalUpdates += "', " + TableConstants.APP_CURRENT_VERSION
					+ "='" + AppCurrentVersion;
		}
		
		if (AppCurrentShortStringVersion != null && AppCurrentShortStringVersion != "") {
			additionalUpdates += "', " + TableConstants.APP_CURRENT_VERSION_SHORT_STRING
					+ "='" + AppCurrentShortStringVersion;
		}
		if(bundleID != null) {
			additionalUpdates += "', " + TableConstants.APP_BUNDLE_ID
					+ "='" + bundleID;
		}

		String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
				+ TableConstants.RESULT + "='" + result + additionalUpdates
				+ "' WHERE " + TableConstants.APP_TITLE + "='" + appTitle
				+ "';";

		return query;
	}

	@Override
	protected void reset(HttpServletRequest request) {
		super.reset(request);
		error = "Unable to connect to Database. Please try again later!";
		try {
			result = request.getParameter(TableConstants.RESULT);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
		}
		try {
			appTitle = request.getParameter(TableConstants.APP_TITLE);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
		}
		try {
			String buildNumberString = request.getParameter(TableConstants.BUILD_NUMBER);
			buildNumber = (buildNumberString != null) ? Integer
					.parseInt(buildNumberString)
					: -1;
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			buildNumber = -1;
		}
		try {
			currentBuildPath = request
					.getParameter(TableConstants.CURRENT_BUILD_PATH);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			currentBuildPath = null;
		}
		try {
			previousBuildPath = request
					.getParameter(TableConstants.PREVIOUS_BUILD_PATH);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			previousBuildPath =  null;
		}
		try {
			AppCurrentVersion = request
					.getParameter(TableConstants.APP_CURRENT_VERSION);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			AppCurrentVersion = null;
		}
		try {
			AppCurrentShortStringVersion = request
					.getParameter(TableConstants.APP_CURRENT_VERSION_SHORT_STRING);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			AppCurrentShortStringVersion = null;
		}
		try {
			appInstallUrl = request
					.getParameter(TableConstants.APP_INSTALL_URL);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			appInstallUrl = null;
		}
		try {
			url = request
					.getParameter(TableConstants.URL);
		} catch (Exception e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
			url = null;
		}
		try {
			bundleID = request
					.getParameter(TableConstants.APP_BUNDLE_ID);
		} catch (Exception e) {
			bundleID = null;
			getLogger().error(ErrorUtils.getStackTrace(e));
		}
		isFailure = false;
		isSuccess = false;
	}

	@Override
	protected void addElementsToJSON() {
		// TODO Auto-generated method stub

	}

}
