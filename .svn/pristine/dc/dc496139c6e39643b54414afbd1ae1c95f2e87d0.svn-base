/*
 * MobileCheck utility class has all the methods that are required to do the following with Mobile:
 * 	-> isMobile : Returns true or false
 * 	-> getDeviceName: Gives the information about the device name
 * 	-> doTokenExists: Check whether the device token has already been inserted into the Business owner table
 * 	-> isDuplicateToken: Tries to figure out if its a token which already exists in the tabel or not
 * 	-> doTokenMatches: Validates the token
 * 	-> doInsertToken: Insert the device token into the Business Owner table.
 * 
 * */
package com.nike.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobileCheck {

	private static String deviceName = "";
	static Logger logger = LogManager.getLogger(MobileCheck.class.getName());

	public static boolean isMobile(String ua) {
		// String ua = request.getHeader("User-Agent").toLowerCase();
		deviceName = getDeviceName(ua);
		if (ua.matches(".*(android.+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*")
				|| ua.substring(0, 4)
						.matches(
								"1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|e\\-|e\\/|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\\-|2|g)|yas\\-|your|zeto|zte\\-")) {
			logger.info("Mobile device request " + ua);
			return true;
		} else if (deviceName != null
				&& (deviceName.equalsIgnoreCase(TableConstants.IPHONE)
						|| deviceName.equalsIgnoreCase(TableConstants.IPAD) || deviceName
							.equalsIgnoreCase(TableConstants.IPOD))) {
			logger.info("Mobile device request " + deviceName);
			return true;
		} else {
			logger.info("Not a Mobile device " + ua);
			return false;
		}
	}

	public static String getDeviceName(String ua) {
		if (ua != null && !ua.equals("") && ua.contains("(")
				&& ua.contains(";")) {
			String[] uaArray = ua.split("\\(");
			if (uaArray.length > 0) {
				ua = uaArray[1].split(";").length > 0 ? uaArray[1].split(";")[0]
						: null;
				return ua;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static boolean doTokenExists(String Token, ResultSet rs, String ua) {
		try {
			deviceName = getDeviceName(ua);
			if (deviceName.equalsIgnoreCase(TableConstants.IPHONE)
					&& rs.getString(TableConstants.IPHONE).equalsIgnoreCase(
							"YES")) {
				logger.info("Token exists Iphone ");
				return true;
			} else if (deviceName.equalsIgnoreCase(TableConstants.IPAD)
					&& rs.getString(TableConstants.IPAD)
							.equalsIgnoreCase("YES")) {
				logger.info("Token exists Ipad ");
				return true;
			} else if (deviceName.equalsIgnoreCase(TableConstants.IPOD)
					&& rs.getString(TableConstants.IPOD)
							.equalsIgnoreCase("YES")) {
				logger.info("Token exists Ipod ");
				return true;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the device name " + ErrorUtils.getStackTrace(e));
			return false;
		}
		return false;
	}

	public static boolean isDuplicateToken(String Token, Statement stmt,
			String userID) {
		String query = "SELECT COUNT(*) as " + TableConstants.COUNT + " FROM "
				+ TableConstants.TABLE_BUSINESS_OWNER + " WHERE ("
				+ TableConstants.IPHONE_TOKEN + "='" + Token + "' OR "
				+ TableConstants.IPAD_TOKEN + "='" + Token + "' OR "
				+ TableConstants.IPOD_TOKEN + "='" + Token + "') AND "
				+ TableConstants.USER_ID + "!='" + userID + "';";
		try {
			ResultSet res = stmt.executeQuery(query);
			if (res.next() && res.getInt("count") > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the device name " + ErrorUtils.getStackTrace(e));
			return false;
		}
	}

	public static boolean doTokenMatches(String Token, ResultSet rs) {
		try {
			if (Token.equals(rs.getString(TableConstants.IPHONE_TOKEN))
					|| Token.equals(rs.getString(TableConstants.IPAD_TOKEN))
					|| Token.equals(rs.getString(TableConstants.IPOD_TOKEN))) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the device name " + ErrorUtils.getStackTrace(e));
		}
		return false;
	}

	public static boolean doInsertToken(String Token, String userID,
			Statement stmt, String ua) {
		deviceName = getDeviceName(ua);
		String query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER
				+ " SET " + TableConstants.IPHONE_TOKEN + "='" + Token + "', "
				+ TableConstants.IPHONE + "='YES' WHERE "
				+ TableConstants.USER_ID + "='" + userID + "';";
		if (deviceName.equalsIgnoreCase(TableConstants.IPHONE)) {

		} else if (deviceName.equalsIgnoreCase(TableConstants.IPAD)) {
			query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET "
					+ TableConstants.IPAD_TOKEN + "='" + Token + "', "
					+ TableConstants.IPAD + "='YES' WHERE "
					+ TableConstants.USER_ID + "='" + userID + "';";
		} else if (deviceName.equalsIgnoreCase(TableConstants.IPOD)) {
			query = "UPDATE " + TableConstants.TABLE_BUSINESS_OWNER + " SET "
					+ TableConstants.IPOD_TOKEN + "='" + Token + "', "
					+ TableConstants.IPOD + "='YES' WHERE "
					+ TableConstants.USER_ID + "='" + userID + "';";
		} else {
			return false;
		}
		try {
			System.out.println(query);
			if (stmt.executeUpdate(query) == 1) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("SQLException in getting the device name " + ErrorUtils.getStackTrace(e));
			return false;
		}
		return false;
	}
}
