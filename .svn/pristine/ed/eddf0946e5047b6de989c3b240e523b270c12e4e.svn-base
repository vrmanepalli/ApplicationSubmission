/*
 * TriggerJenkinsBuild is the utility class (Singleton) which exposes the methods to the servlets to trigger the sign process on Jenkins server
 * 	->  setInProcess(boolean inProcess): maintains the state of the Jenkins, true if it is running else false.
 * 	-> 	getNextAppTitle(): gives the next sign request in the pipeline
 * 	->	getNextFolderName(String appTitle): returns the folder name/path on the hard disk where the build exists
 * 	->	addAppToPipeLine(String appTitle, String projectName, ServletContext servletContext): adds the request to the pipeline
 * 	->	startSigningApp(String appTitle, String projectName, ServletContext servletContext): Does the maintenance of pipeline and sends the signing request to Jenkins by calling another method
 * 	->	startSigningProcess(String projectName, ServletContext servletContext): Used by startSigningApp method. This method depends on JenkinsAuth.triggerJenkinsBuild(projectName, servletContext) method.
 */

package com.nike.util;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TriggerJenkinsBuild {

	static Logger logger =  LogManager.getLogger(TriggerJenkinsBuild.class.getName());
	private static boolean inProcess = false;
	
	public static boolean isInProcess() {
		return inProcess;
	}
	
	public static boolean reset() {
		TriggerJenkinsBuild.inProcess = false;
		return true;
	}

	public static void setInProcess(boolean inProcess) {
		TriggerJenkinsBuild.inProcess = inProcess;
	}

	private static ArrayList<String> projectNames = new ArrayList<String>();
	private static HashMap<String, ParametersObject> parametersObjectsMap = new HashMap<String, ParametersObject>();

	public static String getNextAppTitle() {
		if (projectNames.size() > 0) {
			return projectNames.get(0);
		} else { 
			return null;
		}
	}
	
	public static String getNextFolderName(String appTitle) {
		if (parametersObjectsMap.size() > 0) {
			return parametersObjectsMap.get(appTitle).getFinalFolderName();
		} else {
			return null;
		}
	}

	public static void addAppToPipeLine(String appTitle, String projectName, String isPush,
			String appRequestType, String version, ServletContext servletContext, String bundleID) {
		logger.info("Started working on Jenkins trigger");
		if (!projectNames.contains(appTitle)) {
			ParametersObject params = new ParametersObject();
			params.setAppTitle(appTitle);
			params.setIsPush(isPush);
			params.setAppRequestType(appRequestType);
			params.setVersion(version);
			params.setFinalFolderName(projectName);
			params.setBundleID(bundleID);
			projectNames.add(appTitle);
			parametersObjectsMap.put(appTitle, params);
			startSigningNextApp(appTitle, servletContext);
		}
	}

	public static boolean startSigningNextApp(String appTitle, ServletContext servletContext) {
		if (inProcess) {
			return false;
		} else if (projectNames.contains(appTitle)) {
			projectNames.remove(appTitle);
			return startSigningProcess(appTitle, servletContext);
		}
		return false;
	}

	public static boolean startSigningProcess(String appTitle, ServletContext servletContext) {
		inProcess = true;
        try {  
        	ParametersObject parametersObject = parametersObjectsMap.get(appTitle);
			parametersObjectsMap.remove(appTitle);
        	JenkinsAuth.triggerJenkinsBuild(parametersObject.getFinalFolderName(), parametersObject.getIsPush(), parametersObject.getAppRequestType(), parametersObject.getVersion(), parametersObject.getAppTitle(), servletContext, parametersObject.getBundleID());
        	logger.info("Started the signing process on Jenkins");
        	return true;
        } catch (Exception e) {  
        	logger.error("Exception in triggering the Jenkins build " + ErrorUtils.getStackTrace(e));
            return false;  
        } 
	}

}
