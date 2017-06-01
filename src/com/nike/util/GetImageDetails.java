/*
 * This used class is used to generate the image url of any given project based on the project name
 * This is because all the images or saved in a folder with project name. so the project name acts as key in this case.
 * This class have methods:
 * 	-> isValidAppTitleAndDir : checks whether there exists a valid image based on directory and appTitle/projectName
 *  -> getProjectIcon: return the url of the project icon, it find outs the smallest size among all the images in the project folder to find out the icon
 *  -> getSmallestImageName : helper class used by getProjectIcon
 *  -> getProjectImages : returns a list of image urls in that project with projectName which was passed
 *  -> getListOfImageUrls : helper method used by getProjectImages
 * */

package com.nike.util;

import java.io.File;
import java.util.ArrayList;

public class GetImageDetails {
	

	public static boolean isValidAppTitleAndDir(String appTitle) {
		if (appTitle == null) {
			return false;
		} else {
			File imageFolder = new File(TableConstants.UPLOAD_IMAGE_DIRECTORY + "/"
					+ appTitle);
			if (imageFolder.exists() && imageFolder.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static String getProjectIcon(String appTitle) {
		File imageFolder = new File(TableConstants.UPLOAD_IMAGE_DIRECTORY + "/" + appTitle);
		long totalSpace = -1;
		String imageName = null;
		if (isValidAppTitleAndDir(appTitle)) {
			imageName = getSmallestImageName(imageFolder.listFiles(),
					totalSpace, imageName);
			if (imageName != null) {
				return TableConstants.ROOT_URL + TableConstants.IMAGES_PART
						+ appTitle + "/" + imageName;
			} 
		} 
		return imageName;
	}

	private static String getSmallestImageName(File[] childFiles,
			long totalSpace, String imageName) {
		for (int i = 0; i < childFiles.length; i++) {
			if (childFiles[i].isDirectory()) {
				getSmallestImageName(childFiles[i].listFiles(), totalSpace,
						imageName);
			} else {
				long space = childFiles[i].length();
				if (!childFiles[i].getName().contains(".db")) {
					if (totalSpace == -1) {
						totalSpace = space;
						imageName = childFiles[i].getName();
					} else if (totalSpace > space) {
						totalSpace = space;
						imageName = childFiles[i].getName();
					}
				}
			}
		}
		return imageName;
	}

	public static ArrayList<String> getProjectImages(String appTitle) {
		File imageFolder = new File(TableConstants.UPLOAD_IMAGE_DIRECTORY + "/" + appTitle);
		ArrayList<String> imageUrls = new ArrayList<String>();
		if (isValidAppTitleAndDir(appTitle)) {
			return getListOfImageUrls(imageFolder.listFiles(), imageUrls, appTitle);
		} else {
			return imageUrls;
		}
	}

	private static ArrayList<String> getListOfImageUrls(File[] childFiles,
			ArrayList<String> imageUrls, String appTitle) {
		for (int i = 0; i < childFiles.length; i++) {
			if (childFiles[i].isDirectory()) {
				getListOfImageUrls(childFiles[i].listFiles(), imageUrls, appTitle);
			} else {
				imageUrls.add(TableConstants.ROOT_URL
						+ TableConstants.IMAGES_PART + appTitle + "/"
						+ childFiles[i].getName());
			}
		}
		return imageUrls;
	}

}
