package com.lecootech.market;

import java.io.File;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.lecootech.market.common.Util;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

/*
 * @author zhaweijin
 * 通知广播，针对软件包的安装监听
 */
public class ReceivePackageManage extends BroadcastReceiver {

	private SharedPreferences settingPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent.getAction()
					.equals("android.intent.action.PACKAGE_ADDED")
					|| intent.getAction().equals(
							"android.intent.action.PACKAGE_REPLACED")) {
				String packagename = intent.getDataString();
				packagename = packagename.substring(
						packagename.indexOf(":") + 1, packagename.length());
				Util.print("name", packagename);
				DownloadDatabase downloadDatabase = DatabaseManager
						.getDownloadDatabase(context);
				
				NotificationManager manager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				if (packagename != "") // 接收到重复安装通知，删除通知ID号，且删除数据库记录
				{
					Cursor cursor = downloadDatabase.queryByPackage(packagename);
					int size = cursor.getCount();
					Util.print("o", "" + size);
					cursor.moveToFirst();
					String filepath = "";
					if (size != 0) {
						String swid = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
						int id = Integer.parseInt(swid.substring(
								swid.lastIndexOf("/") + 9, swid.length()));
						filepath = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_FILEPATH));
						Util.print("swid", swid);
						manager.cancel(id);
						//删除线程表记录
						String name = cursor.getString(cursor.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
						
						
						downloadDatabase.deleteByPackageData(packagename);// 删除数据为记录
					}
					cursor.close();
					if (!Util.avaiableMedia()) // 在没有sd卡的时候，安装完成后删除下载文件
					{
						File file = new File(filepath);
						if (file.exists())
							file.delete();
					}

					if (intent.getAction().equals(
							"android.intent.action.PACKAGE_REPLACED")) {
						Util.print("updated", "updated");
						int count = Util.updateManagerDatas.size();
						// 如果是更新在安装软件包当中的一个，那么总数-1
						if (count > 0) {
							for (int i = 0; i < count; i++) {
								if (Util.updateManagerDatas.get(i)
										.getPackageName().equals(packagename))
									Util.updateManagerDatas.remove(i);
							}
						}
					}

					if (intent.getAction().equals(
							"android.intent.action.PACKAGE_REMOVED")) {
						// 增加了当系统包升级的时候，判断之前是否已经创建
						if (packagename.equals(context.getPackageName())) {
							settingPreferences = PreferenceManager
									.getDefaultSharedPreferences(context);
							if (settingPreferences
									.getBoolean("shortcut", false)) {
								delShortcut(context);
							}
						}
					}
				}
			} else if (intent.getAction().equals(
					"android.intent.action.BOOT_COMPLETED")) {
				Intent intent2 = new Intent(context,
						SendNotificationServer.class);
				context.startActivity(intent2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除程序的快捷方式
	 */
	private void delShortcut(Context context) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
		String appClass = context.getPackageName() + "." + "SplashActivity";
		ComponentName comp = new ComponentName(context.getPackageName(),
				appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		context.sendBroadcast(shortcut);

	}

}
