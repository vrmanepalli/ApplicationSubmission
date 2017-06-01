package com.nike.appsubm.servlets;

import java.io.IOException;
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

import net.sf.json.JSONException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.EmailValidator;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class SearchUserServlet
 */
@WebServlet("/Private/SearchUserServlet")
public class SearchUserServlet extends SuperGetMethodsServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(SearchUserServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		logger.exit(false);
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.createNewJChild();
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}

	
	@Override
	protected boolean isValidCheck(BuildJSON parentJSON,
			HttpServletRequest request, UserSessionObject userSessionObject) {
		String emailID = request.getParameter(TableConstants.EMAIL_ID);
		parentJSON.setLocalVariableString(emailID);
		return userSessionObject.isAdmin() && EmailValidator.validate(emailID);
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		try {
			parentJSON.createNewJSONArray();
			while (res != null && res.next()) {
				try {
					addElementsToJSON(parentJSON, res);
				} catch (JSONException e) {
					parentJSON = callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), parentJSON);
				}
			}
			parentJSON.getParentWithArray(TableConstants.USERS);
			parentJSON.createNewJChild();
			parentJSON.addElement("Success", "Successfully retreived the user ids.");
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			try {
				res.close();
			} catch (Exception e) {
				parentJSON = callFailure("Failed to complete the request.", e, parentJSON);
			}
		} catch (SQLException e) {
			parentJSON = callFailure("Failed to execute database transaction.", e, parentJSON);
		}
	}	
	
	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}
	
	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			getLogger().error("Failed to write the response to user.");
		}
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJSON, String userID) {
		String query = "SELECT " + TableConstants.FIRST_NAME + ", "
				+ TableConstants.LAST_NAME + ", " + TableConstants.USER_ID
				+ ", " + TableConstants.BO_DEPT + ", "
				+ TableConstants.PHONE_NO + ", " + TableConstants.EMAIL_ID
				+ ", " + TableConstants.DEPT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE "
				+ TableConstants.EMAIL_ID + "='" + parentJSON.getLocalVariableString() + "' ORDER BY "
				+ TableConstants.FIRST_NAME + " ASC;";
		return query;
	}

	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res) {
		parentJSON.createNewJChild();
		try {
			String boDeptString = res.getString(TableConstants.BO_DEPT);
			parentJSON.addElement(TableConstants.BO_DEPT,
					boDeptString);
			parentJSON.addElement(TableConstants.FIRST_NAME,
					res.getString(TableConstants.FIRST_NAME));
			parentJSON.addElement(TableConstants.LAST_NAME,
					res.getString(TableConstants.LAST_NAME));
			parentJSON.addElement(TableConstants.USER_ID,
					res.getString(TableConstants.USER_ID));
			parentJSON.addElement(TableConstants.PHONE_NO,
					res.getString(TableConstants.PHONE_NO));
			parentJSON.addElement(TableConstants.EMAIL_ID,
					res.getString(TableConstants.EMAIL_ID));
			parentJSON.addElement(TableConstants.DEPT,
					res.getString(TableConstants.DEPT));
			
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			parentJSON = callFailure("Failed to complete database transaction.", e, parentJSON);
		}
		return parentJSON;
	}
}
