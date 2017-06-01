/*
 * PostSuperClass servlet which a basic behavior. All the sservlet which has only post calls can extend this class.
 * ->callSuccess(): returns none. doPost method calls this method when the intended data processing is successful. This is abstract method
 * ->writeFinalOP(): returns none.This is the method which prints JSON object or JSON Array based on the repsonse and Servlet  that is extending this class.
 * ->reset(HttpServletRequest request): returns none. Resets the variables to default values for every call.
 * ->callFailure(String error): returns none, abstract method. Intended for creating a failure JSON response.
 * ->createQuery(): returns String, abstract method. The main intension of this method is to provide specific behavior which in turn provides respective query string.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class PostSuperClass
 */
public class PostSuperClass extends HttpServlet {
	static Logger superLogger =  LogManager.getLogger(PostSuperClass.class.getName());
	private static final long serialVersionUID = 1L;
	protected Connection con;
	protected BuildJSON parentJSON;
	protected JSONObject jsonObject;
	protected PrintWriter out;
	protected String className = "PostSuperClass";
	protected Statement stmt;
	protected ResultSet res;
	protected String error = "Failure.";
	protected boolean isArray = false;
	protected String boDept;
	protected UserSessionObject userSession;
	protected HttpSession session;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		parentJSON = new BuildJSON();
		jsonObject = MySqlDatabaseConnection.getJSONObject(request, parentJSON);
		processSession(request);
		reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		doRoutine(request, response);
	}
	
	protected void processSession(HttpServletRequest request) {
		session = request.getSession(false);
	}
	
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			out = response.getWriter();
			if (jsonObject != null && isSessionValid(boDept, request)) {
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				callSuccess(request);
			} else {
				callFailure(error);
			}
			writeFinalOP();
		} catch (IOException e) {
			callFailure(error, e);
		}
	}
	
	
	protected boolean isSessionValid(String boDept, HttpServletRequest request) {
		if (boDept != null && session != null) {
			synchronized (session) {
				userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null
						&& session.getId().equals(boDept)) {
					con = MySqlDatabaseConnection.getConnection(getServletContext());
					setLoggerEntry(userSession.getUserID());
					session.setAttribute(TableConstants.BO_DEPT, userSession);
					return true;
				}
			}
		}
		getLogger()
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		callFailure(TableConstants.PLEASE_LOGIN_MSG);
		setLoggerEntry("Unknown user or Session expired user!");
		return false;
	}
	
	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}

	protected void writeFinalOP() {
		if (isArray) {
			out.print(parentJSON.getParentWithArray(TableConstants.RESPONSE));
		} else {
			out.print(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		}
		out.close();
	}

	protected void reset() {
		className = this.getServletName();
		isArray = false;
		try {
			boDept = jsonObject.getString(TableConstants.BO_DEPT);
		} catch (Exception e) {
			getLogger().error(getStackTrace(e));
		}
	}

	protected String createQuery(JSONObject jsonObject) {
		getLogger().info("createQuery execution. ");
		return null;
	}

	protected void callFailure(String error) {
		parentJSON
				.addElement(TableConstants.ERROR, error);
		getLogger().error("Failure execution. "
				+ error);
	}
	
	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}
	
	protected void callFailure(String error, Exception e) {
		parentJSON.addElement(TableConstants.ERROR, error);
		getLogger().error(error + " "+ getStackTrace(e));
	}

	protected Logger getLogger() {
		return superLogger;
	}

	protected void callSuccess(HttpServletRequest request) {
		getLogger().info("success execution. ");
	}
	
	protected void callSuccess(HttpSession httpSession, HttpServletResponse response) {
		getLogger().info("success execution. ");
	}

	protected boolean isAdmin() {
		String query = "SELECT COUNT(*) AS " + TableConstants.COUNT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.BO_DEPT + "='" + userSession.getUniqueID() + "' AND "
				+ TableConstants.IS_ADMIN + "=" + TableConstants.IS_PROD_ADMIN_VALUE + ";";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result != null && result.next()
					&& result.getInt(TableConstants.COUNT) > 0) {
				result.close();
				return true;
			}
		} catch (SQLException e) {
			callFailure("Unable to find out Admin or not.", e);
		}
		return false;
	}
	
}
