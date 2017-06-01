package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.TableConstants;
import com.nike.util.TriggerJenkinsBuild;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class ResetServerServlet
 */
public class ResetServerServlet extends SuperPostMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(ResetServerServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetServerServlet() {
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
	protected String createQuery(JSONObject jsonObject) {
		String query = "UPDATE " + TableConstants.TABLE_PROJECTS + " SET "
				+ TableConstants.CURRENT_STATUS + "='Failed' WHERE "
				+ TableConstants.CURRENT_STATUS + "='Progress';";
		return query;
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return logger;
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		logger.info("Attempt to reset of server by user: " + userSession.getUserID());
		if(userSession.isAdmin()) {
			try {
				stmt = con.createStatement();
				if(TriggerJenkinsBuild.reset() && stmt.executeUpdate(createQuery(jsonObject)) >= 0) {
					parentJSON
					.addElement(
							TableConstants.SUCCESS,
							"Server is reset.");
					logger.info("Successful reset of server by user: " + userSession.getUserID());
				} else {
					logger.info("UnSuccessful reset of server by user: " + userSession.getUserID());
					parentJSON
					.addElement(
							TableConstants.ERROR, "Unable to reset the server.");
				}
				parentJSON.setValid(false);
			} catch (SQLException e) {
				callFailure("Unable to process your request because of database outage. Please try after some time.", e, parentJSON);
			}
		} else {
			callFailure("Unable to process your request because of database outage. Permission denied.", parentJSON);
		}
	}

}
