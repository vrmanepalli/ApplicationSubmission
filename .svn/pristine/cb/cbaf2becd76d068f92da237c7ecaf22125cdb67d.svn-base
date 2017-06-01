/*
 * GetUserProjects servlet provides the Project details of a user that was owned i.e the details of Project name, Technical details, image urls and so on.
 * supports get call only. This servlet also provides the users details as part of the response.
 * ->callFailure(String error):	returns none. Creates a JSON response with failure details.
 * ->callSuccess():	returns none. Runs the query and generates a JSON response out of the query result. 
 * 					Creates JSON response with all the project details.
 * ->writeFinalOP(): returns none. Prints the JSON response to the output stream.
 * ->createQuery(): returns String. Creates a query string for selecting the rows of all the projects of a user.
 * ->addElementsToJSON(): return none. Constructing the JSON response with the particular project of a specific user.
 * ->reset(HttpServletRequest request): Reset the variables to default values.
 * -> addUsers(): returns none. Selects all the users rows from database and create a JSON Array out of it and add it to the final response.
 * -> addUsersToJSON(): returns none. Helper method to addUsers method. Creates a JSON object so that can be added to JSON array.
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
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.GetImageDetails;
import com.nike.util.GetUserDetails;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;
import com.nike.util.UpdateRequestCount;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class UpdateJobResult
 */
public class GetUserProjects extends SuperGetMethodsServlet {
	static Logger logger = LogManager
			.getLogger(GetUserProjects.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUserProjects() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// String userAgent = request.getHeader("User-Agent").toLowerCase();
		// isMobile = MobileCheck.isMobile(userAgent);
		super.doGet(request, response);
		getLogger().exit(false);
	}

	@Override
	protected void setLoggerEntry(String userID) {
		getLogger().entry(userID);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected BuildJSON callFailure(String error, BuildJSON parentJSON) {
		getLogger().error(error);
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		return parentJSON;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	@Override
	protected void processRequest(UserSessionObject userSession, String boDept,
			BuildJSON parentJSON, String error,
			HttpServletRequest request, HttpSession session,
			ServletOutputStream out, String result, boolean inProgress,
			int inProgressIndex) throws IOException {
		parentJSON.setLocalVariableInt(0);
		int currentPage = 1;
		if (userSession != null) {
			String appTitle = null;
			try {
				appTitle = request.getParameter(TableConstants.APP_TITLE);
			} catch (Exception e1) {
				getLogger().info("The user have not provided the app title." + ErrorUtils.getStackTrace(e1));
			}
			try {
				currentPage = Integer.parseInt(request.getParameter(TableConstants.CURRENT_PAGE) == null ? "1" : request.getParameter(TableConstants.CURRENT_PAGE));
			} catch (Exception e1) {
				getLogger().info("The user have not provided the current page." + ErrorUtils.getStackTrace(e1));
			}
			String filterUserID = null;
			try {
				filterUserID = request.getParameter(TableConstants.FILTER_USER_ID);
			} catch (Exception e1) {
				getLogger().info("The user have not provided the user details." + ErrorUtils.getStackTrace(e1));
			}
			setFilterUserID(filterUserID, parentJSON);
			setCurrentPage(currentPage, parentJSON);
			parentJSON.addElement(TableConstants.BO_DEPT, boDept);
			boolean isSessionExpired = true;
			String userID = null;
			Statement stmt = null, localStmt = null;
			ResultSet res = null;
			Connection con = MySqlDatabaseConnection
					.getConnection(getServletContext());
			synchronized (session) {
				userID = userSession.getUserID();
				getLogger().info("User: " + userID
						+ " requested for his/her projects whose session is valid");
			}
			isSessionExpired = false;
			try {
				stmt = con.createStatement();
				localStmt = con.createStatement();
				String genericProfileDate = callSuccess(parentJSON, localStmt);
				String boDeptString = getBoDept(userSession, localStmt, parentJSON);
				res = stmt
						.executeQuery(createQuery(userSession, appTitle, userID, boDeptString, parentJSON, genericProfileDate));
				if (res == null) {
					parentJSON = callFailure(error, parentJSON);
				} else {
					if (res.wasNull()) {
						error = "Unable to connect to database, right now. Please try after some time!";
						parentJSON = callFailure(error, parentJSON);
					} else {
						parentJSON.createNewJSONArray();
						try {
							boolean doGetProjectsValid = true;
							int total = 0;
							inProgressIndex = 0;
							int currentPageBegin = (currentPage - 1) * 20;
							int currentPageEnd = currentPage * 20;
							while (res.next()) {
								try {
									if(isWithinTheRange(currentPageBegin, currentPageEnd, total)) {
										parentJSON = callAddElementsToJSON(userSession, parentJSON,
												appTitle, res, doGetProjectsValid,
												inProgressIndex, localStmt,
												inProgressIndex, genericProfileDate);
									}
									total++;
								} catch (JSONException e) {
									parentJSON = callFailure(
											"Failed to get Projects. Please try again later!",
											e, parentJSON);
									parentJSON.setValid(false);
									doGetProjectsValid = false;
									break;
								}
							}
							setTotalProjects(total, parentJSON);
							getLogger().info("Total projects count: " + total);
							if (doGetProjectsValid) {
								getLogger().info("Succesful retreival of all his/her projects by: "
										+ userSession.getUserID());
							}
						} catch (SQLException e) {
							parentJSON = callFailure(
									"May be no Projects available under your account. ",
									e, parentJSON);
							parentJSON.setValid(false);
						}
					}
				}

			} catch (SQLException e) {
				parentJSON = callFailure(error, e, parentJSON);
			} finally {
				try {
					if (isSessionExpired) {
						super.writeFinalOP(parentJSON, null,
								boDept, error, isSessionExpired, 0);
					} else {
						writeFinalOP(parentJSON, out, boDept,
								userID, inProgress, inProgressIndex,
								isAdmin(userSession, localStmt, parentJSON), res, stmt);
					}
					if(stmt != null) {
						stmt.close();
					}
					if(localStmt != null) {
						localStmt.close();
					}
					if(res != null) {
						res.close();
					}
				} catch (SQLException e) {
					parentJSON = callFailure(error, e, parentJSON);
					super.writeFinalOP(parentJSON, null, boDept,
							error, isSessionExpired, 0);
				}
			}
		} else {
			super.writeFinalOP(parentJSON, out, boDept, result, inProgress, inProgressIndex);
		}
	}
	
	protected boolean isWithinTheRange(int currentPageBegin, int currentPageEnd, int total) {
		return total >= currentPageBegin && total < currentPageEnd;
	}

	protected void setTotalProjects(int total, BuildJSON parentJSON) {
		parentJSON.setLocalVariableInt(total);
	}
	
	protected void setFilterUserID(String filterUserID, BuildJSON parentJSON) {
		parentJSON.setFilterUserID(filterUserID);
	}
	
	protected void setCurrentPage(int currentPage, BuildJSON parentJSON) {
		parentJSON.setCurrentPage(currentPage);
	}

	protected String callSuccess(BuildJSON parentJSON, Statement localStmt) {
		return null;
	}
	
	protected BuildJSON callAddElementsToJSON(UserSessionObject userSession,
			BuildJSON parentJSON, String error, ResultSet res,
			boolean inProgress, int inProgressIndex, Statement localStmt,
			int index, String genericProfileDate) throws SQLException {
		if (userSession.isAdmin()
				|| res.getString(TableConstants.BO_DEPT).equalsIgnoreCase(
						userSession.getUniqueID())) {
			index++;
		}
		parentJSON = addElementsToJSON(parentJSON, error, res, inProgress,
				index, localStmt, index);
		return parentJSON;
	}

	protected String createQuery(UserSessionObject userSession,
			String appTitle, String userID, String boDept, BuildJSON parentJson, String genericProfileDate) {
		String query;
		if (userSession.isAdmin()) {
			getLogger().info(userID + ": Admin's request for his/her Projects.");
			if (parentJson.getFilterUserID() != null
					&& !parentJson.getFilterUserID().equals("All Users")
					&& !parentJson.getFilterUserID().equals("All")) {
				query = "SELECT * FROM Projects WHERE "
						+ TableConstants.BO_DEPT + " LIKE '%"
						+ parentJson.getFilterUserID() + "%' ORDER BY "
						+ TableConstants.DATE + " DESC;";
			} else {
				query = "SELECT * FROM Projects ORDER BY " + TableConstants.DATE
						+ " DESC;";
			}
		} else {
			getLogger().info(userID
					+ ": Normal User's request for his/her Projects.");
			String parameter = appTitle != null ? "' AND "
					+ TableConstants.APP_TITLE + "='" + appTitle
					+ "' ORDER BY " + TableConstants.DATE + " DESC;"
					: "' ORDER BY " + TableConstants.DATE + " DESC;";
			String filterString;
			if (userSession.isAdmin()) {
				filterString = parameter;
			} else {
				filterString = "' AND " + TableConstants.BO_DEPT + " LIKE '%"
						+ boDept + "%" + parameter;
			}
			query = "SELECT * FROM " + TableConstants.TABLE_PROJECTS
					+ " WHERE " + TableConstants.CURRENT_STATUS + "!='"
					+ TableConstants.REMOVE + filterString;
		}

		return query;
	}
	
	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, String error,
			ResultSet res, boolean inProgress, int inProgressIndex,
			Statement localStmt, int index) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.BO_DEPT,
					res.getString(TableConstants.BO_DEPT));
			parentJSON.addElement(TableConstants.ID,
					res.getInt(TableConstants.ID));
			parentJSON.addElement(TableConstants.TSC_NAME,
					res.getString(TableConstants.TSC_NAME));
			parentJSON.addElement(TableConstants.TSC_TITLE,
					res.getString(TableConstants.TSC_TITLE));
			parentJSON.addElement(TableConstants.TSC_DEPT,
					res.getString(TableConstants.TSC_DEPT));
			parentJSON.addElement(TableConstants.TSC_EMAIL,
					res.getString(TableConstants.TSC_EMAIL));
			parentJSON.addElement(TableConstants.TSC_PHONE,
					res.getString(TableConstants.TSC_PHONE));
			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));
			String isSigningReq = res.getString(TableConstants.IS_SIGNING_REQ);
			parentJSON.addElement(TableConstants.IS_SIGNING_REQ,
					isSigningReq);
			String MI_Instance = res.getString(TableConstants.MI_INSTANCE).replace(" ", "_");
			parentJSON.addElement(TableConstants.MI_INSTANCE,
					MI_Instance);
			parentJSON.addElement(TableConstants.APP_TITLE,
					res.getString(TableConstants.APP_TITLE));
			parentJSON.addElement(TableConstants.APP_DESCRIPTION,
					res.getString(TableConstants.APP_DESCRIPTION));
			parentJSON.addElement(TableConstants.APP_CURRENT_VERSION,
					res.getString(TableConstants.APP_CURRENT_VERSION));
			parentJSON.addElement(TableConstants.APP_MIN_OS,
					res.getString(TableConstants.APP_MIN_OS));
			parentJSON.addElement(TableConstants.APP_SECURITY,
					res.getString(TableConstants.APP_SECURITY));
			parentJSON.addElement(TableConstants.APP_ADG_LEVEL,
					res.getString(TableConstants.APP_ADG_LEVEL));
			String imgUrl = GetImageDetails.getProjectIcon(res
					.getString(TableConstants.APP_TITLE));
			if (imgUrl == null) {
				imgUrl = TableConstants.ROOT_URL
						+ TableConstants.PROJECT_URL_PART
						+ TableConstants.IMAGE_PART
						+ TableConstants.NO_IMAGE_ICON;
			}
			parentJSON.addListOfElements(TableConstants.LIST_OF_IMAGES,
					GetImageDetails.getProjectImages(res
							.getString(TableConstants.APP_TITLE)));
			parentJSON.addElement(TableConstants.PROJECT_ICON, imgUrl);
			parentJSON.addElement(TableConstants.APP_DEVICES,
					res.getString(TableConstants.APP_DEVICES));

			parentJSON.addElement(TableConstants.IS_PUSH_DB,
					res.getString(TableConstants.IS_PUSH_DB));

			parentJSON.addElement(TableConstants.PUSH_PROFILE_PATH,
					res.getString(TableConstants.PUSH_PROFILE_PATH));

			String result;
			if (isSigningReq.equals("0")) {
				result = res.getString(TableConstants.RESULT).replace("</p>", "") + " on Mobile Iron " + MI_Instance + "</p>";
			} else {
				result = res.getString(TableConstants.RESULT);
			}
			parentJSON.addElement(TableConstants.RESULT, result);

			parentJSON.addElement(TableConstants.DATE,
					res.getString(TableConstants.DATE));
			if (res.getString(TableConstants.CURRENT_STATUS).equalsIgnoreCase(
					TableConstants.IN_PROGRESS)) {
				inProgress = true;
				inProgressIndex = index;
			}
			parentJSON.addElement(TableConstants.CURRENT_STATUS,
					res.getString(TableConstants.CURRENT_STATUS));
			parentJSON.addElement(
					TableConstants.REQUEST_COUNT,
					UpdateRequestCount.getAppMaxReqCount(localStmt,
							res.getString(TableConstants.APP_TITLE)));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			parentJSON = callFailure(error, e, parentJSON);
		}
		return parentJSON;
	}

	protected void writeFinalOP(BuildJSON parentJSON,
			ServletOutputStream out, String boDept, String result,
			boolean inProgress, int inProgressIndex, boolean isAdmin, ResultSet res,
			Statement stmt) {
		parentJSON.createNewJChild();
		parentJSON.addElement("InProgress", inProgress);
		parentJSON.addElement(TableConstants.BO_DEPT, boDept);
		parentJSON.addElement("Index", inProgress ? inProgressIndex : -1);
		parentJSON.addElement("Total", parentJSON.getLocalVariableInt());
		parentJSON.addElement(TableConstants.CURRENT_PAGE, parentJSON.getCurrentPage());
		parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
		try {
			if (!isAdmin) {
				if (parentJSON.isValid()) {

					out.write(parentJSON.getParentWithArray(
							TableConstants.RESPONSE).toString().getBytes());

				} else {
					out.write(parentJSON.getParentWithChild(
							TableConstants.RESPONSE).toString().getBytes());
				}
			} else {
				JSONObject responseObj = parentJSON
						.getParentWithArray(TableConstants.RESPONSE);
				res = GetUserDetails.getUsers(stmt);
				if (res != null) {
					addUsers(parentJSON, res, "Sorry, unable to process your request right now! Please try after some time.");
					if (parentJSON.isValid()) {
						out.write(parentJSON.getParentWithArray(
								TableConstants.USERS).toString().getBytes());
					} else {
						out.write(parentJSON.getParentWithChild(
								TableConstants.RESPONSE).toString().getBytes());
					}
				} else {
					if (parentJSON.isValid()) {
						out.write(responseObj.toString().getBytes());
					} else {
						out.write(parentJSON.getParentWithChild(
								TableConstants.RESPONSE).toString().getBytes());
					}
				}
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			parentJSON = callFailure("IO exception", e, parentJSON);
		}
	}

	private BuildJSON addUsers(BuildJSON parentJSON, ResultSet res, String error) {
		try {
			parentJSON.createNewJSONArray();
			while (res.next()) {
				try {
					addUsersToJSON(parentJSON, res, error);
				} catch (JSONException e) {
					parentJSON = callFailure("Unable to connect to Database. Please try again later!"
							+ e.getMessage(), parentJSON);
				}
			}
		} catch (SQLException e) {
			parentJSON = callFailure(error, e, parentJSON);
		}
		return parentJSON;
	}

	private BuildJSON addUsersToJSON(BuildJSON parentJSON, ResultSet res,
			String error) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.BO_DEPT,
					res.getString(TableConstants.BO_DEPT));
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
			parentJSON = callFailure(error, e, parentJSON);
		}
		return parentJSON;
	}

}
