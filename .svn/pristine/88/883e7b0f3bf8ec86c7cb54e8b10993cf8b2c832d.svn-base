package com.nike.appsubm.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
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
import com.nike.util.ErrorUtils;
import com.nike.util.FieldValidator;
import com.nike.util.GetProjectDetails;
import com.nike.util.GetProjectDetails.AppDetailsForJenkinsCall;
import com.nike.util.MoveDirToADir;
import com.nike.util.MoveZipFile;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.TriggerJenkinsBuild;
import com.nike.util.UpdateBusinessOwners;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class SubmitApplication
 */
public class SubmitApplication extends PostSuperClass {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(SubmitApplication.class.getName());
	private String imageUrl;
	private String appTitle;
	private boolean isRemove;
	private String date;
	private String finalFileName;
	private int push;
	private String resultStatus;
	private String pushCurrentStatus;
	private boolean isPushCertValid = false;
	private String requestType;
	private String tscDepartment;
	private String tscEmail;
	private String tscName;
	private String tscPhone;
	private String tscTitle;
	private String appADGLevel;
	private String appCurrentVersion;
	private String appDescription;
	private String appDevices;
	private String appMinOS;
	private String appSecurity;
	private boolean isPushDB;
	private String currentStatus;
	private String boDeptUniqueValue;
	private String mobileIronInstance;
	private boolean isSigningReq = true;

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
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}



	@Override
	protected void callFailure(String error) {
		logger.error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
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
			callFailure("Unable to process isAdmin or Nor.", e);
		} finally {
			if(result != null) {
				result.close();
			}
		}
		return null;
	}
	
	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response) {
		boolean doDetailsValid = true;
		if (!FieldValidator.isAValidAppTitle(appTitle)) {
			callFailure("Invalid Application name. Please submit a valid application name.");
			doDetailsValid = false;
		} else if(isRemove) {
			try {
				out = response.getWriter();
				if (isSessionValid(boDept, request)) {
					parentJSON.addElement(TableConstants.BO_DEPT, boDept);
					doSubmitApp(request);
				}
				writeFinalOP();
				return;
			} catch (IOException e) {
				callFailure("Error in getting the writer from response: "
						+ e.getLocalizedMessage(), e);
			}
		} else if (!FieldValidator.isAValidString(tscName)) {
			callFailure("Invalid TSC name. Please submit a valid TSC name.");
			doDetailsValid = false;
		} else if (!EmailValidator.validate(tscEmail)) {
			callFailure("Invalid TSC Email. Please submit a valid TSC Email.");
			doDetailsValid = false;
		} else if (!FieldValidator.validatePhoneNumber(tscPhone)) {
			callFailure("Invalid TSC Phone Number. Please submit a valid TSC Phone number.");
			doDetailsValid = false;
		} else if (!FieldValidator.isAValidString(tscTitle)) {
			callFailure("Invalid TSC Title. Please submit a valid TSC Title.");
			doDetailsValid = false;
		} else if (!FieldValidator.isAValidString(tscDepartment)) {
			callFailure("Invalid TSC Department. Please submit a valid TSC Department.");
			doDetailsValid = false;
		} else if (!FieldValidator.isValidVersionNumber(appCurrentVersion)) {
			logger.info("Version Number: " + appCurrentVersion);
//			callFailure("Invalid Version number. Please submit a valid Version number.");
//			doDetailsValid = false;
		} 
//		else if (!FieldValidator.isValidVersionNumber(appCurrentVersion)) {
//			callFailure("Invalid Version number. Please submit a valid Version number.");
//			doDetailsValid = false;
//		} 
		else if (!FieldValidator.isAValidAppDevices(appDevices)) {
			callFailure("Invalid device details. Please submit valid app device details.");
			doDetailsValid = false;
		} else if (!FieldValidator.isAValidString(mobileIronInstance)) {
			callFailure("Invalid mobile iron details. Please submit valid mobile iron details.");
			doDetailsValid = false;
		}
		if(doDetailsValid) {
			super.doRoutine(request, response);
		} else {
			try {
				out = response.getWriter();
				writeFinalOP();
			} catch (IOException e) {
				logger.error("Error in getting the writer from response: "
						+ e.getLocalizedMessage());
			}
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request) {
		doSubmitApp(request);
	}
	
	private void doSubmitApp(HttpServletRequest request) {
		try {
			stmt = con.createStatement();
			boDeptUniqueValue = getBoDept(userSession, stmt, parentJSON);
			appTitle = appTitle.replace(" ", "_");
			imageUrl = TableConstants.UPLOAD_IMAGE_DIRECTORY + "/"
					+ appTitle;
			isPushCertValid = PushProfileDetails.isPushCertificateValid(
					stmt, appTitle);
			String query = createQuery(jsonObject, boDeptUniqueValue);
			String UAUserName = null;
			String UAPwd = null;
			if (!isRemove) {
				UAUserName = jsonObject
						.getString(TableConstants.UA_USER_NAME);
				UAPwd = jsonObject.getString(TableConstants.UA_PWD);
			}
			if (isRemove && stmt.executeUpdate(query) == 1) {
				String content = "The application "
						+ appTitle
						+ " is submitted for removal from Mobile Iron by the Buiness Owner whose user id is "
						+ userSession.getUserID();
				parentJSON.addElement(TableConstants.RESULT,
						"<p  class='checkmark'>" + content + "</p>");
				parentJSON.addElement(TableConstants.DATE, date);
				try {
					EmailNotification emailNotification = new EmailNotification(
							TableConstants.ADMIN_EMAIL_IDS,
							"App Removal Receipt: " + appTitle, content, TableConstants.APPLICATION_ICON);
					emailNotification.start();
					logger.info("Successful submission of request for removal of " + appTitle + " by user: " + userSession.getUserID());
				} catch (Exception e) {
					callFailure("Failed submission of request for removal of " + appTitle + " by user: " + userSession.getUserID(), e);
				}
			} else if (createTextFileWithContent(jsonObject)
					&& stmt.executeUpdate(query) == 1
					&& UpdateBusinessOwners.updateLastAppReqTitle(appTitle,
							userSession.getEmailID(), stmt)
					&& PushProfileDetails.putUADetails(stmt, appTitle,
							UAUserName, UAPwd)) {
				parentJSON.addElement(TableConstants.SUCCESS,
						"Successful Submission!");
				String content = "The application "
						+ appTitle
						+ " is submitted for uploadig to Mobile Iron by the Buiness Owner whose user id is "
						+ userSession.getUserID();
				try {  
					final AppDetailsForJenkinsCall appDetails = GetProjectDetails
							.getUserProjectAppOldVersionAlongWithShotString(
									stmt, appTitle);
					final ServletContext servletContext = getServletContext();
					EmailNotification emailNotification = new EmailNotification(
							TableConstants.ADMIN_EMAIL_IDS,
							"App Submission Receipt: " + appTitle, content, TableConstants.APPLICATION_ICON);
					emailNotification.start();
					if (isSigningReq) {
						Runnable runnable = new Runnable() {

							@Override
							public void run() {
								TriggerJenkinsBuild.addAppToPipeLine(appTitle,
										finalFileName, String.valueOf(isPushDB),
										requestType, appDetails!=null?appDetails.getVersion():"0.0.0^0.0.0", servletContext, appDetails!=null?appDetails.getBundleID():TableConstants.NEW_APP);
								logger.info("Successful submission of request for signing of "
										+ appTitle
										+ " by user: "
										+ userSession.getUserID());
							}
						};
						if (push == 0) {
							Thread t = new Thread(runnable,
									"Jenkins job trigger call.");
							t.start();
						} else {
							if (isPushCertValid) {
								Thread t = new Thread(runnable,
										"Jenkins job trigger call.");
								t.start();
							} else {
								callFailure(TableConstants.PUSH_MSG);
							}
						}
					} else {
						callFailure(TableConstants.N0_SIGNING_REQUIRED);
					}
				} catch (Exception e) {
					callFailure("Unable to notify the Business Owner about the App submission via email.", e);
				}
			} else {
				parentJSON.addElement(TableConstants.ERROR,
						"Failed to Submit the app " + appTitle + " by user: " + userSession.getUserID() + " ! Please try again later");
			}
		} catch (SQLException e) {
			callFailure("Unable to use the database for Application Submission. Please try again after some time!", e);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				callFailure("Unable to complete the database transaction.", e);
			}
		}
	}

	@Override
	protected void reset() {
		super.reset();
		imageUrl = null;
		appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		requestType = jsonObject.getString(TableConstants.REQ_TYPE);
		currentStatus = jsonObject.getString(TableConstants.CURRENT_STATUS);
		if (FieldValidator.isAValidString(requestType) && requestType.equalsIgnoreCase(
				TableConstants.NEW_APP) || requestType
				.equalsIgnoreCase(TableConstants.UPDATE_EXT_APP)) {
			isRemove = false;
			tscDepartment = jsonObject.getString(TableConstants.TSC_DEPT);
			tscEmail = jsonObject.getString(TableConstants.TSC_EMAIL);
			tscName = jsonObject.getString(TableConstants.TSC_NAME);
			tscPhone = jsonObject.getString(TableConstants.TSC_PHONE);
			tscTitle = jsonObject.getString(TableConstants.TSC_TITLE);
			appADGLevel = jsonObject.getString(TableConstants.APP_ADG_LEVEL);
			try {
				appCurrentVersion = jsonObject.getString(TableConstants.APP_CURRENT_VERSION);
			} catch (Exception e) {
				appCurrentVersion = "0.0.0";
			}
			appDescription = jsonObject.getString(TableConstants.APP_DESCRIPTION);
			appDevices = jsonObject.getString(TableConstants.APP_DEVICES);
			appMinOS = jsonObject.getString(TableConstants.APP_MIN_OS);
			appSecurity = jsonObject.getString(TableConstants.APP_SECURITY);
			isPushDB = jsonObject.getBoolean(TableConstants.IS_PUSH_DB);
			mobileIronInstance = jsonObject.getString(TableConstants.MI_INSTANCE);
			isSigningReq = jsonObject.getBoolean("SignYN");
		} else {
			isRemove = true;
		}
		error = "Data is not provided to Submit an App.";
		isPushCertValid = false;
	}

	protected String createQuery(JSONObject jsonObject, String boDeptString) {
		if (requestType.equalsIgnoreCase(
				TableConstants.NEW_APP)) {
			isRemove = false;
			return submitNewRequestQuery(jsonObject);
		} else if (requestType
				.equalsIgnoreCase(TableConstants.UPDATE_EXT_APP)) {
			isRemove = false;
			return updateExistingAppQuery(jsonObject, boDeptString);
		} else {
			isRemove = true;
			return removeAppQuery(jsonObject, boDeptString);
		}
	}

	private void doPushRoutine(JSONObject jsonObject) {
		if(isPushDB) {
			push = 1;
			if (!isPushCertValid) {
				resultStatus = "<p  class=\"checkmark\">"
						+ TableConstants.PUSH_MSG + "</p>";
				pushCurrentStatus = TableConstants.PUSH_PENDING;
			} else if(!isSigningReq){
				resultStatus = "<p  class=\"checkmark\">"
						+ TableConstants.N0_SIGNING_REQUIRED + "</p>";
				pushCurrentStatus = TableConstants.SUCCESS;
			} else {
				resultStatus = "<p  class=\"checkmark\">In progress...</p>";
				pushCurrentStatus = TableConstants.IN_PROGRESS;
			}
		} else {
			if(!isSigningReq){
				resultStatus = "<p  class=\"checkmark\">"
						+ TableConstants.N0_SIGNING_REQUIRED + "</p>";
				pushCurrentStatus = TableConstants.SUCCESS;
			} else { 
				pushCurrentStatus = TableConstants.IN_PROGRESS;
				resultStatus = "<p  class=\"checkmark\">In progress...</p>";
			}
			push = 0;
		}
	}
	
	private String removeAppQuery(JSONObject jsonObject, String boDeptString) {
		date = Dates.getCurrentDate();
		UpdateRequestCount.removeRecordFromRequestCount(stmt, appTitle, userSession.isAdmin());
		String filterString;
		if(userSession.isAdmin()) {
			filterString = "';";
		} else {
			filterString = "' AND "
					+ TableConstants.BO_DEPT + " LIKE '%" + boDeptString + "%';";
		}
		String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
				+ TableConstants.CURRENT_STATUS + "='" + currentStatus + "', "
				+ TableConstants.DATE + "='" + date + "', "
				+ TableConstants.SUBMITTED_BY + "='" + boDeptUniqueValue
				+ "' WHERE " + TableConstants.APP_TITLE + "='" + appTitle
				+ filterString;
		return query;
	}

	private String updateExistingAppQuery(JSONObject jsonObject, String boDeptString) {
		doPushRoutine(jsonObject);
		String filterString;
		if(userSession.isAdmin()) {
			filterString = "';";
		} else {
			filterString = "' AND "
					+ TableConstants.BO_DEPT + " LIKE '%" + boDeptString + "%';";
		}
		String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
				+ TableConstants.REQ_TYPE + "='"
				+ requestType + "', "
				+ TableConstants.TSC_DEPT + "='"
				+ tscDepartment + "', "
				+ TableConstants.TSC_EMAIL + "='"
				+ tscEmail + "', "
				+ TableConstants.TSC_NAME + "='"
				+ tscName + "', "
				+ TableConstants.TSC_PHONE + "='"
				+ tscPhone + "', "
				+ TableConstants.TSC_TITLE + "='"
				+ tscTitle + "', "
				+ TableConstants.CURRENT_STATUS + "='"
				+ pushCurrentStatus + "', "
				+ TableConstants.APP_ADG_LEVEL + "='"
				+ appADGLevel + "', "
				+ TableConstants.APP_CURRENT_VERSION + "='"
				+ appCurrentVersion
				+ "', " + TableConstants.APP_DESCRIPTION + "='"
				+ appDescription + "', "
				+ TableConstants.APP_DEVICES + "='"
				+ appDevices + "', "
				+ TableConstants.APP_MIN_OS + "='"
				+ appMinOS + "', "
				+ TableConstants.APP_SECURITY + "='"
				+ appSecurity + "', "
				+ TableConstants.REQUEST_NUMBER + "='"
				+ GetProjectDetails.getIncrementedRequestNumber(stmt, appTitle) 
				+ "', " + TableConstants.RESULT + "='"
				+ resultStatus + "', "
				+ TableConstants.DATE + "='"
				+ Dates.getCurrentDate() + "', "
				+ TableConstants.IS_SIGNING_REQ + "="
				+ isSigningReq + ", "
				+ TableConstants.MI_INSTANCE + "='"
				+ mobileIronInstance + "', " 
				+ TableConstants.SUBMITTED_BY + "='" + boDeptUniqueValue  + "', "
				+ TableConstants.IS_PUSH_DB + "=" + push +" WHERE "
				+ TableConstants.APP_TITLE + "='" + appTitle + filterString;
		getLogger().info(query);
		return query;
	}

	private String submitNewRequestQuery(JSONObject jsonObject) {
		doPushRoutine(jsonObject);
		String query = "INSERT INTO "
				+ TableConstants.TABLE_PROJECTS
				+ " ("
				+ TableConstants.BO_DEPT
				+ ", "
				+ TableConstants.APP_TITLE
				+ ", "
				+ TableConstants.REQ_TYPE
				+ ", "
				+ TableConstants.TSC_DEPT
				+ ", "
				+ TableConstants.TSC_EMAIL
				+ ", "
				+ TableConstants.TSC_NAME
				+ ", "
				+ TableConstants.TSC_PHONE
				+ ", "
				+ TableConstants.TSC_TITLE
				+ ", "
				+ TableConstants.CURRENT_STATUS
				+ ", "
				+ TableConstants.APP_ADG_LEVEL
				+ ", "
				+ TableConstants.APP_CURRENT_VERSION
				+ ", "
				+ TableConstants.APP_DESCRIPTION
				+ ", "
				+ TableConstants.APP_DEVICES
				+ ", "
				+ TableConstants.APP_MIN_OS
				+ ", "
				// + TableConstants.APP_OLD_VERSION + ", "
				+ TableConstants.APP_SECURITY + ", "
				+ TableConstants.REQUEST_NUMBER + ", "
				+ TableConstants.RESULT + ", " + TableConstants.DATE + ", "
				+ TableConstants.IMAGE_FOLDER_URL + ", " 
				+ TableConstants.IS_PUSH_DB + ", "
				+ TableConstants.IS_SIGNING_REQ + ", "
				+ TableConstants.MI_INSTANCE + ", "
				+ TableConstants.SUBMITTED_BY
				+ ") " + "VALUES ('" + boDeptUniqueValue
				+ "', '" + appTitle + "', '"
				+ requestType + "', '"
				+ tscDepartment + "', '"
				+ tscEmail + "', '"
				+ tscName + "', '"
				+ tscPhone
				+ "', '"
				+ tscTitle
				+ "', '"
				+ pushCurrentStatus
				+ "', '"
				+ appADGLevel
				+ "', '"+appCurrentVersion+"', '"
				+ appDescription
				+ "', '"
				+ appDevices
				+ "', '"
				+ appMinOS
				+ "', '" + appSecurity
				+ "', '" + GetProjectDetails.getIncrementedRequestNumber(stmt, appTitle)
				+ "', '" + resultStatus
				+ "', '" + Dates.getCurrentDate() + "', '"
				+ imageUrl + "', "
				+ push + ", "
				+ isSigningReq + ", '"
				+ mobileIronInstance + "', '"
				+ boDeptUniqueValue
				+ "');";
		return query;
	}

	private boolean createTextFileWithContent(JSONObject jsonObject) throws SQLException {
		String boDept = boDeptUniqueValue;
		String folderUrl;
		try {
			folderUrl = UploadZipFile.uploadDetails.get(boDept)
					.getProjectFolderUrl();
		} catch (Exception e) {
			logger.error(ErrorUtils.getStackTrace(e));
			return false;
		}
		// Process the files by zipping and moving to a proper directory. So
		// that Jenkins can pick it up.
		finalFileName = boDept + "___" + appTitle;
		UploadZipFile.uploadDetails.remove(boDept);
		String[] args = { TableConstants.VOLUMES_UPLOAD_IMAGE_DIRECTORY + "/" + boDept,
				TableConstants.VOLUMES_UPLOAD_IMAGE_DIRECTORY + "/" + appTitle };
		java.io.File imageFolder = new java.io.File(
				TableConstants.VOLUMES_UPLOAD_IMAGE_DIRECTORY + "/" + boDept);
		if (imageFolder.exists()) {
			MoveDirToADir.doMove(args);
		}
		if (isSigningReq) {
			String[] mSourD = { folderUrl,
					TableConstants.VOLUMES_UPLOAD_FINAL_DIRECTORY + "/" + finalFileName };
			getLogger().info("App folder info: " + folderUrl + " destination: " + TableConstants.VOLUMES_UPLOAD_FINAL_DIRECTORY + "/" + finalFileName);
			if (MoveZipFile.reNameFile(mSourD)) {
				return true;
			} else {
				getLogger().info("Unable to change the name of the file: " + folderUrl + " to : " + folderUrl + ".zip");
				return false;
			}
		} else {
			String[] mSourD = { folderUrl,
					TableConstants.VOLUMES_UPLOAD_FINAL_DIRECTORY + "/" + finalFileName };
			String destinationProjectFolder = TableConstants.ROOT_FILE_PATH_PROJECTS + appTitle + "/";
			String newFolder = destinationProjectFolder + "New";
			String oldFolder = destinationProjectFolder + "Old";
			File newFile = new File(newFolder);
			File oldFile = new File(oldFolder);
			if(!newFile.exists()) {
				newFile.mkdirs();
				mSourD[0] = folderUrl;
				mSourD[1] = newFolder;
			} else {
				if(!oldFile.exists()) {
					oldFile.mkdir();
				}
				mSourD[0] = newFolder;
				mSourD[1] = oldFolder;
				MoveZipFile.reNameFile(mSourD);
				newFile.mkdirs();
				mSourD[0] = folderUrl;
				mSourD[1] = newFolder;
			}
			if (MoveZipFile.reNameFile(mSourD)) {
				return true;
			} else {
				logger.info("Unable to change the name of the file: " + folderUrl + " to : " + folderUrl + ".zip");
				return false;
			}
		}
	}

}
