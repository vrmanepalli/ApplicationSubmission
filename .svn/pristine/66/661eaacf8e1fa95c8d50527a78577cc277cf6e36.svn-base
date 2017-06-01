/*
 * 	GetInUseUserID servlet is used to get a JSON response with the specific user details.
 *	->	addElementsToJSON():	returns none. Overrride method which Creates a JSON Object with user details
 *	->	callFailure(String error):	returns none. Creates a JSON response with failure details.
 *	->	createQuery():	returns none. Creates Select statement in order to pull the specific user details.
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

import net.sf.json.JSONException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;

/**
 * Servlet implementation class GetInUseUserID
 */
public class GetInUseUserID extends SuperGetMethodsServlet {
	static Logger logger =  LogManager.getLogger(GetInUseUserID.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetInUseUserID() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		ServletOutputStream outStream = response.getOutputStream();
		BuildJSON parentJSON = new BuildJSON();
		HttpSession session = null;
		String error = "Unable to connect to Database. Please try again later!";
		try {
			session = request.getSession(false);
			setLoggerEntry(session.getId());
			if (session != null) {
				Connection con = MySqlDatabaseConnection.getConnection(request
						.getServletContext());
				Statement stmt = null;
				Statement localStmt = null;
				ResultSet res = null;
				try {
					stmt = con.createStatement();
					localStmt = con.createStatement();
					res = stmt.executeQuery(createQuery());
					if (res == null) {
						parentJSON = callFailure(error, parentJSON);
					} else {
						parentJSON.createNewJSONArray();
						try {
							while (res.next()) {
								try {
									parentJSON = addElementsToJSON(parentJSON,
											res);
								} catch (JSONException e) {
									parentJSON = callFailure(error, e,
											parentJSON);
									writeFinalOP(parentJSON, outStream, error, error,
											false, 0);
									return;
								}
							}
							logger.info("Successful retreival of UserIDs from database!");
						} catch (SQLException e) {
							parentJSON = callFailure(error, e, parentJSON);
						}
					}
				} catch (SQLException e) {
					parentJSON = callFailure(error, e, parentJSON);
					logger.error("Sorry, unable to access the database.", e);
				} finally {
					try {
						if (res != null) {
							res.close();
						}
						if (stmt != null) {
							stmt.close();
						}
						if (localStmt != null) {
							localStmt.close();
						}
						writeFinalOP(parentJSON, outStream, error, error,
								false, 0);
					} catch (SQLException e) {
						parentJSON = callFailure(error, e, parentJSON);
						writeFinalOP(parentJSON, outStream, error, error,
								false, 0);
					}
				}
			} else {
				parentJSON = callFailure(TableConstants.PLEASE_LOGIN_MSG,
						parentJSON);
				writeFinalOP(parentJSON, outStream, error, error, false, 0);
			}
		} catch (Exception e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
		logger.exit(false);
	}
	
	

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.EMAIL_ID,
					res.getString(TableConstants.EMAIL_ID));
			parentJSON.addElement(TableConstants.USER_ID,
					res.getString(TableConstants.USER_ID));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}

	protected String createQuery() {
		return "select " + TableConstants.USER_ID + ", "
				+ TableConstants.EMAIL_ID + " from "
				+ TableConstants.TABLE_BUSINESS_OWNER + ";";
	}

}
