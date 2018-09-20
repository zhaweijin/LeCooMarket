package com.lecootech.market.data;

public class SecondFloorData {

	private String categoryid;
	private String bcid;
	private String name;
	private String createddate;
	private String modifieddate;
	private String imagepath;

	public SecondFloorData() {

	}

	// categoryid
	public void setCategoryID(String id) {
		this.categoryid = id;
	}

	public String getCategoryID() {
		return categoryid;
	}

	// bcid
	public void setBcid(String bcid) {
		this.bcid = bcid;
	}

	public String getBcid() {
		return bcid;
	}

	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// createeddata
	public void setCreateData(String data) {
		this.createddate = data;
	}

	public String getCreateData() {
		return createddate;
	}

	// modifieddate

	public void setModifyData(String data) {
		this.modifieddate = data;
	}

	public String getModifyData() {
		return modifieddate;
	}

	// imagepath
	public void setImagePath(String path) {
		this.imagepath = path;
	}

	public String getImagePath() {
		return imagepath;
	}
}
