package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetProjectsExpiresServlet
 */
@WebServlet("/Private/GetProjectsExpiresServlet")
public class GetProjectsExpiresServlet extends GetUserProjects {
	static Logger logger =  LogManager.getLogger(GetProjectsExpiresServlet.class.getName());
	private static final long serialVersionUID = 1L;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProjectsExpiresServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected String callSuccess(BuildJSON parentJSON, Statement localStmt) {
		String genericProfileDate;
		genericProfileDate = PushProfileDetails.getCreationDate(localStmt, TableConstants.GENERIC_NIKE);
		if(genericProfileDate == null) {
			parentJSON = callFailure("Unable to process your request. Please try again after sometime.", parentJSON);
		}
		return genericProfileDate;
	}
	
	@Override
	protected BuildJSON callAddElementsToJSON(UserSessionObject userSession,
			BuildJSON parentJSON, String error, ResultSet res,
			boolean inProgress, int inProgressIndex, Statement localStmt,
			int index, String genericProfileDate) throws SQLException {
		if(checkValidityOfProject(res.getString(TableConstants.DATE), genericProfileDate, res.getString(TableConstants.CURRENT_STATUS))){
			if (userSession.isAdmin()
					|| res.getString(TableConstants.BO_DEPT)
							.equalsIgnoreCase(userSession.getUniqueID())) {
				index++;
				int count = parentJSON.getLocalVariableInt();
				parentJSON.setLocalVariableInt(++count);
			}
			parentJSON = super.addElementsToJSON(parentJSON, error, res, inProgress, inProgressIndex, localStmt, index);
		}
		return parentJSON;
	}
	

	@Override
	protected Logger getLogger() {
		return GetProjectsExpiresServlet.logger;
	}


	protected boolean checkValidityOfProject(String projectDate,
			String genericProfileDate, String status) {
		return Dates.getNumberOfDaysDiffBtwTwoDates(projectDate,
				genericProfileDate) > 0 ? true : false;
	}
	
	@Override
	protected String createQuery(UserSessionObject userSession,
			String appTitle, String userID, String boDept,
			BuildJSON parentJson, String genericProfileDate) {
		String query = "";
		if (userSession.isAdmin()) {
			logger.info(userID + ": Admin's request for his/her Projects.");
			String condition = " AND (" + TableConstants.IS_SIGNING_REQ + "='true' OR " + TableConstants.IS_SIGNING_REQ + "=1) AND " + TableConstants.IS_PUSH + "=0 ";
			if (parentJson.getFilterUserID() != null
					&& !parentJson.getFilterUserID().equals("All Users")
					&& !parentJson.getFilterUserID().equals("All")) {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ "<'" + genericProfileDate +  "' AND " + TableConstants.BO_DEPT + " LIKE '%"
								+ parentJson.getFilterUserID() + "%'" + condition + "ORDER BY "
						+ TableConstants.DATE + " DESC;";
			} else {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ "<'" + genericProfileDate + "'" + condition + "ORDER BY "
						+ TableConstants.DATE + " DESC;";
			}
		} else {
			logger.info(userID
					+ ": Normal User's request for his/her Projects.");
			String parameter = appTitle != null ? "' AND "
					+ TableConstants.DATE + "<'" + genericProfileDate
					+ "' AND " + TableConstants.APP_TITLE + "='" + appTitle
					+ "' ORDER BY " + TableConstants.DATE + " DESC;" : "' AND "
					+ TableConstants.DATE + "<'" + genericProfileDate
					+ "' ORDER BY " + TableConstants.DATE + " DESC;";
			String filterString;
			if (userSession.isAdmin()) {
				filterString = parameter;
			} else {
				filterString = "' AND " + TableConstants.BO_DEPT + " LIKE '%"
						+ boDept + "%" + parameter;
			}
			query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
					+ " WHERE " + TableConstants.CURRENT_STATUS + "!='"
					+ TableConstants.REMOVE + filterString;
		}
		logger.info(query);
		return query;
	}

}
