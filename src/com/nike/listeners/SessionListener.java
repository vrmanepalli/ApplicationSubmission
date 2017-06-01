package com.nike.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Application Lifecycle Listener implementation class SessionListener
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener {
	
	static Logger logger = LogManager.getLogger(SessionListener.class.getName());
	static String JENKINS_PWD = null;
	static String DATABASE_PWD = null;
	static String GENERIC_USER_PWD = null;
	

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		try {
//			HttpSession session = arg0.getSession();
//			UserSessionObject sessionObject = (UserSessionObject) session.getAttribute(TableConstants.BO_DEPT);
//			session.setAttribute(TableConstants.BO_DEPT, sessionObject);
//			logger.info("Session Created for user " + sessionObject.getUniqueID());
		} catch (Exception e) {
			logger.info("New unregistered Session Created " + arg0.getSession().getId());
		}
		
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		try {
			UserSessionObject sessionObject = (UserSessionObject) arg0.getSession().getAttribute(TableConstants.BO_DEPT);
			logger.info("Session destroyed for user " + sessionObject.getUniqueID());
		} catch (Exception e) {
			logger.info("New unregistered Session destroyed " + arg0.getSession().getId());
		}
	}
	
}
