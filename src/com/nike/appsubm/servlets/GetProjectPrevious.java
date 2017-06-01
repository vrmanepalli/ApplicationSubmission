package com.nike.appsubm.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.nike.util.ErrorUtils;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetProjectPrevious
 */
@WebServlet("/Private/GetProjectPrevious")
public class GetProjectPrevious extends GetProjectTodayServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager
			.getLogger(GetProjectPrevious.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProjectPrevious() {
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
	protected Logger getLogger() {
		return GetProjectTodayServlet.logger;
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			String appTitle, String userID, String boDept,
			BuildJSON parentJson, String genericProfileDate) {
		String query = "";
		if (userSession.isAdmin()) {
			getLogger()
					.info(userID + ": Admin's request for his/her Projects.");
			if (parentJson.getFilterUserID() != null
					&& !parentJson.getFilterUserID().equals("All Users")
					&& !parentJson.getFilterUserID().equals("All")) {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ "<'" + parentJson.getCurrentStatus() + "' AND " + TableConstants.BO_DEPT + " LIKE '%"
								+ parentJson.getFilterUserID() + "%' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			} else {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ "<'" + parentJson.getCurrentStatus() + "' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			}
		} else {
			getLogger().info(
					userID + ": Normal User's request for his/her Projects.");
			String parameter = "' AND " + TableConstants.DATE + "<'"
					+ parentJson.getCurrentStatus() + "' AND "
					+ TableConstants.APP_TITLE + "='" + appTitle
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
					+ TableConstants.REMOVE + "' OR "
					+ TableConstants.CURRENT_STATUS + "='"
					+ TableConstants.REMOVE + filterString;
		}
		return query;
	}
}
