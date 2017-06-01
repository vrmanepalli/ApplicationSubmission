package com.nike.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class EmailValidator {
 
	private static Matcher matcher;
 
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
 
	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validate(final String hex) {
		if(hex != null) {
			matcher = EmailValidator.pattern.matcher(hex);
			return matcher.matches();
		} else {
			return false;
		}
 
	}
	
	public static boolean isAValidApprovedEmailIDs(String approvedEmailIDs) {
		if (approvedEmailIDs.contains(", ")) {
			String[] emails = approvedEmailIDs.split(", ");
			boolean isValid = true;
			for (String email : emails) {
				if (!validate(email)) {
					isValid = false;
					break;
				}
			}
			return isValid;
		} else if (validate(approvedEmailIDs)) {
			return true;
		}
		return false;
	}
	
	public static boolean isAValidBoDepts(String validBoDepts) {
		if (validBoDepts.contains(", ")) {
			String[] boDpets = validBoDepts.split(", ");
			boolean isValid = true;
			for (String boDept : boDpets) {
				if (boDept != null && boDept.length() > 2) {
					isValid = true;
				}
			}
			return isValid;
		} else if (validBoDepts != null && validBoDepts.length() > 2) {
			return true;
		}
		return false;
	}
}
