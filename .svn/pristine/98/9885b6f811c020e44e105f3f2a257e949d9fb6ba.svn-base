package com.nike.appsubm.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
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
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserRequest;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UploadZipFile
 */
public class UploadZipFile extends SuperUploadServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UploadZipFile.class.getName());
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final long MAX_FILE_SIZE_PROMPT = 1024 * 1024 * 40; // 40MB
	private static final long MAX_REQUEST_SIZE_PROMPT = 1024 * 1024 * 50; // 50MB
//	private static final long MAX_FILE_SIZE = 1024 * 1024 * 100; // 100MB
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1GB
//	private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 110; // 110MB
	private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 1024; // 1GB
	public static HashMap<String, UserRequest> uploadDetails = new HashMap<String, UserRequest>();
	
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		BuildJSON parentJSON = new BuildJSON();
		String boDept = request.getParameter(TableConstants.BO_DEPT);
		ServletOutputStream writer = response.getOutputStream();
		UserSessionObject userSession = isSessionValid(boDept, request, processSession(request), parentJSON , writer);
		if (userSession != null) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			if (!ServletFileUpload.isMultipartContent(request) || boDept == null) {
				writer.write(BuildJSON
						.getStaticJSONParent("<div><label class='error'>Request does not contain upload data</label><div>").toString().getBytes());
				logger.error("Request does not contain upload data, by user: " + userSession.getUserID());
				writer.flush();
				writer.close();
				return;
			}
			// configures upload settings
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(THRESHOLD_SIZE);
			factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(MAX_FILE_SIZE);
			upload.setSizeMax(MAX_REQUEST_SIZE);
			// constructs the directory path to store upload file
			String uploadPath = TableConstants.VOLUMES_UPLOAD_FINAL_DIRECTORY;
			// creates the directory if it does not exist
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			File fileDir = null;
			try {
				// parses the request's content to extract file data
				List formItems = upload.parseRequest(request);
				Iterator iter = formItems.iterator();

				String filePath = null;
				String result = "";
				Connection connection = MySqlDatabaseConnection.getConnection(getServletContext());
				Statement stmt = connection.createStatement();
				String boDeptString = getBoDept(userSession, stmt, parentJSON, writer);
				// iterates over form's fields
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					// processes only fields that are not form fields
					if (!item.isFormField()) {
						fileDir = new File(uploadPath + File.separator
								+ boDeptString);
						if (!fileDir.exists()) {
							fileDir.mkdir();
						}
						filePath = fileDir.getAbsolutePath() + File.separator
								+ boDeptString
								+ ".ipa";
						File storeFile = new File(filePath);
						// saves the file on disk
						item.write(storeFile);
						if (storeFile.length() > MAX_REQUEST_SIZE_PROMPT) {
							result  += TableConstants.MAXIMUM_FILE_MSG;
							logger.info("File " + filePath + " of size " + storeFile.length() + " by user: " + userSession.getUserID());
						}
						storeFile.exists();
					}
				}
				if (!uploadDetails.containsKey(boDeptString)) {
					uploadDetails.put(
							boDeptString,
							new UserRequest(fileDir.getAbsolutePath(), ""));
				} else {
					UserRequest userReq = uploadDetails.get(boDeptString);
					userReq.setProjectFolderUrl(fileDir.getAbsolutePath());
					logger.info("File Name: " + fileDir.getAbsolutePath());
					uploadDetails.put(
							boDeptString,
							userReq);
				}
				result += "<div><label>Upload has been successful.</label><div>";
				logger.info("Upload of Zip file for app: " + filePath + " has been successful by user: " + userSession.getUserID());
				parentJSON.addElement(TableConstants.MESSAGE, result);
				parentJSON.addElement(TableConstants.PATH, filePath);
			} catch (Exception ex) {
				callFailure(
						"<div><label class='error'>There was an error: "
								+ ex.getMessage() + "</label><div>", ex, parentJSON, writer);
			} finally {
				if (fileDir != null) {
					fileDir.exists();
				}
			}
		}
		writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
		writer.flush();
		writer.close();
		logger.exit(false);
	}	

}
