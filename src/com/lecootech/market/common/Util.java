package com.lecootech.market.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.lecootech.market.InformationTabHost;
import com.lecootech.market.MainTabHost;
import com.lecootech.market.R;
import com.lecootech.market.data.TempPictureThread;
import com.lecootech.market.data.UpdateManagerData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;
import com.lecootech.market.database.ItemPicDatabase;
import com.lecootech.market.download.DownladUtil;
import com.lecootech.market.handle.DataHandle;
import com.lecootech.market.handle.DataSet;

public class Util {
	private static final int ERROR = -1;

	public static ArrayList<UpdateManagerData> updateManagerDatas = new ArrayList<UpdateManagerData>();

	public static Map<String, Activity> stack = new HashMap<String, Activity>();
	// 装机必备选中变量状态
	public static ArrayList<Boolean> installcheck = new ArrayList<Boolean>();

	public static final int threadMAX = 2;
	// 线程池管理下载线程
	public static ExecutorService downloadThreadPool;

	public static ExecutorService getDownloadThreadPool() {
		if (downloadThreadPool == null) {
			downloadThreadPool = Executors.newFixedThreadPool(threadMAX);
		}
		return downloadThreadPool;
		
	}
	
	public static void setDownloadThreadPoolNull()
	{
		downloadThreadPool = null;
	}

	public static void print(String tag, String value) {
		Log.v(tag, value);
	}

	public static boolean getNetworkState(Context context) {
		boolean mIsNetworkUp = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			mIsNetworkUp = info.isAvailable();
		}
		return mIsNetworkUp;
	}

	public static ProgressDialog getProgressDialog(Context context,
			String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setIcon(R.drawable.icon);
		progressDialog.setIndeterminate(false);
		return progressDialog;
	}

	public static int getDisplayMetricsHeight(Activity activity) {
		android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getDisplayMetricsWidth(Activity activity) {
		android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}
	
	public static void setListViewHeight(Activity activity, ListView listView,
			int hasUsedHeight) {
		ViewGroup.LayoutParams lParams;
		listView.setLayoutParams(ApplicationConstants.LP_FW);
		// listView.setDividerHeight(1);
		// listView.setDivider(context.getResources().getDrawable(R.drawable.mid));
		listView.setCacheColorHint(R.color.listview_cacheColorHint);
		lParams = listView.getLayoutParams();

		// Display display = activity.getWindowManager().getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		lParams.height = metrics.heightPixels - hasUsedHeight;
		listView.setLayoutParams(lParams);

	}

	/*
	 * author carter
	 * 
	 * @ArrayList<String> 获取已安装列表
	 */
	public static ArrayList<String> getInstallPackageName(Context context) {
		List<ApplicationInfo> applicationInfos = context.getPackageManager()
				.getInstalledApplications(0);
		ArrayList<String> packagename = new ArrayList<String>();
		for (int i = 0; i < applicationInfos.size(); i++) {
			ApplicationInfo appInfo = applicationInfos.get(i);
			String temppackagename = appInfo.packageName;
			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				packagename.add(temppackagename);
			}
		}
		return packagename;
	}

//	public static String getTextContent(String urlpath, String encoding)
//			throws Exception {
//		URL url = new URL(urlpath);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("GET");
//		conn.setConnectTimeout(6 * 1000);
//		// 请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，
//		// post请求服务器返回的响应码是206（貌似）。
//		if (conn.getResponseCode() == 200) {
//			InputStream inStream = conn.getInputStream();
//			byte[] data = readStream(inStream);
//			return new String(data, encoding);
//		}
//		return null;
//	}

	public static InputStream getContent(String urlpath, String encoding)
			throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;
	}

	public static byte[] getImage(String urlpath) throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			return readStream(inStream);
		}
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	

	public static List<PackageInfo> getpackageName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager
				.getInstalledPackages(0);
		return packageInfoList;
	}

	public static List<ApplicationInfo> getUnintalledApp(
			PackageManager packageManager) {
		List<ApplicationInfo> installedAppList = packageManager
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

		List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo appInfo : installedAppList) {
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
					|| (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				result.add(appInfo);
			}
		}
		installedAppList.clear();
		installedAppList = null;
		Collections.sort(result, new ApplicationInfo.DisplayNameComparator(
				packageManager));
		return result;
	}

	public static void dialogAbout(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.menu_dialog_about, null);

		final Dialog dialog = new Dialog(context, R.style.TrafficWarningTheme);
		dialog.setContentView(view);
		TextView verTextView = (TextView) view.findViewById(R.id.version_code);
		try {
			verTextView.setText(context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button okButton = (Button) view
				.findViewById(R.id.dialog_about_okbutton);
		okButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public static boolean checkPackageIsExist(Context context,
			String packageName) {
		try {
			PackageManager mPm = context.getPackageManager();
			ApplicationInfo mAppInfo = mPm.getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			// show dialog
			return false;
		}
	}

	public static Boolean isSystemApp(ApplicationInfo mAppInfo) {
		Boolean mUpdatedSysApp = (mAppInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
		if (mUpdatedSysApp) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compare two versions.
	 * 
	 * @param newVersion
	 *            new version to be compared
	 * @param oldVersion
	 *            old version to be compared
	 * @return true if newVersion is greater then oldVersion, false on
	 *         exceptions or newVersion=oldVersion and newVersion is lower then
	 *         oldVersion
	 */
	public static boolean compareVersions(String newVersion, String oldVersion) {
		if (newVersion.equals(oldVersion))
			return false;

		// Replace all - by . So a CyanogenMod-4.5.4-r2 will be a
		// CyanogenMod.4.5.4.r2
		newVersion = newVersion.replaceAll("-", "\\.");
		oldVersion = oldVersion.replaceAll("-", "\\.");

		String[] sNewVersion = newVersion.split("\\.");
		String[] sOldVersion = oldVersion.split("\\.");

		ArrayList<String> newVersionArray = new ArrayList<String>();
		ArrayList<String> oldVersionArray = new ArrayList<String>();

		newVersionArray.addAll(Arrays.asList(sNewVersion));
		oldVersionArray.addAll(Arrays.asList(sOldVersion));

		// Make the 2 Arrays the Same size filling it with 0. So Version 2
		// compared to 2.1 will be 2.0 to 2.1
		if (newVersionArray.size() > oldVersionArray.size()) {
			int difference = newVersionArray.size() - oldVersionArray.size();
			for (int i = 0; i < difference; i++) {
				oldVersionArray.add("0");
			}
		} else {
			int difference = oldVersionArray.size() - newVersionArray.size();
			for (int i = 0; i < difference; i++) {
				newVersionArray.add("0");
			}
		}

		int i = 0;
		for (String s : newVersionArray) {
			String old = oldVersionArray.get(i);
			// First try an Int Compare, if its a string, make a string compare
			try {
				int newVer = Integer.parseInt(s);
				int oldVer = Integer.parseInt(old);
				if (newVer > oldVer)
					return true;
				else if (newVer < oldVer)
					return false;
				else
					i++;
			} catch (Exception ex) {
				// If we reach here, we have to string compare cause the version
				// contains strings
				int temp = s.compareToIgnoreCase(old);
				if (temp < 0)
					return false;
				else if (temp > 0)
					return true;
				else
					// its the same value so continue
					i++;
			}
		}
		// Its Bigger so return true
		return true;
	}

	/**
	 * @picString 图片下载网络地址
	 * @name 名称
	 * @date 日期
	 * @swid 文件名称
	 * @parentname 父目录
	 * @fucntion 获取位图并把记录存储数据库，图片以文件的方式存储 返回Bitmap 做为异步加载的图标来显示在列表当中
	 */
	private static ItemPicDatabase picDatabase;
	private static String temppicpath = "";
	private static String picpath;

	public synchronized static Bitmap getWeBitmap(Activity activity,
			String picString, String name, String date, String swid,
			String parentname, Boolean BigImageOrSmall) {
		Bitmap bitmap = null;
		try {
			String filepath = "";
			picDatabase = DatabaseManager.getItemPicDatabase(activity);
			temppicpath = getStorePicPath();

			if (picString.equals("")) // 图标地址为空的情况
			{
				if (BigImageOrSmall) {
					bitmap = BitmapFactory.decodeResource(
							activity.getResources(),
							R.drawable.listview_top_image_bg);
				} else {
					bitmap = BitmapFactory.decodeResource(
							activity.getResources(), R.drawable.icon2);
				}
			} else {
				Cursor myCursor = null;
				if (!avaiableMedia()) // 没有SDcard的情况
				{
					myCursor = picDatabase.queryOneData(name, parentname);
					myCursor.moveToFirst();

					int size = myCursor.getCount();
					if (size != 0) // 如果是不是第一次下载图标
					{
						// 查询已下载图标的文件地址
						for (int i = 0; i < size; i++) {
							String flagSDcard = myCursor
									.getString(myCursor
											.getColumnIndexOrThrow(ItemPicDatabase.KEY_DATE));
							if (flagSDcard.equals("nosdcard")) {
								if (BigImageOrSmall)
									filepath = myCursor
											.getString(myCursor
													.getColumnIndexOrThrow(ItemPicDatabase.KEY_BIGIMAGE_PATH));
								else
									filepath = myCursor
											.getString(myCursor
													.getColumnIndexOrThrow(ItemPicDatabase.KEY_PATH));
								break;
							} else {
								myCursor.moveToNext();
							}
						}
						if (!myCursor.isClosed())
							myCursor.close();
						// 如果文件地址为空，则重新下载
						if (filepath.equals("")) {
							if (BigImageOrSmall)
								picDatabase.insertData(name, parentname, "",
										"nosdcard", picString);
							else
								picDatabase.insertData(name, parentname,
										picString, "nosdcard", "");
							createWebPicData(
									activity,
									ApplicationConstants.system_download_pic_path,
									picString, swid, name, parentname,
									picDatabase);
							bitmap = BitmapFactory.decodeFile(picpath);
						} else // 如果还存在则直接
						{
							File file = new File(filepath);
							boolean tempflag = (file.length() == 0);
							if (tempflag) {
								file.delete();
							}
							// 如果已下载文件已经被删除，则重新下载
							if (!file.exists()) {
								filepath = ApplicationConstants.system_download_pic_path
										+ swid;
								if (BigImageOrSmall)
									picDatabase.updateDataBigPath(name,
											parentname, date, filepath);
								else
									picDatabase.updateDataIconPath(name,
											parentname, filepath, date);
								createWebPicData(
										activity,
										ApplicationConstants.system_download_pic_path,
										picString, swid, name, parentname,
										picDatabase);
							}
							bitmap = BitmapFactory.decodeFile(filepath);
						}
					} else { // 第一次下载图标
						picpath = ApplicationConstants.system_download_pic_path
								+ swid;
						if (BigImageOrSmall)
							picDatabase.insertData(name, parentname, "",
									"nosdcard", picString);
						else
							picDatabase.insertData(name, parentname, picString,
									"nosdcard", "");

						createWebPicData(activity,
								ApplicationConstants.system_download_pic_path,
								picString, swid, name, parentname, picDatabase);
						bitmap = BitmapFactory.decodeFile(picpath);

					}
				} else { // 有SDcard的情况

					myCursor = picDatabase.queryOneData(name, parentname);
					myCursor.moveToFirst();
//                    Util.print("has sdcard", "has sdcard");
					if (myCursor.getCount() == 0) // 第一次下载图标，则开始下载
					{
						filepath = temppicpath + swid;
						if (BigImageOrSmall)
							picDatabase.insertData(name, parentname, "", date,
									filepath);
						else
							picDatabase.insertData(name, parentname, filepath,
									date, "");
						if (!myCursor.isClosed())
							myCursor.close();
						createWebPicData(activity, temppicpath, picString,
								swid, name, parentname, picDatabase);

					} else { // 已经下载当前图标
						
						//如果有多于一条记录，则删除其它一条
						if(myCursor.getCount()>1)
						{
							long rowid = myCursor.getLong(myCursor.getColumnIndexOrThrow(ItemPicDatabase.KEY_ROWID));
							picDatabase.deleteData(rowid);
							myCursor.moveToNext();
						}
						
						if (BigImageOrSmall)
							filepath = myCursor
									.getString(myCursor
											.getColumnIndexOrThrow(ItemPicDatabase.KEY_BIGIMAGE_PATH));
						else
							filepath = myCursor
									.getString(myCursor
											.getColumnIndexOrThrow(ItemPicDatabase.KEY_PATH));
						String tempdate = myCursor
								.getString(myCursor
										.getColumnIndexOrThrow(ItemPicDatabase.KEY_DATE));
						if (!myCursor.isClosed())
							myCursor.close();
						if (filepath.equals("")) {
							filepath = temppicpath + swid;
						}
						// 如果已下载的文件长度为零，则删除当前文件
						File file = new File(filepath);
						boolean tempflag = (file.length() == 0);
						if (tempflag) {
							file.delete();
						}
						// 如果已下载的不存在或者 图标地址有更新，则重新下载
						if (!file.exists() || !tempdate.equals(date)) {
							if (BigImageOrSmall)
								picDatabase.updateDataBigPath(name, parentname,
										date, filepath);
							else {
								picDatabase.updateDataIconPath(name,
										parentname, filepath, date);
							}

							createWebPicData(activity, temppicpath, picString,
									swid, name, parentname, picDatabase);
						}
					}
					// Util.print("filepath", filepath);
					bitmap = BitmapFactory.decodeFile(filepath);
				}
				myCursor.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (BigImageOrSmall) {
				bitmap = BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.listview_top_image_bg);
			} else {
				bitmap = BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.icon2);
			}

		}

		return bitmap;
	}

	/**
	 * @author zhaweijin
	 * @fucntion 解析网络数据---->软件
	 */
	public static DataSet getSoftwareWebData(Context context,String webpage) {
		try {
			URL url=null;
			HttpURLConnection conn = null;
			if(checkIsCmwapNetwork(context))
			{
				Util.print("cmwap-->", "cmwap-->");
				url = new URL(WebAddress.proxypath+
						webpage.substring(webpage.lastIndexOf("/")+1, webpage.length()));
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setRequestProperty("X-Online-Host", WebAddress.comString);				
			}
			else {
				Util.print("no cmwap-->", "no cmwap-->");
				url = new URL(webpage);
				conn = (HttpURLConnection) url.openConnection();
			}

			conn.setDoInput(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(40000);
			conn.connect();
			

			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			DataHandle gwh = new DataHandle();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			InputSource is = new InputSource(isr);
			xr.setContentHandler(gwh);
			xr.parse(is);
			return gwh.getDataSet();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean checkIsCmwapNetwork(Context context) {
		boolean result = false;
		if(context==null)
		{
			Util.print("context null", "context null");
		}
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		String extraInfo = "";
		if (networkInfo != null) {
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE) {
				extraInfo = networkInfo.getExtraInfo();
			}
		}

		if ((extraInfo.trim().toLowerCase().equals("cmwap"))) {
			result = true;
		}

		return result;
	}
	
	
	/**
	 * @author zhaweijin
	 * @fucntion 截取网络地址的文件名
	 */
	public static String[] fileName(String webpath) {
		try {
			String temp[] = new String[2];
			temp[0] = webpath.substring(webpath.lastIndexOf(".") + 1,
					webpath.length()).toLowerCase();
			temp[1] = webpath.substring(webpath.lastIndexOf("/") + 1,
					webpath.lastIndexOf("."));
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @author zhaweijin
	 * @fucntion 获取打开文件文件的类型
	 */
	public static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		return type;
	}

	/**
	 * @author zhaweijin
	 * @fucntion 创建图片文件在SDcard上面
	 */
	public static void createWebPicData(Activity activity, String parent,
			String webpath, String swid, String name, String parentname,
			ItemPicDatabase picDatabase) {
		temppicpath = parent;
		Util.print("pic path", ""+webpath);
		try {
			URL url = null;
			HttpURLConnection conn = null;
			if(checkIsCmwapNetwork(activity))
			{
				Util.print("cmwap-->", "cmwap-->");
				url = new URL(WebAddress.proxypath+
						webpath.substring(webpath.indexOf("9899")+5, webpath.length()));
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setRequestProperty("X-Online-Host", WebAddress.comString);				
			}
			else {
				url = new URL(webpath);
				conn = (HttpURLConnection)url.openConnection();
			}
			conn.setDoInput(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(40000);
			conn.connect();
			
			

			InputStream inputStream = conn.getInputStream();
			File sdcardFile = new File(temppicpath);
			if (!sdcardFile.exists())
				sdcardFile.mkdirs();
			// String[] tempname = fileName(webpath);
			picpath = temppicpath + swid;
			File tempFile = new File(picpath);
			if (!tempFile.exists())
				tempFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(tempFile);

			byte[] b = new byte[1024];
			int length = -1;
			while ((length = inputStream.read(b)) != -1) {
				fos.write(b, 0, length);
			}
			fos.close();
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * @author zhaweijin
	 * @fucntion 检测SDcard是否插入
	 */
	public static boolean avaiableMedia() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
			return true;
		else {
			return false;
		}
	}

	/**
	 * @author zhaweijin
	 * @fucntion 删除文件，根据路径名
	 */
	public static void delFile(String strFileName) {
		File myFile = new File(strFileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

	/**
	 * @author zhaweijin
	 * @fucntion 删除目录文件
	 */
	public static boolean deleteFileDir(File f) {
		boolean result = false;
		try {
			if (f.exists()) {

				File[] files = f.listFiles();
				for (File file : files) {

					if (file.isDirectory()) {
						if (deleteFile(file))
							result = false;
					} else {
						deleteFile(file);
					}
				}
				f.delete();
				result = true;
			}
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	/**
	 * @author zhaweijin
	 * @fucntion 删除文件，根据文件对象
	 */
	public static boolean deleteFile(File f) {
		boolean result = false;
		try {
			if (f.exists()) {
				f.delete();
				result = true;
			}
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	/**
	 * @author zhaweijin
	 * @fucntion 获取系统版本号
	 */
	public static int getSystemVersion() {
		return Integer.parseInt(android.os.Build.VERSION.SDK);
	}

	public static List<TempPictureThread> threads = new ArrayList<TempPictureThread>();

	/*
	 * @TempPictureThread 图标对象 添加当前线程对象到数组当中
	 */
	public static void setData(TempPictureThread thread) {
		threads.add(thread);
	}

	/*
	 * 返回当前线程数组
	 */
	public static List<TempPictureThread> getData() {
		return threads;
	}

	/*
	 * 检测当前线程是否已经关闭，且返回状态
	 */
//	public static boolean CheckThreadIsClosed() {
//		int size = getData().size();
//		ImagePageTask task;
//		boolean finishFlag = true;
//
//		for (int i = 0; i < size; i++) {
//			task = getData().get(i).getTask();
//
//			if (task.getStatus() != AsyncTask.Status.FINISHED) {
//
//				finishFlag = false;
//				break;
//			}
//		}
//		return finishFlag;
//	}

	// 删除在没有SD卡下面，产生的数据库记录。包括清除内存当中的数据
	public static void ClearDatabase_SDcard(Context context) {
		File file;
		picDatabase = DatabaseManager.getItemPicDatabase(context);
		file = new File(ApplicationConstants.system_download_apk_path);

		if (file.exists()) {
			deleteFileDir(file);
		}

		file = new File(ApplicationConstants.system_download_pic_path);

		if (file.exists()) {
			deleteFileDir(file);
		}

		picDatabase.deleteMulNoSDcardData();

	}

	/*
	 * 获取手机设备ID
	 */
	public static String getDeviceID(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

	// 获取软件存放的文件夹路径
	public static String getStoreApkPath() {

		if (avaiableMedia())
			return Environment.getExternalStorageDirectory()
					+ ApplicationConstants.store_download_apk_path;
		else
			return ApplicationConstants.system_download_apk_path;
	}

	/**
	 *  获取软件存放的图标存放文件夹
	 * @return
	 */
	public static String getStorePicPath() {
		if (avaiableMedia())
			return Environment.getExternalStorageDirectory()
					+ ApplicationConstants.store_download_pic_path;
		else
			return ApplicationConstants.system_download_pic_path;
	}

	/*
	 * @name 名称
	 * 
	 * @parentname 父类名称 根据上面两个字段查找itempicdatabase数据库存放的图标地址，且生成Bitmap做返回值
	 */
	public static Bitmap loadImageView(Context context, String name,
			String parentname) {
		Cursor cursor = null;
		try {
			ItemPicDatabase picDatabase = DatabaseManager
					.getItemPicDatabase(context);
			Bitmap bitmap;
			if (!Util.avaiableMedia()) {
				cursor = picDatabase.queryOneData(name, parentname);
				cursor.moveToFirst();
				String filepath = "";
				int size = cursor.getCount();
				if (size != 0) {
					for (int i = 0; i < size; i++) {
						String flagSDcard = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ItemPicDatabase.KEY_DATE));
						if (flagSDcard.equals("nosdcard")) {
							filepath = cursor
									.getString(cursor
											.getColumnIndexOrThrow(ItemPicDatabase.KEY_PATH));
							break;
						} else {
							cursor.moveToNext();
						}
					}

					// print("2", filepath);
					File file = new File(filepath);
					if (file.exists()) {
						bitmap = BitmapFactory.decodeFile(filepath);
						if (bitmap == null)
							bitmap = BitmapFactory.decodeResource(
									context.getResources(), R.drawable.icon2);
					} else {
						bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.icon2);
					}
				} else {
					bitmap = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.icon2);
				}
				if (cursor != null)
					cursor.close();
			} else {
				cursor = picDatabase.queryOneData(name, parentname);
				cursor.moveToFirst();
				int count = cursor.getCount();
				if (count != 0) {
					String filepath = cursor.getString(cursor
							.getColumnIndexOrThrow(ItemPicDatabase.KEY_PATH));

					cursor.close();
					File file = new File(filepath);
					if (file.exists()) {
						bitmap = BitmapFactory.decodeFile(filepath);
						if (bitmap == null)
							bitmap = BitmapFactory.decodeResource(
									context.getResources(), R.drawable.icon2);
					} else {
						bitmap = BitmapFactory.decodeResource(
								context.getResources(), R.drawable.icon2);
					}

				} else {
					bitmap = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.icon2);
				}
				if (cursor != null)
					cursor.close();
			}
			if (cursor != null)
				cursor.close();
			return bitmap;
		} catch (Exception e) {
			Util.print("bitmap error", "bitmap error");
			cursor.close();
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * @software_name 软件名称
	 * 
	 * @webpath 网络地址
	 * 
	 * @packagename 包名
	 * 
	 * @swid 通知ID 发送下载通知 且启动下载服务
	 */
	public static void sendMarketDownloadNotification(Context context,
			String software_name, String webpath, String packagename, int swid,
			String soft_size) {

		try {
			String temppicpath;
			String tempapkpath;
			String apkpath;

			String[] fileName = Util.fileName(webpath);

			// 判断有无sd卡
			temppicpath = getStorePicPath();
			apkpath = getStoreApkPath() + fileName[1] + "." + fileName[0];
			tempapkpath = getStoreApkPath();
			// if (Util.avaiableMedia()) {
			// temppicpath = Util.getStorePicSDcardPath();
			// apkpath = Util.getStoreApkSDcardPath() + fileName[1] + "."+
			// fileName[0];
			// tempapkpath = Util.getStoreApkSDcardPath();
			//
			// } else {
			// temppicpath = "data/data/com.lexteltech.mon/XPicMarket/";
			// apkpath = "data/data/com.lexteltech.mon/download/" + fileName[1]
			// + "."+ fileName[0];
			// tempapkpath = "data/data/com.lexteltech.mon/download/";
			// }
			// 插入记录到数据库
			DownloadDatabase downloadDatabase = DatabaseManager
					.getDownloadDatabase(context);
			Cursor temCursor = downloadDatabase.queryID(software_name);
			temCursor.moveToFirst();
			int size = temCursor.getCount();
			if (size != 0) {
				downloadDatabase.updateDownloadOperation(software_name, 0);
				// downloadDatabase.updateAll(software_name, 0, 0);
			} else {
				downloadDatabase.insertData(software_name, temppicpath
						+ "software" + swid, packagename, 0, 0, webpath,
						apkpath, 0, 0, soft_size);
			}
			Util.print("software_name", software_name);
			Util.print("temppicpath + swid", temppicpath + swid);
			Util.print("packagename", packagename);
			Util.print("webpath", webpath);
			Util.print("apkpath", apkpath);

			// 开始设置下载数据

			temCursor.close();
			DownladUtil.startDownloadService(context, swid, software_name,
					tempapkpath, webpath);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @keyword 关键字
	 * 
	 * @value 关键值 提交下载数据到服务器
	 */
	public static void CommitDownloadNum(final Context context,final String webpath,
			final String[] keyword, final String[] value) {
		new Thread(new Runnable() {

			public void run() {
				try {
					if(checkIsCmwapNetwork(context))
					{
						String parameter = "";
						for(int i=0;i<keyword.length;i++)
						{
							parameter = parameter + keyword[i] + "=" + value[i] + "&";
						}
						if(!parameter.equals(""))
							parameter = parameter.substring(0, parameter.length()-1);
						Util.print("cmwap-->", "cmwap-->"+parameter);
						URL url = new URL(WebAddress.proxypath+
								webpath.substring(webpath.lastIndexOf("/")+1, webpath.length())+parameter);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
						conn.setRequestProperty("X-Online-Host", WebAddress.comString);	
						
						conn.setDoInput(true);
						conn.setConnectTimeout(20000);
						conn.setReadTimeout(40000);
						conn.connect();
						
					}
					else {
						DefaultHttpClient httpclient = new DefaultHttpClient();
						HttpPost httpost = new HttpPost(webpath);
						httpost.getParams().setBooleanParameter(
								CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						for (int i = 0; i < keyword.length; i++) {
							nvps.add(new BasicNameValuePair(keyword[i], value[i]));
						}
						httpost.setEntity(new UrlEncodedFormEntity(nvps, "gb2312"));
						httpclient.execute(httpost);
					}

				} catch (Exception e) {
					e.printStackTrace();
					Util.print("commit download num error",
							"commit download num error");
				}
			}
		}).start();
	}

	/**
	 * 给内存文件修改权限
	 */
	public static void execMethod(String path) {

		String args[] = new String[3];
		args[0] = "chmod";
		args[1] = "777";
		args[2] = path;
		try {
			Runtime.getRuntime().exec(args);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * 将汉字转为UTF8编码的串
	 * 
	 * @param s
	 * @return
	 */
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	/*
	 * @packagename 包名 根据包名打开软件
	 */
	public static void OpenSoftware(Activity activity, String packagename) {
		Intent intent = new Intent();
		PackageManager packageManager = activity.getPackageManager();
		try {
			intent = packageManager.getLaunchIntentForPackage(packagename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		activity.startActivity(intent);
	}

	/**
	 * @author zhaweijin
	 * @function 下载截图并以文件的方式存储
	 */

	public static void getSoftwareImage(Activity activity, String name,
			String filepath, String dot, boolean f, String temppicpath) {
		Util.print("information->pic", filepath);
		if (!f) {
			temppicpath = Util.getStorePicPath();
		}
		try {

			URL url = null;
			HttpURLConnection conn = null;
			if(checkIsCmwapNetwork(activity))
			{
				Util.print("cmwap-->", "cmwap-->");
				url = new URL(WebAddress.proxypath+
						filepath.substring(filepath.indexOf("9899")+5, filepath.length()));
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setRequestProperty("X-Online-Host", WebAddress.comString);				
			}
			else {
				url = new URL(filepath);
				conn = (HttpURLConnection)url.openConnection();				
			}
			conn.setDoInput(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(40000);
			conn.connect();
			
			
			InputStream inputStream = conn.getInputStream();
			File sdcardFile = new File(temppicpath);
			if (!sdcardFile.exists())
				sdcardFile.mkdirs();
			File tempFile = new File(temppicpath + dot);
			Util.print("filepath", temppicpath + dot);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
				Util.print("created", "created");
			}

			FileOutputStream fos = new FileOutputStream(tempFile);

			byte[] b = new byte[1024];
			int length = -1;
			while ((length = inputStream.read(b)) != -1) {
				fos.write(b, 0, length);
			}
			fos.close();
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			Util.print("software_info_getWebpicture_exception",
					"software_info_getWebpicture_exception");
		}
	}

	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	// 类似的wifi是否打开
	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前的网络状态 null：没有网络 W：WIFI网络 G：GPRS网络
	 * 
	 * @param context
	 * @return
	 */
	public static String getAPNType(Context context) {
		String net = null;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return net;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			net = "G";
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			net = "W";
		}
		return net;
	}

	public static String postPerpageNum(int page, int perpage) {
		String result = "page=" + page + "&pagesize=" + perpage;
		return result;
	}

	public static String postPerpageNum2(int page, int perpage, String productID,String network ) {
		String result = "page=" + page + "&pagesize=" + perpage + "&productID="
				+ productID+"&n="+network;
		return result;
	}

	public static String translateWebChar(String content) {
		StringBuffer buffer = new StringBuffer();
		if (content.contains("\n")) {
			String[] temp_content = content.split("\n");
			Util.print("aa", "bb" + temp_content.length);
			for (int i = 0; i < temp_content.length; i++) {
				buffer.append(temp_content[i] + "\n");
			}
			return buffer.toString();
		} else {
			return content;
		}
	}

	public static void loadManagerData(final Context context) {
		DataSet managerDataSet = null;
		String packagename = "";
		updateManagerDatas.clear();
		List<ApplicationInfo> appInfoList = Util.getUnintalledApp(context
				.getPackageManager());
		Util.print("size", appInfoList.size() + "");
		int count = appInfoList.size();
		for (ApplicationInfo appInfo : appInfoList) {
			// Filter system application
			packagename = packagename + appInfo.packageName + "|"; // 包名
		}
		if (packagename.length() > 0)
			packagename = packagename.substring(0, packagename.length() - 1);
		Util.print(
				"getupdatedata",
				SharedPrefsUtil.getValue(context, "DomainName",
						WebAddress.comString)
						+ WebAddress.software_update_manager + packagename);
		managerDataSet = Util.getSoftwareWebData(context,SharedPrefsUtil.getValue(
				context, "DomainName", WebAddress.comString)
				+ WebAddress.software_update_manager + packagename);

		if (managerDataSet != null) {
			int size = managerDataSet.getUpdateManagerDatas().size();
			if (count == size) {
				try {
					int i = 0;
					Util.print("app_size", appInfoList.size() + "");
					for (ApplicationInfo appInfo : appInfoList) {
						// Filter system application

						PackageInfo info = getPackageInfo(context,
								appInfo.packageName);
						if (info != null) {
							String oldversion = info.versionName; // 版本
							if (Util.compareVersions(managerDataSet
									.getUpdateManagerDatas().get(i)
									.getVersion(), oldversion)) {
								updateManagerDatas.add(managerDataSet
										.getUpdateManagerDatas().get(i));
							}
						}

						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static PackageInfo getPackageInfo(Context context, String packagename) {
		try {
			PackageManager manager = context.getPackageManager();
			return manager.getPackageInfo(packagename, 0);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * 用Hashmap管理Activity，方便随时退出整个程序
	 */
	public static void addActivityToStack(Activity currentActivity,
			String activityname) {

		if (currentActivity != null) {

			if (stack.containsKey(activityname)) {
				if (!stack.get(activityname).isFinishing()) {
					stack.get(activityname).finish();
				}

				stack.remove(activityname);
			}
			stack.put(activityname, currentActivity);
		}

	}

	public static boolean checkCurrentSoftwareDownloading(Context context,
			String software_name) {
		Util.print("name", software_name);
		boolean result = false;
		DownloadDatabase downloadDatabase = DatabaseManager
				.getDownloadDatabase(context);
		Cursor temCursor = downloadDatabase.queryID(software_name);
		int returnvalue = -1;
		int downloadFlag = 0;
		int size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size > 0) {
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
			if (returnvalue != 100) {
				downloadFlag = temCursor.getInt(temCursor
						.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
				if (downloadFlag == 0) {
					result = true;
				}
			}
		}

		temCursor.close();

		return result;
	}

	public static boolean checkCurrentSoftwareDownloadState(Context context,
			String software_name) {
		boolean result = false;
		DownloadDatabase downloadDatabase = DatabaseManager
				.getDownloadDatabase(context);
		Cursor temCursor = downloadDatabase.queryID(software_name);
		int returnvalue = 0;
		int size = temCursor.getCount();
		temCursor.moveToFirst();
		if (size > 0) {
			returnvalue = temCursor.getInt(temCursor
					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
			if (returnvalue != 0) {
				result = true;
			}
		}
		temCursor.close();

		return result;
	}

	public static boolean isExistActivity(String packageName,
			String activityName, Activity activity) {
		Intent intent = new Intent();
		intent.setClassName(packageName, activityName);
		if (activity.getPackageManager().resolveActivity(intent, 0) == null)
			return false;
		else
			return true;
	}

	public static boolean checkSoftwareHasUpdate(Context context,
			String packagename, String oldversion) {
		boolean flag = false;
		int size = Util.updateManagerDatas.size();
		if (size > 0) {
			int position = -1;
			for (int i = 0; i < size; i++) {
				if (packagename.equals(Util.updateManagerDatas.get(i)
						.getPackageName())) {
					position = i;
					break;
				}
			}

			if (position != -1) {
				if (Util.compareVersions(Util.updateManagerDatas.get(position)
						.getVersion(), oldversion)) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 检测当前软件是否正在下载当中
	 */
	public static boolean checkIsDownloading(Context context,
			String software_name) {
		DownloadDatabase downloadDatabase = DatabaseManager
				.getDownloadDatabase(context);
		boolean value = false;
		try {
			Cursor cursor = downloadDatabase.queryID(software_name);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int downnum = cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
				if (downnum != 100 && downnum != -1) {
					value = true;
				}
			}
			cursor.close();
		} catch (Exception e) {
		}
		return value;
	}

	public static int getItemNameWidth(Context context, boolean port) {

		DisplayMetrics localDisplayMetrics = context.getResources()
				.getDisplayMetrics();
		int width = localDisplayMetrics.widthPixels;
		int height = localDisplayMetrics.heightPixels;
		// Util.print("land", height+"");
		// Util.print("land1", width+"");
		if (port)
			return (240 * width / height);
		else
			return (200 * width / width);

	}

	public static boolean checkCurrentDirection(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return false;
		}
		return false;
	}

	public static void sendImei(final Context context,final String packagename) {


		try {
			String[] key = { "imei", "productID", "mobiletype", "imsi", "n", "version" };
			String imei = Util.getDeviceID(context);
			String imsi = Util.getIMsI(context);
			String model = Util.getMODEL(context);
			String[] value = {
					imei,
					WebAddress.getproductID(context),
					model,
					imsi,
					Util.getAPNType(context),
					context.getPackageManager().getPackageInfo(packagename, 0).versionName };
			CommitDownloadNum(
					context,
					SharedPrefsUtil.getValue(context, "DomainName",
							WebAddress.comString) + WebAddress.send_IMEI, key,
					value);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	public static String getIMsI(Context context) {
		String imsi = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		imsi = telephonyManager.getSubscriberId();
		return imsi;
	}

	/**
	 * 手机型号
	 * 
	 * @param context
	 * @return
	 */
	public static String getMODEL(Context context) {
		String model = null;
		model = android.os.Build.MODEL;
		return model;

	}

	/**
	 * 获取手机内部存储总量
	 * 
	 * @return
	 */
	public static float getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 获取手机内部剩余存储空间
	 * 
	 * @return
	 */
	public static float getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * SD卡总量
	 * 
	 * @return
	 */
	public static float getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}
	
	
	
	public static String getSDPath(Context context) {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.getPath();
		} else {
			return null;
		}

	}

	/**
	 * SD卡剩余容量
	 * 
	 * @return
	 */

	public static float getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	/**
	 * 判断SD卡存在
	 * 
	 * @return
	 */
	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 大小
	 * 
	 * @param length
	 * @return
	 */
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	}

	public static boolean is24HourFormat(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if (strTimeFormat != null) {
			if (strTimeFormat.equals("24")) {
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	public static boolean checkNetworkIsActive(Context context) {
		boolean mIsNetworkUp = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			mIsNetworkUp = info.isAvailable();
		}
		return mIsNetworkUp;
	}

	public static void initNotificationProgress(Context context,
			String software_name, int Noti) {
		// 初始化通知
		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(
				android.R.drawable.stat_sys_download, "正在下载",
				System.currentTimeMillis());

		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.init_notification);

		notification.contentView.setTextViewText(R.id.progress_software_name,
				software_name);

		// 设置点击通知跳转的方向
		Intent intent = new Intent(context, InformationTabHost.class);
		intent.putExtra("downloading", true);
		intent.putExtra("swid", Noti + "");
		intent.putExtra("name", software_name);
		// 第一次发送通知
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!software_name.equals("乐酷市场"))
			notification.contentIntent = PendingIntent.getActivity(
					context.getApplicationContext(), Noti, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		else {
			notification.contentIntent = PendingIntent.getActivity(
					context.getApplicationContext(), Noti, null, 0);
		}
		notificationManager.notify(Noti, notification);
	}

	public static void networkFailedNotification(Context context,
			String software_name, int Noti, String message) {
		// 初始化通知
		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification ni = new Notification(
				android.R.drawable.stat_sys_download_done, "下载停止",
				System.currentTimeMillis());
		ni.flags |= Notification.FLAG_AUTO_CANCEL;
		
		
		Intent intent = new Intent(context,MainTabHost.class);
		intent.putExtra("topFloorPosition", 8);//跳转到下载管理页面设置为8
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ni.flags |= Notification.FLAG_AUTO_CANCEL;

		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, software_name.equals("乐酷市场")?null:intent, 0);

		ni.setLatestEventInfo(context, software_name, message, pIntent);
		notificationManager.notify(Noti, ni);

	}
	
	public static void clearAllExceptionNotification(final Context context)
	{
		
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					NotificationManager notificationManager = (NotificationManager) context
			           .getSystemService(Context.NOTIFICATION_SERVICE);
			
			DownloadDatabase downloadDatabase = DatabaseManager.getDownloadDatabase(context);
			Cursor cursor = downloadDatabase.queryAllData();
			while(cursor.moveToNext())
			{
				if(cursor.getInt(cursor.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION))==-3)
				{
					String iconpath = cursor.getString(cursor
							.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
			        String str = iconpath.trim();
			        String find = "software".trim();
			        int index = str.indexOf(find);
			        String tmpStr = "";
			        if (index > -1) {
				         tmpStr = str.substring(index+ find.length());
			        }
			        String swid = tmpStr;
			        int Noti = Integer.parseInt(swid);
					notificationManager.cancel(Noti);
				}
			}
			cursor.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	

	public static View getListView(Context mContext) {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		return layoutInflater.inflate(R.layout.commom_listview_item, null);
	}
	
	
	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
