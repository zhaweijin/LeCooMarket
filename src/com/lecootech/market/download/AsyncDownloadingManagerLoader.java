package com.lecootech.market.download;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.lecootech.market.SoftwareDownloadActivity;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.DownloadManagerSendData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

/**
 * @author zhaweijin
 * 加载正在下载的软件及壁纸进度条
 * @ DownloadManagerSendData  传进来的数据
 * @ Integer 传出去的实时值
 */
public class AsyncDownloadingManagerLoader {

	private HashMap<DownloadManagerSendData, SoftReference<Integer>> progressValue;
	private static DownloadDatabase downloadDatabase;
	private SharedPreferences settingPreferences;

	public AsyncDownloadingManagerLoader(Context context) {

		progressValue = new HashMap<DownloadManagerSendData, SoftReference<Integer>>();
		downloadDatabase = DatabaseManager.getDownloadDatabase(context);
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public Integer loadInteger(final DownloadManagerSendData sendData,
			final ImageCallback imageCallback, Boolean again) {

		if (!again) {
			if (progressValue.containsKey(sendData)) {
				SoftReference<Integer> softReference = progressValue.get(sendData);
				Integer Integer = softReference.get();
				if (Integer != null) {
					return Integer;
				}
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Integer) message.obj, sendData);
			}
		};
		new Thread() {
			@Override
			public void run() {
				try {
					
					Activity activity = sendData.getActivity();
					while (!activity.isFinishing() && !SoftwareDownloadActivity.stop) {
						if (checkIsBreak(sendData.getSwid())) {
							break;
						}

						Integer Integer = getDownloadNum(sendData);
						if (Integer == -1) {
							break;
						} else {
							if (Integer != 100) {
								Thread.sleep(1000);
							}
							if (Integer != -1) {
								progressValue.put(sendData,new SoftReference<Integer>(Integer));
								Message message = handler.obtainMessage(0,Integer);
								handler.sendMessage(message);
							} else {
								break;
							}
						}

						if (Integer == 100) {
							break;
						}
					}
					Util.print("threadbreak", "threadbreak");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	/*
	 * @DownloadManagerSendData 传进来的数据对象 通过传进来的数据对象，返回当前正在下载的进度值
	 */
	public static Integer getDownloadNum(DownloadManagerSendData sendData) {

		Cursor temCursor = null;
		int returnvalue = -1;
		int size;
		String software_name = sendData.getName();
//		Context context = sendData.getContext();

		int tempflag = sendData.getFlag();
		if (tempflag == 1) {
			temCursor = downloadDatabase.queryID(software_name);
			size = temCursor.getCount();
			temCursor.moveToFirst();
			if (size != 0) {
				returnvalue = temCursor
						.getInt(temCursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM));
			}
		}
		temCursor.close();
		return returnvalue;

	}

	/*
	 * @software_name 软件名称 根据软件名称查找数据库 operation 状态，如果不为零那么返回true
	 */
//	public boolean checkIsBreak(String software_name) {
//		boolean returnvalue = false;
//		Cursor cursor = null;
//		int size;
//		int operation = 0;
//		cursor = downloadDatabase.queryID(software_name);
//		cursor.moveToFirst();
//		size = cursor.getCount();
//		if (size != 0) {
//			operation = cursor.getInt(cursor
//					.getColumnIndexOrThrow(DownloadDatabase.KEY_OPERATION));
//			if (operation != 0) {
//				Util.print("break", "break");
//				returnvalue = true;
//			}
//		} else {
//			Util.print("break", "break");
//			returnvalue = true;
//		}
//
//		cursor.close();
//		return returnvalue;
//	}

	public boolean checkIsBreak(String swid) {
		boolean returnvalue = false;
		if(!settingPreferences.getBoolean(swid, false))
		{
			returnvalue = true;
		}
		return returnvalue;
	}
	
	public interface ImageCallback {
		public void imageLoaded(Integer imageInteger,DownloadManagerSendData sendData);
	}

}
