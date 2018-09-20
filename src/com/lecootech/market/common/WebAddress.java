package com.lecootech.market.common;

import com.lecootech.market.R;

import android.content.Context;

/*
 * @author zhaweijin
 * 存放所有使用到的网络地址
 */
public class WebAddress {

	/**
	 * 客户ID，用来区分不同的客户
	 */
	public static String getproductID (Context context){
		return context.getResources().getString(R.string.productID);
	}
	
     /**
      * 代理服务器地址
      */
	public static String proxypath = "http://10.0.0.172/";
	
	/**
	 * 三个域名都指向同一个服务器，防止其中域名出错,IP暂时不使用,优先使用 http://market.lecoo.com:9899/
	 */
	public static String comString3 = "http://market.lecoo.com:9899/";
	public static String comString2 = "http://market.lecoo.com.cn:9899/";
	public static String comString = "http://market.lecoo.cn:9899/";

	/**
	 * 主页地址 bcid 大分类参数:所以软件、软件、游戏 category 小分类参数:新闻、社交等 page 第几页 perpage 每页的数据
	 */
	// login
	/**
	 * 登录页面
	 */
	public static String logingsString = "User_Login.ashx?";
	// userid=&password=

	// register
	public static String registString = "User_Register.ashx?"; // 注册页面
	// userid=%&password=&email=

	// recomment
	// public static String edit_recommentString = comString
	// +"Recommend_edit.ashx?";
	/*
	 * 首页》编辑推荐
	 */
	public static String edit_recommentString = "Head_Edit.ashx?";

	/*
	 * 安装必备
	 */
	public static String special_recommentString = "Recommend_Column.ashx?";

	/*
	 * 首页》最新推荐
	 */
	public static String new_recommentString = "Recommend_new.ashx?";

//	public static String special_recomntString_content = "Recomment_Column_Content.ashx?categoryid=";

	/**
	 * 小说排行
	 */
	public static String fiction_Ranking = "Rank_Book.ashx?";

	/**
	 * 小说分类
	 */

	public static String fiction_category = "Category_Book.ashx?";
	/**
	 * 游戏分类
	 */
	public static String categoryGame = "Category_Game.ashx?";

	/**
	 * 专题
	 */
	public static String categorySubject = "Category_Subject.ashx?";

	/**
	 * 应用
	 */
	public static String cagegoryApplication = "Category_Application.ashx?";

	/**
	 * 搜索热点
	 */
	public static String searchHot = "Search_Hot.ashx?";

	/**
	 * 添加关键词返回数据
	 */
	public static String searchResult = "Search_Reasult.ashx?SearchName=";

	/**
	 * 日排行
	 */
	public static String paihangDay = "Rank_Day.ashx?";

	/**
	 * 月排行
	 */
	public static String paihangMonth = "Rank_Month.ashx?";

	/**
	 * 周排行
	 */
	public static String paihangWeek = "Rank_Week.ashx?";

	/**
	 * 总排行
	 */
	public static String paihangTotal = "Rank_Total.ashx?";

	/**
	 * 应用排行
	 */
	public static String applicationPaihang = "Rank_Application.ashx?";

	/**
	 * 游戏排行
	 */
	public static String gamePaihang = "Rank_game.ashx?";

	// information
	public static String softwareDescription = "Introduction_Information.ashx?swid=";

	/**
	 * 软件评论
	 */
	public static String softwareComment = "Introduction_Comment.ashx?swid=";

	/**
	 * 软件相关列表
	 */
	public static String softwareRelative = "Introduction_Advice.ashx?categoryid=";

	/**
	 * 软件错误报告
	 */
	public static String softwareErrorReport = "Introduction_Error.ashx?";
	// swid=""&report=""

	// commit pingrun
	public static String softwareCommitComment = "Introduction_FBComment.ashx?";
	// swid=&username=&content=&soft_score=

	public static String installnecessary = "Recommend_Column.ashx?"; // 安装必备

	/*
	 * 提交下载数
	 */
	public static String sendIMEI = "DownNUM.ashx?";
	// imei=""&swid=""

	// 评论内容地址
//	public static String pingRunDataString = "guestbookXml.php?swid=";
	// 提交评论数据的地址
//	public static String pingRunCommitString = "guestbook_add.php?";

	// from category enter software list
	public static String thridfloor_software_listString = "Category_Content.ashx?categoryid=";

	// software update manager
	public static String software_update_manager = "SoftwareUpdateManager.ashx?packageName=";

	// send imei
	public static String send_IMEI = "ImeiInfo.ashx?";

	// send market update times
	public static String send_market_times = "Edition_Type.ashx?";

	// send market feedback message
	public static String send_feedback = "Leave_Message.ashx?";
	
	public static String boot_message_server = "YNLecooMarket.ashx?";
	//版本重大修改记录
	/*
	 * version 1.2.1 cmwap 解决方案，修改的地方
	 * 1.Util.getSoftwareWebData   --->增加了cmwap 网络的判断
	 * 2.Util.CommitDownloadNum    --->增加了cmwap 网络的判断
	 * 3.ReportErrorActivity.commentReport   --->增加了cmwap 网络的判断
	 * 4.Util.createWebPicData       --->增加了cmwap 网络的判断
	 * 5.Util.getSoftwareImage       --->增加了cmwap 网络的判断
	 * 6.FileDownloader.startDownload    --->增加了cmwap 网络的判断
	 */
}