package com.nike.appsubm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class GetProjectsFailedServlet
 */
@WebServlet("/Private/GetProjectsFailedServlet")
public class GetProjectsFailedServlet extends GetProjectsSuccessServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager
			.getLogger(GetProjectsFailedServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProjectsFailedServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected Logger getLogger() {
		return GetProjectsFailedServlet.logger;
	}
	
}
