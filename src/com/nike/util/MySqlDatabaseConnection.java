/*
 * MySqlDatabaseConnection is just a utility class which provides the database connection reference:
 * 	-> getConnection(ServletContext servletContext) : returns a connection
 * 	-> getJSONObject(HttpServletRequest request, BuildJSON parentJSON) : returns a JSONObject
 */
package com.nike.util;

import java.io.BufferedReader;
import java.sql.Connection;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;

public class MySqlDatabaseConnection {
	static Logger logger = LogManager.getLogger(MySqlDatabaseConnection.class.getName());
	
	public static Connection getConnection(ServletContext servletContext) {
		return (Connection) servletContext.getAttribute("DBConnection");
	}
	
	public static JSONObject getJSONObject(HttpServletRequest request, BuildJSON parentJSON) {
		String line;
		StringBuffer jb = new StringBuffer();
		JSONObject jsonObject = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
			jsonObject = JSONObject.fromObject(jb.toString());
		} catch (Exception e) {
			parentJSON.addElement("Error", ErrorUtils.getStackTrace(e));
			logger.error("Error " + ErrorUtils.getStackTrace(e));
		}
		
		return jsonObject;
	}
	
	public static JSONObject getJSONObjectFromGetCall(HttpServletRequest request, BuildJSON parentJSON) {
		JSONObject jsonObject = new JSONObject();
		Enumeration<String> attributes = request.getParameterNames();
		while(attributes.hasMoreElements()) {
			String element = attributes.nextElement();
			jsonObject.put(element, request.getParameter(element));
		}
		return jsonObject;
	}
}
