package com.nike.appsubm.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class DownloadIPAFile
 */
public class DownloadIPAFile extends SuperGetMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(GetSuperClass.class.getName());
	private static final int BYTES_DOWNLOAD = 1024;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadIPAFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String iPAFilePath = null;
		String boDept = null;
		HttpSession session = null;
		String error = "Unable to connect to Database. Please try again later!";
		ServletOutputStream outStream = response.getOutputStream();
		try{
			session = request.getSession(false);
			iPAFilePath = request.getParameter(TableConstants.IPA_FILE_PATH);
			boDept = request.getParameter(TableConstants.BO_DEPT);
		} catch(Exception e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
		UserSessionObject userSession = isSessionValid(boDept , session);
		BuildJSON parentJSON = new BuildJSON();
		if (iPAFilePath == null) {
			parentJSON = callFailure("IPA File is missing." , response, parentJSON);
			writeFinalOP(parentJSON, outStream, boDept, error, false, 0);
		} else if (userSession != null) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			callSuccess(request, response, iPAFilePath, parentJSON, userSession, outStream, boDept);
		} else {
			parentJSON = callFailure(TableConstants.PLEASE_LOGIN_MSG , response, parentJSON);
			writeFinalOP(parentJSON, outStream, boDept, error, false, 0);
		}
		logger.exit(false);
	}
	
	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	protected void callSuccess(HttpServletRequest request, HttpServletResponse response, String iPAFilePath, BuildJSON parentJSON, UserSessionObject userSession, ServletOutputStream outStream, String boDept) {
		if(iPAFilePath == null) {
			parentJSON = callFailure("", response, parentJSON);
		} else {
			String replaceFilePath = TableConstants.ROOT_FILE_PATH_PROJECTS + iPAFilePath;
			File downloadFile = new File(replaceFilePath);
			if(!downloadFile.exists()) {
				parentJSON = callFailure("Sorry, unable to find the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
			} else {
				try {
					Connection con = MySqlDatabaseConnection.getConnection(getServletContext());
					Statement stmt = con.createStatement();
						if (userSession.isAdmin()) {
							if(downloadFile.isDirectory()) {
								File[] childFiles = downloadFile.listFiles();
								boolean isFileFound = false;
								String fileName = "";
								for (File child : childFiles) {
									if(child.getName().contains(".ipa")) {
										fileName = child.getName();
										String headerValue = String.format("attachment; filename='%s\'", fileName);
										response.setContentType("application/octet-stream .bin .exe");
										response.setHeader("Content-Transfer-Encoding", "binary");
										response.setHeader("Content-Disposition",
												headerValue);
										String downloadFilePath = downloadFile + File.separator + child.getName();
										File tragetFile = new File(downloadFilePath);
										response.setContentLength((int) tragetFile.length());
										FileInputStream is;
										try {
											is = new FileInputStream(tragetFile);
											int read = 0;
											byte[] bytes = new byte[BYTES_DOWNLOAD];
											try {
												while ((read = is.read(bytes)) != -1) {
													outStream.write(bytes, 0, read);
												}
												outStream.flush();
												outStream.close();
												is.close();
												isFileFound = true;
												break;
											} catch (IOException e) {
												parentJSON = callFailure("Sorry, Failed to download the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
												logger.error(getStackTrace(e));
												isFileFound = false;
												break;
											}
										} catch (FileNotFoundException e1) {
											parentJSON = callFailure("Sorry, Failed to download the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
											logger.error(getStackTrace(e1));
											isFileFound = false;
											break;
										}
									} 
								}
								if (isFileFound) {
									logger.info("Successful download of file: " + fileName + " by user: " + userSession.getUserID());
									return;
								}
							} else {
								parentJSON = callFailure("Sorry, Failed to download the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
							}
						} else {
							parentJSON = callFailure("You do not have administrative access to download the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
						}
				} catch (SQLException e1) {
					parentJSON = callFailure("Sorry, Failed to download the IPA file, " + iPAFilePath + " by user: " + userSession.getUserID(), response, parentJSON);
					logger.error(getStackTrace(e1));
				}
			}
			writeFinalOP(parentJSON, outStream, boDept, replaceFilePath, false, 0);
		}
	}

	
	
	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			if(parentJSON.isValid()){
				
			} else {
				out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("Failed to write the result back to user: " + ErrorUtils.getStackTrace(e));
		}
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}
	
	protected BuildJSON callFailure(String error, HttpServletResponse response, BuildJSON parentJSON) {
		try {
			ServletOutputStream out = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");
			parentJSON = callFailure(error, parentJSON);
		} catch (IOException e1) {
			logger.error(getStackTrace(e1));
		}
		return parentJSON;
	}
	
}
