/*
 * This utility file is used to generate the JSON response using the JSONObject and JSONArray classes
 * This class methods widely used by all the web services to generate the JSON response
 * The output of this object is either JSONObject or JSONArray 
 * 
 * */
package com.nike.util;

import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BuildJSON {
	
	private JSONObject parent;
	private JSONObject child;
	private JSONArray childArray;
	private boolean isValid = true;
	private String localVariableString;
	private int localVariableInt;
	private int currentPage = -1;
	private boolean localVariableBoolean;
	private String currentStatus;
	private String filterUserID;
	
	public String getFilterUserID() {
		return filterUserID;
	}

	public void setFilterUserID(String filterUserID) {
		this.filterUserID = filterUserID;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public boolean isLocalVariableBoolean() {
		return localVariableBoolean;
	}

	public void setLocalVariableBoolean(boolean localVariableBoolean) {
		this.localVariableBoolean = localVariableBoolean;
	}

	public int getLocalVariableInt() {
		return localVariableInt;
	}

	public void setLocalVariableInt(int localVariableInt) {
		this.localVariableInt = localVariableInt;
	}

	public String getLocalVariableString() {
		return localVariableString;
	}

	public void setLocalVariableString(String localVariableString) {
		this.localVariableString = localVariableString;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public BuildJSON() {
		parent = new JSONObject();
		child = new JSONObject();
	}
	
	public static JSONObject getStaticJSONParent(String error) {
		JSONObject parent = new JSONObject();
		JSONObject child = new JSONObject();
		child.put("Error", error);
		parent.put("Response", child);
		return parent;
	}
	
	public void addElement(String key, String value) {
		child.put(key, value);
	}
	
	public String getElement(String key) {
		return child.getString(key);
	}
	
	public void addListOfElements(String key, ArrayList<String> values) {
		child.put(key, values);
	}
	
	public void addElement(String key, boolean value) {
		child.put(key, value);
	}
	
	public void addJSONObject(String key, JSONObject value) {
		child.put(key, value);
	}
	
	public void createNewJSONArray() {
		childArray = new JSONArray();
	}
	
	public void addJObjectToJArray() {
		childArray.add(child);
	}
	
	public void createNewJChild() {
		child = new JSONObject();
	}
	
	public JSONObject getParentWithChild(String key) {
		parent.put(key, child);
		return parent;
	}
	
	public JSONObject getParentWithArray(String key) {
		parent.put(key, childArray);
		return parent;
	}

	public void addElement(String key, int index) {
		child.put(key, index);
	}

	public JSONObject getChild() {
		return child;
	}

	public void setChild(JSONObject child) {
		this.child = child;
	}
}
