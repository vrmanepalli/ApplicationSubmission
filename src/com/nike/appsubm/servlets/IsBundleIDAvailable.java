package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.ErrorUtils;
import com.nike.util.MySqlDatabaseConnection;
import com.nike.util.TableConstants;

/**
 * Servlet implementation class IsBundleIDAvailable
 */
@WebServlet("/Private/IsBundleIDAvailable")
public class IsBundleIDAvailable extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger =  LogManager.getLogger(IsBundleIDAvailable.class.getName());    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IsBundleIDAvailable() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bundleID = getBundleID(request);
		String requestType = getRequestType(request);
		String appTitle = getAppTitle(request);
		String mIappTitle = getMIAppTitle(request);
		Connection con = MySqlDatabaseConnection.getConnection(request.getServletContext());
		Statement stmt = null;
		BuildJSON jsonResp = new BuildJSON();
		jsonResp.createNewJChild();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		try {
			stmt = con.createStatement();
			if(bundleID != null && appTitle != null) {
				ResultSet res = stmt
						.executeQuery("SELECT COUNT(*) from Projects WHERE "
								+ TableConstants.APP_BUNDLE_ID + "='"
								+ bundleID + "' AND "
								+ TableConstants.APP_TITLE + "!='" + appTitle
								+ "';");
				res.next();
				if (res.getInt(1) > 0) {
					logger.info("Have a project with BundleID: " + bundleID);
					jsonResp.addElement(TableConstants.MESSAGE, false);
				} else {
					jsonResp.addElement(TableConstants.MESSAGE, true);
					logger.info("Have no projects with BundleID: " + bundleID);
				}
				int updateCount = stmt.executeUpdate("UPDATE "
						+ TableConstants.TABLE_PROJECTS + " SET "
						+ TableConstants.MI_APP_TITLE + "='" + mIappTitle
						+ "' WHERE " + TableConstants.APP_TITLE + "='"
						+ appTitle + "';");
				res.close();
			}
		} catch (SQLException e) {
			jsonResp.addElement(TableConstants.MESSAGE, true);
			logger.error("Failed to run the check about Bundle ID." + ErrorUtils.getStackTrace(e));
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Failed to close the Statement: " + ErrorUtils.getStackTrace(e));
				}
			}
		}
		out.write(jsonResp.getParentWithChild(TableConstants.RESPONSE).toString());
	}
	
	protected String getBundleID(HttpServletRequest request) {
		String bundleID;
		try {
			bundleID = request
					.getParameter(TableConstants.APP_BUNDLE_ID);
		} catch (Exception e) {
			bundleID = null;
			logger.error(ErrorUtils.getStackTrace(e));
		}
		return bundleID;
	}
	
	protected String getRequestType(HttpServletRequest request) {
		String requestType;
		try {
			requestType = request
					.getParameter("RequestType");
		} catch (Exception e) {
			requestType = null;
			logger.error(ErrorUtils.getStackTrace(e));
		}
		return requestType;
	}
	
	protected String getAppTitle(HttpServletRequest request) {
		String appTitle;
		try {
			appTitle = request
					.getParameter(TableConstants.APP_TITLE);
		} catch (Exception e) {
			appTitle = null;
			logger.error(ErrorUtils.getStackTrace(e));
		}
		return appTitle;
	}
	
	protected String getMIAppTitle(HttpServletRequest request) {
		String mIappTitle;
		try {
			mIappTitle = request
					.getParameter(TableConstants.MI_APP_TITLE);
		} catch (Exception e) {
			mIappTitle = null;
			logger.error(ErrorUtils.getStackTrace(e));
		}
		return mIappTitle;
	}

}
