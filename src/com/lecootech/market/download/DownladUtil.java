package com.lecootech.market.download;

import java.io.File;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lecootech.market.InformationDescriptionActivity;
import com.lecootech.market.R;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.DownloadSoftwareData;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

/*
 * @author zhaweijin
 * 针对下载线程，用到的实用方法
 */
public class DownladUtil {

	/*
	 * @software_name 名称
	 * 
	 * @flag 数据库标识
	 */
	public static String getFilePath(Context activity, String software_name) {
		Cursor cursor = null;
		String filepath = "";
		int size = 0;
		DownloadDatabase downloadDatabase = DatabaseManager
				.getDownloadDatabase(activity);
		cursor = downloadDatabase.queryID(software_name);
		cursor.moveToFirst();
		size = cursor.getCount();
		if (size != 0) {
			filepath = cursor.getString(cursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_FILEPATH));
		}

		cursor.close();
		return filepath;
	}

	/*
	 * @Noti 通知ID
	 * 
	 * @software_name 名称
	 * 
	 * @path 文件存放的路径
	 * 
	 * @webpath 网络下载地址
	 */
	public static void startDownloadService(Context context, int Noti,
			String software_name, String path, String webpath) {
		Intent intent = new Intent(context, MyDownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putInt("Noti", Noti);
		bundle.putString("software_name", software_name);
		bundle.putString("path", path);
		bundle.putString("webpath", webpath);
		intent.putExtras(bundle);
		context.startService(intent);
	}

	/*
	 * 执行下载按钮事件的相应提示及路径设置
	 */
	public static void downloadAction(final Context context, String webpath,
			final String swid, final String packagename,
			final String software_name, final boolean second,
			final String software_size,final Handler handler) {
		// second false 是点击下载，true是点击继续下载
		Toast.makeText(context, "正在下载...", Toast.LENGTH_SHORT).show();
		webpath = webpath.replaceAll(" ", "%20");
		final String tempwebpath = webpath;
		new Thread(new Runnable() {

			public void run() {
				try {
					String downloadPathDir = Util.getStoreApkPath();

					String[] fileName = Util.fileName(tempwebpath);
					int Noti = Integer.parseInt(swid);

					// init notification
					Util.initNotificationProgress(context, software_name, Noti);

					String apkpath = downloadPathDir + fileName[1] + "."
							+ fileName[0]; // 下载数据库要的存放文件路径

					// 把下载相应的记录写入下载数据库当中
					downloadDatabaseManager(context, software_name,
							packagename, tempwebpath, apkpath, swid,
							software_size);
					
					//在点击下载的时候更新列表状态
					if(handler!=null)
					{
						handler.sendEmptyMessage(MessageControl.ADAPTER_CHANGE);
					}
					// 开始启动下载服务
					DownladUtil.startDownloadService(context, Noti,
							software_name, downloadPathDir, tempwebpath);
					// 提交下载一次的数据到服务器，做统计
					if (!second) {
						String[] key = { "imei", "swid", "productID",
								"mobiletype", "imsi", "n" ,"version"};
						String[] value = { Util.getDeviceID(context), swid,
								WebAddress.getproductID(context), Util.getMODEL(context),
								Util.getIMsI(context), Util.getAPNType(context),
								context.getPackageManager().getPackageInfo(
										context.getPackageName(), 0).versionName};
//						Util.CommitDownloadNum(context,
//								SharedPrefsUtil.getValue(context, "DomainName",
//										WebAddress.comString)
//										+ WebAddress.sendIMEI, key, value);

					}

					// finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/*
	 * 下载数据库的管理
	 */
	public static void downloadDatabaseManager(Context context,
			String software_name, String packagename, String webpath,
			String apkpath, String swid, String software_size) {
		DownloadDatabase downloadDatabase = DatabaseManager
				.getDownloadDatabase(context);
		String temppicpath = Util.getStorePicPath();
		try {

			Cursor temCursor = downloadDatabase.queryID(software_name);
			temCursor.moveToFirst();
			int size = temCursor.getCount();
			if (size != 0) {
				downloadDatabase.updateDownloadOperation(software_name, 0);
			} else {
				downloadDatabase.insertData(software_name, temppicpath
						+ "software" + swid, packagename, 0, 0, webpath,
						apkpath, 0, 0, software_size);
			}
			temCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @file 文件对象 根据文件对象 调用系统安装包，安装当前软件
	 */
	public static void installFile(File file, Context context) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		String type = Util.getMIMEType(file);
		Util.print("filepath", file.getAbsolutePath());
		intent.setDataAndType(Uri.fromFile(file), type);
		context.startActivity(intent);
	}

	public static boolean checkIsBreak(Context context, String software_name) {
		boolean returnvalue = false;
		Cursor cursor = null;
		int size;
		int operation = 0;

		cursor = DatabaseManager.getDownloadDatabase(context).queryID(
				software_name);
		cursor.moveToFirst();
		size = cursor.getCount();
		if (size != 0) {
			operation = cursor.getInt(cursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
			if (operation != 0) {
				returnvalue = true;
			}
		} else {
			returnvalue = true;
		}

		cursor.close();
		return returnvalue;
	}

	public static DownloadManagerThread startDownload(
			final DownloadSoftwareData softwareData) {
		try {
			initNotification(softwareData.getContext(), softwareData.getSoftwareName(),
					softwareData.getNoti());

			DownloadManagerThread download = new DownloadManagerThread(
					softwareData);
			Util.getDownloadThreadPool().execute(download);

			return download;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void initNotification(Context context,String software_name,int Noti)
	{
		// 初始化通知
		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(
				android.R.drawable.stat_sys_download, "正在下载",
				System.currentTimeMillis());

		notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		notification.contentView.setProgressBar(R.id.ProgressBar01, 100, 0,
				true);
		
		notification.contentView.setTextViewText(R.id.progress_software_name,
				software_name);
		Intent intent = new Intent();
		notification.contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), Noti, intent, 0);
		notificationManager.notify(Noti, notification);
	}
}
