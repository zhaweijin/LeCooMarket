package com.lecootech.market.handle;

import java.util.ArrayList;

import com.lecootech.market.data.CommentData;
import com.lecootech.market.data.FourFloorData;
import com.lecootech.market.data.ImagesPush;
import com.lecootech.market.data.InstallNecessaryData;
import com.lecootech.market.data.NotificationData;
import com.lecootech.market.data.SearchHotData;
import com.lecootech.market.data.SecondFloorData;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.data.ThridFloorSpecialData;
import com.lecootech.market.data.UpdateManagerData;
import com.lecootech.market.data.UpdateMarketData;

/**
 * @author zhaweijin
 * @function 存储网络解析数据的集
 */
public class DataSet {

	// 当前列表大小
	public int currentListSize = 0;
	// 二级分类（软件分类）
	private ArrayList<SecondFloorData> secondCates = new ArrayList<SecondFloorData>();
	// 三级分类（软件分类）
	private ArrayList<ThridFloorData> threeCates = new ArrayList<ThridFloorData>();

	// special
	private ArrayList<ThridFloorSpecialData> thridFloorSpecialDatas = new ArrayList<ThridFloorSpecialData>();

	// special software introduce
	private String special_software_introduce;

	// search hot
	private ArrayList<SearchHotData> searchHotDatas = new ArrayList<SearchHotData>();

	private FourFloorData info = null;
	// 管理页面
	private ArrayList<UpdateManagerData> updateManagerDatas = new ArrayList<UpdateManagerData>();

	// 软件详细页面，截图所需要的数据
	private ArrayList<String> imageArray = new ArrayList<String>();
	private ArrayList<String> imageArrayID = new ArrayList<String>();

	// 评论页面
	private ArrayList<CommentData> pingruns = new ArrayList<CommentData>();

	// //安装必备页面
	private ArrayList<InstallNecessaryData> installNecessaryDatas = new ArrayList<InstallNecessaryData>();

	// update market data
	private UpdateMarketData marketData;

	
	//开机后通知数据
	private NotificationData notificationData;
	
	//编辑首页顶部的拖动数据
	private ArrayList<ImagesPush> imagesPushs = new ArrayList<ImagesPush>();
	
	
	public DataSet() {

	}

	// 二级分类
	public ArrayList<SecondFloorData> getSecondCates() {
		return secondCates;
	}

	public SecondFloorData getSecondCate() {
		return secondCates.get(secondCates.size() - 1);
	}

	// 三级分类
	public ArrayList<ThridFloorData> getThreeCates() {
		return threeCates;
	}

	public ThridFloorData getThreeCate() {
		return threeCates.get(threeCates.size() - 1);
	}

	// 详细页面
	public FourFloorData getSoftwareInfo() {
		return info;
	}

	public void setSoftwareInfo(FourFloorData info) {
		this.info = info;
	}

	// 软件截图
	// imageArray
	public ArrayList<String> getImageArrays() {
		return imageArray;
	}

	public String getImageArray() {
		return imageArray.get(imageArray.size() - 1);
	}

	// imageArrayID
	public ArrayList<String> getImageArraysID() {
		return imageArrayID;
	}

	public String getImageArrayID() {
		return imageArrayID.get(imageArrayID.size() - 1);
	}

	// 评论页面
	public ArrayList<CommentData> getPingRuns() {
		return pingruns;
	}

	public CommentData getPingRun() {
		return pingruns.get(pingruns.size() - 1);
	}

	// manager 管理页面
	public ArrayList<UpdateManagerData> getUpdateManagerDatas() {
		return updateManagerDatas;
	}

	public UpdateManagerData getUpdateManagerData() {
		return updateManagerDatas.get(updateManagerDatas.size() - 1);
	}

	// installnecessary 安装必备
	public ArrayList<InstallNecessaryData> getInstallNecessaryDatas() {
		return installNecessaryDatas;
	}

	public InstallNecessaryData getInstallNecessaryData() {
		return installNecessaryDatas.get(installNecessaryDatas.size() - 1);
	}

	public int getCurrentSoftwareCount() {
		return currentListSize;
	}

	// special
	public ArrayList<ThridFloorSpecialData> getThridFloorSpecialDatas() {
		return thridFloorSpecialDatas;
	}

	public ThridFloorSpecialData getThridFloorSpecialData() {
		return thridFloorSpecialDatas.get(thridFloorSpecialDatas.size() - 1);
	}

	// special softwarelist introduce
	public void setSpecialSoftwareIntroduce(String introduce) {
		this.special_software_introduce = introduce;
	}

	public String getSpecialSoftwareIntroduce() {
		return special_software_introduce;
	}

	// search hot
	public ArrayList<SearchHotData> getSearchHotDatas() {
		return searchHotDatas;
	}

	public SearchHotData getSearchHotData() {
		return searchHotDatas.get(searchHotDatas.size() - 1);
	}

	// market data
	public UpdateMarketData getMarketData() {
		return marketData;
	}

	public void setMarketData(UpdateMarketData data) {
		this.marketData = data;
	}
	
	// 编辑首页顶部数据
	public ArrayList<ImagesPush> getImagesPushs() {
		return imagesPushs;
	}

	public ImagesPush getiImagesPush() {
		return imagesPushs.get(imagesPushs.size() - 1);
	}
	
	//开机后通知消息
	public void setNotificationData(NotificationData notificationData)
	{
		this.notificationData = notificationData;
	}
	
	public NotificationData getNotificationData()
	{
		return notificationData;
	}
}
