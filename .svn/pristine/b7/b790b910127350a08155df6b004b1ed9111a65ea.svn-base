package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.MobileCheck;
import com.nike.util.TableConstants;

/**
 * Servlet implementation class Login
 */
public class VerifyUser extends PostSuperClass {
	private static final long serialVersionUID = 1L;
	private String ua;
	private String Token;
	static Logger logger =  LogManager.getLogger(VerifyUser.class.getName());

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		error = "Incorrect password or unable to process. Please try again.";
//		ua = request.getHeader("User-Agent").toLowerCase();
		super.doPost(request, response);
		logger.exit(false);
	}
	
	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected void callFailure(String error, Exception e) {
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.addElement(TableConstants.IS_USER, "false");
		logger.error(error + " "+ getStackTrace(e));
	}

	@Override
	protected void callFailure(String error) {
		logger.error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.addElement(TableConstants.IS_USER, "false");
	}
	
	

	@Override
	protected void reset() {
		super.reset();
		try {
			Token = jsonObject.getString(TableConstants.TOKEN);
		} catch (Exception e) {
			callFailure("Sorry, cannot process your request with null token values.", e);
		}
	}

	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			out = response.getWriter();
			callSuccess(request);
			writeFinalOP();
		} catch (IOException e) {
			callFailure("Error in getting the writer from response: " + e.getMessage(), e);
		}
	}

	@Override
	protected void callSuccess(HttpServletRequest request) {
		try {
			stmt = con.createStatement();
			res = stmt.executeQuery(createCountQuery(jsonObject));
			if (res.wasNull()) {
				callFailure("Incorrect Password!");
			} else {
				int rowCount = 0;
				if (res.next() && res.getInt("count") > 0) {
					rowCount = res.getInt("count");
					try {
						if (Token != null && MobileCheck.isMobile(ua)
								&& MobileCheck.doTokenExists(Token, res, ua)
								&& MobileCheck.doTokenMatches(Token, res)) {
							parentJSON.addElement(TableConstants.IS_USER, "true");
							parentJSON.addElement(TableConstants.CURRENT_BUILD_PATH, jsonObject.getString(TableConstants.CURRENT_BUILD_PATH));
						} else {
							callFailure("Incorrect Password!");
						}
					} catch (JSONException e) {
						callFailure("Incorrect Password! "
								+ e.getMessage());
					}
				} else {
					callFailure("Incorrect Password! ");
				}
			}
		} catch (SQLException e) {
			callFailure("Not able to retreive the data from database.", e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				callFailure("Not able to complete the database transaction.", e);
			}
		}
	}

	private String createCountQuery(JSONObject jsonObject) {
		String query = "select COUNT(*) as " + TableConstants.COUNT + ", " + TableConstants.IPHONE_TOKEN
				+ ", " + TableConstants.IPAD_TOKEN + ", "
				+ TableConstants.IPOD_TOKEN + ", " + TableConstants.IPHONE
				+ ", " + TableConstants.IPAD + ", " + TableConstants.IPOD + " from "
				+ TableConstants.TABLE_BUSINESS_OWNER + " where "
				+ TableConstants.EMAIL_ID + "='"
				+ jsonObject.getString(TableConstants.EMAIL_ID) + "' and "
				+ TableConstants.PWD + "=AES_ENCRYPT('"
				+ jsonObject.getString(TableConstants.PWD) + "', '"
				+ jsonObject.getString(TableConstants.PWD) + "');";
		return query;
	}

}
