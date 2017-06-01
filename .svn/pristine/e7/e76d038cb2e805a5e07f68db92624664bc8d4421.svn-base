package com.nike.appsubm.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
 * Servlet implementation class GetProjectTodayServlet
 */
@WebServlet("/Private/GetProjectTodayServlet")
public class GetProjectTodayServlet extends GetUserProjects {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager
			.getLogger(GetProjectTodayServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProjectTodayServlet() {
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
	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error, HttpServletRequest request,
			HttpSession session, ServletOutputStream out, String result,
			boolean inProgress, int inProgressIndex) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String currentStatus = dateFormat.format(date);
    	try {
    		currentStatus = request.getParameter(TableConstants.DATE);
		} catch (Exception e1) {
			currentStatus = dateFormat.format(date);
			getLogger().info("The user have not provided the current page." + ErrorUtils.getStackTrace(e1));
		}
    	parentJSON.setCurrentStatus(currentStatus);
		super.processRequest(userSession, boDept, parentJSON, error, request, session,
				out, result, inProgress, inProgressIndex);
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
						+ "='" + parentJson.getCurrentStatus() + "' AND " + TableConstants.BO_DEPT + " LIKE '%"
								+ parentJson.getFilterUserID() + "%' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			}  else {
				query = "SELECT * FROM Projects WHERE " + TableConstants.DATE
						+ "='" + parentJson.getCurrentStatus() + "' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			}
		} else {
			getLogger().info(
					userID + ": Normal User's request for his/her Projects.");
			String parameter = "' AND " + TableConstants.DATE + "='"
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
