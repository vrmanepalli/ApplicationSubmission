package com.nike.appsubm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetProjectsRenewedServlet
 */
@WebServlet("/Private/GetProjectsRenewedServlet")
public class GetProjectsRenewedServlet extends GetProjectsExpiresServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProjectsRenewedServlet() {
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
	protected boolean checkValidityOfProject(String projectDate,
			String genericProfileDate, String status) {
		boolean isStatusCorrect = (status.equalsIgnoreCase("Complete") || status.equalsIgnoreCase("Remove"));
		return Dates.getNumberOfDaysDiffBtwTwoDates(projectDate, genericProfileDate) <= 0 ? true : false && isStatusCorrect;
	}
	
	@Override
	protected Logger getLogger() {
		return GetProjectsRenewedServlet.logger;
	}
	
	@Override
	protected String createQuery(UserSessionObject userSession,
			String appTitle, String userID, String boDept,
			BuildJSON parentJson, String genericProfileDate) {
		String query = "";
		if (userSession.isAdmin()) {
			logger.info(userID + ": Admin's request for his/her Projects.");
			if (parentJson.getFilterUserID() != null
					&& !parentJson.getFilterUserID().equals("All Users")
					&& !parentJson.getFilterUserID().equals("All")) {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ ">'" + genericProfileDate + "' AND "
						+ TableConstants.BO_DEPT + " LIKE '%"
						+ parentJson.getFilterUserID() + "%' OR "
						+ TableConstants.DATE + "='" + genericProfileDate
						+ "' ORDER BY " + TableConstants.DATE + " DESC;";
			} else {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ ">'" + genericProfileDate + "' OR " + TableConstants.DATE + "='" + genericProfileDate + "' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			}
		} else {
			logger.info(userID
					+ ": Normal User's request for his/her Projects.");
			String parameter = appTitle != null ? "' AND "
					+ TableConstants.DATE + "<'" + genericProfileDate
					+ "' AND " + TableConstants.APP_TITLE + "='" + appTitle
					+ "' ORDER BY " + TableConstants.DATE + " DESC;" : "' AND "
					+ TableConstants.DATE + ">'" + genericProfileDate
					 + "' OR " + TableConstants.DATE + "='" + genericProfileDate + "' ORDER BY " + TableConstants.DATE + " DESC;";
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
		return query;
	}

}
