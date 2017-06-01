package com.nike.appsubm.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserRequest;
import com.nike.util.UserSessionObject;
import com.nike.util.ZipFolder;

/**
 * Servlet implementation class UploadZipFile
 */
public class UploadImageFiles extends SuperUploadServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(UploadImageFiles.class.getName());
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private void callFailureWithHTML(String string, ServletOutputStream writer) {
		try {
			writer.write(BuildJSON.getStaticJSONParent(string).toString().getBytes());
			logger.error(string);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			getLogger().error("Failed to write Failure HTML resonse.");
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
		String filePath = null;
		File fileDir = null;
		super.doPost(request, response);
		String boDept = request.getParameter(TableConstants.BO_DEPT);
		BuildJSON parentJSON = new BuildJSON();
		ServletOutputStream writer = response.getOutputStream();
		UserSessionObject userSession = isSessionValid(boDept, request, processSession(request), parentJSON, null);
		if (userSession != null) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			if (!ServletFileUpload.isMultipartContent(request)) {
				callFailureWithHTML(
						"<div><label class='error'>Request does not contain upload data</label><div>",
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
			// constructs the directory path to store upload file
			String uploadPath = TableConstants.UPLOAD_IMAGE_DIRECTORY;
			// creates the directory if it does not exist
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			try {
				// parses the request's content to extract file data
				List formItems = upload.parseRequest(request);
				Iterator iter = formItems.iterator();
				HttpSession session = processSession(request);
				Connection connection = MySqlDatabaseConnection.getConnection(getServletContext());
				Statement stmt = connection.createStatement();
				String boDeptString = getBoDept(userSession, stmt, parentJSON, writer);
				fileDir = new File(uploadPath + File.separator
						+ boDeptString);
				if (fileDir.exists()) {
					ZipFolder.delete(fileDir);
				}
				fileDir.mkdir();
				// iterates over form's fields
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					// processes only fields that are not form fields
					if (!item.isFormField()) {
						filePath = fileDir.getAbsolutePath() + File.separator
								+ item.getName();
						File storeFile = new File(filePath);
						// saves the file on disk
						item.write(storeFile);
					}
				}
				if (!UploadZipFile.uploadDetails.containsKey(boDeptString)) {
					UploadZipFile.uploadDetails.put(
							boDeptString,
							new UserRequest("", fileDir.getAbsolutePath()));
				} else {
					UserRequest userReq = UploadZipFile.uploadDetails
							.get(boDeptString);
					userReq.setImageFolderUrl(fileDir.getAbsolutePath());
					UploadZipFile.uploadDetails.put(
							boDeptString,
							userReq);
				}
				parentJSON.addElement(TableConstants.MESSAGE,
						"<div><label>Upload has been successful.</label><div>");
				parentJSON.addElement(TableConstants.PATH,
						fileDir.getAbsolutePath());
				writer.write(parentJSON
						.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
				writer.flush();
				writer.close();
				logger.info("Upload of image files has been successful by user: " + userSession.getUserID());
			} catch (Exception ex) {
				callFailure("<div><label class='error'>There was an error: "
						+ ex.getMessage() + "</label></div>", ex, parentJSON, writer);
			}
		}
		logger.exit(false);
	}
	
}
