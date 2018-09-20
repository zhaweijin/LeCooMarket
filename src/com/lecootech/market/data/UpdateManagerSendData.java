package com.lecootech.market.data;

/*
 * @author zhaweijin
 * 管理页面  异步检测是否有更新传送过去的数据集
 */
public class UpdateManagerSendData {
	private String apppackagename = "";
	private String appversion = "";

	public UpdateManagerSendData(String apppackagename, String appversion) {
		this.apppackagename = apppackagename;
		this.appversion = appversion;

	}

	public String getAppPackageName() {
		return apppackagename;
	}

	public String getAppVersion() {
		return appversion;
	}

}
