/*
 * MoveZipFile
 * 	-> doMoveZipFile: Moves the zip file from source location, which is a temporary location, to the destination location.
 */

package com.nike.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveZipFile {
	static Logger logger = LogManager.getLogger(MoveZipFile.class.getName());

	public static boolean doMoveZipFile(String[] args) {

		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			logger.entry();
			File afile = new File(args[0]);
			File bfile = new File(args[1]);

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {

				outStream.write(buffer, 0, length);

			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();
			logger.info("File is copied successful!");
			logger.exit(false);
			return true;
		} catch (IOException e) {
			logger.error("IOException in moving the files from one location to another location "
					+ ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean reNameFile(String[] args) {
		logger.entry();
		File oldFile = new File(args[0]);
		File newFile = new File(args[1]);
		if(newFile.exists()) {
			File[] files = newFile.listFiles();
			logger.info("Directory: " + newFile.getName() + " exists and have " + files.length + " files.");
			for(File file: files) {
				logger.info("Deletion of existing file: " + file.getName() + " isSuccess: " + file.delete());
			}
		} else {
			logger.info("Creation of new directory: " + newFile.getName() + " isSuccess: " + newFile.mkdirs());
		}
		if(oldFile.exists()) {
			File[] oldFiles = oldFile.listFiles();
			for(File file:oldFiles) {
				File destFile = new File(newFile.getAbsolutePath() + File.separator + file.getName());
				try {
					FileUtils.moveFile(file, destFile);
					if(file.exists()) {
						logger.info("Deletion of moved file: " + file.getName() + " isSuccess: " + file.delete());
					}
				} catch (IOException e) {
					logger.error(ErrorUtils.getStackTrace(e));
					return false;
				}
			}
			logger.info("Deletion of old directory: " + oldFile.getName() + " isSuccess: " + oldFile.delete());
		}
		logger.exit(false);
		return true;
	}

	public static boolean doMoveZipFileToJenkins(String[] args, ServletContext servletContext) {
		String currentFolder = args[0];
		String fileName = args[1];
		// Credentials
		String username = servletContext.getInitParameter("jenkinsUser");
		String password = servletContext.getInitParameter("jenkinsPassword");

		// Jenkins url
		String jenkinsUrl = servletContext.getInitParameter("jenkinsUrl");

		// Build name
		String jobName = servletContext.getInitParameter("jenkinsJobName");

		// Build token
		String buildToken = servletContext.getInitParameter("jenkinsBuildToken");
		String JenkinsCLI = "/Users/a.appsubmission/Documents/workspace_j2ee/jenkins-cli.jar";
		String getUrl = jenkinsUrl + "/job/" + jobName + "/buildWithParameters?token=" + buildToken;
		String command = "java jar " + JenkinsCLI + " -s " + getUrl + " build Build -p " + fileName + "=" + currentFolder;
		
		return false;
	}
}
