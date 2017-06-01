package com.nike.appsubm.servlets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UploadSuperServlet
 */
public class SuperUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static Logger superLogger =  LogManager.getLogger(UploadSuperServlet.class.getName());
	protected final static Charset ENCODING = StandardCharsets.UTF_8;
	
	protected String getBoDept(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON, ServletOutputStream writer) throws SQLException {
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
					parentJSON, writer);
		} finally {
			if(result != null) {
				result.close();
			}
		}
		return null;
	}
	
	protected UserSessionObject isSessionValid(String boDept, HttpServletRequest request, HttpSession session, BuildJSON parentJSON, ServletOutputStream writer) {

		if (boDept != null && session != null) {
			synchronized (session) {
				UserSessionObject userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null && session.getId().equals(boDept)) {
					setLoggerEntry(userSession.getUserID());
					session.setAttribute(TableConstants.BO_DEPT, userSession);
					return userSession;
				}
			}

		}
		getLogger()
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		callFailure("Please login!", parentJSON, writer);
		return null;
	}
	
	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}
	
	protected void callFailure(String string, BuildJSON parentJSON, ServletOutputStream writer) {
		parentJSON.addElement(TableConstants.MESSAGE, string);
		getLogger().error(string);
		try {
			writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			getLogger().error("Failed to write error response to user.");
		}
	}
	
	protected void callFailure(String error, Exception e, BuildJSON parentJSON, ServletOutputStream writer) {
		parentJSON.addElement(TableConstants.MESSAGE, error);
		getLogger().error(error + " "+ getStackTrace(e));
		try {
			writer.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			getLogger().error("Failed to write error response to user.");
		}
	}
	
	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}
	
	protected Logger getLogger() {
		return superLogger;
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SuperUploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processSession(request);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
	}
	
	protected HttpSession processSession(HttpServletRequest request) {
		return request.getSession(false);
	}
	
	public FileDetails readProfileFile(String filePath) throws IOException {
		Path fFilePath = Paths.get(filePath);
		return processLineByLine(fFilePath);
	}


	/** Template method that calls {@link #processLine(String)}. 
	 * @param fFilePath2 */
	public FileDetails processLineByLine(Path fFilePath) throws IOException {
		FileDetails fileDetails = null;
		try (Scanner scanner = new Scanner(fFilePath, ENCODING.name())) {
			fileDetails = new FileDetails();
			while (scanner.hasNextLine()) {
				String value = scanner.nextLine();
				if(value != null && value.contains("ExpirationDate")){
					String expirationDate = scanner.nextLine().trim();
					fileDetails.setExpirationDate(expirationDate.replaceAll("<date>", "").replaceAll("</date>", "").trim());;
					log("ExpirationDate: " + expirationDate);
					break;
				} else if(value != null && value.contains("CreationDate")){
					String creationDate = scanner.nextLine().trim();
					fileDetails.setCreationDate(creationDate.replaceAll("<date>", "").replaceAll("</date>", "").trim());
					log("CreationDate: " + creationDate);
				} else if(value != null && value.contains("application-identifier")){
					String appId = scanner.nextLine().trim();
					fileDetails.setAppId(appId.replaceAll("<string>", "").replaceAll("</string>", "").trim());
					log("application-identifier: " + appId);
				} 
			}
		} catch (Exception e) {
			fileDetails = null;
		}
		return fileDetails;
	}

	protected class FileDetails {
		private String expirationDate;
		private String creationDate;
		private String appId;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(String creationDate) {
			this.creationDate = creationDate;
		}

		public String getExpirationDate() {
			return expirationDate;
		}

		public void setExpirationDate(String expirationDate) {
			this.expirationDate = expirationDate;
		}
	}
}
