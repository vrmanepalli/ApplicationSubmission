package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.InternalAuthentication;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.TokenHandler;
import com.nike.util.TokenWrapper;
import com.nike.util.UserCheckUtil;
import com.pingidentity.opentoken.TokenException;

/**
 * Servlet implementation class CosumeSSOServlet
 */
@WebServlet("/Private/CosumeSSOServlet")
public class CosumeSSOServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(CosumeSSOServlet.class
			.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CosumeSSOServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.entry();
		BuildJSON jsonResponse = null;
		TokenWrapper tokenWrapper = new TokenWrapper(TableConstants.AGENT_CONFIG_PATH);
		InternalAuthentication authentication = new InternalAuthentication();
		Statement stmt = null;
		BuildJSON parentJSON;
		try {
			MultiMap data = tokenWrapper.read(TokenHandler.getTokenString(
					request, response));
			logger.info("Successful reading of token");
			Connection con = MySqlDatabaseConnection
					.getConnection(getServletContext());
			stmt = con.createStatement();
			boolean doUserBelongsToAGroup = authentication.processDataToken(data, stmt);
			if (doUserBelongsToAGroup ) {
				parentJSON = new BuildJSON();
				parentJSON = UserCheckUtil.doUserExist(stmt, authentication.getEmailID(), parentJSON,
						logger);	// Check if the user exists in the local database or not.
				if (parentJSON == null) { // The user does not exist in the database.
					try {
						jsonResponse = authentication.addUserToLocalDatabase(request, response, stmt, parentJSON);
						if(jsonResponse == null) {
							callFailure("Failed to process the sign in", null, response);
						} else {
							parentJSON = jsonResponse;
						}
					} catch (Exception e) {
						callFailure("Failed to add user to local database.", e, response);
					}
				} else { // The user exists in the local database.
					jsonResponse = authentication.processUserDetailsFromLocalDatabase(request, response, parentJSON);
					if(jsonResponse == null) {
						callFailure("Failed to process the sign in", null, response);
					} else {
						parentJSON = jsonResponse;
					}
				}
			} else {
				callFailure("User does not have access to this web application.", null, response);
			}
		} catch (TokenException | SQLException e) {
			if (e instanceof TokenException) {
				callFailure("Failed to process the token.", e, response);
			} else {
				callFailure("Failed to access the database.", e, response);
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				callFailure(e.getMessage(), e, response);
			}
		}
		logger.exit();
	}

	private void callFailure(String message, Exception e,
			HttpServletResponse response) {
		if (e != null) {
			logger.error(message + "  " + ErrorUtils.getStackTrace(e));
		} else {
			logger.error(message);
		}
		try {
			response.sendRedirect(TableConstants.ERROR_PAGE_URL + "?error=" + message);
		} catch (IOException e1) {
			logger.error(ErrorUtils.getStackTrace(e1));
		}
	}

}
