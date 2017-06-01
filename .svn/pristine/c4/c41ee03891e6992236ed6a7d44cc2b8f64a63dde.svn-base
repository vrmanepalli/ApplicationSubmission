/*
 * GetSuperClass is super class/servlet which has the control flow predefined based on the result.
 * Such as what needs to be called when if the data processed is successful or failure.
 * The methods that can be overriden are as follows:
 * ->doRoutine(): returns none. This is the routine which gets usually called for every successful processing. The successful flows starts from here.
 * ->callSuccess(): returns none. doRoutine method calls this method when the intended data processing is successful. This is abstract method
 * ->writeFinalOP(): returns none.This is the method which prints JSON object or JSON Array based on the repsonse and Servlet  that is extending this class.
 * ->reset(HttpServletRequest request): returns none. Resets the variables to default values for every call.
 * ->callFailure(String error): returns none, abstract method. Intended for creating a failure JSON response.
 * ->createQuery(): returns String, abstract method. The main intension of this method is to provide specific behavior which in turn provides respective query string.
 * ->addElementsToJSON(): returns none, abstract method. 
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetSuperClass
 */
public abstract class GetSuperClass extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger superLogger = LogManager.getLogger(GetSuperClass.class.getName());
	protected Connection con;
	protected BuildJSON parentJSON;
	protected Statement stmt;
	protected ResultSet res;
	protected PrintWriter out;
	protected String className = "GetSuperClass";
	protected boolean isValid = true;
	protected String error = "Failure!";
	protected Statement localStmt;
	protected String boDept;
	protected UserSessionObject userSession;
	protected HttpSession session;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetSuperClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected boolean isSessionValid(String boDept, HttpSession session) {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processSession(request);
		reset(request);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		out = response.getWriter();
		parentJSON = new BuildJSON();
		doRoutine(request);
	}
	
	protected void processSession(HttpServletRequest request) {
		session = request.getSession(false);
	}

	protected void doRoutine(HttpServletRequest request) {
		if (isSessionValid(boDept, session)) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			getLogger().info("Valid session of user: " + userSession.getUserID() + ", request for " + getClass().getName());
			try {
				stmt = con.createStatement();
				localStmt = con.createStatement();
				res = stmt.executeQuery(createQuery());
				if (res == null) {
					callFailure(error);
				} else {
					callSuccess(session);
				}
			} catch (SQLException e) {
				callFailure(e.getMessage(), e);
			} finally {
				try {
					writeFinalOP();
					stmt.close();
					localStmt.close();
				} catch (SQLException e) {
					callFailure(e.getMessage(), e);
					writeFinalOP();
				}
			}
		} else {
			writeFinalOP();
		}
	}

	protected void writeFinalOP() {
		if (isValid) {
			out.print(parentJSON.getParentWithArray(TableConstants.RESPONSE));
		} else {
			out.print(parentJSON.getParentWithChild(TableConstants.RESPONSE));
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void reset(HttpServletRequest request) {
		isValid = true;
		error = "Failure!";
		className = this.getServletName();
		try {
			boDept = request.getParameter(TableConstants.BO_DEPT);
		} catch (Exception e) {
			getLogger().error(getStackTrace(e));
		}
	}
	
	protected boolean isAdmin() {
		String query = "SELECT COUNT(*) AS " + TableConstants.COUNT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.BO_DEPT + "='" + userSession.getUniqueID() + "' AND "
				+ TableConstants.IS_ADMIN + "=1;";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result != null && result.next()
					&& result.getInt(TableConstants.COUNT) > 0) {
				return true;
			}
		} catch (SQLException e) {
			callFailure("Unable to process isAdmin or Nor.", e);
		}
		return false;
	}
	
	protected Logger getLogger() {
		return superLogger;
	}

	protected void callFailure(String error, Exception e) {
		parentJSON.addElement(TableConstants.ERROR, error);
		isValid = false;
		getLogger().error(error + " "+ getStackTrace(e));
	}
	
	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}
	
	protected abstract void callFailure(String error);

	protected abstract void callSuccess(HttpSession httpSession);

	protected abstract String createQuery();

	protected abstract void addElementsToJSON();

}
