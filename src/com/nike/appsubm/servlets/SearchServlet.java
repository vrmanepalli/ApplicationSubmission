package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.FieldValidator;
import com.nike.util.GetImageDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class SearchServlet
 */
public class SearchServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(SearchServlet.class.getName());
	/**
	 * Default constructor.
	 */
	public SearchServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		String userAgent = request.getHeader("User-Agent").toLowerCase();
//		isMobile = MobileCheck.isMobile(userAgent);
		super.doPost(request, response);
		logger.exit(false);
	}
	
	

	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON,
			JSONObject jsonObject) {
		String error = "Failed to perform the service.";
		HttpSession session = processSession(request);
		ServletOutputStream out = null;
		String boDept = null;
		try {
			try {
				boDept = jsonObject.getString(TableConstants.BO_DEPT);
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				parentJSON.setLocalVariableString(boDept);
			} catch (Exception e1) {
				getLogger().info("Guest user trying to access the Sub Class. BoDept Not found.");
			}
			try {
				out = response.getOutputStream();
				UserSessionObject userSession = isSessionValid(boDept, session, parentJSON);
				if (jsonObject != null && isValidCheck(parentJSON, jsonObject, userSession)) {
					Connection con = MySqlDatabaseConnection.getConnection(getServletContext());
					try {
						Statement stmt = con.createStatement();
//						if(isAdmin(userSession, stmt, parentJSON)) {
							callSuccess(request, stmt, con, parentJSON, userSession, jsonObject, out);
//						}
					} catch (SQLException e) {
						callFailure(error, e, parentJSON);
					}
				} else {
					processFailure(error, parentJSON, userSession, jsonObject);
					writeFinalOP(parentJSON, out);
				}
			} catch (IOException e) {
				callFailure(error, parentJSON);
			}
		} catch (Exception e) {
			callFailure(error, e, parentJSON);
			writeFinalOP(parentJSON, out);
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

	@Override
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject,
			UserSessionObject userSessionObject) {
		return FieldValidator.isAValidAppTitle(jsonObject.getString(TableConstants.APP_TITLE).replace(" ", "_")) && super.isValidCheck(parentJSON, jsonObject, userSessionObject);
	}

	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject, ServletOutputStream out) {
		ResultSet res = null;
		Statement localStmt = null;
		try {
			int index = 0;
			int inProgressIndex = -1;
			stmt = con.createStatement();
			localStmt = con.createStatement();
			boolean isAdmin = userSession.isAdmin();
			index = 0;
			String appTitle = null;
			parentJSON.setValid(true);
			try {
				appTitle = jsonObject.getString(TableConstants.APP_TITLE).replace(" ", "_");
			} catch (Exception e1) {
				callFailure("Failed to process request because of missing App Title.", e1, parentJSON);
			}
			String query;
			if(!isAdmin) {
				String userBoDept = getBoDept(userSession, stmt, parentJSON);
				query = createQueryForUser(jsonObject, userSession, userBoDept);
			} else {
				query = createQuery(jsonObject, userSession);
			}
			logger.info("Query for search " + query);
			boolean inProgress = false;
			res = stmt.executeQuery(query);
			parentJSON.createNewJSONArray();
			while (res.next()) {
				try {
					index++;
					inProgress = addElementsToJSON(parentJSON, res, inProgressIndex, localStmt, inProgress);
				} catch (JSONException e) {
					callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), e,parentJSON);
				}
			}
			if (parentJSON.isValid()) {
				logger.info("Successful search of application whose name is: "
						+ appTitle + " by user: " + userSession.getUserID());
			}
			writeFinalOP(parentJSON, out, inProgress, res, localStmt, isAdmin, index);
		} catch (Exception e) {
			callFailure("Failed to process your request.", e, parentJSON);
		} finally {
			if (res != null) {
				try {
					res.close();
				} catch (SQLException e) {
					callFailure("Failed to connect with database.", e, parentJSON);
				}
			}
			if(localStmt != null) {
				try {
					localStmt.close();
				} catch (SQLException e) {
					callFailure(TableConstants.DATABASE_ERROR_MSG, e, parentJSON);
				}
			}
		}
	}
	
	private String createQueryForUser(JSONObject jsonObject,
			UserSessionObject userSession, String userBoDept) {
		return "SELECT * FROM " + TableConstants.TABLE_PROJECTS
				+ " WHERE " + TableConstants.APP_TITLE + " LIKE '%"
				+ jsonObject.getString(TableConstants.APP_TITLE) + "%' AND " + TableConstants.BO_DEPT + " LIKE '%" + userBoDept + "%';";
	}

	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out, boolean inProgress, ResultSet res, Statement stmt, boolean isAdmin, int index) throws IOException {
		if(parentJSON.isValid()) {
			parentJSON.createNewJChild();
			parentJSON.addElement("InProgress", inProgress);
			parentJSON.addElement("Total", index);
			parentJSON.addElement(TableConstants.BO_DEPT, parentJSON.getLocalVariableString());
			parentJSON.addElement("Index", inProgress ? parentJSON.getLocalVariableInt() : -1);
			parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
			if (isAdmin) {
				out.write(parentJSON
						.getParentWithArray(TableConstants.RESPONSE).toString().getBytes());
			} else {
				JSONObject responseObj = parentJSON
						.getParentWithArray(TableConstants.RESPONSE);
				res = GetUserDetails.getUsers(stmt);
				if (res != null) {
					addUsers(parentJSON, res);
					out.write(parentJSON
							.getParentWithArray(TableConstants.USERS).toString().getBytes());
				} else {
					out.write(responseObj.toString().getBytes());
				}
			}
		} else {
			String error = parentJSON.getElement(TableConstants.ERROR);
			parentJSON.createNewJChild();
			parentJSON.addElement("InProgress", inProgress);
			parentJSON.addElement(TableConstants.BO_DEPT,  parentJSON.getLocalVariableString());
			parentJSON.addElement("Index", inProgress ?  parentJSON.getLocalVariableInt() : -1);
			parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
			parentJSON.addElement(TableConstants.ERROR, error);
			out.write(parentJSON
					.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
		}
		out.flush();
		out.close();
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				callFailure(TableConstants.DATABASE_ERROR_MSG, e, parentJSON);
			}
		}
	}
	
	private void addUsers(BuildJSON parentJSON, ResultSet res) {
		try {
			parentJSON.createNewJSONArray();
			while (res.next()) {
				try {
					addUsersToJSON(parentJSON, res);
				} catch (JSONException e) {
					callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), e, parentJSON);
				}
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		}
	}

	private void addUsersToJSON(BuildJSON parentJSON, ResultSet res) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.BO_DEPT,
					res.getString(TableConstants.BO_DEPT));
			parentJSON.addElement(TableConstants.FIRST_NAME,
					res.getString(TableConstants.FIRST_NAME));
			parentJSON.addElement(TableConstants.LAST_NAME,
					res.getString(TableConstants.LAST_NAME));
			parentJSON.addElement(TableConstants.USER_ID,
					res.getString(TableConstants.USER_ID));
			parentJSON.addElement(TableConstants.PHONE_NO,
					res.getString(TableConstants.PHONE_NO));
			parentJSON.addElement(TableConstants.EMAIL_ID,
					res.getString(TableConstants.EMAIL_ID));
			parentJSON.addElement(TableConstants.DEPT,
					res.getString(TableConstants.DEPT));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			callFailure(TableConstants.DATABASE_ERROR_MSG, e, parentJSON);
		}
	}

	private boolean addElementsToJSON(BuildJSON parentJSON, ResultSet res, int index, Statement localStmt, boolean inProgress) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.BO_DEPT,
					res.getString(TableConstants.BO_DEPT));
			parentJSON.addElement(TableConstants.ID,
					res.getInt(TableConstants.ID));
			parentJSON.addElement(TableConstants.TSC_NAME,
					res.getString(TableConstants.TSC_NAME));
			parentJSON.addElement(TableConstants.TSC_TITLE,
					res.getString(TableConstants.TSC_TITLE));
			parentJSON.addElement(TableConstants.TSC_DEPT,
					res.getString(TableConstants.TSC_DEPT));
			parentJSON.addElement(TableConstants.TSC_EMAIL,
					res.getString(TableConstants.TSC_EMAIL));
			parentJSON.addElement(TableConstants.TSC_PHONE,
					res.getString(TableConstants.TSC_PHONE));
			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));
			parentJSON.addElement(TableConstants.IS_SIGNING_REQ,
					res.getString(TableConstants.IS_SIGNING_REQ));
			parentJSON.addElement(TableConstants.MI_INSTANCE,
					res.getString(TableConstants.MI_INSTANCE));
			parentJSON.addElement(TableConstants.APP_TITLE,
					res.getString(TableConstants.APP_TITLE));
			parentJSON.addElement(TableConstants.APP_DESCRIPTION,
					res.getString(TableConstants.APP_DESCRIPTION));
			parentJSON.addElement(TableConstants.APP_CURRENT_VERSION,
					res.getString(TableConstants.APP_CURRENT_VERSION));
			parentJSON.addElement(TableConstants.APP_MIN_OS,
					res.getString(TableConstants.APP_MIN_OS));
			parentJSON.addElement(TableConstants.APP_SECURITY,
					res.getString(TableConstants.APP_SECURITY));
			parentJSON.addElement(TableConstants.APP_ADG_LEVEL,
					res.getString(TableConstants.APP_ADG_LEVEL));
			String imgUrl = GetImageDetails.getProjectIcon(res
					.getString(TableConstants.APP_TITLE));
			if (imgUrl == null) {
				imgUrl = TableConstants.ROOT_URL
						+ TableConstants.PROJECT_URL_PART
						+ TableConstants.IMAGE_PART
						+ TableConstants.NO_IMAGE_ICON;
			}
			parentJSON.addListOfElements(TableConstants.LIST_OF_IMAGES,
					GetImageDetails.getProjectImages(res
							.getString(TableConstants.APP_TITLE)));
			parentJSON.addElement(TableConstants.PROJECT_ICON, imgUrl);
			parentJSON.addElement(TableConstants.APP_DEVICES,
					res.getString(TableConstants.APP_DEVICES));

			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));
			
			parentJSON.addElement(TableConstants.PUSH_PROFILE_PATH,
					res.getString(TableConstants.PUSH_PROFILE_PATH));
			
			String result = res.getString(TableConstants.RESULT);
			// result = "<p  class='checkmark'>" + result != null ?
			// res.getString(TableConstants.RESULT).replace(",",
			// "</p><p  class='checkmark'>") : result + "</p>";
			// result.replace("<p  class='checkmark'> Failed",
			// "<p  class='cross'> Failed");
			parentJSON.addElement(TableConstants.RESULT, result);

			parentJSON.addElement(TableConstants.DATE,
					res.getString(TableConstants.DATE));
			if (res.getString(TableConstants.CURRENT_STATUS).equalsIgnoreCase(
					TableConstants.IN_PROGRESS)) {
				parentJSON.setLocalVariableInt(index);
				inProgress = true;
			}
			parentJSON.addElement(TableConstants.CURRENT_STATUS,
					res.getString(TableConstants.CURRENT_STATUS));
			parentJSON.addElement(TableConstants.REQUEST_COUNT, UpdateRequestCount.getAppMaxReqCount(localStmt, res.getString(TableConstants.APP_TITLE)));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		}
		return inProgress;
	}

	protected String createQuery(JSONObject jsonObject,
			UserSessionObject userSession) {
		String query;
		query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS + " WHERE "
				+ TableConstants.APP_TITLE + " LIKE '%"
				+ jsonObject.getString(TableConstants.APP_TITLE) + "%';";
		return query;
	}
	
	
}
