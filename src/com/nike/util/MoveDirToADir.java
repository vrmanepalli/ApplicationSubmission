/*
 * MoveDirToADir utitlity provides the following methods to the project:
 * 	-> doMove(String[] args): Moves all the files from the source to destination
 * 	-> copyFile(File source, File dest): Helper class to doMove. Makes a copy of the source at the destination
 * 	-> copyDirectory(File sourceDir, File destDir): Make a copy of the directory at source to destination
 * 	-> delete(File resource): Helper class to doMove. Deletes the file or files if it is directory.
 * 	-> 
 */
package com.nike.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveDirToADir {
	
	static Logger logger =  LogManager.getLogger(MoveDirToADir.class.getName());
	
	public static boolean doMove(String[] args) {

		File source = new File(args[0]);
		File destination = new File(args[1]);

		try {
			copyDirectory(source, destination);
			if (!delete(source)) {
				throw new IOException("Unable to delete original folder");
			}
			return true;
		} catch (IOException e) {
			logger.error("IOException in moving the file from source to destination " + ErrorUtils.getStackTrace(e));
			return false;
		}

	}

	public static void copyFile(File source, File dest) throws IOException {

		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}

	}

	public static void copyDirectory(File sourceDir, File destDir)
			throws IOException {

		if (!destDir.exists()) {
			destDir.mkdir();
		}

		File[] children = sourceDir.listFiles();

		for (File sourceChild : children) {
			String name = sourceChild.getName();
			File destChild = new File(destDir, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, destChild);
			} else {
				copyFile(sourceChild, destChild);
			}
		}
	}

	public static boolean delete(File resource) throws IOException {
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}

		}
//		Runtime.getRuntime().exec("chmod 777 " + resource);
		boolean isDeleted = resource.delete();
		if(!isDeleted) {
			logger.info("Absoulte path of the file that is deleted is " + resource.getAbsolutePath());
		}
		return isDeleted;

	}
}
