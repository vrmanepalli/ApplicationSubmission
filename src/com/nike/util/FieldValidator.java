package com.nike.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nike.appsubm.servlets.SearchServlet;

public class FieldValidator {

	private static Pattern noSpecialCharInStringMatch = Pattern
			.compile("^[a-zA-Z]");
	
	private static Pattern firstNameMatch = Pattern.compile("^[A-Za-z]*");
	
	private static Pattern lastNameMatch = Pattern.compile("^[a-zA-z]+([ '-][a-zA-Z0-9]+)*");
	
	private static Pattern userNameMatch = Pattern.compile("^[A-Za-z0-9_-]*");
	
	private static Pattern appTitleMatch = Pattern.compile("^[a-zA-Z0-9_ ]*");
	
	private static Pattern appDevicesMatch = Pattern.compile("^[a-zA-Z, ]+$");
	
	private static Pattern regularStringMatch = Pattern.compile("^[a-zA-Z ]*");
	
	private static Pattern currentVersionNumberMatch = Pattern.compile("(?!\\.)(\\d+(\\.\\d+)+)(?![\\d\\.])");
	
	private static Pattern VersionNumberMatch = Pattern.compile("^\\d+(\\.\\d+){0,2}$");
	
	static Logger logger =  LogManager.getLogger(FieldValidator.class.getName());

	protected static boolean isAValidEmailID(final String emailID) {
		return EmailValidator.validate(emailID);
	}

	public static boolean isAValidName(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = noSpecialCharInStringMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}
	
	public static boolean isAValidAppDevices(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = appDevicesMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}
	
	public static boolean isAValidFirstName(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = firstNameMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}
	
	public static boolean isAValidLastName(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = lastNameMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}
	
	public static boolean isAValidUserName(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = userNameMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}

	public static boolean isAValidAppTitle(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = appTitleMatch.matcher(name);
			return m.matches();
		} else {
			logger.info("Incorrect app title: " + name);
			return false;
		}
	}

	public static boolean isAValidString(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = regularStringMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}
	
	public static boolean isValidyear(int yearValue) {
		try {
			if (yearValue >= 2012 && yearValue <= 2100) {
				return true;
			} else {
				return false;
			}
		} catch (Exception pe) {
			return false;
		}
	}
	
	public static boolean isValidVersionNumber(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = VersionNumberMatch.matcher(name);
			if(m.matches()) {
				return true;
			} else {
				return isCurrentValidVersionNumber(name);
			}
		} else {
			return false;
		}
	}
	
	public static boolean isCurrentValidVersionNumber(final String name) {
		if (name != null && name != "" && name.length() > 1) {
			Matcher m = currentVersionNumberMatch.matcher(name);
			return m.matches();
		} else {
			return false;
		}
	}

	public static boolean validatePhoneNumber(String phoneNo) {
		// validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}"))
			return true;
		// validating phone number with -, . or spaces
		else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
			return true;
		// validating phone number with extension length from 3 to 5
		else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
			return true;
		// validating phone number where area code is in braces ()
		else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
			return true;
		// return false if nothing matches the input
		else
			return false;

	}

}
