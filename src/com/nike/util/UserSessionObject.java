package com.nike.util;

import java.io.Serializable;

public class UserSessionObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserSessionObject(String userID, String uniqueID, int isAdmin,
			String emailID) {
		super();
		this.userID = userID;
		this.uniqueID = uniqueID;
		this.isAdmin = isAdmin;
		this.emailID = emailID;
	}

	private String userID;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	private String uniqueID;
	
	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	private int isAdmin;

	public int getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	private String emailID;

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	
	public boolean isAdmin() {
		if (isAdmin == TableConstants.IS_PROD_ADMIN_VALUE) {
			return true;
		} else {
			return false;
		}
	}
}
