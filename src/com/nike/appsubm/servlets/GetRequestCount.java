/*
 * GetRequestCount servlet provides the analytics i.e the details of how many signing + deployment requests as per each month and year.
 * supports get call only.
 * ->callFailure(String error):	returns none. Creates a JSON response with failure details.
 * ->callSuccess():	returns none. Runs the query and generates a JSON response out of the query result. 
 * 					Creates JSON response with number of requests details.
 * ->addTotalValuesToJSON(): returns none. Constructing the JSON response with the total request of a particular project in a particular year.
 * ->createQuery(): returns String. Creates a query string for selecting the rows of all the projects in a particular year.
 * ->addElementsToJSON(): return none. Constructing the JSON response with the request counts as per month of a particular project in a particular year.
 * ->getTotalRequestPerMonth(): returns none. Runs a query to calculate the total requests count in a year of each project and iterates through each project.
 */
package com.nike.appsubm.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.util.BuildJSON;
import com.nike.util.Dates;
import com.nike.util.FieldValidator;
import com.nike.util.TableConstants;
import com.nike.util.UserSessionObject;

/**
 * Servlet implementation class GetRequestCount
 */
public class GetRequestCount extends SuperGetMethodsServlet {
	static Logger logger = LogManager
			.getLogger(GetRequestCount.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRequestCount() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		logger.exit(false);
	}

	@Override
	protected void setLoggerEntry(String userID) {
		logger.entry(userID);
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
		parentJSON.addElement(TableConstants.ERROR, error);
		parentJSON.setValid(false);
		logger.error(error);
		return parentJSON;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	
	@Override
	protected boolean isValidCheck(BuildJSON parentJSON,
			HttpServletRequest request, UserSessionObject userSessionObject) {
		String year = request.getParameter("InYear") == null
				|| request.getParameter("InYear") == "" ? "0" : request
				.getParameter("InYear");
		int inYear = Integer.parseInt(year);
		parentJSON.setLocalVariableInt(inYear);
		return FieldValidator.isValidyear(inYear);
	}

	@Override
	protected void callSuccess(HttpSession httpSession, BuildJSON parentJSON,
			ResultSet res, UserSessionObject userSession, String boDept,
			Statement stmt, Statement localStmt, HttpServletRequest request,
			ServletOutputStream out) {
		try {
			parentJSON.createNewJSONArray();
			while (res.next()) {
				parentJSON = addElementsToJSON(parentJSON, res);
			}
			parentJSON = addTotalValuesToJSON(userSession, parentJSON, localStmt);
			logger.info("Successful retreival of Metrics by user: "
					+ userSession.getUserID() + " For year " + parentJSON.getLocalVariableInt());
		} catch (SQLException e) {
			parentJSON = callFailure(e.getLocalizedMessage(), e, parentJSON);
		}
	}

	@Override
	protected void writeFinalOP(BuildJSON parentJSON, ServletOutputStream out,
			String boDept, String result, boolean inProgress,
			int inProgressIndex) {
		try {
			if (parentJSON.isValid()) {
				parentJSON.createNewJChild();
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				parentJSON.getParentWithChild(TableConstants.CURRENT_STATUS);
				out.write(parentJSON
						.getParentWithArray(TableConstants.RESPONSE).toString().getBytes());
			} else {
				parentJSON.addElement(TableConstants.BO_DEPT, boDept);
				out.write(parentJSON.getParentWithChild(TableConstants.RESPONSE).toString().getBytes());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			getLogger().error("Failed to write back to user.");
		}
		
	}

	private BuildJSON addTotalValuesToJSON(UserSessionObject userSession, BuildJSON parentJSON, Statement localStmt) {
		HashMap<String, Integer> hashMapOfTotal = getTotalRequestPerMonth(userSession, parentJSON, localStmt);

		parentJSON.createNewJChild();
		parentJSON.addElement("TotalRequests", "TotalRequests");
		parentJSON.addElement(TableConstants.IN_YEAR, Dates.getCurrentYear());
		parentJSON.addElement("In_" + Dates.months[0],
				hashMapOfTotal.get(Dates.months[0]));
		parentJSON.addElement("In_" + Dates.months[1],
				hashMapOfTotal.get(Dates.months[1]));
		parentJSON.addElement("In_" + Dates.months[2],
				hashMapOfTotal.get(Dates.months[2]));
		parentJSON.addElement("In_" + Dates.months[3],
				hashMapOfTotal.get(Dates.months[3]));
		parentJSON.addElement("In_" + Dates.months[4],
				hashMapOfTotal.get(Dates.months[4]));
		parentJSON.addElement("In_" + Dates.months[5],
				hashMapOfTotal.get(Dates.months[5]));
		parentJSON.addElement("In_" + Dates.months[6],
				hashMapOfTotal.get(Dates.months[6]));
		parentJSON.addElement("In_" + Dates.months[7],
				hashMapOfTotal.get(Dates.months[7]));
		parentJSON.addElement("In_" + Dates.months[8],
				hashMapOfTotal.get(Dates.months[8]));
		parentJSON.addElement("In_" + Dates.months[9],
				hashMapOfTotal.get(Dates.months[9]));
		parentJSON.addElement("In_" + Dates.months[10],
				hashMapOfTotal.get(Dates.months[10]));
		parentJSON.addElement("In_" + Dates.months[11],
				hashMapOfTotal.get(Dates.months[11]));
		parentJSON.addElement(TableConstants.PREVIOUS_YEAR,
				Dates.getCurrentYear() - 1);
		parentJSON.addElement(TableConstants.CURRENT_YEAR,
				Dates.getCurrentYear());
		parentJSON.addJObjectToJArray();
		return parentJSON;
	}

	@Override
	protected String createQuery(UserSessionObject userSession,
			BuildJSON parentJson, String userID) {
		if (userSession.isAdmin()) {
			return "SELECT * FROM " + TableConstants.TABLE_REQUEST_COUNT
					+ " WHERE " + TableConstants.IN_YEAR + "=" + parentJson.getLocalVariableInt() + ";";// LIMIT
																				// 0,
																				// 50;";
		} else {
			return "SELECT * FROM " + TableConstants.TABLE_REQUEST_COUNT
					+ " WHERE " + TableConstants.IN_YEAR + "=" + parentJson.getLocalVariableInt()
					+ " AND " + TableConstants.BO_DEPT + " LIKE '%" + userSession.getUniqueID() + "%';";// LIMIT
																				// 0,
																				// 25';";
		}
	}

	protected BuildJSON addElementsToJSON(BuildJSON parentJSON, ResultSet res) {
		parentJSON.createNewJChild();
		try {
			parentJSON.addElement(TableConstants.APP_TITLE,
					res.getString(TableConstants.APP_TITLE));
			parentJSON.addElement(TableConstants.IN_YEAR,
					Dates.getCurrentYear());
			parentJSON.addElement(Dates.months[0], res.getInt(Dates.months[0]));
			parentJSON.addElement(Dates.months[1], res.getInt(Dates.months[1]));
			parentJSON.addElement(Dates.months[2], res.getInt(Dates.months[2]));
			parentJSON.addElement(Dates.months[3], res.getInt(Dates.months[3]));
			parentJSON.addElement(Dates.months[4], res.getInt(Dates.months[4]));
			parentJSON.addElement(Dates.months[5], res.getInt(Dates.months[5]));
			parentJSON.addElement(Dates.months[6], res.getInt(Dates.months[6]));
			parentJSON.addElement(Dates.months[7], res.getInt(Dates.months[7]));
			parentJSON.addElement(Dates.months[8], res.getInt(Dates.months[8]));
			parentJSON.addElement(Dates.months[9], res.getInt(Dates.months[9]));
			parentJSON.addElement(Dates.months[10],
					res.getInt(Dates.months[10]));
			parentJSON.addElement(Dates.months[11],
					res.getInt(Dates.months[11]));
			parentJSON.addJObjectToJArray();
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		}
		return parentJSON;
	}

	public HashMap<String, Integer> getTotalRequestPerMonth(UserSessionObject userSession, BuildJSON parentJSON, Statement localStmt) {
		String filterString;
		if(userSession.isAdmin()) {
			filterString = "";
		} else {
			filterString = " AND " + TableConstants.BO_DEPT + " LIKE '%" + userSession.getUniqueID() + "%'";
		}
		String query = "SELECT SUM(January) as January, SUM(February) as February, SUM(March) as March, SUM(April) as April, SUM(May) as May, SUM(June) as June, SUM(July) as July, SUM(August) as August, SUM(September) as September, SUM(October) as October, SUM(November) as November, SUM(December) as December FROM "
				+ TableConstants.TABLE_REQUEST_COUNT
				+ " WHERE "
				+ TableConstants.IN_YEAR + "=" + parentJSON.getLocalVariableInt() + filterString + ";";
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		try {
			ResultSet result = localStmt.executeQuery(query);
			while (result.next()) {
				for (int i = 0; i < 12; i++) {
					hashMap.put(Dates.months[i], result.getInt(Dates.months[i]));
				}
			}
		} catch (SQLException e) {
			callFailure(e.getMessage(), e, parentJSON);
		}
		return hashMap;
	}


}
