package com.nike.appsubm.servlets;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetProjectsSuccessServlet
 */
@WebServlet("/Private/GetProjectsSuccessServlet")
public class GetProjectsSuccessServlet extends GetUserProjects {
	static Logger logger = LogManager
			.getLogger(GetProjectsSuccessServlet.class.getName());
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Default constructor. 
     */
    public GetProjectsSuccessServlet() {
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error, HttpServletRequest request,
			HttpSession session, ServletOutputStream out, String result,
			boolean inProgress, int inProgressIndex) throws IOException {
		String currentStatus = "All";
    	try {
    		currentStatus = request.getParameter(TableConstants.CURRENT_STATUS);
		} catch (Exception e1) {
			currentStatus = "All";
			getLogger().info("The user have not provided the current page." + ErrorUtils.getStackTrace(e1));
		}
    	parentJSON.setCurrentStatus(currentStatus);
		super.processRequest(userSession, boDept, parentJSON, error, request, session,
				out, result, inProgress, inProgressIndex);
	}



	@Override
	protected Logger getLogger() {
		return GetProjectsSuccessServlet.logger;
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			String appTitle, String userID, String boDept, BuildJSON parentJson, String genericProfileDate) {
		String query = "";
		if (parentJson.getCurrentStatus().equals("All")) {
			return super.createQuery(userSession, appTitle, userID, boDept,
					parentJson, genericProfileDate);
		} else {
			if (userSession.isAdmin()) {
				getLogger().info(userID + ": Admin's request for his/her Projects.");
				if (parentJson.getFilterUserID() != null
						&& !parentJson.getFilterUserID().equals("All Users")
						&& !parentJson.getFilterUserID().equals("All")) {
					query = "SELECT * FROM Projects WHERE "
							+ TableConstants.CURRENT_STATUS + "='"
							+ parentJson.getCurrentStatus() + "' AND " + TableConstants.BO_DEPT + " LIKE '%"
									+ parentJson.getFilterUserID() + "%' ORDER BY "
							+ TableConstants.DATE + " DESC;";
				} else {
					query = "SELECT * FROM Projects WHERE "
							+ TableConstants.CURRENT_STATUS + "='"
							+ parentJson.getCurrentStatus() + "' ORDER BY "
							+ TableConstants.DATE + " DESC;";
				}
			} else {
				getLogger().info(userID
						+ ": Normal User's request for his/her Projects.");
				String parameter = "' AND "
						+ TableConstants.CURRENT_STATUS + "='"
						+ parentJson.getCurrentStatus() + "' AND "
						+ TableConstants.APP_TITLE + "='" + appTitle
						+ "' ORDER BY " + TableConstants.DATE + " DESC;";
				String filterString;
				if (userSession.isAdmin()) {
					filterString = parameter;
				} else {
					filterString = "' AND " + TableConstants.BO_DEPT
							+ " LIKE '%" + boDept + "%" + parameter;
				}
				query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
						+ " WHERE " + getFinalQueryParameter(filterString);
			}
		}
		return query;
	}
	
	protected String getFinalQueryParameter(String parameter) {
		return TableConstants.CURRENT_STATUS + "!='"
				+ TableConstants.REMOVE + parameter;
	}

}
