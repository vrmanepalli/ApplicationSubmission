package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class SuperGetMethodsServlet
 */
@WebServlet("/Private/SuperGetMethodsServlet")
public abstract class SuperGetMethodsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger superLogger = LogManager
			.getLogger(SuperGetMethodsServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SuperGetMethodsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected UserSessionObject isSessionValid(String boDept,
			HttpSession session) {
		if (boDept != null && session != null) {
			synchronized (session) {
				UserSessionObject userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null && session.getId().equals(boDept)) {
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
		callFailure(TableConstants.PLEASE_LOGIN_MSG, new BuildJSON());
		setLoggerEntry("Unknown user or Session expired user!");
		return null;
	}

	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}

	protected Logger getLogger() {
		return superLogger;
	}

	protected BuildJSON callFailure(String error, Exception e,
			BuildJSON parentJSON) {
		getLogger().error(error + " " + getStackTrace(e));
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		return parentJSON;
	}

	protected String getStackTrace(Exception e) {
		return ErrorUtils.getStackTrace(e);
	}

	protected abstract BuildJSON callFailure(String error, BuildJSON parentJSON);

	protected boolean isAdmin(UserSessionObject userSession, Statement stmt,
			BuildJSON parentJSON) {
		String query = "SELECT COUNT(*) AS " + TableConstants.COUNT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.EMAIL_ID + "='" + userSession.getEmailID()
				+ "' AND " + TableConstants.IS_ADMIN + "=1;";
		try {
			ResultSet result = stmt.executeQuery(query);
			if (result != null && result.next()
					&& result.getInt(TableConstants.COUNT) > 0) {
				result.close();
				return true;
			}
		} catch (SQLException e) {
			parentJSON = callFailure("Unable to process isAdmin or Nor.", e,
					parentJSON);
		}
		return false;
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

	protected HttpSession processSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session;
	}

	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = processSession(request);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		ServletOutputStream out = response.getOutputStream();
		BuildJSON parentJSON = new BuildJSON();
		String error = "Failure!";
		String boDept = null;
		boolean inProgress = false;
		int index = -1;
		int inProgressIndex = -1;
		String result = "";
		try {
			boDept = request.getParameter(TableConstants.BO_DEPT);
		} catch (Exception e) {
			getLogger().error(getStackTrace(e));
		}

		UserSessionObject userSession = isSessionValid(boDept, session);

		processRequest(userSession, boDept, parentJSON, error, request,
				session, out, result, inProgress, inProgressIndex);

	}
	
	protected boolean isValidCheck(BuildJSON parentJSON, HttpServletRequest request, UserSessionObject userSessionObject) {
		return true;
	}

	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error, HttpServletRequest request,
			HttpSession session, ServletOutputStream out, String result,
			boolean inProgress, int inProgressIndex) throws IOException {
		if (userSession != null && isValidCheck(parentJSON, request, userSession)) {
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			getLogger().info(
					"Valid session of user: " + userSession.getUserID()
							+ ", request for " + getClass().getName());
			Connection con = MySqlDatabaseConnection
					.getConnection(getServletContext());
			Statement stmt = null;
			Statement localStmt = null;
			try {
				stmt = con.createStatement();
				localStmt = con.createStatement();
				ResultSet res = getResultSet(request, userSession, localStmt,
						parentJSON);
				if (res == null) {
					parentJSON = callFailure(error, parentJSON);
				} else {
					callSuccess(session, parentJSON, res, userSession, boDept,
							stmt, localStmt, request, out);
				}
			} catch (SQLException e) {
				parentJSON.addElement(TableConstants.ERROR, getStackTrace(e));
				parentJSON = callFailure(e.getMessage(), e, parentJSON);
			} finally {
				try {
					writeFinalOP(parentJSON, out, boDept, result, inProgress,
							inProgressIndex);
					stmt.close();
					localStmt.close();
				} catch (SQLException e) {
					parentJSON = callFailure(e.getMessage(), e, parentJSON);
					writeFinalOP(parentJSON, out, boDept, result, inProgress,
							inProgressIndex);
				}
			}
		} else {
			writeFinalOP(parentJSON, out, boDept, result, inProgress,
					inProgressIndex);
		}
	}

	protected ResultSet getResultSet(HttpServletRequest request,
			UserSessionObject userSession, Statement stmt, BuildJSON parentJSON)
			throws SQLException {
		return stmt.executeQuery(createQuery(userSession, parentJSON, userSession.getUserID()));
	}

	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJson, String userID) {
		return createQuery(userSession);
	}

	protected String createQuery(UserSessionObject userSession) {
		return null;
	}

	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res,
			UserSessionObject userSession, boolean inProgress,
			Object inProgressIndex, String result, Object index) {
		// TODO Auto-generated method stub
		return parentJSON;
	}

	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			if (parentJSON.isValid()) {
				out.write(parentJSON
						.getParentWithArray(TableConstants.RESPONSE).toString()
						.getBytes());
			} else {
				out.write(parentJSON
						.getParentWithChild(TableConstants.RESPONSE).toString()
						.getBytes());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			getLogger().error(
					"Failed to write response to user. "
							+ ErrorUtils.getStackTrace(e));
		}
	}

}
