package com.nike.util;

import java.io.IOException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import com.idmworks.nike.usermanagerclient.AuthenticatedStatus;
import com.idmworks.nike.usermanagerclient.User;
import com.idmworks.nike.usermanagerclient.UserManager;
import com.idmworks.nike.usermanagerclient.UserManagerRest;


public class ExternalAuthentication extends Authentication {
	
	private String[] userMembership;
	private User user;

	public BuildJSON authenticate(String username, String password,
			Logger logger, Statement stmt, HttpServletRequest request,
			BuildJSON parentJSON, HttpServletResponse response)
			throws Exception {
		String authUserName = "nikemobappsub";
		String authPassword = "N1keM0b3!4@@ppSub3";
		String appId = "MOBILEAPPSUB";
		String baseEndPointUrl = "https://extusrmgr-dev.nike.com/usermanager/";
		UserManager userManager = new UserManagerRest(baseEndPointUrl, appId,
				authUserName, authPassword);
		AuthenticatedStatus authenticatedStatusObj = userManager.authenticate(
				username, password);
		if (authenticatedStatusObj == AuthenticatedStatus.Authenticated
				|| authenticatedStatusObj == AuthenticatedStatus.AuthenticatedInternalUser) {
			user = userManager.getUser(username);
			logger.info("User details: " + user.toString());
			processUser();
			userMembership = userManager.getUserGroupMembership(username);
			logger.info("User Membership details: " + userMembership.toString());
			BuildJSON doUserExistJSON = UserCheckUtil.doUserExist(stmt, userID, parentJSON, logger);
			if (doUserBelongsToAGroup(userMembership, authUserName, stmt)) {
				if(doUserExistJSON != null) {
					parentJSON = doUserExistJSON;
					return processUserDetailsFromLocalDatabase(request, response,
							parentJSON);
				} else {
					return addUserToLocalDatabase(request, response, stmt,
							parentJSON);
				}
			} 
		}
		return null;

	}

	private void processUser() {
		userID = user.userId;
		emailID = user.email;
		firstName = user.firstName;
		lastName = user.lastName;
		phoneNo = user.telephoneNumber;
		localPasswordString = user.password;
		
	}
	
	@Override
	public void redirectResponse(HttpServletResponse response, String boDept) throws IOException {
		//Do nothing like page redirect for external user login. because it will be handled by the client script.
//		response.sendRedirect(TableConstants.WELCOME_PAGE_URL);
	}

	public  boolean doUserBelongsToAGroup(String[] data, String userID, Statement stmt) {
		for (String val : data) {
			// Group:
			// CN=Shareddata.Projects.WHQ.Owner,OU=SharedData,OU=Groups,DC=ad,DC=nike,DC=com
			if(doExist(val, stmt)) {
				return true;
			}
		}
		return false;
	}

	public String[] getUserMembership() {
		return userMembership;
	}

	public void setUserMembership(String[] userMembership) {
		this.userMembership = userMembership;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
