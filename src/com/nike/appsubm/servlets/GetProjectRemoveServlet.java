package com.nike.appsubm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.TableConstants;

/**
 * Servlet implementation class GetProjectRemoveServlet
 */
@WebServlet("/Private/GetProjectRemoveServlet")
public class GetProjectRemoveServlet extends GetProjectsSuccessServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager
			.getLogger(GetProjectRemoveServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected Logger getLogger() {
		return GetProjectRemoveServlet.logger;
	}

	@Override
	protected String getFinalQueryParameter(String parameter) {
		return TableConstants.CURRENT_STATUS + "='"
				+ TableConstants.REMOVE + parameter;
	}
	
	
	
}
