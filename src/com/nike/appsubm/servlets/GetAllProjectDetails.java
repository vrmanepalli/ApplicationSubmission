/*
 * GetAllProjectDetails servlet is extended from GetSuperClass which has ovrriden the doGet call.
 * Only supports get call
 * Returns a JSON response with all the project details
 * 	->	callFailure(String error): Returns none. This methods gets called whenever there is an exception or failure in the servlet. 
 * 									Created failure JSON response.
 * 	->	callSuccess():	Retruns none. This method is called by doGet, in order to do run a query and create JSON response using query response.
 * 						The response is nothing but Projects.
 * 	->	addUserProjects(): Returns none. This method is used to iterate through the result set and create JSON Array with project details of a user as objects.
 * 							Used by addUserProjects method.
 * ->	addProjectsToJSON(): returns none. This method creates JSON object using the result set to fill the details of the Project in each JSON obj.
 * 	->	createQuery():	returns a query String. The query is a select statement against Projects table.
 * 	->	addElementsToJSON():	returns none. This method create a JSON array with the each Project status. Called by callSuccess
 * 	->	writeFinalOP():	returns none. Wirte the JSON Response to the output stream. 
 * 						Based on type of response created (JSON Obj or JSON array) it decide which methods to call.
 * 	->	reset(HttpServletRequest request): returns none. Resets all the variables to default values.
 * 
 * Some of the methods are called by the default behavior of the Super class methods.
 */

package com.nike.appsubm.servlets;

import java.io.IOException;
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
import com.nike.util.GetImageDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.TableConstants;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetAllProjectDetails
 */
public class GetAllProjectDetails extends SuperGetMethodsServlet {
	static Logger logger =  LogManager.getLogger(GetAllProjectDetails.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetAllProjectDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		super.doGet(request, response);
		logger.exit(false);
	}
	
	

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		logger.error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		return parentJSON;
	}
	
	
	
	@Override
	protected ResultSet getResultSet(HttpServletRequest request,
			UserSessionObject userSession, Statement stmt, BuildJSON parentJSON)
			throws SQLException {
		String boDeptString = getBoDept(userSession, stmt, parentJSON);
		parentJSON.setLocalVariableString(boDeptString);
		return stmt.executeQuery(createQuery(userSession, boDeptString));
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON, ResultSet res, UserSessionObject userSession, String boDept, Statement stmt, Statement localStmt, HttpServletRequest request, ServletOutputStream out) {
		parentJSON.createNewJSONArray();
		boolean inProgress = false;
		int index = -1;
		int inProgressIndex = -1;
		String result = "";
		try {
			while (res.next()) {
				try {
					index++;
					parentJSON = addElementsToJSON(parentJSON, res, userSession, inProgress, result, result, result);
				} catch (JSONException e) {
					parentJSON = callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), e, parentJSON);
				}
			}
			parentJSON = addUserProjects(userSession, stmt, localStmt, parentJSON);
			logger.info("Successful retreival of the projects by user: " + userSession.getUserID());
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	protected BuildJSON addUserProjects(UserSessionObject userSession, Statement stmt, Statement localStmt, BuildJSON parentJSON) {
		try {
			ResultSet resultSetLocal = GetUserDetails.getUserProjectsWithRange(stmt, parentJSON.getLocalVariableString(), 0, 30, userSession.isAdmin());
			parentJSON.getParentWithArray(TableConstants.RESPONSE);
			parentJSON.createNewJSONArray();
			while (resultSetLocal != null && resultSetLocal.next()) {
				try {
					parentJSON = addProjectsToJSON(parentJSON, resultSetLocal, localStmt);
				} catch (JSONException e) {
					parentJSON = callFailure("Unable to connect to Database. Please try again later!", e, parentJSON);
				}
			}
			if(resultSetLocal != null) {
				resultSetLocal.close();
			}
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}

	protected BuildJSON addProjectsToJSON(BuildJSON parentJSON, ResultSet res, Statement localStmt) {
		parentJSON = createNewArrayObject(parentJSON);
		try {
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

			parentJSON.addElement(TableConstants.APP_TITLE,
					res.getString(TableConstants.APP_TITLE));
			parentJSON.addElement(TableConstants.APP_DESCRIPTION,
					res.getString(TableConstants.APP_DESCRIPTION));
			parentJSON.addElement(TableConstants.APP_CURRENT_VERSION,
					res.getString(TableConstants.APP_CURRENT_VERSION));
			parentJSON.addElement(TableConstants.APP_OLD_VERSION,
					res.getString(TableConstants.APP_OLD_VERSION));
			parentJSON.addElement(TableConstants.APP_MIN_OS,
					res.getString(TableConstants.APP_MIN_OS));
			parentJSON.addElement(TableConstants.APP_SECURITY,
					res.getString(TableConstants.APP_SECURITY));
			parentJSON.addElement(TableConstants.APP_ADG_LEVEL,
					res.getString(TableConstants.APP_ADG_LEVEL));

			parentJSON.addElement(TableConstants.APP_DEVICES,
					res.getString(TableConstants.APP_DEVICES));

			parentJSON.addElement(TableConstants.CURRENT_STATUS,
					res.getString(TableConstants.CURRENT_STATUS));
			
			parentJSON.addElement(TableConstants.DATE,
					res.getString(TableConstants.DATE));
			String imgUrl = GetImageDetails.getProjectIcon(res.getString(TableConstants.APP_TITLE));
			if(imgUrl == null) {
				imgUrl = TableConstants.ROOT_URL + TableConstants.PROJECT_URL_PART + TableConstants.IMAGE_PART + TableConstants.NO_IMAGE_ICON;
			}
			parentJSON.addElement(TableConstants.PROJECT_ICON,
					imgUrl);
			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));
			parentJSON.addElement(TableConstants.IS_SIGNING_REQ,
					res.getString(TableConstants.IS_SIGNING_REQ));
			parentJSON.addElement(TableConstants.MI_INSTANCE,
					res.getString(TableConstants.MI_INSTANCE));
			parentJSON.addElement(TableConstants.PUSH_PROFILE_PATH,
					res.getString(TableConstants.PUSH_PROFILE_PATH));
			parentJSON.addElement(TableConstants.REQUEST_COUNT, UpdateRequestCount.getAppMaxReqCount(localStmt, res.getString(TableConstants.APP_TITLE)));
			parentJSON = addObjectToArray(parentJSON);
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}
	
	protected BuildJSON addObjectToArray(BuildJSON parentJSON) {
		parentJSON.addJObjectToJArray();
		return parentJSON;
	}
	
	protected BuildJSON createNewArrayObject(BuildJSON parentJSON) {
		parentJSON.createNewJChild();
		return parentJSON;
	}

	protected String createQuery(UserSessionObject userSession, String boDeptString) {
		String query;
		String parameter = "";
		String filterString;
		if(userSession.isAdmin()) {
			filterString = parameter;
		} else {
			filterString = " WHERE " + TableConstants.BO_DEPT + " LIKE '%" + boDeptString + "%'";
		}
		query = "SELECT " + getTheAttributes() + " FROM "+ TableConstants.TABLE_PROJECTS + filterString + " ORDER BY " 
							+ getOrder();
		return query;
	}
	
	protected String getOrder() {
		return TableConstants.APP_TITLE + " ASC;";
	}

	protected String getTheAttributes() {
		return TableConstants.APP_TITLE + ", "
				+ TableConstants.BO_DEPT + ", " + TableConstants.RESULT + ", " + TableConstants.DATE + ", "
				+ TableConstants.CURRENT_STATUS;
	}

	@Override
	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res, UserSessionObject userSession, boolean inProgress, Object inProgressIndex, String result, Object index) {
		parentJSON = createNewArrayObject(parentJSON);
		try {
			parentJSON.addElement(TableConstants.APP_TITLE,
					res.getString(TableConstants.APP_TITLE));

			String status = res.getString(TableConstants.CURRENT_STATUS);
			if (status.equalsIgnoreCase(TableConstants.IN_PROGRESS)
					&& userSession.getUniqueID().equalsIgnoreCase(res
							.getString(TableConstants.BO_DEPT))) {
				inProgress = true;
				inProgressIndex = index;
				result = res.getString(TableConstants.RESULT);
			}
			parentJSON.addElement(TableConstants.CURRENT_STATUS, status);
			parentJSON = addObjectToArray(parentJSON);
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out, String boDept, String result, boolean inProgress, int inProgressIndex) {
		if (parentJSON.isValid()) {
			parentJSON.createNewJChild();
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			parentJSON.addElement("InProgress", inProgress);
			parentJSON.addElement(TableConstants.RESULT, result);
			parentJSON.addElement("Index", inProgress ? inProgressIndex : -1);
			parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
			try {
				out.write(parentJSON
						.getParentWithArray(TableConstants.USER_PROJECTS).toString().getBytes());
			} catch (IOException e) {
				parentJSON = callFailure("Failed to write the output to client.", e, parentJSON);
			}
			
		} else {
			JSONObject currentStatus = new JSONObject();
			currentStatus.put("InProgress", inProgress);
			currentStatus.put(TableConstants.RESULT, result);
			currentStatus.put("Index", inProgress ? inProgressIndex : -1);
			JSONObject output = new JSONObject();
			output = parentJSON.getParentWithChild(TableConstants.RESPONSE);
			output.put(TableConstants.CURRENT_STATUS, currentStatus);
			try {
				out.write(output.toString().getBytes());
			} catch (IOException e) {
				parentJSON = callFailure("Failed to write the output to client.", e, parentJSON);
			}
		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			parentJSON = callFailure("Failed to close output stream.", e, parentJSON);
		}
	}

}
