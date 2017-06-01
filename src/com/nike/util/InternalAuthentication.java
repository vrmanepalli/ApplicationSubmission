package com.nike.util;

import java.sql.Statement;
import java.util.Collection;

import org.apache.commons.collections.MultiMap;

public class InternalAuthentication extends Authentication{

	public boolean processDataToken(MultiMap data, Statement stmt) {
		// {Groups=[Group:CN=Shareddata.Projects.WHQ.Owner,OU=SharedData,OU=Groups,DC=ad,DC=nike,DC=com],
		// not-before=[2014-10-22T18:48:21Z],
		// authnContext=[urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified],
		// subject=[VasudevaRao.Manepalli@nike.com],
		// not-on-or-after=[2014-10-22T18:53:21Z, UserID=VMANEP],
		// FirstName=[Vasudeva Rao, renew-until=2014-10-22T18:53:21Z],
		// LastName=[Manepalli, Department=TAS - APS, PhoneNo=503-532-3456}]
		Collection collection = (Collection) data.get("Department");
		department = "";
		if (collection != null) {
			for (Object val : collection) {
				department = val.toString();
			}
		}
		userID = null;
		collection = (Collection) data.get(TableConstants.USER_ID);
		for (Object val : collection) {
			userID = val.toString();
			String uniqueID = department.hashCode() + "_" + userID + "_"
					+ department.hashCode();
		}
		collection = (Collection) data.get("subject");
		emailID = null;
		for (Object val : collection) {
			emailID = val.toString();
		}

		collection = (Collection) data.get(TableConstants.PHONE_NO);
		phoneNo = null;
		if (collection != null) {
			for (Object val : collection) {
				phoneNo = val.toString();
			}
		} else {
			phoneNo = "";
		}
		collection = (Collection) data.get(TableConstants.FIRST_NAME);
		firstName = null;
		for (Object val : collection) {
			firstName = val.toString();
		}

		collection = (Collection) data.get(TableConstants.LAST_NAME);
		lastName = null;
		for (Object val : collection) {
			lastName = val.toString();
		}

		question = "What is your company name?";
		answer = "NIKE";
		
		return super.doUserBelongsToAGroup(data, userID, stmt);
		
	}
	
}
