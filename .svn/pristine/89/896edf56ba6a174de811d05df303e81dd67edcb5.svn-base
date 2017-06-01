package com.nike.appsubm.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
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

import com.nike.appsubm.servlets.SuperUploadServlet.FileDetails;
import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.GetProjectDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.UpdateProjectDetails;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UploadProfileFile
 */
public class UploadProfileFile extends SuperUploadServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UploadProfileFile.class.getName());
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
	

	@Override
	protected void callFailure(String string, BuildJSON parentJSON, ServletOutputStream writer) {
		parentJSON.addElement(TableConstants.MESSAGE, string);
		logger.error(string);
		try {
			writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			getLogger().error("Failed to write error response to user.");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		String fileName = request.getParameter("AppTitle");
		ServletOutputStream writer = response.getOutputStream();
		BuildJSON parentJSON = new BuildJSON();
		if(fileName == null) {
			parentJSON.addElement(TableConstants.ERROR, "Incorrect request. Please check your request.");
			writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			logger.error("Improper reset of the fields.");
			writer.flush();
			writer.close();
			return;
		} 
		String profilePath = TableConstants.UPLOAD_APNS_PROFILE_DIRECTORY + File.separator + fileName + ".mobileprovision";
		if (!ServletFileUpload.isMultipartContent(request)) {
			callFailure("<div><label class='error'>Request does not contain upload data</label><div>", parentJSON, writer);
			return;
		}
		String boDept = request.getParameter(TableConstants.BO_DEPT);
		UserSessionObject userSession = isSessionValid(boDept, request, processSession(request), parentJSON , null);
		if (userSession != null) {
			// configures upload settings
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(THRESHOLD_SIZE);
			factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(MAX_FILE_SIZE);
			upload.setSizeMax(MAX_REQUEST_SIZE);
			// constructs the directory path to store upload file
			String uploadPath = TableConstants.UPLOAD_APNS_PROFILE_DIRECTORY;
			// creates the directory if it does not exist
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				callFailure("Project does not exists!", parentJSON, writer);
				return;
			} else {

				try {
					// parses the request's content to extract file data
					List formItems = upload.parseRequest(request);
					Iterator iter = formItems.iterator();
					File fileDir = new File(uploadPath);
					// iterates over form's fields
					while (iter.hasNext()) {
						FileItem item = (FileItem) iter.next();
						// processes only fields that are not form fields
						if (!item.isFormField()) {
							String filePath = fileDir.getAbsolutePath()
									+ File.separator + fileName
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
					logger.info("Successful upload of the profile file: " + fileName + " by user: " + userSession.getUserID());

				} catch (Exception ex) {
					callFailure(TableConstants.MESSAGE
							+ "<div><label class='error'>There was an error: "
							+ ex.getMessage() + "</label><div>", ex, parentJSON, writer);
				}
			}
			FileDetails fileDetails = readProfileFile(profilePath);
			String creationDate;
			String expirationDate;
			//			String expirationDate = "2015-11-13T9:58:45Z"; //"egrep -a -A 2 ExpirationDate "
//					+ profilePath;//+" | grep date | sed -e 's/<date>//' -e 's/<\\/date>//'";
//			String comando = String.format(expirationDate);
//			Runtime rt = Runtime.getRuntime();
//			Process p = rt.exec(comando);
//			int result = 0;
//			String creationDate = null, appId = null;
//			boolean doExpirationExist = true, doCreationExist = true, doAppIdExist = true;
//			try { 
//				result = p.waitFor();
//				BufferedReader buf = new BufferedReader(new InputStreamReader(
//						p.getInputStream()));
//				String line = "";
//				while ((line = buf.readLine()) != null) {
//					if (line.contains("<date>")) {
//						doExpirationExist = true;
//						expirationDate = line.replace("<date>", "")
//								.replace("</date>", "").trim();
//					}
//				}
//				creationDate = "2014-12-24T9:58:45Z"; //"egrep -a -A 2 CreationDate " + profilePath;//+" | grep date | sed -e 's/<date>//' -e 's/<\\/date>//'";
//				comando = String.format(creationDate);
//				p = rt.exec(comando);
//				result = p.waitFor();
//				buf = new BufferedReader(new InputStreamReader(
//						p.getInputStream()));
//				line = "";
//				while ((line = buf.readLine()) != null) {
//					if (line.contains("<date>")) {
//						doCreationExist = true;
//						creationDate = line.replace("<date>", "")
//								.replace("</date>", "").trim();
//					}
//				}
//				appId = "com.nike.prounlimited.flexiphone"; //"egrep -a -A 2 application-identifier " + profilePath;//+" | grep date | sed -e 's/<date>//' -e 's/<\\/date>//'";
//				comando = String.format(appId);
//				p = rt.exec(comando);
//				result = p.waitFor();
//				buf = new BufferedReader(new InputStreamReader(
//						p.getInputStream()));
//				line = "";
//				while ((line = buf.readLine()) != null) {
//					if (line.contains("<string>")) {
//						doAppIdExist = true;
//						appId = line.replace("<string>", "")
//								.replace("</string>", "").trim();
//					} 
//				}
//			} catch (InterruptedException e1) {
//				callFailure("You request of update of Nike Generic Profile is interrupted.", e1);
//			}
			if (fileDetails == null || fileDetails.getCreationDate() == null || fileDetails.getExpirationDate() == null || fileDetails.getAppId() == null) {
				callFailure("<div><label class='error'>The uploaded profile file is not a valid provision profile.</label><div>", parentJSON, writer);
				return;
			} else {
				creationDate = Dates.getDate(fileDetails.getCreationDate());
				expirationDate = Dates.getDate(fileDetails.getExpirationDate());
				if (logger.isDebugEnabled()) {
					logger.debug(creationDate + " " + expirationDate);
				}
				Connection connection = MySqlDatabaseConnection.getConnection(getServletContext());
				try {
					Statement stmt = connection.createStatement();
					if (!GetProjectDetails.doProjectExist(stmt, fileName)
							|| !UpdateProjectDetails.updateProjectPushProfileDetails(
									stmt, fileName, profilePath)
							|| !PushProfileDetails.putRow(stmt, fileName, creationDate,
									expirationDate, fileDetails.getAppId())) {
						callFailure("<div><label class='error'>There was an error: "
										+ "There is no such project with AppTitle: "
										+ fileName + "</label><div>", parentJSON, writer);
						return;
					} else {
						logger.info("Successful upload of the profile file: " + fileName + " by user: " + userSession.getUserID());
						writer.write(parentJSON
								.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
						writer.flush();
						writer.close();
					}
					stmt.close();
					//			con.close();
				} catch (SQLException e) {
					callFailure(
							"<div><label class='error'>There was an error: "
									+ e.getMessage() + "</label><div>",e, parentJSON, writer);
				}
			}
		}
		logger.exit(false);
	}
}
