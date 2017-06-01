/*
 * 
 * This class has all the utility functions that deal with 
 * 	# processing the format of date,
 * 	# getCurrentYear
 * 	# getCurrentMonth
 * 	# getDate
 * 	# getNumberOfMonthDiff -> which returns the number of months between the current date and the date that is been passed.
 * 
 * */

package com.nike.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dates {

	static Logger logger =  LogManager.getLogger(Dates.class.getName());

	public static String getCurrentDate() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date).toString();
	}

	public static String[] months = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

	public static String getCurrentMonth() {
		return months[Calendar.getInstance().get(Calendar.MONTH)];
	}

	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	static DateFormat datePostFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String getDate(String date) {
		try {
			return datePostFormat.format(dateFormat.parse(date)).toString();
		} catch (ParseException e) {
			logger.error("ParseException in parsing the date " + ErrorUtils.getStackTrace(e));
			return date;
		}
	}

	static DateFormat ourDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static Date getOurFormatDate(String date) {

		try {
			return ourDateFormat.parse(date);
		} catch (ParseException e) {
			logger.error("ParseException in parsing the date " + ErrorUtils.getStackTrace(e));
			return null;
		}
	}

	public static int getNumberOfMonthDiff(String endDateString, String appTitle) {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(new Date());
		Date endDate = getOurFormatDate(endDateString);
		if (endDate == null) {
			endDate = new Date();
		}
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		int diffYear = endCalendar.get(Calendar.YEAR)
				- startCalendar.get(Calendar.YEAR);
		int months = diffYear * 12 + endCalendar.get(Calendar.MONTH)
				- startCalendar.get(Calendar.MONTH);
		logger.info(appTitle + " number of months active: " + months);
		return months;
	}

	public static int getNumberOfMonthDiffWithPreviousDate(
			String beginDateString) {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(new Date());
		Date beginDate = getOurFormatDate(beginDateString);
		if (beginDate == null) {
			beginDate = new Date();
		}
		Calendar beginCalendar = new GregorianCalendar();
		beginCalendar.setTime(beginDate);

		int diffYear = startCalendar.get(Calendar.YEAR)
				- beginCalendar.get(Calendar.YEAR);
		int months = diffYear * 12 + startCalendar.get(Calendar.MONTH)
				- beginCalendar.get(Calendar.MONTH);
		logger.info("number of months active: " + months);
		return months;
	}
	
	public static int getNumberOfDaysDiffBtwTwoDates(
			String beginDateString, String endDateString) {
		Date beginDate = getOurFormatDate(beginDateString);
		Date endDate = getOurFormatDate(endDateString);
		if (beginDate == null) {
			beginDate = new Date();
		}
		if(endDate == null) {
			endDate = new Date();
		}
		
		Calendar beginCalendar = new GregorianCalendar();
		beginCalendar.setTime(beginDate);

		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);
		
		int diffYear = endCalendar.get(Calendar.YEAR)
				- beginCalendar.get(Calendar.YEAR);
		int months = diffYear * 12 + endCalendar.get(Calendar.MONTH)
				- beginCalendar.get(Calendar.MONTH);
		int days = months * 30 + endCalendar.get(Calendar.DAY_OF_MONTH) - beginCalendar.get(Calendar.DAY_OF_MONTH);
		return days;
	}

	public static String getPreviousMonth() {
		return months[Calendar.getInstance().get(Calendar.MONTH) - 1];
	}

	public static boolean isDateGreater(String value, String compareTo) {
		Date valueDate = getOurFormatDate(value);
		Date compareToDate = getOurFormatDate(compareTo);
		if (valueDate.compareTo(compareToDate) == 0
				|| valueDate.compareTo(compareToDate) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
