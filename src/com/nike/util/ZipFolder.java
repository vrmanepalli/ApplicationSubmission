/*
 * ZipFolder utility class provides
 * 	->	doZipFolder(String args[]):	Zips all the files that are available inside the String array 
 * 	->	delete(File resource):	Deletes the files once the files are zipped.
 */

package com.nike.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipFolder {
	
	static Logger logger = LogManager.getLogger(ZipFolder.class.getName());
	
	public static boolean doZipFolder(String args[]) {
		try {
			logger.entry("Zipping file " + args[0]);
			File inFolder = new File(args[0]);
			File outFolder = new File(args[1]);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(outFolder)));
			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list();
			for (int i = 0; i < files.length; i++) {
				in = new BufferedInputStream(new FileInputStream(
						inFolder.getPath() + "/" + files[i]), 1000);
				out.putNextEntry(new ZipEntry(files[i]));
				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
			}
			out.flush();
			out.close();
			delete(inFolder);
			return true;
		} catch (Exception e) {
			logger.error("Exception in performing the zip of files " +ErrorUtils.getStackTrace(e));
			return false;
		}
	}
	
	public static boolean delete(File resource) throws IOException {
		logger.entry("delete file/folder " + resource);
		if (resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}

		}
		return resource.delete();

	}
}
