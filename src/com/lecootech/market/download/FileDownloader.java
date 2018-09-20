package com.lecootech.market.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.lecootech.market.InformationDescriptionActivity;
import com.lecootech.market.InformationTabHost;
import com.lecootech.market.R;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.DownloadSoftwareData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

public class FileDownloader {
	private BufferedRandomAccessFile bufferedRandomAccessFile;
	private BufferedInputStream bis;
	private String webpath;
	private String tempfilepath;
	private String apkpath;
	private String software_name;
	private String name;
	private int tempcount = 0;
	private Context activity;
	private int downloadlength = 0;
	private int count;
	private int Noti;


	private DownloadDatabase downloadDatabase;
	private SharedPreferences settingPreferences;
	private String TAG = "FileDownloader";
	private boolean notificationThreadFlag = false;

	public FileDownloader(DownloadSoftwareData data) {

		startDownload(data);

	}

	public void startDownload(DownloadSoftwareData data) {
		try {

			software_name = data.getSoftwareName();
			webpath = data.getWebPath();
			tempfilepath = data.getTempFilePath();
			Noti = data.getNoti();
			activity = data.getContext();
			
			
			//发送广播到详情页面，发边进度条的更新
			Intent intent = new Intent("com.lecootech.network");
			activity.sendBroadcast(intent);
			
			// 获取数据库对象且已下载的文件长度
			settingPreferences = PreferenceManager
					.getDefaultSharedPreferences(activity);

			downloadDatabase = DatabaseManager.getDownloadDatabase(activity);
			downloadlength = downloadDatabase
					.getUpdateDownloadLength(software_name);

			// 连接网络，设置网络参数
			String downpathString = Util.toUtf8String(webpath);
			
			count = downloadlength;
			tempcount = downloadDatabase.getDownloadNum(software_name);
			Util.print(TAG,"start_download"+ "-------------------------------"+software_name);
			Util.print(TAG,"start_download"+ ""+downloadlength);
			Util.print(TAG, webpath);
			URL url = null;
			HttpURLConnection http = null;
			if(Util.checkIsCmwapNetwork(activity))
			{
//				Util.print("cmwap-->", "cmwap-->");
				url = new URL(WebAddress.proxypath+
						downpathString.substring(downpathString.indexOf("9899")+5, downpathString.length()));
				http = (HttpURLConnection) url.openConnection(); 
				http.setRequestProperty("X-Online-Host", WebAddress.comString);				
			}
			else {
				URL downUrl = new URL(downpathString);
				http = (HttpURLConnection) downUrl.openConnection();
			}
			
			http.setDoInput(true);
			http.setConnectTimeout(6 * 10 * 1000);
			http.setReadTimeout(40*1000);
			http.setRequestProperty("Range", "bytes=" + downloadlength + "-");
			http.connect();
			
			Util.print(TAG,"code    " + http.getResponseCode()+"");
			if(http.getResponseCode()==206)
			{
				InputStream inStream = http.getInputStream();
				Util.print(TAG,"SSSS  "+ http.getHeaderField("Content-Length")+"");
				long length = Long.parseLong(http.getHeaderField("Content-Length"));
				if(length<1)
				{
					error();
				}
				else {
					Util.print(TAG,"GET-length"+ "   "+length);
					length = length + downloadlength;
					Util.print(TAG,"zongdexiazai   "+ length+"");
					// 创建文件夹
					File sdcardFile = new File(tempfilepath);
					if (!sdcardFile.exists()) {
						sdcardFile.mkdirs();
					}
					// 创建文件
					if (downloadlength > 0) {
						apkpath = DownladUtil.getFilePath(activity, software_name);
					} else {
						String[] fileName = Util.fileName(webpath);
						name = fileName[1] + "." + fileName[0];
						apkpath = tempfilepath + name;
					}
				
					File tempFile = new File(apkpath);
					if (!tempFile.exists())
						tempFile.createNewFile();
					bufferedRandomAccessFile = new BufferedRandomAccessFile(tempFile,"rwd");
					if (downloadlength > 0)
						bufferedRandomAccessFile.setLength(downloadlength);

					// 修改系统文件权限
//					Util.print("pagetaskfile", apkpath);
					if (tempfilepath
							.equals(ApplicationConstants.system_download_apk_path)) {
						Util.execMethod(apkpath);
					}

					// 从网络流写入文件内容
					bis = new BufferedInputStream(inStream, 1024);
					bufferedRandomAccessFile.seek(downloadlength);
					sendNotification(software_name, webpath, tempfilepath, activity,
							Noti);
				try {
					if (bis != null) {
						byte[] buf = new byte[8 * 1024];
						int ch = -1;
					
						while ((ch = bis.read(buf)) != -1) {
							bufferedRandomAccessFile.write(buf, 0, ch);
							count += ch;
							tempcount = (int) ((count / (float) length) * 100);
							if (!settingPreferences.getBoolean(Noti + "", false)) {
								break;
							}
						}
					}
				} catch (Exception e) {
						e.printStackTrace();
						error();
				}
				finally{
					Util.print(TAG, "download thread finished");
					bis.close();
					bufferedRandomAccessFile.close();
				}
			}
		}
		else {
			error();
		}

	} catch (Exception e) {
		e.printStackTrace();
		error();
	}
   
	Util.print(TAG,"main download thread end"+ "main download thread end");
		
		
}

	
	public void error()
	{
		if(settingPreferences.getBoolean(Noti + "", false))
		{
			downloadDatabase.updateDownloadOperation(software_name, -3);

			settingPreferences.edit().putBoolean(Noti + "", false).commit();
			notificationCancel(Noti);
			Util.networkFailedNotification(activity,
					software_name, Noti, "下载数据请求超时，请稍后继续下载");	
		}
		Util.print(TAG,"888  "+tempcount +"-----" + count);
		if(!notificationThreadFlag)
		{
			Util.print(TAG,"888  "+tempcount +"-----" + count);
			updateDabaseLength_DownloadNum(software_name);
		}
	}
	
	
	public void notificationCancel(int swid) {
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(swid);
	}

	/**
	 * @software_name 软件名称 根据软件名称查找数据库 operation 状态，如果不为零那么返回true
	 */
	public boolean checkIsBreak(String software_name) {
		boolean returnvalue = false;
		Cursor cursor = null;
		int size;
		int operation = 0;
		cursor = downloadDatabase.queryID(software_name);
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

	/**
	 * @software_name1 软件名称
	 * 
	 * @webpath1 网络地址 供获取文件名称
	 * 
	 * @tempfilepath1 临时存放文件的路径
	 * 
	 * @activity1 当前的activity
	 * 
	 * @flag1 数据库标识 flag=1 软件数据库 flag=2 壁纸数据库
	 * 
	 * @Noti1 通知 发送进行当中的通知
	 */
	public void sendNotification(String software_name1, String webpath1,
			String tempfilepath1, Context activity1, int Noti1) {

		final String software_name = software_name1;
		final String webpath = webpath1;
		final String tempfilepath = tempfilepath1;
		final Context activity = activity1;
		final int Noti = Noti1;
		long downloadlength = 0;

		// 初始化数据库，并获取已下载的百分比

		downloadDatabase = DatabaseManager.getDownloadDatabase(activity);
		downloadlength = downloadDatabase
				.getUpdateDownloadLength(software_name);

		// 初始化通知
		final NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(
				android.R.drawable.stat_sys_download, "正在下载",
				System.currentTimeMillis());
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(activity.getPackageName(),
				R.layout.notification);
		notification.contentView.setProgressBar(R.id.ProgressBar01, 100, 0,
				false);
		notification.contentView.setTextViewText(R.id.value, 0 + "%");
		notification.contentView.setTextViewText(R.id.progress_software_name,
				software_name);

		// 设置点击通知跳转的方向
		Intent intent = new Intent(activity, InformationTabHost.class);
		intent.putExtra("downloading", true);
		intent.putExtra("swid", Noti + "");
		intent.putExtra("name", software_name);
		// 第一次发送通知
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!software_name.equals("乐酷市场")) {
			notification.contentIntent = PendingIntent.getActivity(
					activity.getApplicationContext(), Noti, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			notification.contentIntent = PendingIntent.getActivity(
					activity.getApplicationContext(), Noti, null, 0);
		}
		notificationManager.notify(Noti1, notification);

		// 下载的文件路径
		if (downloadlength > 0) {
			apkpath = DownladUtil.getFilePath(activity, software_name);
		} else {
			String[] fileName = Util.fileName(webpath);
			name = fileName[1] + "." + fileName[0];
			apkpath = tempfilepath + name;
		}
		// 启动连续发送通知的线程
		new Thread(new Runnable() {

			public void run() {
				try {
					long oldDownLength = 0;
					int LengthSame = 0;
					int operation = 0;
					long downloadSize = 0;
					notificationThreadFlag = true;
					while (true) {
						downloadDatabase = DatabaseManager
								.getDownloadDatabase(activity);
						downloadSize = downloadDatabase
								.getUpdateDownloadLength(software_name);
						// -1 暂停下载 -3 异常网络断开暂停下载 -2 相当于取消下载 0 正常下载
						operation = getOperation(software_name); //
						// threadtempcount=getDownloadNum(flag,software_name);
						Util.print(TAG,"operation:" +operation + "tempcount:" + tempcount+"count:"+count);
						if (operation == -1 || operation == -3) //
						{
							break;
						} else if (operation == 0 && tempcount < 100)// 下载 发送消息
						{

							notification.contentView.setProgressBar(
									R.id.ProgressBar01, 100, tempcount, false);
							notification.contentView.setTextViewText(
									R.id.value, tempcount + "%");
							notificationManager.notify(Noti, notification);
							updateDabaseLength_DownloadNum(software_name);
							Thread.sleep(1000);
						} else if (operation == 0 && tempcount == 100) // 下载完成
																		// 发送消息
						{
							break;
						} else if (operation == -2) {
							notificationManager.cancel(Noti);
							settingPreferences.edit().remove(Noti + "")
									.commit();
							break;
						} else {
							notificationManager.cancel(Noti);
							break;
						}
					}
					updateDabaseLength_DownloadNum(software_name);
					if(tempcount==100 && operation==0)
					{
						notificationManager.cancel(Noti);
						settingPreferences.edit().remove(Noti + "").commit();
						Thread.sleep(300);
						sendResultNotification(notificationManager, Noti);						
					}		
					Util.print(TAG,"999   "+tempcount +"-----" + count);
					notificationThreadFlag = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*
	 * @notificationManager 通知管理
	 * 
	 * @Noti 通知ID号 发送最后一次的通知
	 */
	public void sendResultNotification(NotificationManager notificationManager,
			int Noti) {
		try {
			Notification ni = new Notification(
					android.R.drawable.stat_sys_download_done, "下载完成",
					System.currentTimeMillis());
			File file = new File(apkpath);
			Intent intent = new Intent();
			// 直接跳转到软件安装页面
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			String type = Util.getMIMEType(file);
			intent.setDataAndType(Uri.fromFile(file), type);
			PendingIntent pIntent = PendingIntent.getActivity(activity, 0,
					intent, 0);

			// 点击跳转到软件管理页面
			// Intent intent = new Intent(activity,
			// MyDownloadFileManager.class);
			// intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
			//
			// PendingIntent pIntent = PendingIntent.getActivity(activity, 0,
			// intent, 0);

			ni.setLatestEventInfo(activity, software_name,
					software_name + "下载完成", pIntent);
			notificationManager.notify(Noti, ni);

			if (file.exists())
				DownladUtil.installFile(file, activity);
			
			commitDownloadNum();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * @name 名称 获取当前下载的状态
	 */
	public int getOperation(String name) {
		int operation = -1;
		operation = downloadDatabase.getDownloadOperation(name);
		return operation;
	}

	/*
	 * @software_name 名称
	 * 
	 * @flag 数据库状态 更新数据库当前下载的进度值
	 */
	public void updateDabaseLength_DownloadNum(String software_name) {
		downloadDatabase.updateAll(software_name, count, tempcount);
	}
	
	
	public void commitDownloadNum()
	{
		try {
			// DownladUtil.downloadAction(InformationDescriptionActivity.this,
			// webpath,
			// ""+Noti,
			// packagename,
			// software_name);
			// 提交下载一次的数据到服务器，做统计
			String[] key = { "imei", "swid", "productID", "mobiletype",
					"imsi", "n", "version" };

			String[] value = {
					Util.getDeviceID(activity),
					Noti+"",
					WebAddress.getproductID(activity),
					Util.getMODEL(activity),
					Util.getIMsI(activity),
					Util.getAPNType(activity),
					activity.getPackageManager().getPackageInfo(
							activity.getPackageName(), 0).versionName };
			Util.CommitDownloadNum(
					activity,
					SharedPrefsUtil.getValue(activity,
							"DomainName", WebAddress.comString)
							+ WebAddress.sendIMEI, key, value);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
