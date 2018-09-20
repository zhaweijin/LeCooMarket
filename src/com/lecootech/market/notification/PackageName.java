package com.lecootech.market.notification;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageName {

	/**
	 * 获取手机已安装所有应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getpackageName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager
				.getInstalledPackages(0);
		return packageInfoList;
	}

	/**
	 * 获取手机内安装非系统应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getAllApps(Context context) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = paklist.get(i);
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				apps.add(pak);
			}
		}
		return apps;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getEdition(Context context) {

		// PackageManager pManager = context.getPackageManager();
		List<PackageInfo> appList = getAllApps(context);
		String Editionstr = null;
		String[] Editionstr2 = new String[appList.size()];
		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			Editionstr = pinfo.versionName;
			Editionstr2[i] = Editionstr;

		}
		return Editionstr2;
	}

	/**
	 * 获取包名
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getname(Context context) {

		// PackageManager pManager = context.getPackageManager();
		List<PackageInfo> appList = getAllApps(context);
		String str = null;
		String Editionstr = null;
		String[] Editionstr2 = new String[appList.size()];
		String[] str2 = new String[appList.size()];
		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			str = pinfo.packageName;
			Editionstr = pinfo.versionName;
			str2[i] = str;
			Editionstr2[i] = Editionstr;

		}
		return str2;
	}

}
