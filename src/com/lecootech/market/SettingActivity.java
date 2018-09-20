package com.lecootech.market;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.Util;

public class SettingActivity extends PreferenceActivity implements
		OnPreferenceClickListener {
	/** Called when the activity is first created. */

	private Preference cleanDataPreference;
	private ProgressDialog progressDialog;

	private CheckBoxPreference switch_display_preference;

	private SharedPreferences settingPreferences;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ApplicationConstants.main:
				progressDialog.cancel();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preferences);
		settingPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (settingPreferences
				.getBoolean("checkbox_switch_display_start", true)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		progressDialog = Util.getProgressDialog(this, "清空中...");

		cleanDataPreference = findPreference("data_clean");
		cleanDataPreference.setOnPreferenceClickListener(this);

//		switch_display_preference = (CheckBoxPreference) findPreference("checkbox_switch_display_start");

//		switch_display_preference.setOnPreferenceClickListener(this);
		// switch_display_preference.setOnPreferenceChangeListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("data_clean")) {
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("确认清空已下载的图片?")
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									progressDialog.show();
									new Thread(new Runnable() {

										public void run() {
											try {
												File file = new File(Util
														.getStoreApkPath());
												if (file.exists())
													Util.deleteFileDir(file);
												file = new File(Util
														.getStorePicPath());
												if (file.exists())
													Util.deleteFileDir(file);
												handler.sendEmptyMessage(ApplicationConstants.main);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}).start();
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
			return true;
		} 
//		else if (preference.getKey().equals("checkbox_switch_display_start")) {
//			Toast.makeText(this, "重启后生效", 1500).show();
//			Intent intent = new Intent(SettingActivity.this,
//					SplashActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//
//			finish();
//
//			return true;
//		}
		return false;
	}

}