package com.nike.appsubm.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class BundleIDUpdateServlet
 */
@WebServlet("/Private/BundleIDUpdateServlet")
public class BundleIDUpdateServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(BundleIDUpdateServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BundleIDUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	@Override
	protected Logger getLogger() {
		return this.logger;
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.entry();
		HttpSession session = processSession(request);
		String boDept = request.getParameter(TableConstants.BO_DEPT);
		ServletOutputStream out = response.getOutputStream();
		String projectsFolder = "Z:/MobileApps/Test/Projects/";
		BuildJSON parentJSON = new BuildJSON();
		ArrayList<String> addedBundleIDs = new ArrayList<String>();
		ArrayList<String> duplicateBundleIDs = new ArrayList<String>();
		
		parentJSON.setValid(false);
		UserSessionObject userSession = new UserSessionObject(null, null, 0, null);
		if(userSession != null) {
			File folder = new File(projectsFolder);
			if(folder.exists() && folder.isDirectory()) {
				File[] files = folder.listFiles();
				Connection connection = MySqlDatabaseConnection.getConnection(getServletContext());
				String appTitle = null;
				String line = null;
				try {
					Statement stmt = connection.createStatement();
					for(File file:files) {
						if(file.isDirectory()) {
							appTitle = file.getName();
							File newFile = new File(file.getAbsolutePath() + "/New");
							if(newFile.exists() && newFile.isDirectory()) {
								File entitlementsFile = new File(newFile.getAbsolutePath() + "/Entitlements.xml");
								if (entitlementsFile.exists()) {
									FileReader reader = new FileReader(
											entitlementsFile);
									BufferedReader bReader = new BufferedReader(
											reader);
									line = bReader.readLine();
									while (line != null) {
										line = line.trim();
										if (line.equals("<key>application-identifier</key>")) {
											line = bReader.readLine();
											line = line.replace("<string>", "");
											line = line
													.replace("</string>", "");
											line = line.replace("L52544R8JN.",
													"");
											if (!addedBundleIDs.contains(line)) {
												String query = "UPDATE "
														+ TableConstants.TABLE_PROJECTS
														+ " SET "
														+ TableConstants.APP_BUNDLE_ID
														+ "='"
														+ line
														+ "' WHERE "
														+ TableConstants.APP_TITLE
														+ "='" + appTitle
														+ "';";
												int isUpdateSuccessful = stmt
														.executeUpdate(query);
												if (isUpdateSuccessful <= 0) {
													getLogger().info(
															"Unable to update "
																	+ appTitle
																	+ ": "
																	+ line);
												} else {
													addedBundleIDs.add(line);
													getLogger()
															.info(isUpdateSuccessful
																	+ " Successful update "
																	+ appTitle
																	+ ": "
																	+ line);
												}
											} else {
												getLogger().info(
														"Duplicate update "
																+ appTitle
																+ ": " + line);
												duplicateBundleIDs.add(line);
											}
										} else {
											line = bReader.readLine();
										}
									}
								}
							}
						}
					}
					parentJSON.addElement(TableConstants.BO_DEPT, boDept);
					parentJSON.addElement(TableConstants.MESSAGE, "Successfully updated the Bundle IDs.");
				} catch (SQLException e) {
					callFailure("Failed to access the database for app " + appTitle + " to update line " + line, e, parentJSON);
				}
			}
		} else {
			callFailure("Invalid user. Please login!", parentJSON);
		}
		writeFinalOP(parentJSON, out);
		logger.exit(false);
	}

}
