package com.nike.appsubm.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.nike.parser.JsonFlattener;
import com.nike.util.BuildJSON;
import com.nike.util.EmailNotification;
import com.nike.util.EmailNotificationWithAttachment;
import com.nike.util.GetImageDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.HttpsClient;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;
import com.nike.writer.CSVWriter;

/**
 * Servlet implementation class GetAppDownloadCountServlet
 */
@WebServlet("/Private/GetAppDownloadCountServlet")
public class GetAppDownloadCountServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(GetAppDownloadCountServlet.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAppDownloadCountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	protected void callSuccess(HttpServletRequest request, Statement stmt,
			Connection con, final BuildJSON parentJSON,
			final UserSessionObject userSession, JSONObject jsonObject) {
		parentJSON.setValid(false);
		String appTitle = null, type = null;
		try {
			appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		} catch (Exception e) {
			callFailure("Cannot process this request with null values in Apptitle", e, parentJSON);
			return;
		}
		String limit = null;
		try {
			limit = jsonObject.getString(TableConstants.LIMIT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String offset = null;
		try {
			offset = jsonObject.getString(TableConstants.OFFSET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			type = jsonObject.getString(TableConstants.TYPE);
		} catch (Exception e) {
			callFailure("Cannot process the request with null values " + TableConstants.TYPE + ".", e, parentJSON);
			return;
		}
		boolean changeAppTitle = true;
		try {
			changeAppTitle  = jsonObject.getBoolean("ChangeAppTitle");
		} catch (Exception e) {
			callFailure("Cannot process the request with null values of ChangeAppTitle.", e, parentJSON);
			return;
		}
		String miInstance = null;
		try {
			miInstance  = jsonObject.getString(TableConstants.MI_INSTANCE);
		} catch (Exception e) {
			callFailure("Cannot process the request with null values of ChangeAppTitle.", e, parentJSON);
			return;
		}
		if(appTitle != null) {
			final String finalAppTitle = appTitle;
			try {
				stmt = con.createStatement();
				ResultSet result = stmt.executeQuery(createQuery(jsonObject));
				String miAppTitle = null;
				if (changeAppTitle) {
					if (result != null
							&& result.next()
							&& result.getString(TableConstants.MI_APP_TITLE) != null) {
						miAppTitle = result
								.getString(TableConstants.MI_APP_TITLE);
					}
					if (miAppTitle != null) {
						appTitle = miAppTitle.replace("_", " ");
					}
				} else {
					appTitle = appTitle.replace("_", " ");
				}
				HttpsClient client = new HttpsClient();
				String https_url_string;
				if (miInstance != null && miInstance.equalsIgnoreCase("Retail Instance")) {
					https_url_string = TableConstants.RETAIL_MOBILE_IRON_APP_INVENTORY
							+ appTitle + "&limit=" + limit + "&offset="
							+ offset;
				} else {
					https_url_string = TableConstants.MOBILE_IRON_APP_INVENTORY
							+ appTitle + "&limit=" + limit + "&offset="
							+ offset;
				}
				String jsonResponse = client.getContent(https_url_string);
				if(jsonResponse != null && jsonResponse != "") {
					final JSONObject jsonResponseObject = JSONObject
							.fromObject(jsonResponse);
					if (type.equalsIgnoreCase("VIEW")) {
						parentJSON.addJSONObject(TableConstants.MESSAGE,
								jsonResponseObject);
						parentJSON.addElement(TableConstants.TYPE, "VIEW");
					} else if(type.equalsIgnoreCase("EMAIL")) {
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								File csvFolder = new File(
										TableConstants.CSV_FILE_PATH);
								if (!csvFolder.exists()
										&& !csvFolder.isDirectory()) {
									csvFolder.mkdirs();
								}
								File csvFile = new File(
										TableConstants.CSV_FILE_PATH + "/"
												+ finalAppTitle + ".csv");
								if (!csvFile.exists()) {
									csvFile.delete();
								}
								JSONObject messages = jsonResponseObject
										.getJSONObject(TableConstants.MESSAGES);
								String message = messages.getString("message");
								if (message != null
										&& !message
												.equalsIgnoreCase("No devices found.")) {
									JSONArray arrayJson = jsonResponseObject
											.getJSONObject("devices")
											.getJSONArray("device");
									JsonFlattener parser = new JsonFlattener();
									CSVWriter writer = new CSVWriter();
									List<Map<String, String>> flatJson = parser
											.parse(arrayJson);
									try {
										writer.writeAsCSV(flatJson,
												csvFile.getPath());
										new EmailNotificationWithAttachment(
												userSession.getEmailID(),
												finalAppTitle
														+ ": Number Of Downloads CSV attachment.",
												"Please find the enclosed CSV file.",
												"", csvFile.getPath()).start();
										getLogger()
												.info("Successfully completion of the CSV file creation and Email.");
									} catch (FileNotFoundException e) {
										callFailure(
												"Unable to create csv file", e,
												parentJSON);
									} catch (Exception e) {
										callFailure(
												"Unable to send csv file email",
												e, parentJSON);
									}
								} else if (message != null
										&& message
												.equalsIgnoreCase("No devices found.")) {
									try {
										new EmailNotificationWithAttachment(
												userSession.getEmailID(),
												finalAppTitle
														+ ": Number Of Downloads CSV attachment.",
														"No devices found.",
												"", null).start();
									} catch (Exception e) {
										callFailure(
												"Unable to send csv file email",
												e, parentJSON);
									}
								}
							}
						});
						thread.start();
						parentJSON.addElement(TableConstants.MESSAGE,
								"You will shortly receive an email with attachment or details about the number of downloads. Please don't click on Email button.");
						parentJSON.addElement(TableConstants.TYPE, "EMAIL");
					}
				
				} else {
					parentJSON.addElement(TableConstants.ERROR, "Failed to call Mobile Iron Web Service.");
					parentJSON.addElement(TableConstants.TYPE, "ERROR");
				}
			} catch (SQLException e) {
				callFailure("Cannot process this request with invalid values in Apptitle", e, parentJSON);
			}
		}
	}
	
	

	@Override
	protected boolean isAdmin(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON) {
		return true;
	}

	@Override
	protected String createQuery(JSONObject jsonObject) {
		String appTitle = jsonObject.getString(TableConstants.APP_TITLE);
		return "SELECT " + TableConstants.MI_APP_TITLE + " FROM " + TableConstants.TABLE_PROJECTS + " WHERE " + TableConstants.APP_TITLE + "='" + appTitle + "';";
	}
	
	
}
