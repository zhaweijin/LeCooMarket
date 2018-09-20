package com.lecootech.market.data;

/*
 * @author zhaweijin
 * 管理页面  异步检测是否有更新返回的数据集
 */
public class UpdateManagerReturnData {
	private String appapkpath = "";
	private String appswid = "";

	public void setAppapkpath(String appapkpath) {
		this.appapkpath = appapkpath;
	}

	public void setAppswid(String appswid) {
		this.appswid = appswid;
	}

	public String getAppApkPath() {
		return appapkpath;
	}

	public String getAppSwid() {
		return appswid;
	}
}
