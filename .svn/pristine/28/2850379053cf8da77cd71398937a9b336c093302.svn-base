package com.nike.appsubm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class LogoutServlet
 */
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger superLogger = LogManager.getLogger(LogoutServlet.class.getName());
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		superLogger.entry();
		if(session != null) {
			synchronized (session) {
				UserSessionObject userSession = (UserSessionObject) session
						.getAttribute(TableConstants.BO_DEPT);
				if (userSession != null) {
					superLogger.info("User: " + userSession.getUserID() + " has logged out.");
				} else {
					superLogger.info("User whose session id: " + session.getId() + " has logged out.");
				}
				logoutHelper(session, response, request);
			}
		}
		superLogger.exit(false);
	}
	
	private void logoutHelper(HttpSession session, HttpServletResponse response, ServletRequest request) {
		// Standard HTTP session invalidation
		session.invalidate();

		// Invalidate the SSL Session
		org.apache.tomcat.util.net.SSLSessionManager mgr =
		    (org.apache.tomcat.util.net.SSLSessionManager)
		    request.getAttribute("javax.servlet.request.ssl_session_mgr");
		mgr.invalidateSession();

		// Close the connection since the SSL session will be active until the connection
		// is closed
		response.setHeader("Connection", "close");
	}

}
