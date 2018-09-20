package com.lecootech.market;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.Util;

public class SendNotificationServer extends Service {

	private boolean threadFlag = true;
	private NotificationManager nm;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ApplicationConstants.main:
				sendNotification();
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

		new Thread(new Runnable() {

			public void run() {
				try {
					while (threadFlag) {
						Thread.sleep(15 * 60 * 1000);

						if (Util.getNetworkState(SendNotificationServer.this)) {
							Util.loadManagerData(SendNotificationServer.this);
							handler.sendEmptyMessage(ApplicationConstants.main);
						}
						Thread.sleep(12 * 60 * 60 * 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void sendNotification() {
		int size = Util.updateManagerDatas.size();
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Util.print("send noti", "send noti");
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				init_notification(Util.updateManagerDatas.get(i).getName(),
						Integer.parseInt(Util.updateManagerDatas.get(i)
								.getSwid()));
			}
		}
	}

	public void init_notification(String name, int Noti) {
		Notification ni = new Notification(R.drawable.icon2, "市场有更新软件",
				System.currentTimeMillis());

		Intent intent = new Intent(SendNotificationServer.this,
				InformationTabHost.class);
		intent.putExtra("downloading", false);
		intent.putExtra("swid", Noti + "");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		PendingIntent pIntent = PendingIntent.getActivity(
				SendNotificationServer.this, Noti, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		ni.setLatestEventInfo(SendNotificationServer.this, "市场有更新软件", name
				+ "可更新", pIntent);

		nm.notify(Noti, ni);
	}

}
