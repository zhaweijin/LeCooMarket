package com.lecootech.market.notification;

import com.lecootech.market.common.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 接收开机广播，启动服务
 * 
 * @author Administrator
 * 
 */
public class Broadcast extends BroadcastReceiver {

	private SharedPreferences settingPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			settingPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (settingPreferences.getBoolean("checkbox_reboot_start", true)) {
				Intent service = new Intent(context, StartService.class);
				service.putExtra("reboot", true);
				context.startService(service);
			}
		}

	}

}
