package com.lecootech.market.data;

import android.app.Activity;
import android.content.Context;

/*
 * @author zhaweijin
 * 软件管理页面，做异步处理加载判断当前是否有更新，需要传送的数据对象
 */
public class DownloadManagerSendData {
	private String name = "";
	private Context context;
	private int flag;
	private Activity activity;
	private String swid="";

	public DownloadManagerSendData(String name, Context context, int flag,
			Activity activity,String swid) {
		this.name = name;
		this.context = context;
		this.flag = flag;
		this.activity = activity;
		this.swid = swid;
	}

	public String getName() {
		return name;
	}

	public Context getContext() {
		return context;
	}

	public int getFlag() {
		return flag;
	}

	public Activity getActivity() {
		return activity;
	}
	
	public String getSwid()
	{
		return swid;
	}
}
