/*
 * TableConstants utility class have all the constants that are used across the project, especially for database column names.
 */

package com.nike.util;

public class TableConstants {

	/********* Application URL Constants ********/

	public static final String HTTPS_PORT_NUMBER = "8443";
	public static final String HTTP_PORT_NUMBER = "8090";
	public static String BASIC_URL_HTTPS = "https://mobileappssign.nike.com:";	//"http://mobileappstdev.nike.com:"
	public static String BASIC_URL_HTTP = "http://mobileappssign.nike.com:";
	public static final String ROOT_URL_WITH_JENKINS_PORT_NUMBER = BASIC_URL_HTTP
			+ HTTP_PORT_NUMBER + "/";
	public static final String ROOT_URL = BASIC_URL_HTTPS
			+ HTTPS_PORT_NUMBER + "/";
	public static final String PROJECT_URL_PART = "ApplicationSubmission/";
	public static final String WELCOME_PAGE_URL = ROOT_URL + PROJECT_URL_PART + "#/Welcome";
	public static final String ERROR_PAGE_URL = ROOT_URL + PROJECT_URL_PART + "#/Error";
	public static final String URL = "url";

	public static final String SSO_LOGIN_LINK = "https://whqpfed.nike.com:9031/sp/startSSO.ping?PartnerIdpId=nike:mobappsub:idp&TargetResource=" + ROOT_URL + "ApplicationSubmission/Private/CosumeSSOServlet";

	public static final String MOBILE_IRON_BASIC_WEB_SERVICE_URL = "https://vsp.nike.com/api/v1/";//"https://vsptest.nike.com/api/v1/";
	public static final String RETAIL_MOBILE_IRON_BASIC_WEB_SERVICE_URL = "https://retailmdm.nike.com/api/v1/";//"https://vsptest.nike.com/api/v1/";
	public static final String MOBILE_IRON_APP_INVENTORY = MOBILE_IRON_BASIC_WEB_SERVICE_URL + "apps/inventory/app?appname=";
	public static final String RETAIL_MOBILE_IRON_APP_INVENTORY = RETAIL_MOBILE_IRON_BASIC_WEB_SERVICE_URL + "apps/inventory/app?appname=";
	
	/********* Storage Folder Paths Constants ********/

	public static final String ROOT_FILE_PATH = "Y:/MobileApps/Production/";	//"Y:/MobileApps/Test/"
	public static final String ROOT_FILE_PATH_PROJECTS = ROOT_FILE_PATH + "Projects/";//"/Users/appsubmission/.jenkins/jobs/UnZipProcess/workspace/Projects/";
	public static final String UPLOAD_DIRECTORY = ROOT_FILE_PATH
			+ "TemporaryUploadZipFiles";
	public static final String UPLOAD_APNS_PROFILE_DIRECTORY = ROOT_FILE_PATH
			+ "PushNotificationProfiles";
	public static final String UPLOAD_PROFILE_DIRECTORY = ROOT_FILE_PATH
			+ "ProvisioningProfiles";
	public static final String UPLOAD_IMAGE_DIRECTORY = ROOT_FILE_PATH
			+ "Images";
	public static final String VOLUMES_UPLOAD_IMAGE_DIRECTORY = ROOT_FILE_PATH + "Images";	
	public static final String UPLOAD_FINAL_DIRECTORY = ROOT_FILE_PATH
			+ "UploadZipFiles";
	public static final String VOLUMES_UPLOAD_FINAL_DIRECTORY = ROOT_FILE_PATH + "UploadZipFiles";	

	public static final String AGENT_CONFIG_PATH = ROOT_FILE_PATH + "agent-config.txt";		
	
	public static final String CSV_FILE_PATH = ROOT_FILE_PATH + "CSV_NO_OF_DOWNLOADS";

	public static final String IPA_FILE_PATH = "IPAFilePath";

	/********* Email Constants ********/

	public static final String SUBJECT = "Subject";
	public static final String BODY = "Body";
	public static final String ADMIN_EMAIL_IDS = "mobileapps@nike.com, vasudevarao.manepalli@nike.com";

	/********* Database Table Constants ********/

	// Table Name Constants

	public static final String TABLE_PROJECTS = "Projects";
	public static final String TABLE_BUILD_COUNT = "BuildCount";
	public static final String TABLE_BUSINESS_OWNER = "BusinessOwners";
	public static final String TABLE_DEVELOPMENT_REQUEST = "DevelopmentRequest";
	public static final String TABLE_PUSH_PROFILE_DETAILS = "PushProfiles";
	public static final String TABLE_REQUEST_COUNT = "RequestCount";

	// Projects Table Column Name Constants

	public static final String ID = "id";
	public final static String BO_DEPT = "BoDept";
	public static final String APP_ADG_LEVEL = "AppADGLevel";
	public static final String APP_CURRENT_VERSION = "AppCurrentVersion";
	public static final String APP_OLD_VERSION = "AppOldVersion";
	public static final String APP_CURRENT_VERSION_SHORT_STRING = "AppCurrentVersionShortString";
	public static final String APP_OLD_VERSION_SHORT_STRING = "AppOldVersionShortString";
	public static final String APP_DESCRIPTION = "AppDescription";
	public static final String APP_DEVICES = "AppDevices";
	public static final String APP_MIN_OS = "AppMinOS";
	public static final String APP_SECURITY = "AppSecurity";
	public static final String APP_TITLE = "AppTitle";
	public static final String APP_INSTALL_URL = "AppInstallUrl";
	public static final String REQ_TYPE = "ReqType";
	public static final String TSC_DEPT = "TSCDept";
	public static final String TSC_EMAIL = "TSCEmail";
	public static final String TSC_NAME = "TSCName";
	public static final String TSC_PHONE = "TSCPhone";
	public static final String TSC_TITLE = "TSCTitle";
	public static final String CURRENT_STATUS = "CurrentStatus";
	public static final String REQUEST_NUMBER = "RequestNumber";
	public static final String SUCCESSFUL_BUILDS = "SuccessfulBuilds";
	public static final String CURRENT_BUILD_PATH = "CurrentBuildPath";
	public static final String PREVIOUS_BUILD_PATH = "PreviousBuildPath";
	public static final String RESULT = "Result";
	public static final String IMAGE_FOLDER_URL = "ImageFolderUrl";
	public static final String IS_PUSH = "isPush";
	public static final String PUSH_PROFILE_PATH = "PushProfilePath";
	public final static String DATE = "Date";

	// BusinessOwner Table Column Name Constants

	public final static String FIRST_NAME = "FirstName";
	public final static String LAST_NAME = "LastName";
	public final static String EMAIL_ID = "EmailID";
	public final static String PHONE_NO = "PhoneNo";
	public final static String DEPT = "Dept";
	public final static String USER_ID = "UserID";
	public final static String PWD = "Pwd";
	public final static String IS_ADMIN = "IsAdmin";
	public final static String QUESTION = "Question";
	public final static String ANSWER = "Answer";
	public static final String LAST_APP_REQ_TITLE = "LastAppReqTitle";
	public static final String IPHONE_TOKEN = "IphoneToken";
	public static final String IPAD_TOKEN = "IpadToken";
	public static final String IPOD_TOKEN = "IpodToken";
	public static final String IPHONE = "Iphone";
	public static final String IPAD = "Ipad";
	public static final String IPOD = "Ipod";

	// PushProfiles Table Column Name Constants

	public static final String CREATION_DATE = "CreationDate";
	public static final String EXPIRATION_DATE = "ExpirationDate";
	public static final String APPLICATION_IDENTIFIER = "ApplicationIdentifier";
	public static final String UA_USER_NAME = "UAUserName";
	public static final String UA_PWD = "UAPwd";

	// RequestCount Table Column Name Constants

	public static final String IN_YEAR = "InYear";

	/*********** All Other Constants *************/
	public final static String ERROR = "Error";
	public final static String WRITE_STREAM_ERROR_MSG = "Failed to write the message to user.";
	public final static String IS_USER = "isUser";

	public final static String SUCCESS = "Success";
	public final static String UN_SUCCESS = "Unsuccess";
	public final static String MESSAGE = "Message";
	public static final String PATH = "Path";
	public static final String RESPONSE = "Response";

	public static final String COLUMN = "Column";
	public static final String BUILD_NUMBER = "BuildNumber";
	public static final String FINAL_OUTPUT_SUCCESS = "Creation of final signed";
	public static final String FAILED = "Failed";

	public static final String NEW_APP = "New App";
	public static final String UPDATE_EXT_APP = "Update Existing App";

	public static final String REMOVE = "Remove";
	public static final String USERS = "Users";
	public static final String COUNT = "count";
	public static final String IN_PROGRESS = "Progress";
	public static final String USER_PROJECTS = "UserProjects";
	public static final String REGISTRATION_MSG = "Successful Registration.";
	public static final String REGISTRATION_SUBJECT = "Registration Status";
	public static final String COMPLETE = "Complete";

	public static final String IMAGES_PART = "Images/";
	public static final String IMAGE_PART = "image/";
	public static final String NO_IMAGE_ICON = "NoImage.jpeg";
	public static final String PROJECT_ICON = "ProjectIcon";
	public static final String LIST_OF_IMAGES = "LisOfImages";
	public static final String MAXIMUM_FILE_MSG = "<p>For future reference, consider reducing your app size to meet best practice guildlines. <a class=\"btn\" target=\"_blank\" href=\"http://nikepedia/wiki/index.php/ManagingMobileAppSize\">Click here</a> for instructions.</p>";

	public static final String WHEN_DATE = "WhenDate";
	public static final String DESCRIPTION = "Description";

	public static final String TOKEN = "Token";

	public static final String IS_PUSH_DB = "IsPush";
	public static final String PUSH_PENDING = "PushPending";
	public static final String PUSH_MSG = "Push Notification signing will take 24 to 48 hrs. You will be informed about the status by email.";

	public static final String MONTH = "Month";
	public static final String REQUEST_COUNT = "RequestCount";
	public static final String USER = "User";
	public static final String COMMA_SPACE_SPERATOR = ", ";
	public static String PREVIOUS_YEAR = "PreviousYear";
	public static String CURRENT_YEAR = "CurrentYear";
	public static String GENERIC_NIKE = "Generic_Nike";
	public static String EXISTING_USER_ID = "Existing_User_ID";
	
	public final static String COOKIE_NAME = "opentoken";
	public static final String ADMIN_GROUP = "Application.CognosBI.WHQ.Dev.ServerAdmin"; //"Shareddata.Projects.WHQ.Change";
	public static final String USER_GROUP = "Shareddata.Projects.WHQ.Change";
	public static final String USER_GROUP_OWNER = "Shareddata.Projects.WHQ.Owner";
	public static final String RETAIL_USER_GROUP = "Application.MobileApp.SellInPackage.GTMS.QA";
	public static final String JOHN_KEITH_AD_GROUP = "Lst-gtm.it.ecomm.mobile";
	
	public static final String MOBILE_APPS_DEV_GROUP_USERS = "Application.MobileApps.Dev.iOS.Users";
	public static final String MOBILE_APPS_DEV_GROUP_ADMINS = "Application.MobileApps.Prod.iOS.Administrator";
	public static final String MOBILE_APPS_DEV_GROUP_DEVELOPERS = "Application.MobileApps.Dev.iOS.Developers";
	
	public static final String MOBILE_APPS_PROD_GROUP_ADMINS = "Application.MobileApps.Prod.iOS.Administrator";
	public static final String MOBILE_APPS_PROD_GROUP_USERS = "Application.MobileApps.Prod.iOS.Users";
	
	public static final String CONFIG_FILE_PATH = ROOT_FILE_PATH + "ConfigProfile.txt";
	
	public static final String APPLICATION_ICON = BASIC_URL_HTTPS + "8443/ApplicationSubmission/image/MediumappIcon.gif";
	public static final String PLEASE_LOGIN_MSG = "Please login!";
	public static final String NEW_TO_ADD_USER_ID = "NewToAddUserID";
	public static final String GENERAL = "GENERAL";
	public static final String SPECIFIC_USER = "SPECIFIC_USER";
	public static final String DATABASE_ERROR_MSG = "Failed to complete database transaction.";
	public static final String APP_BUNDLE_ID = "AppBundleID";
	public static final int IS_PROD_ADMIN_VALUE = 1;
	public static final int IS_DEV_ADMIN_VALUE = 2;
	public static final int IS_DEV_USER_VALUE = 3;
	public static final int IS_PROD_USER_VALUE = 0;
	public static final String PROGRESS = "Progress";
	public static final String CURRENT_PAGE = "CurrentPage";
	public static final String FILTER_USER_ID = "FilterUserID";
	public static final String LIMIT = "limit";
	public static final String OFFSET = "offset";
	public static final String MI_APP_TITLE = "MI_AppTitle";
	public static final String MESSAGES = "messages";
	public static final String TYPE = "Type";
	public static final String MI_INSTANCE = "MI_Instance";
	public static final String IS_SIGNING_REQ = "Is_Signing_Req";
	public static final String N0_SIGNING_REQUIRED = "Your application will be deployed on Mobile Iron shortly. You will get notified once the app is available.";
	public static final String SUBMITTED_BY = "SubmittedBy";
}
