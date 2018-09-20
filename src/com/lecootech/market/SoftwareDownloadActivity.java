package com.lecootech.market;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecootech.market.adapter.SoftwareDownloadedAdapter;
import com.lecootech.market.adapter.SoftwareDownloadingAdapter;
import com.lecootech.market.adapter.SoftwareManagerAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.DatabaseManager;
import com.lecootech.market.database.DownloadDatabase;

public class SoftwareDownloadActivity extends Activity {
	/** Called when the activity is first created. */
	private ImageView manager_image;
	private LinearLayout layoutlistView;
	private ListView listView;
	// private ProgressBar main_progressbar;

	private SoftwareDownloadingAdapter softwareDownloadingAdapter;
	private SoftwareDownloadedAdapter softwareDownloadedAdapter;

	// top layout background
	private TextView Left;
	private TextView Right;
	
	private RelativeLayout layoutLeft;
	private RelativeLayout layoutRight;

	public static boolean stop = true;
	private ActivityReceiver activityReceiver;

	private int downloadingSize = 0;
	private int downloadedSize = 0;

	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();
	private ArrayList<ThridFloorData> downloadedFloorDatas = new ArrayList<ThridFloorData>();
	private SoftwareManagerAdapter softwareManagerAdapter;

	private Cursor cursor;
	private DownloadDatabase downloadDatabase;
	// has update
	// private ArrayList<ManagerFloorData> hasUpdateDatas = null;

	public final static int UN_INSTALL = 1;
	// page flag
	private int Currentposition = 1; // 1正在下载，2等待安装

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_download);

		downloadDatabase = DatabaseManager.getDownloadDatabase(this);
		try {
			notificationCancel(1022);
			// main_progressbar = (ProgressBar)
			// findViewById(R.id.main_progressbar);
			// 初始化listview
			layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);
			listView = new ListView(SoftwareDownloadActivity.this);
			listView.setDivider(getResources().getDrawable(
					R.drawable.gallery_diver));
			listView.setVerticalFadingEdgeEnabled(false);
			layoutlistView.addView(listView, ApplicationConstants.LP_FF);
			manager_image = (ImageView) findViewById(R.id.manager_image);
			
			layoutLeft = (RelativeLayout)findViewById(R.id.layout_installed);
			Left = (TextView) findViewById(R.id.installed);
			Left.setText("正在下载");
			layoutLeft.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
			layoutLeft.setOnClickListener(onClickListener);

			layoutRight = (RelativeLayout)findViewById(R.id.layout_has_update);
			Right = (TextView) findViewById(R.id.has_update);
			Right.setText("等待安装");
			layoutRight.setOnClickListener(onClickListener);

			// 注册广播
			IntentFilter filter = new IntentFilter(
					"com.lecootech.market.updateNum");
			activityReceiver = new ActivityReceiver();
			registerReceiver(activityReceiver, filter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void notificationCancel(int swid) {
		NotificationManager notificationManager = (NotificationManager) SoftwareDownloadActivity.this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(swid);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(activityReceiver);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration", "con");
//		try {
//			softwareManagerAdapter.changeItemNameWidthFlag(Util
//					.checkCurrentDirection(this));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		super.onConfigurationChanged(newConfig);
	}

	public void main() {
		downloadedSize = 0;
		downloadingSize = 0;
		thridFloorDatas.clear();
		downloadedFloorDatas.clear();
		try {
			if (Currentposition == 1) // 加载正在下载的数据
			{
				setDownloadingListData();
				
				if (downloadingSize > 0) {
					Left.setText("正在下载(" + downloadingSize + ")");
					manager_image.setVisibility(View.INVISIBLE);
				} else {
					Left.setText("正在下载(0)");
					manager_image.setVisibility(View.VISIBLE);
				}
				
				if (downloadedSize > 0) {
					Right.setText("等待安装(" + downloadedSize + ")");
//					manager_image.setVisibility(View.INVISIBLE);
				} else {
					Right.setText("等待安装(0)");
//					manager_image.setVisibility(View.VISIBLE);
				}

			} else if (Currentposition == 2) // 加载已下载的数据
			{
				setDownloadedListData();
				if (downloadedSize > 0) {
					Right.setText("等待安装(" + downloadedSize + ")");
					manager_image.setVisibility(View.INVISIBLE);
				} else {
					Right.setText("等待安装(0)");
					manager_image.setVisibility(View.VISIBLE);
				}
			}

			listView.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Util.print("resume", "resume");
		stop = false;
		try {
//			if (Currentposition == 1) // 加载正在下载的数据
//			{
//				setDownloadingListData();
//                Util.print("999", "999"+downloadingSize);
//				if (downloadingSize > 0) {
//					Left.setText("正在下载(" + downloadingSize + ")");
//					manager_linear.setVisibility(View.INVISIBLE);
//				} else {
//
//					Left.setText("正在下载(0)");
//					manager_linear.setVisibility(View.VISIBLE);
//				}
//				
//				
//				setDownloadedListData();
//
//				if (downloadedSize > 0) {
//					Right.setText("等待安装(" + downloadedSize + ")");
//					manager_linear.setVisibility(View.INVISIBLE);
//				} else {
//					Right.setText("等待安装(0)");
//					manager_linear.setVisibility(View.VISIBLE);
//				}
//				
//			} else if (Currentposition == 2) // 加载已下载的数据
//			{
//				setDownloadedListData();
//
//				if (downloadedSize > 0) {
//					Right.setText("等待安装(" + downloadedSize + ")");
//					manager_linear.setVisibility(View.INVISIBLE);
//				} else {
//					Right.setText("等待安装(0)");
//					manager_linear.setVisibility(View.VISIBLE);
//				}
//			}
			main();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		stop = true;
	}

	public void updateNumDisplay() {
		Util.print("updateNumDisplay", "updateNumDisplay");
		if (Currentposition == 1) {
			int size = softwareDownloadingAdapter.thridFloorDatas.size();
			if (size > 0) {
				Left.setText("正在下载(" + size + ")");
				manager_image.setVisibility(View.INVISIBLE);
			} else {
				Left.setText("正在下载(0)");
				manager_image.setVisibility(View.VISIBLE);
			}
		} else if (Currentposition == 2) {
			int size = softwareDownloadedAdapter.thridFloorDatas.size();
			if (size > 0) {
				Right.setText("等待安装(" + size + ")");
				manager_image.setVisibility(View.INVISIBLE);
			} else {
				Right.setText("等待安装(0)");
				manager_image.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * @加载正在下载中的数据列表
	 */
	public void setDownloadingListData() {
		try {
			cursor = downloadDatabase.queryAllData();
			ThridFloorData data;
			while (cursor.moveToNext()) {
				if (cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) != 100) {
					data = new ThridFloorData();
					// icon的路径
					String iconpath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
					data.setImagePath(iconpath);
					// 软件名字
					String softwareName = cursor.getString(cursor
							.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
					data.setName(softwareName);
					// 软件存储的路径
					String filepath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_WEBPATH));
					data.setDownloadPath(filepath);

					String tempswid = getSwid(iconpath);
					data.setSwid(tempswid);

					String software_size = cursor.getString(cursor
							.getColumnIndexOrThrow(DownloadDatabase.KEY_SIZE));
					data.setSize(software_size);

					if (!"乐酷市场".equals(softwareName)) {
						downloadingSize++;
						thridFloorDatas.add(data);
					}

				}
				else {
					downloadedSize++;
				}
			}
			cursor.close();
			
			
			softwareDownloadingAdapter = new SoftwareDownloadingAdapter(this,
					SoftwareDownloadActivity.this, thridFloorDatas,
					Util.checkCurrentDirection(SoftwareDownloadActivity.this),
					listView);
			if (Currentposition == 1) {

				if (softwareDownloadingAdapter.isEmpty()) {
					manager_image.setVisibility(View.VISIBLE);
				} else {
					manager_image.setVisibility(View.INVISIBLE);
				}
				listView.setAdapter(softwareDownloadingAdapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						try {
							Intent intent;
							intent = new Intent(SoftwareDownloadActivity.this,
									InformationTabHost.class);
							intent.putExtra("downloading", true);
							intent.putExtra("swid", thridFloorDatas.get(arg2)
									.getSwid());
							intent.putExtra("name", thridFloorDatas.get(arg2)
									.getName());
							startActivity(intent);

						} catch (Exception e) {
						}

					}

				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public String getSwid(String path) {
		String swid = path.substring(path.lastIndexOf("/") + 9, path.length());
		return swid;
	}

	/**
	 * @加载已经下载的列表数据
	 */
	public void setDownloadedListData() {
		try {
			cursor = downloadDatabase.queryAllData();
			int size = cursor.getCount();
			Util.print("size", size + "");
			ThridFloorData data;
			while (cursor.moveToNext()) {

				if (cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DownloadDatabase.KEY_DOWNLOND_NUM)) == 100) {
					String filepath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(DownloadDatabase.KEY_FILEPATH));
					Util.print("file", filepath);
					File file = new File(filepath);
					if (file.exists()) {
						data = new ThridFloorData();

						String softwareName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
						data.setName(softwareName);

						String iconpath = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_ICON_PATH));
						data.setImagePath(iconpath);

						data.setDownloadPath(filepath);

						String software_size = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_SIZE));
						data.setSize(software_size);

						downloadedFloorDatas.add(data);
						downloadedSize++;

					} else {
						String name = cursor
								.getString(cursor
										.getColumnIndexOrThrow(DownloadDatabase.KEY_NAME));
						downloadDatabase.deleteData(name);

					}
				}
			}
			cursor.close();
			softwareDownloadedAdapter = new SoftwareDownloadedAdapter(this,
					SoftwareDownloadActivity.this, downloadedFloorDatas,
					Util.checkCurrentDirection(SoftwareDownloadActivity.this));
			if (Currentposition == 2) {
				if (softwareDownloadedAdapter.isEmpty()) {
					manager_image.setVisibility(View.VISIBLE);
				} else {
					manager_image.setVisibility(View.INVISIBLE);
				}
				listView.setAdapter(softwareDownloadedAdapter);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			new AlertDialog.Builder(this.getParent())
					.setIcon(R.drawable.report_error)
					.setTitle(getResources().getString(R.string.exit_title))
					.setMessage(getResources().getString(R.string.exit_message))
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Util.print("finish_main", "finish main");
									finish();
									SharedPrefsUtil.delValue(
											SoftwareDownloadActivity.this,
											"DomainName");
									// android.os.Process
									// .killProcess(android.os.Process
									// .myPid());
									// System.exit(0);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.layout_installed:
					Currentposition = 1;
					layoutLeft.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
					layoutRight.setBackgroundColor(Color.TRANSPARENT);
					listView.setVisibility(View.INVISIBLE);
					main();
					break;
				case R.id.layout_has_update:
					Currentposition = 2;
					layoutRight.setBackgroundResource(R.drawable.manager_top_bar_bg_selected);
					layoutLeft.setBackgroundColor(Color.TRANSPARENT);
					listView.setVisibility(View.INVISIBLE);
					main();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 内部广播接收器，用于处理适配器里发来的重新查询数据
	 * 
	 * @author carter
	 * 
	 */
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String flag = intent.getStringExtra("update");
			if (flag.equals("updateCount")) {
				updateNumDisplay();
			} else if (flag.equals("updateList")) {
				main();
			}
		}
	}
	

}