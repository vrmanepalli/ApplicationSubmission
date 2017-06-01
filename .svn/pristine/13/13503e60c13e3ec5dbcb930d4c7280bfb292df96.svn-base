package com.nike.appsubm.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UploadProfileFile
 */
public class UploadNikeGenericProfile extends SuperUploadServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UploadNikeGenericProfile.class.getName());
	public static final String PROFILE_NAME = "Generic_Nike";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private void callFailureWithHTML(String string, ServletOutputStream writer) {
		try {
			writer.write(BuildJSON.getStaticJSONParent(string).toString().getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			getLogger().error(ErrorUtils.getStackTrace(e));
		}
		logger.error(string);
	}
	
	
	@Override
	protected void callFailure(String string, Exception e, BuildJSON parentJSON, ServletOutputStream writer) {
		parentJSON.addElement(TableConstants.MESSAGE, string);
		logger.error(string);
		try {
			writer.write(BuildJSON.getStaticJSONParent(string).toString().getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			getLogger().error(ErrorUtils.getStackTrace(e1));
		}
	}


	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	private boolean renameExistingProfile(String fileName) {
		File profile = new File(fileName);
		File dest = new File(TableConstants.UPLOAD_PROFILE_DIRECTORY + File.separator
				+ getNumberOfFilesInDir(TableConstants.UPLOAD_PROFILE_DIRECTORY) + ".mobileprovision");
		if (profile.exists()) {
			return profile.renameTo(dest);
		}
		return true;
	}

	private int getNumberOfFilesInDir(String directoryPath) {
		File profileDir = new File(directoryPath);
		if (profileDir.exists() && profileDir.isDirectory()) {
			return profileDir.listFiles().length;
		} else {
			return 0;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		String boDept = request.getParameter(TableConstants.BO_DEPT);
		BuildJSON parentJSON = new BuildJSON();
		ServletOutputStream writer = response.getOutputStream();
		if(boDept == null) {
			parentJSON.addElement(TableConstants.ERROR, "Incorrect request. Please check your request.");
			writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			logger.error("Improper reset of the fields.");
			writer.flush();
			writer.close();
			return;
		} 
		UserSessionObject userSession = isSessionValid(boDept, request, processSession(request), parentJSON , writer);
		if (userSession != null) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			String profilePath = TableConstants.UPLOAD_PROFILE_DIRECTORY + File.separator
					+ PROFILE_NAME + ".mobileprovision";
			if (!ServletFileUpload.isMultipartContent(request)) {
				callFailureWithHTML(
						"<div><label class='error'>Request does not contain upload data</label><div>",
						writer);
				return;
			}
			if (!renameExistingProfile(profilePath)) {
				callFailureWithHTML(
						"<div><label class='error'>Soory, unable to upload the profile. Try again after sometime.</label><div>",
						writer);
				return;
			}
			// configures upload settings
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(THRESHOLD_SIZE);
			factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(MAX_FILE_SIZE);
			upload.setSizeMax(MAX_REQUEST_SIZE);
			// creates the directory if it does not exist
			File uploadDir = new File(TableConstants.UPLOAD_PROFILE_DIRECTORY);
			if (!uploadDir.exists()) {
				callFailure("Project does not exists!", parentJSON, writer);
				return;
			} else {

				try {
					// parses the request's content to extract file data
					List formItems = upload.parseRequest(request);
					Iterator iter = formItems.iterator();
					File fileDir = new File(TableConstants.UPLOAD_PROFILE_DIRECTORY);
					// iterates over form's fields
					while (iter.hasNext()) {
						FileItem item = (FileItem) iter.next();
						// processes only fields that are not form fields
						if (!item.isFormField()) {
							String filePath = fileDir.getAbsolutePath()
									+ File.separator + PROFILE_NAME
									+ ".mobileprovision";
							File storeFile = new File(filePath);
							// saves the file on disk
							item.write(storeFile);
						}
					}
					parentJSON
							.addElement(TableConstants.MESSAGE,
									"<div><label>Upload has been successful.</label><div>");
					parentJSON.addElement(TableConstants.PATH,
							fileDir.getAbsolutePath());
					logger.info("Successful upload of the profile file: " + PROFILE_NAME + " by user: " + userSession.getUserID());
				} catch (Exception ex) {
					callFailure(
							"<div><label class='error'>There was an error during upload: "
									+ ex.getMessage() + "</label><div>", ex, parentJSON, writer);
					return;
				}
			}
			FileDetails fileDetails = readProfileFile(profilePath);
			String creationDate;
			String expirationDate;
			if (fileDetails == null || fileDetails.getCreationDate() == null || fileDetails.getExpirationDate() == null || fileDetails.getAppId() == null) {
				callFailure(
						"<div><label class='error'>The uploaded profile file is not a valid provision profile.</label><div>", parentJSON, writer);
			} else {
				creationDate = Dates.getDate(fileDetails.getCreationDate());
				expirationDate = Dates.getDate(fileDetails.getExpirationDate());
				if (logger.isDebugEnabled()) {
					logger.debug(creationDate + " " + expirationDate);
				}
				Connection connection = MySqlDatabaseConnection.getConnection(getServletContext());
				try {
					Statement stmt = connection.createStatement();
					if (!PushProfileDetails.putRow(stmt, PROFILE_NAME, creationDate,
							expirationDate, fileDetails.getAppId())) {
						callFailure("<div><label class='error'>There was an error: "
								+ "There is no such project with AppTitle: "
								+ PROFILE_NAME + "</label><div>", parentJSON, writer);
						return;
					} else {
						writer.write(parentJSON
								.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
						writer.flush();
						writer.close();
						logger.info("Successful update of Nike Generic Profile by user: " + userSession.getUserID());
					}
					stmt.close();
				} catch (SQLException e) {
					callFailure("<div><label class='error'>There was an error: "
							+ e.getMessage() + "</label><div>", e, parentJSON, writer);
				}
			}
		}
		logger.exit(false);
	}
}
