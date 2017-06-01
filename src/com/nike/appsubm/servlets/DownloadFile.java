/*
 * DownloadFile servlet allows the mobile iOS devices to let them install the app on 
 * their device by downloading the Manifest file.
 * This Servlet supports get calls only.
 * Every Project that is signed by Jenkins, do maintain or generates a manifest file which will downloaded.
 * This file is intended to let the users download the Manifest file.
 * 
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class DownloadFile
 */
public class DownloadFile extends HttpServlet {
	static Logger logger =  LogManager.getLogger(DownloadFile.class.getName());
	private static final long serialVersionUID = 1L;
	private static final int BYTES_DOWNLOAD = 1024;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition",
	                     "attachment;filename=manifest.plist");
		ServletContext ctx = getServletContext();
		InputStream is = ctx.getResourceAsStream("/Projects/Nike_Golf/New/manifest.plist");
	 
		int read=0;
		byte[] bytes = new byte[BYTES_DOWNLOAD];
		OutputStream os = response.getOutputStream();
	 
		while((read = is.read(bytes))!= -1){
			os.write(bytes, 0, read);
		}
		os.flush();
		os.close();	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
