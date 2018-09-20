package com.lecootech.market;

import java.io.File;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.database.CollectionDatabase;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.download.MyDownloadService;
import com.lecootech.market.handle.DataSet;
import com.lecootech.market.notification.StartService;
import com.mobclick.android.MobclickAgent;

public class SplashActivity extends Activity {

	private int seekbar = 0;
	private SeekBar splash_seekbar;
	private boolean loading = true;

	private MyDownloadService isdownload;
	private int  NOTIFICATION_ID = 1022;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	
	private final static int dispalyRatingbarFull = 0x111;
	private final static int dispalyRatingbarNull = 0x333;
	private final static int loadingFinished = 0x222;

	private SharedPreferences settingPreferences;

	private boolean back = false;

	private DownloadDatabase downloadDatabase;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case dispalyRatingbarFull:
				splash_seekbar.setProgress(seekbar);
				break;
			case dispalyRatingbarNull:
				break;
			case loadingFinished:
				if (!back) {
					if (!checkIsCreadShortCut()) {
						addShortCut();
						Intent intent = new Intent(SplashActivity.this,
								MainTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("topFloorPosition", 1);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(SplashActivity.this,
								MainTabHost.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("topFloorPosition", 1);
						startActivity(intent);
						finish();
					}
				}
				break;
			}
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		loadingRatingBar();
		Main();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.splash_day);
			splash_seekbar = (SeekBar) findViewById(R.id.splash_seekbar);
			splash_seekbar.setMax(100);
			settingPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			Intent service = new Intent(this, StartService.class);// 启动定时显示系统更新通知服务
			service.putExtra("reboot", false);
			downloadDatabase = DatabaseManager.getDownloadDatabase(this);
			startService(service);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	
	
	public void loadingRatingBar() {

		new Thread(new Runnable() {

			public void run() {
				try {
					while (loading) {
						seekbar += 3;
						handler.sendEmptyMessage(SplashActivity.dispalyRatingbarFull);
						Thread.sleep(80);
					}
					handler.sendEmptyMessage(SplashActivity.loadingFinished);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void Main() {
		if (Util.getNetworkState(SplashActivity.this)) {
			isDeleteDatabaseTable();
			
			checkdownloadDatabase();
			
			new Thread(new Runnable() {

				public void run() {
					try {
						if (!Util.avaiableMedia())
							// 在没有SD卡的情况下，判断数据库相应的记录及文件内容是否需要删除。如果有已经浏览过的数据，那么就删除
							Util.ClearDatabase_SDcard(SplashActivity.this);
					
						File systemFile = new File(
								ApplicationConstants.system_download_apk_path);
						if (!systemFile.exists()) {
							systemFile.mkdirs();
						}

						Util.execMethod(ApplicationConstants.system_download_apk_path); // 设置内存下载文件夹的权限

						if (Util.getNetworkState(SplashActivity.this)) {
							Util.sendImei(SplashActivity.this,getPackageName());
						}
						Thread.sleep(2420);
						loading = false;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			networkException(getResources().getString(
					R.string.web_cannot_connection));
		}
	}

	/*
	 * 判断文件是桌面方式是否已经创建
	 */
	public boolean checkIsCreadShortCut() {
		Boolean existCheck = settingPreferences.getBoolean("shortcut", false);
		if (!existCheck) {
			SharedPreferences.Editor editor = settingPreferences.edit();
			editor.putBoolean("shortcut", false);
			editor.commit();
		}
		return existCheck;
	}

	
	/**
	 * 重新启动服务时，去检测数据库是否有未下载完成的下载，继续下载
	 */
	private void checkdownloadDatabase() {
		Cursor cursor = null;
		boolean isrunning = false;
		if (downloadDatabase!=null) {
			
		
		cursor = downloadDatabase.queryAllData();
		if (cursor != null) {

			while (cursor.moveToNext()) {
				if (((cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) != 100) && (cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION)) == -3))
						|| ((cursor
								.getInt(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION)) == -1) && (cursor
								.getInt(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) != 100))) {

					downloadDatabase
							.updateDownloadOperation(
									cursor.getString(cursor
											.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME)),
									-1);
					
					isrunning=true;
					
				}
				
				if (((cursor.getInt(cursor
						.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION)) == 0)
						&& (cursor
								.getInt(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) != 100) )) {
					isdownload = new MyDownloadService();

					if (isdownload.getDownloadIng(cursor.getString(cursor
							.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME))) == 0) {

					} else if (isdownload
							.getDownloadIng(cursor.getString(cursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME))) == 1) {
						downloadDatabase
								.updateDownloadOperation(
										cursor.getString(cursor
												.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME)),
										-1);
						isrunning=true;
					} else if(isdownload
							.getDownloadIng(cursor.getString(cursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME))) == -1){
						downloadDatabase
								.updateDownloadOperation(
										cursor.getString(cursor
												.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME)),
										-1);
						isrunning=true;
					}

				}
				if (isrunning) {
					mNotification = new Notification(R.drawable.icon2,"有未完成的下载",System.currentTimeMillis());
//					mNotification.defaults = Notification.DEFAULT_SOUND;
					mNotificationManager = (NotificationManager) SplashActivity.this
							.getSystemService(Context.NOTIFICATION_SERVICE);
					Intent mIntent = new Intent(SplashActivity.this,
							MainTabHost.class);
					mIntent.putExtra("topFloorPosition", 8);//跳转到下载管理页面设置为8
					mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					PendingIntent mContentIntent = PendingIntent.getActivity(
							SplashActivity.this, 0, mIntent, 0);
					mNotification.setLatestEventInfo(SplashActivity.this,
							"您还有未完成的下载任务", "点击进入下载管理",
							mContentIntent);
					mNotificationManager.notify(NOTIFICATION_ID, mNotification);
				}
				
			}
		}
		
		if(!cursor.isClosed())
		    cursor.close();
		}
	}
	/*
	 * @添加桌面快捷方式
	 */
	public void addShortCut() {
		Boolean existCheck = settingPreferences.getBoolean("shortcut", false);
		if (!existCheck) {
			SharedPreferences.Editor editor = settingPreferences.edit();
			editor.putBoolean("shortcut", true);
			editor.commit();
		}

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		ComponentName comp = new ComponentName(this.getPackageName(),
				getPackageName() + "." + this.getLocalClassName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		sendBroadcast(shortcut);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			back = true;
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// WIFI MOBILE
	public void checkNetworkStyle() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Boolean Flag = false;
		if (connectivityManager != null) {
		} else {
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						settingPreferences.edit().putString("networkStyle",
								info[i].getTypeName());
						Util.print("style", info[0].getTypeName());
						Flag = true;
					}
				}
				if (!Flag) {
					String networkStyle = settingPreferences.getString(
							"networkStyle", "mobile");
					Intent intent = null;
					if (networkStyle.toLowerCase().equals("wifi")) {
						if (Util.isExistActivity("com.android.settings",
								"com.android.settings.WirelessSettings",
								SplashActivity.this)) {
							intent = new Intent();
							intent.setClassName("com.android.settings",
									"com.android.settings.WirelessSettings");
						}
					} else {
						if (Util.isExistActivity("com.android.phone",
								"com.android.phone.Settings",
								SplashActivity.this)) {
							intent = new Intent();
							intent.setClassName("com.android.phone",
									"com.android.phone.Settings");
						}
					}
					if (intent != null)
						startActivity(intent);
				}
			}
		}
	}

	public void networkException(String title) {
		new AlertDialog.Builder(SplashActivity.this)
				.setTitle(title)
				.setIcon(R.drawable.report_error)
				.setCancelable(false)
				.setOnKeyListener(new OnKeyListener() {


					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
							return true;
						}

						return false;
					}

				}).setMessage(getResources().getString(R.string.web_message))
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
						startActivityForResult(intent, 0);

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	/*
	 * 判断是否删除旧版本数据库 --->只对下载数据表，因为数据库与之前版本有变化
	 */
	private void isDeleteDatabaseTable() {
		if(settingPreferences==null)
			Util.print("null", "null");
		String versionFlag = settingPreferences.getString("newinstall", "");
		if(versionFlag.equals("1.2.0"))
		{
			modifyCollectionTable();
		}
		String version = "";
		try {
			version = getPackageManager().getPackageInfo(
					"com.lecootech.market", 0).versionName;
			Util.print("now", version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 针对第一次没有增加这个文件属性的情况下而设.删除原来的数据库
		if (versionFlag.equals("")) {
			downloadDatabase = DatabaseManager.getDownloadDatabase(this);
			downloadDatabase.deleteTable();
			downloadDatabase.close();
			DatabaseManager.setDownloadDatabase();
			downloadDatabase = DatabaseManager.getDownloadDatabase(this);
			settingPreferences.edit().putString("newinstall", version).commit();

		}
		else {
			settingPreferences.edit().putString("newinstall", version).commit();
		}
		
	}
	
	
	public void modifyCollectionTable()
	{
		CollectionDatabase collectionDatabase = DatabaseManager.getCollectionDatabase(this);
		Cursor cursor = collectionDatabase.queryAllData();
		int size = cursor.getCount();
		cursor.close();
		
		if(size!=0)
		   collectionDatabase.addVersionColumn();
	}

	
}
