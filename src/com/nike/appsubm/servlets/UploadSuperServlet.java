package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.servlet.ServletException;
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
@WebServlet("/Private/UploadSuperServlet")
public class UploadSuperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static Logger superLogger =  LogManager.getLogger(UploadSuperServlet.class.getName());

	protected UserSessionObject userSession;

	protected BuildJSON parentJSON;

	protected PrintWriter writer;

	protected HttpSession session;
	
	protected String creationDate = null;
	protected String appId = null;
	protected String expirationDate = null;
	protected final static Charset ENCODING = StandardCharsets.UTF_8;
	
	protected boolean isSessionValid(String boDept, HttpServletRequest request) {

		if (boDept != null && session != null) {
			synchronized (session) {
				userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null && session.getId().equals(boDept)) {
					setLoggerEntry(userSession.getUserID());
					session.setAttribute(TableConstants.BO_DEPT, userSession);
					return true;
				}
			}

		}
		getLogger()
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		callFailure("Please login!");
		return false;
	}
	
	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}
	
	protected void callFailure(String string) {
		parentJSON.addElement(TableConstants.MESSAGE, string);
		getLogger().error(string);
		writer.println(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		writer.flush();
		writer.close();
	}
	
	protected void callFailure(String error, Exception e) {
		parentJSON.addElement(TableConstants.MESSAGE, error);
		getLogger().error(error + " "+ getStackTrace(e));
		writer.println(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		writer.flush();
		writer.close();
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
    public UploadSuperServlet() {
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
		writer = response.getWriter();
		parentJSON = new BuildJSON();
	}
	
	protected void processSession(HttpServletRequest request) {
		session = request.getSession(false);
	}
	
	public void readProfileFile(String filePath) throws IOException {
		Path fFilePath = Paths.get(filePath);
		processLineByLine(fFilePath);
	}


	/** Template method that calls {@link #processLine(String)}. 
	 * @param fFilePath2 */
	public void processLineByLine(Path fFilePath) throws IOException {
		try (Scanner scanner = new Scanner(fFilePath, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				String value = scanner.nextLine();
				if(value != null && value.contains("ExpirationDate")){
					expirationDate  = scanner.nextLine().trim();
					expirationDate = expirationDate.replaceAll("<date>", "").replaceAll("</date>", "").trim();
					log("ExpirationDate: " + expirationDate);
					break;
				} else if(value != null && value.contains("CreationDate")){
					creationDate = scanner.nextLine().trim();
					creationDate = creationDate.replaceAll("<date>", "").replaceAll("</date>", "").trim();
					log("CreationDate: " + creationDate);
				} else if(value != null && value.contains("application-identifier")){
					appId = scanner.nextLine().trim();
					appId = appId.replaceAll("<string>", "").replaceAll("</string>", "").trim();
					log("application-identifier: " + appId);
				} 
			}
		}
	}

}
