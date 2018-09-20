package com.lecootech.market.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Gallery;

import com.lecootech.market.InformationTabHost;
import com.lecootech.market.MainTabHost;
import com.lecootech.market.R;
import com.lecootech.market.SoftwareRecommendedActivity;
import com.lecootech.market.SoftwareThridFloorTabHost;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.UpdateManagerDatabase;
import com.lecootech.market.handle.DataSet;
import com.lecootech.xml.SoftwareInfo;
import com.lecootech.xml.paeserXml;

public class StartService extends Service {

	ArrayList<SoftwareInfo> softList;
	String base_url;

	
	static Notification mNotification;
	static NotificationManager mNotificationManager;
	final static int NOTIFICATION_ID = 0x0001;
	int i = 0;
	int y = 0;

	private int hasUpdateSize = 0;
	// add by carter
	private TimeChangeReceiver timeBroadcastReceiver;
	private UpdateManagerDatabase updateManagerDatabase;
	
    private static final int NOTIFICATION = 0x111;
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

//				Util.loadManagerData(StartService.this);
				FilterData();
				if (Util.updateManagerDatas != null && hasUpdateSize > 0) {
					mNotification = new Notification(R.drawable.icon, "有"
							+ hasUpdateSize + "款软件可升级，点击查看。",
							System.currentTimeMillis());
					mNotification.defaults = Notification.DEFAULT_SOUND;
					mNotificationManager = (NotificationManager) StartService.this
							.getSystemService(Context.NOTIFICATION_SERVICE);
					Intent mIntent = new Intent(StartService.this,
							MainTabHost.class);
					mIntent.putExtra("topFloorPosition", 4);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					PendingIntent mContentIntent = PendingIntent.getActivity(
							StartService.this, 0, mIntent, 0);
					mNotification.setLatestEventInfo(StartService.this,
							"软件升级提示", "有" + hasUpdateSize + "款软件可升级，点击查看。",
							mContentIntent);
					mNotificationManager.notify(NOTIFICATION_ID, mNotification);
				}
				hasUpdateSize = 0;
				break;
			}
		}
	};


	public void add() {

		base_url = "http://market.lecoo.com:9899/SoftwareUpdateManager.ashx?packageName=null|";
		String pageNameStrs[] = PackageName.getname(StartService.this);
		for (int i = 0; i < pageNameStrs.length; i++) {
			if (i == pageNameStrs.length - 1) {
				base_url += pageNameStrs[i] + "&imei="
						+ Util.getDeviceID(StartService.this);
			} else {
				base_url += pageNameStrs[i] + "|";
			}

		}
		softList = null;
		handler.post(new Runnable() {
			public void run() {
				Message msg = new Message();
				try {
					URL url = new URL(base_url);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					byte[] messageBtye = null;
					if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
						InputStream inputStream = conn.getInputStream();
						int length = conn.getContentLength();
						messageBtye = readBytes(inputStream, 0, length);

						String message = new String(messageBtye, "utf-8");

						softList = (new paeserXml(message)).XMLToSoftwareInfo();
					}
					
					Util.loadManagerData(StartService.this);
				} catch (Exception e1) {
					e1.printStackTrace();
					Util.print("eeee", "eeee");
				} finally {
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
		});

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		timeBroadcastReceiver = getInstance();
		updateManagerDatabase = DatabaseManager.getUpdateManagerDatabase(this);
		registerTimerBroadcasts(this);
		if (intent.getBooleanExtra("reboot", false)) {
			new Thread(new Runnable() {

				public void run() {
					try {

						Thread.sleep(5 * 60 * 1000);
						sendMessageToServer(StartService.this);
//						add();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public void sendMessageToServer(final Context context)
	{
		
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
		        try {
		    		Util.print("999", SharedPrefsUtil.getValue(context, "DomainName",
							WebAddress.comString)+WebAddress.boot_message_server
							+ "imei=" + Util.getDeviceID(context)
							+ "&version=" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
							+ "&productid=" + WebAddress.getproductID(context)
							+ "&network=" + Util.getAPNType(context)
							+ "&imsi=" + Util.getIMsI(context)
							+ "&mobiletype=" + Util.getMODEL(context));
		        	
		    		DataSet dataSet = Util.getSoftwareWebData(context,SharedPrefsUtil.getValue(context, "DomainName",
							WebAddress.comString)+WebAddress.boot_message_server
							+ "imei=" + Util.getDeviceID(context)
							+ "&version=" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
							+ "&productid=" + WebAddress.getproductID(context)
							+ "&network=" + Util.getAPNType(context)
							+ "&imsi=" + Util.getIMsI(context)
							+ "&mobiletype=" + Util.getMODEL(context));
							
		            if(dataSet!=null && dataSet.getNotificationData()!=null)
		            {
		            	adviceNotification(dataSet.getNotificationData().getSound().equals("1")?true:false,
		            			           dataSet.getNotificationData().getShake().equals("1")?true:false,
		            			        		   dataSet.getNotificationData().getShortname(),
		            			        		   dataSet.getNotificationData().getLongname(),
		            			        		   dataSet.getNotificationData().getCategoryid(),
		            			        		   dataSet.getNotificationData().getSwid());
		            	
		            }
							
		    	
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();

	}
	
	public void adviceNotification(boolean sound,boolean shake,String shortname,
			String longname,String category,String swid)
	{
		Notification mNotification = new Notification(R.drawable.icon, shortname,System.currentTimeMillis());
		if(sound)
		   mNotification.defaults |= Notification.DEFAULT_SOUND;
		if(shake)
		  mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		mNotificationManager = (NotificationManager) StartService.this.getSystemService(Context.NOTIFICATION_SERVICE);
		/*-------------------------*/
		Util.print("info1", ""+swid);
		Util.print("info1", ""+category);
		Util.print("info1", longname+"---"+shortname);
		
		Intent intent;
		if(!swid.equals("0"))
		{
			Util.print("info", "go info"+swid+shortname);
			intent = new Intent(this,InformationTabHost.class);
			intent.putExtra("swid", swid);
			intent.putExtra("name", shortname);
		}
		else if(!category.equals("0"))
		{
			Util.print("fenlei", "fenlei"+category);
			intent = new Intent(this,SoftwareThridFloorTabHost.class);
			intent.putExtra("name", shortname);
			intent.putExtra("ID", category);
			intent.putExtra("flag", "normal");
		}
		else {
			intent = new Intent();
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent mContentIntent = PendingIntent.getActivity(StartService.this, 0, intent, 0);
		mNotification.setLatestEventInfo(StartService.this,
				shortname, longname,
				mContentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	/**
	 * 以二进制读取数据
	 * 
	 * @param in
	 * @param length
	 * @param start
	 * @return
	 * @throws IOException
	 */
	public byte[] readBytes(InputStream in, int start, int len) {
		if (start >= len) {
			return null;
		}

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] data = new byte[len];
		byte[] temp = new byte[512];
		int readLen = 0;
		int destPos = start;
		try {
			while ((readLen = in.read(temp)) > -1) {
				System.arraycopy(temp, 0, data, destPos, readLen);
				destPos += readLen;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		bo.write(data, 0, destPos);

		try {
			bo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bo.toByteArray();
	}

	public void FilterData() {

		try {
			int size = Util.updateManagerDatas.size();
			for (int i = 0; i < size; i++) {
				if (!checkIsIgnore(Util.updateManagerDatas.get(i)
						.getPackageName())) {
					hasUpdateSize++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean checkIsIgnore(String packagename) {
		Cursor temCursor;
		boolean flag = false;
		temCursor = updateManagerDatabase.queryOneData(packagename);
		int size = temCursor.getCount();
		if (size > 0) {
			flag = true;
		}
		temCursor.close();
		return flag;
	}



	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	// add by carter
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(timeBroadcastReceiver);
	}

	public void registerTimerBroadcasts(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.TIME_TICK");
		intentFilter.addAction("android.intent.action.TIME_SET");
		context.registerReceiver(timeBroadcastReceiver, intentFilter);
	}

	public TimeChangeReceiver getInstance() {
		if (timeBroadcastReceiver == null)
			timeBroadcastReceiver = new TimeChangeReceiver();
		return timeBroadcastReceiver;
	}

	class TimeChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			String str = paramIntent.getAction();
			if (("android.intent.action.TIME_TICK".equals(str))
					|| "android.intent.action.TIME_SET".equals(str)
					|| (str.equals(Intent.ACTION_TIME_CHANGED))) {

				Calendar cal = Calendar.getInstance();
				java.util.Date date = cal.getTime();
				date.setTime(cal.getTimeInMillis());
				Util.print("time", "time");
				if (Util.is24HourFormat(StartService.this)) {
					if (date.getHours() == 21 && date.getMinutes() == 0) {
						Util.print("time1",date.getHours() + "&&" + date.getMinutes());
						add();
					}
				} else {
					if (Calendar.PM == cal.get(Calendar.AM_PM)) {
						if (date.getHours() == 9 && date.getMinutes() == 0) {
							Util.print("time2",date.getHours() + "&&" + date.getMinutes());
							add();
						}
					}
				}
			}
		}
	}
}