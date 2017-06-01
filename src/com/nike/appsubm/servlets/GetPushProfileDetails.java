/*
 * GetPushProfileDetails servlets provides a JSON response that consists of the PUSH notification unique profile details such expity date.
 * Supports Post call only.
 * ->callFailure(String error):	returns none. Creates a JSON response with failure details.
 * ->callSuccess(ServletContext servletContext):	returns none. Runs the query and generates a JSON response out of the query result.
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

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.GetUserDetails;
import com.nike.util.PushProfileDetails;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetPushProfileDetails
 */
public class GetPushProfileDetails extends SuperPostMethodsServlet {
	static Logger logger =  LogManager.getLogger(GetPushProfileDetails.class.getName());
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPushProfileDetails() {
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
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void callSuccess(HttpServletRequest request, Statement stmt, Connection con, BuildJSON parentJSON, UserSessionObject userSession, JSONObject jsonObject) {
		ResultSet res;
		if ((res = PushProfileDetails.getPushProfileDetails(stmt)) != null) {
			try {
				parentJSON.setValid(true);
				parentJSON.setLocalVariableString(jsonObject.getString(TableConstants.BO_DEPT));
				parentJSON.createNewJSONArray();
				while (res.next()) {
					parentJSON.createNewJChild();
					parentJSON.addElement(TableConstants.APP_TITLE,
							res.getString(TableConstants.APP_TITLE));
					parentJSON.addElement(TableConstants.CREATION_DATE,
							res.getInt(TableConstants.CREATION_DATE));
					parentJSON
							.addElement(
									TableConstants.EXPIRATION_DATE,
									res.getString(TableConstants.EXPIRATION_DATE));
					parentJSON
							.addElement(
									TableConstants.APPLICATION_IDENTIFIER,
									res.getString(TableConstants.APPLICATION_IDENTIFIER));
					parentJSON.addElement(TableConstants.UA_USER_NAME,
							res.getString(TableConstants.UA_USER_NAME));
					parentJSON.addElement(TableConstants.UA_PWD,
							res.getString(TableConstants.UA_PWD));
					parentJSON.addJObjectToJArray();
				}
				parentJSON.getParentWithArray(TableConstants.RESPONSE);
				logger.info("Authorized access of Profiles by user " + userSession.getUserID());
			} catch (SQLException e) {
				callFailure(e.getLocalizedMessage(), e, parentJSON);
			}
		} else {
			callFailure("Zero records available.", parentJSON);
		}
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out) {
		parentJSON.createNewJChild();
		parentJSON.addElement(TableConstants.BO_DEPT, parentJSON.getLocalVariableString());
		try {
			out.write(parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS).toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			callFailure(TableConstants.WRITE_STREAM_ERROR_MSG, e, parentJSON);
		}
	}

	
	
}
