package com.lecootech.market.download;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.DownloadSoftwareData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

/*
 * @author zhaweijin
 * 管理下载的服务
 */
public class MyDownloadService extends Service {

	private DownloadDatabase downloadDatabase;
	private int Noti; // 通知ID
	private String software_name; // 软件名称
	private String path; // 文件路径
	private String webpath; // 网络地址

	private String package_name;
	private String categoryID;
	private SharedPreferences settingPreferences;


	private boolean registed = false;
	// network
	private NetworkBroadcast networkBroadcast = new NetworkBroadcast();

	static List<DownloadManagerThread> downloadList;

	private final static int NETWORK_CHANGED = 0x111;
	private boolean already = false;
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWORK_CHANGED:
				if(!already)
				   startDownload();
				break;
			}
		}
		
	};
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (!registed) {
			registed = true;
			downloadDatabase = DatabaseManager.getDownloadDatabase(this);
			registerBroadcast();
		}

		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = intent.getExtras();
		Noti = bundle.getInt("Noti");

		software_name = bundle.getString("software_name");
		path = bundle.getString("path");
		webpath = bundle.getString("webpath");
		package_name = bundle.getString("package_name");
		categoryID = bundle.getString("categoryID");
		SharedPrefsUtil.putValue(MyDownloadService.this, software_name, Noti);
		try {

			DownloadSoftwareData data = new DownloadSoftwareData();
			data.setContext(MyDownloadService.this);
			data.setNoti(Noti);
			data.setSoftwareName(software_name);
			data.setTempFilePath(path);
			data.setWebPath(webpath);
			data.setCategoryID(categoryID);
			data.setPackage(package_name);
			settingPreferences.edit().putBoolean("" + Noti, true).commit(); // 暂停。。网络断开，设置为false,//开始下载
																			// true

			downloadList.add(DownladUtil.startDownload(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		downloadList = new ArrayList<DownloadManagerThread>();
	}

	public int getDownloadIng(String name) {
		int isRight = 1; // 1表示没有在下载 0表示正在下载 -1 表示服务没有启动
		if (downloadList != null) {
			for (int i = 0; i < downloadList.size();) {
				DownloadManagerThread tmpThread = downloadList.get(i);

				if (tmpThread.isrun
						|| (tmpThread.name == null && tmpThread.isrun == false)) {

					if (name.equals(tmpThread.name) || tmpThread.name == null) {
						isRight = 0;
					}
					i++;
				} else {
					downloadList.remove(i);
				}
			}
		} else {
			isRight = -1;
		}

		return isRight;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (registed) {
			registed = false;
			unRegisterBroadcast();
		}
	}

	public void registerBroadcast() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkBroadcast, filter);
	}

	public void notificationCancel(int swid) {
		NotificationManager notificationManager = (NotificationManager) MyDownloadService.this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(swid);
	}

	public void unRegisterBroadcast() {
		unregisterReceiver(networkBroadcast);
	}

	class NetworkBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				
				handler.sendEmptyMessage(NETWORK_CHANGED);

			}
		}
	}

	

	public void startDownload()
	{
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					already = true;
					
					Cursor cursor = null;
					boolean state = Util.checkNetworkIsActive(MyDownloadService.this);
					if (state) {
						
						Thread.sleep(20*1000);
						cursor = downloadDatabase.queryAllData();
						int size = cursor.getCount();
						Util.print("FileDownloader","network start"+"network start"+size);
//						    checkDownloading();
							// 如果异常导致下载暂停的话，检测到网络连接上，重新启动下载

							if (size > 0) {								
								while (cursor.moveToNext()) {
									String software_name = cursor
											.getString(cursor
													.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
									int operation = downloadDatabase
											.getDownloadOperation(software_name);
									if (operation == -3) {

										// 首先要更新数据库下载状态
										downloadDatabase.updateDownloadOperation(software_name, 0);

										// 再继准备下载
										String iconpath = cursor
												.getString(cursor
														.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
										String str = iconpath.trim();
										String find = "software".trim();
										int index = str.indexOf(find);
										String tmpStr = "";
										if (index > -1) {
											tmpStr = str.substring(index
													+ find.length());
										}
										String swid = tmpStr;
										int Noti = Integer.parseInt(swid);

										String path = Util.getStoreApkPath();
										String webpath = cursor
												.getString(cursor
														.getColumnIndexOrThrow(DownloadDatabase.KEY_WEBPATH));
										String package_name = cursor
												.getString(cursor
														.getColumnIndexOrThrow(DownloadDatabase.KEY_PACKAGE_NAME));

										DownloadSoftwareData data = new DownloadSoftwareData();
										data.setContext(MyDownloadService.this);
										data.setNoti(Noti);
										data.setSoftwareName(software_name);
										data.setTempFilePath(path);
										data.setWebPath(webpath);
										data.setPackage(package_name);
										settingPreferences.edit()
												.putBoolean("" + Noti, true)
												.commit(); // 暂停。。网络断开，设置为false,//开始下载
															// true
										MyDownloadService.downloadList.add(DownladUtil.startDownload(data));

									}
								}
								cursor.close();
							}
						
					} else {
						cursor = downloadDatabase.queryAllData();
						int size = cursor.getCount();
						Util.print("FileDownloader","network failed"+ "network failed"+size);
						if (size > 0) {

							while (cursor.moveToNext()) {

								if (((cursor
										.getInt(cursor.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION)) == 0) && (cursor
										.getInt(cursor.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) != 100))) {
									
									
									String iconpath = cursor.getString(cursor
											.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
							        String str = iconpath.trim();
							        String find = "software".trim();
							        int index = str.indexOf(find);
							        String tmpStr = "";
							        if (index > -1) 
							        {
								       tmpStr = str.substring(index+ find.length());
							        }
							        String swid = tmpStr;
							        int Noti = Integer.parseInt(swid);
							        Util.print("network failed noti", ""+Noti);
							        settingPreferences.edit().putBoolean(Noti + "", false).commit();
									
							        
							        String software_name = cursor.getString(cursor
											.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
									downloadDatabase
											.updateDownloadOperation(software_name,-3);

									notificationCancel(SharedPrefsUtil
											.getValue(MyDownloadService.this,software_name,1));

								}
							}
							cursor.close();
						}
						
						if (!cursor.isClosed())
							cursor.close();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				already  = false;
			}
		}).start();
	}

	
	public boolean checkIsHasInterruptDownload()
	{
		boolean result = false;
		Cursor tempCursor = downloadDatabase.queryAllData();
		while(tempCursor.moveToNext())
		{
			String software_name = tempCursor.getString(tempCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
	        int operation = downloadDatabase.getDownloadOperation(software_name);
	        if(operation==3)
	        {
	        	result = true;
	        	break;
	        }
		}
		
		tempCursor.close();
		return result;
	}
	
	public void checkDownloading()
	{
		try {
		    Thread.sleep(2000);
			
			if(checkIsHasInterruptDownload())
			{
				Util.shutdownAndAwaitTermination(Util.getDownloadThreadPool());
				Util.setDownloadThreadPoolNull();
				Util.print("bb", "bb");
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
