/*
 * GetSpecificUserProjects servlet provides the Project details of a user that was owned i.e the details of Project name, Technical details, image urls and so on.
 * supports get call only.
 * ->callFailure(String error):	returns none. Creates a JSON response with failure details.
 * ->callSuccess():	returns none. Runs the query and generates a JSON response out of the query result. 
 * 					Creates JSON response with all the project details.
 * ->writeFinalOP(): returns none. Prints the JSON response to the output stream.
 * ->createQuery(): returns String. Creates a query string for selecting the rows of all the projects of a particular user.
 * ->addElementsToJSON(): return none. Constructing the JSON response with the particular project of a specific user.
 * ->reset(HttpServletRequest request): Reset the variables to default values.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.FieldValidator;
import com.nike.util.TableConstants;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UpdateJobResult
 */
public class GetSpecificUserProjects extends SuperGetMethodsServlet {
	static Logger logger =  LogManager.getLogger(GetSpecificUserProjects.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetSpecificUserProjects() {
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
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	@Override
	protected boolean isValidCheck(BuildJSON parentJSON,
			HttpServletRequest request, UserSessionObject userSessionObject) {
		String appTitle = request.getParameter(TableConstants.APP_TITLE);
		parentJSON.setLocalVariableString(appTitle);
		return FieldValidator.isAValidAppTitle(appTitle);
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		parentJSON.createNewJSONArray();
		try {
			int index = -1;
			while (res.next()) {
				try {
					index++;
					addElementsToJSON(parentJSON, res, false, localStmt);
				} catch (JSONException e) {
					parentJSON = callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), e, parentJSON);
				}
			}
			parentJSON.setLocalVariableInt(index);
			logger.info("Successful retreival of the projects by user: " + userSession.getUserID());
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJSON, String userID) {
		String parameter = parentJSON.getLocalVariableString() != null ? "' AND "
				+ TableConstants.APP_TITLE + "='" + parentJSON.getLocalVariableString() + "';": "';";
		String filterString;
		if(userSession.isAdmin()) {
			filterString = parameter;
		} else {
			filterString = "' AND " + TableConstants.BO_DEPT + " LIKE '%" + userSession.getUniqueID() + "%" + parameter;
		}
		
		String query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS + " WHERE " + TableConstants.CURRENT_STATUS + "!='"+TableConstants.REMOVE + 
				filterString;
		return query;
	}

	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res, boolean inProgress, Statement localStmt) {
		parentJSON.createNewJChild();
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
			parentJSON.addElement(TableConstants.APP_MIN_OS,
					res.getString(TableConstants.APP_MIN_OS));
			parentJSON.addElement(TableConstants.APP_SECURITY,
					res.getString(TableConstants.APP_SECURITY));
			parentJSON.addElement(TableConstants.APP_ADG_LEVEL,
					res.getString(TableConstants.APP_ADG_LEVEL));

			parentJSON.addElement(TableConstants.APP_DEVICES,
					res.getString(TableConstants.APP_DEVICES));

			parentJSON.addElement(TableConstants.APP_DEVICES,
					res.getString(TableConstants.APP_DEVICES));

			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));
			
			parentJSON.addElement(TableConstants.PUSH_PROFILE_PATH,
					res.getString(TableConstants.PUSH_PROFILE_PATH));
			
			String result = res.getString(TableConstants.RESULT); 
			parentJSON.addElement(TableConstants.RESULT,
					 result );
			
			parentJSON.addElement(TableConstants.DATE,
					res.getString(TableConstants.DATE));
			log(res.getString(TableConstants.CURRENT_STATUS));
			if (!res.getString(TableConstants.CURRENT_STATUS).equalsIgnoreCase(
					TableConstants.SUCCESS) && !res.getString(TableConstants.CURRENT_STATUS).equalsIgnoreCase(
							TableConstants.FAILED)) {
				parentJSON.setLocalVariableBoolean(true);
				parentJSON.addElement(TableConstants.CURRENT_STATUS,
						res.getString(TableConstants.CURRENT_STATUS));
			}
			parentJSON.addElement(TableConstants.REQUEST_COUNT, UpdateRequestCount.getAppMaxReqCount(localStmt, res.getString(TableConstants.APP_TITLE)));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			parentJSON = callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		parentJSON.createNewJChild();
		parentJSON.addElement("InProgress", parentJSON.isLocalVariableBoolean());
		parentJSON.addElement("Index", parentJSON.getLocalVariableInt());
		parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
		super.writeFinalOP(parentJSON, out, result, result, inProgress, inProgressIndex);
	}
}
