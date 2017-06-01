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
public abstract class SuperPostMethodsServlet extends HttpServlet {
	static Logger superLogger =  LogManager.getLogger(SuperPostMethodsServlet.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BuildJSON parentJSON = new BuildJSON();
		parentJSON.setValid(false);
		JSONObject jsonObject = MySqlDatabaseConnection.getJSONObject(request, parentJSON);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		doRoutine(request, response, parentJSON, jsonObject);
	}
	
	protected HttpSession processSession(HttpServletRequest request) {
		return request.getSession(false);
	}
	
	protected String getBoDept(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON) throws SQLException {
		String query = "SELECT * FROM " + TableConstants.TABLE_BUSINESS_OWNER + " WHERE " + TableConstants.EMAIL_ID + 
				"='" + userSession.getEmailID() + "';";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
			if (result != null && result.next()) {
				return result.getString(TableConstants.BO_DEPT);
			}
		} catch (SQLException e) {
			parentJSON = callFailure("Unable to process isAdmin or Nor.", e,
					parentJSON);
		} finally {
			if(result != null) {
				result.close();
			}
		}
		return null;
	}
	
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON, JSONObject jsonObject) {
		String error = "Failed to perform the service.";
		HttpSession session = processSession(request);
		ServletOutputStream out;
		String boDept = null;
		try {
			try {
				boDept = jsonObject.getString(TableConstants.BO_DEPT);
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
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
						if(isAdmin(userSession, stmt, parentJSON)) {
							callSuccess(request, stmt, con, parentJSON, userSession, jsonObject);
						}
					} catch (SQLException e) {
						callFailure(error, e, parentJSON);
					}
				} else {
					processFailure(error, parentJSON, userSession, jsonObject);
				}
				writeFinalOP(parentJSON, out);
			} catch (IOException e) {
				callFailure(error, parentJSON);
			}
		} catch (Exception e) {
			callFailure(error, e, parentJSON);
		}
	}
	
	protected void processFailure(String error, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		callFailure(error, parentJSON);
	}
	
	protected boolean isValidCheck(BuildJSON parentJSON, JSONObject jsonObject, UserSessionObject userSessionObject) {
		return userSessionObject != null;
	}
	
	protected UserSessionObject isSessionValid(String boDept, HttpSession session, BuildJSON parentJSON) {
		UserSessionObject userSession;
		if (boDept != null && session != null) {
			synchronized (session) {
				getLogger().entry(session.getId());
				userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null
						&& session.getId().equals(boDept)) {
					setLoggerEntry(userSession.getUserID());
					session.setAttribute(TableConstants.BO_DEPT, userSession);
					return userSession;
				}
			}
		}
		getLogger()
				.error(getClass().getName()
						+ ": Invalid session attempt by user whose sessionID/uniqueID: "
						+ boDept);
		parentJSON = callFailure(TableConstants.PLEASE_LOGIN_MSG, parentJSON);
		setLoggerEntry("Unknown user or Session expired user!");
		return null;
	}
	
	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}

	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out) {
		try {
			if (parentJSON.isValid()) {
				out.write(parentJSON.getParentWithArray(TableConstants.RESPONSE).toString().getBytes());
			} else {
				out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			callFailure("Failed to write response to the user.", e, parentJSON);
		}
	}


	protected String createQuery(JSONObject jsonObject) {
		getLogger().info("createQuery execution.");
		return null;
	}

	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON
				.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		getLogger().error("Failure execution. "
				+ error);
		return parentJSON;
	}
	
	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}
	
	protected BuildJSON callFailure(String error, Exception e, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		getLogger().error(error + " "+ getStackTrace(e));
		return parentJSON;
	}

	protected Logger getLogger() {
		return superLogger;
	}

	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		getLogger().info("success execution. ");
	}
	
	protected void callSuccess(HttpSession httpSession, HttpServletResponse response) {
		getLogger().info("success execution. ");
	}

	protected boolean isAdmin(UserSessionObject userSession, Statement stmt, BuildJSON parentJSON) {
		String query = "SELECT COUNT(*) AS " + TableConstants.COUNT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.EMAIL_ID + "='" + userSession.getEmailID() + "' AND "
				+ TableConstants.IS_ADMIN + "=1;";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result != null && result.next()
					&& result.getInt(TableConstants.COUNT) > 0) {
				result.close();
				return true;
			}
		} catch (SQLException e) {
			parentJSON = callFailure("Unable to find out Admin or not.", e, parentJSON);
		}
		return false;
	}
	
}
