/*
 * Login Servlet provides the JSON response which tells whether the user who is trying to login is valid or not.
 * Only supports Post call.
 * ->callFailure(String error):	returns none. Creates a JSON response with failure details.
 * ->callSuccess():	returns none. Runs the query and generates a JSON response out of the query result. 
 * 					Creates JSON response with all the user details.
 * ->createQuery(): returns String. This method is depreciated/not in use. Creates a query string for selecting the rows of a specific user.
 * ->createCountQuery(JSONObject jsonObject): returns String. Creates a query string for selecting the rows of all a particular user.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.Connection;
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
import com.nike.util.EmailValidator;
import com.nike.util.ErrorUtils;
import com.nike.util.ExternalAuthentication;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.ProtectedConfigFile;
import com.nike.util.TableConstants;
import com.nike.util.UserCheckUtil;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class Login
 */
public class Login extends SuperPostMethodsServlet {
	static Logger logger = LogManager.getLogger(Login.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		logger.exit(false);
	}

	@Override
	protected HttpSession processSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession(true);
		}
		return session;
	}

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		parentJSON.addElement(TableConstants.IS_USER, "false");
		parentJSON = super.callFailure(error, parentJSON);
		return parentJSON;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void doRoutine(HttpServletRequest request,
			HttpServletResponse response, BuildJSON parentJSON, JSONObject jsonObject) {
		try {
			ProtectedConfigFile.doEncrpty();
			ServletOutputStream out = response.getOutputStream();
			String userID = null;
			String pwd = null;
			try {
				userID = jsonObject.getString(TableConstants.USER_ID);
				pwd = jsonObject.getString(TableConstants.PWD);
			} catch (Exception e1) {
				getLogger().info("Invalid user details, may be trying with Federated Login.");
			}
			boolean doUserIDExist;
			String boDept = null;
			try {
				boDept = jsonObject.getString(TableConstants.BO_DEPT);
			} catch (Exception e1) {
				getLogger().info("Invalid boDept details, may be trying with Form Login.");
			}
			if (EmailValidator.validate(userID) && pwd != null
					&& pwd != "") {
				logger.info("User: " + userID + " attempt to login.");
				ExternalAuthentication authentication = new ExternalAuthentication();
				try {
					doUserIDExist = true;
					callSuccess(request, response, doUserIDExist, parentJSON, jsonObject, authentication, null, null);
				} catch (Exception e) {
					callFailure("Failed to sign! Please enter Password and try again. ", e, parentJSON);
				}
				
			} else if(boDept != null){
				HttpSession session = processSession(request);
				UserSessionObject userSession = super.isSessionValid(boDept, session, parentJSON);
				if(userSession != null) {
					doUserIDExist = false;
					callSuccess(request, response, doUserIDExist, parentJSON, jsonObject, null, userSession, session);
				}
			} else {
				if (userID == null) {
					callFailure("UserID cannot be null! Please enter User ID and try again. ", parentJSON);
				} else {
					callFailure(userID
							+ "'s Password cannot be null! Please enter Password and try again. ", parentJSON);
				}
			}
			writeFinalOP(parentJSON, out);
		} catch (IOException e) {
			callFailure("Error in getting the writer from response: ", e, parentJSON);
		} catch (Exception e1) {
			logger.error(ErrorUtils.getStackTrace(e1));
		}
	}
	
	protected void callSuccess(HttpServletRequest request, HttpServletResponse response, boolean doUserIDExist, BuildJSON parentJSON, JSONObject jsonObject, ExternalAuthentication authentication, UserSessionObject userSession, HttpSession session) {

		Statement stmt = null;
		try {
			Connection con = MySqlDatabaseConnection.getConnection(getServletContext());
			stmt = con.createStatement();
			
			if (doUserIDExist) {
				try {
					parentJSON = authentication.authenticate(jsonObject.getString(TableConstants.USER_ID), jsonObject.getString(TableConstants.PWD), logger, stmt, request, parentJSON, response);
				} catch (Exception e) {
					callFailure("Failed to sign! Please enter Password and try again. ", e, parentJSON);
				}
//				processJSONObject();
			} else {
				String userID = null;
				try {
					userID = jsonObject.getString(TableConstants.USER_ID);
				} catch (Exception e) {
					userID = userSession.getUserID();
					getLogger().info("Unable to retreive the userID.");
				}
				processSSOJSONObject(parentJSON, userSession, stmt, userID, session);
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				callFailure(e.getMessage(), e, parentJSON);
			}
		}
		// res = stmt.executeQuery(createCountQuery(jsonObject));
		// // Token = jsonObject.getString(TableConstants.TOKEN);
		// if (res.wasNull()) {
		// callFailure("Incorrect UserID: " + userID + " or Password!");
		// } else {
		// if (res.next() && res.getInt(TableConstants.COUNT) > 0) {
		// try {
		// parentJSON.addElement(TableConstants.IS_USER, "true");
		// parentJSON.addElement(TableConstants.USER_ID, userID);
		// parentJSON.addElement(TableConstants.FIRST_NAME,
		// res.getString(TableConstants.FIRST_NAME));
		// parentJSON.addElement(TableConstants.LAST_NAME,
		// res.getString(TableConstants.LAST_NAME));
		// String emailID = res.getString(TableConstants.EMAIL_ID);
		// parentJSON.addElement(TableConstants.EMAIL_ID, emailID);
		// parentJSON.addElement(TableConstants.PHONE_NO,
		// res.getString(TableConstants.PHONE_NO));
		// parentJSON.addElement(TableConstants.DEPT,
		// res.getString(TableConstants.DEPT));
		// String boDept = session.getId();
		// parentJSON.addElement(TableConstants.BO_DEPT, boDept);
		// parentJSON
		// .addElement(
		// TableConstants.LAST_APP_REQ_TITLE,
		// res.getString(TableConstants.LAST_APP_REQ_TITLE));
		// parentJSON.addElement(TableConstants.ERROR, "");
		// int isAdmin = res.getInt(TableConstants.IS_ADMIN);
		// parentJSON.addElement(TableConstants.IS_ADMIN, isAdmin);
		// UserSessionObject userSession = new UserSessionObject(
		// userID, res.getString(TableConstants.BO_DEPT),
		// isAdmin, emailID);
		// synchronized (session) {
		// session.setAttribute(TableConstants.BO_DEPT,
		// userSession);
		// }
		//
		// // if (Token != null && MobileCheck.isMobile(ua)) {
		// // if (!MobileCheck.doTokenExists(Token, res, ua)
		// // && !MobileCheck.isDuplicateToken(Token,
		// // stmt, userID)) {
		// // if (!MobileCheck
		// // .doInsertToken(
		// // Token,
		// // jsonObject
		// // .getString(TableConstants.USER_ID),
		// // stmt, ua)) {
		// // parentJSON.addElement("Error",
		// // "Incorrect UserID or Password!");
		// // }
		// // } else if (MobileCheck.isDuplicateToken(Token,
		// // stmt, userID)) {
		// // parentJSON
		// // .addElement(
		// // "Error",
		// //
		// "Your registered device is different from the device that you are trying to login!");
		// // }
		// // } else {
		// // logger.debug("No Token or Not a Mobile Device.");
		// // }
		// logger.info("UserID: " + userID
		// + " login attempt was successful.");
		// } catch (JSONException e) {
		// callFailure("Incorrect UserID: " + userID
		// + " or Password! " + e.getMessage(), e);
		// }
		// } else {
		// callFailure("Incorrect UserID: " + userID
		// + " or Password! ");
		// }
		// }
		// } catch (SQLException e) {
		// callFailure(e.getMessage(), e);
		// }

		// finally {
		// try {
		// if (stmt != null) {
		// stmt.close();
		// }
		// } catch (SQLException e) {
		// callFailure(e.getMessage(), e);
		// }
		// }
	}
	
	protected void processJSONObject(BuildJSON parentJSON, String userID, String pwd, Statement stmt, HttpSession session) {
		try {
			parentJSON = UserCheckUtil.doUserExist(stmt, userID, pwd, parentJSON,
					logger);
			if (parentJSON == null) {
				parentJSON = new BuildJSON();
				callFailure("Incorrect UserID: " + userID + " or Password! ", parentJSON);
			} else {
				int isAdmin = Integer.parseInt(parentJSON
						.getElement(TableConstants.IS_ADMIN));
				UserSessionObject userSession = new UserSessionObject(userID,
						parentJSON.getElement(TableConstants.BO_DEPT), isAdmin,
						parentJSON.getElement(TableConstants.EMAIL_ID));

				String boDept = session.getId();
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				synchronized (session) {
					session.setAttribute(TableConstants.BO_DEPT, userSession);
				}
			}
		} catch (SQLException e) {
			logger.error(ErrorUtils.getStackTrace(e));
		}
	} 
	
	protected void processSSOJSONObject(BuildJSON parentJSON, UserSessionObject userSession, Statement stmt, String userID, HttpSession session) {
		try {
			parentJSON = UserCheckUtil.doUserExist(stmt, userSession.getEmailID(), parentJSON, logger);
			if (parentJSON == null) {
				parentJSON = new BuildJSON();
				callFailure("Incorrect UserID: " + userID + " or Password! ", parentJSON);
			} else if(userSession != null && userSession.getUserID() != null) {
				parentJSON.addElement(TableConstants.BO_DEPT, session.getId());
				synchronized (session) {
					session.setAttribute(TableConstants.BO_DEPT, userSession);
				}
			} else {
				int isAdmin = Integer.parseInt(parentJSON
						.getElement(TableConstants.IS_ADMIN) );
				userSession.setEmailID(parentJSON.getElement(TableConstants.EMAIL_ID));
				userSession.setUniqueID(parentJSON.getElement(TableConstants.BO_DEPT));
				userSession.setIsAdmin(isAdmin);
				userSession.setUserID(userID);
				String boDept = session.getId();
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				synchronized (session) {
					session.setAttribute(TableConstants.BO_DEPT, userSession);
				}
			}
		} catch (SQLException e) {
			callFailure("Sorry, unable to login using the User ID and password.", e, parentJSON);
		}
	}

//	private String createCountQuery(JSONObject jsonObject, String userID, String pwd) {
//		String query = "select COUNT(*) as " + TableConstants.COUNT + ", "
//				+ TableConstants.FIRST_NAME + ", " + TableConstants.LAST_NAME
//				+ ", " + TableConstants.EMAIL_ID + ", "
//				+ TableConstants.PHONE_NO + ", " + TableConstants.DEPT + ", "
//				+ TableConstants.BO_DEPT + ", "
//				+ TableConstants.LAST_APP_REQ_TITLE + ", "
//				+ TableConstants.IS_ADMIN + ", " + TableConstants.IPHONE_TOKEN
//				+ ", " + TableConstants.IPAD_TOKEN + ", "
//				+ TableConstants.IPOD_TOKEN + ", " + TableConstants.IPHONE
//				+ ", " + TableConstants.IPAD + ", " + TableConstants.IPOD
//				+ " FROM " + TableConstants.TABLE_BUSINESS_OWNER + " where "
//				+ TableConstants.USER_ID + "='" + userID + "' and "
//				+ TableConstants.PWD + "=AES_ENCRYPT('" + pwd + "', '" + pwd
//				+ "');";
//		return query;
//	}

	@Override
	protected String createQuery(JSONObject jsonObject) {
		return "select * from " + TableConstants.TABLE_BUSINESS_OWNER
				+ " where " + TableConstants.USER_ID + "='" + jsonObject.getString(TableConstants.USER_ID) + "' and "
				+ TableConstants.PWD + "=AES_ENCRYPT('" + jsonObject.getString(TableConstants.PWD) + "', '" + jsonObject.getString(TableConstants.PWD)
				+ "');";
	}
}
