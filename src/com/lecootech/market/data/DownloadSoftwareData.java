package com.lecootech.market.data;

import android.content.Context;

/**
 * @author zhaweijin 存储正在下载的当前软件，或者是壁纸参数
 */
public class DownloadSoftwareData {

	private String webpath;
	private String tempfilepath;
	private Context context;
	private String software_name;
	private int Noti;
	private String package_name;
	private String categord_ID;

	public DownloadSoftwareData() {

	}

	// webpath
	public void setWebPath(String path) {
		this.webpath = path;
	}

	public String getWebPath() {
		return this.webpath;
	}

	// tempfileath
	public void setTempFilePath(String path) {
		this.tempfilepath = path;
	}

	public String getTempFilePath() {
		return tempfilepath;
	}

	// Activity
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return this.context;
	}

	// software_name
	public void setSoftwareName(String name) {
		this.software_name = name;
	}

	public String getSoftwareName() {
		return this.software_name;
	}

	// Noti
	public void setNoti(int noti) {
		this.Noti = noti;
	}

	public int getNoti() {
		return this.Noti;
	}

	// package
	public void setPackage(String package_name) {
		this.package_name = package_name;
	}

	public String getPackage() {
		return package_name;
	}

	// categoryID
	public void setCategoryID(String category_ID) {
		this.categord_ID = category_ID;
	}

	public String getCategoryID() {
		return categord_ID;
	}
}
