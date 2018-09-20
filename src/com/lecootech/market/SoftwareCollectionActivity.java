package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lecootech.market.adapter.SoftwareCollectionAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.data.MessageControl;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.database.CollectionDatabase;
import com.lecootech.market.database.DatabaseManager;

public class SoftwareCollectionActivity extends Activity {
	/** Called when the activity is first created. */

	private LinearLayout layoutlistView;
	private ListView listView;
	private ProgressBar main_progressbar;
//	private TextView colle_txt;
//	private ImageView managerImageView;
	private LinearLayout manager_linear;

	private boolean secondLoading = false;
	private ArrayList<ThridFloorData> thridFloorDatas = new ArrayList<ThridFloorData>();
	private ArrayList<ThridFloorData> tempThridFloorDatas = new ArrayList<ThridFloorData>();
	private SoftwareCollectionAdapter softwareCollectionAdapter;

	private CollectionDatabase collectionDatabase;
	private Cursor cursor;
	private int currentSelectPosition=0;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				switch (msg.what) {
				case ApplicationConstants.main:
					Util.print("main", ""+thridFloorDatas.size());
					softwareCollectionAdapter = new SoftwareCollectionAdapter(
							SoftwareCollectionActivity.this,
							SoftwareCollectionActivity.this,
							thridFloorDatas,
							Util.checkCurrentDirection(SoftwareCollectionActivity.this),
							listView,this);
					listView.setAdapter(softwareCollectionAdapter);
					if (softwareCollectionAdapter.isEmpty()) {
						Util.print("empty", "empth");
						manager_linear.setVisibility(View.VISIBLE);
						listView.setVisibility(View.INVISIBLE);
					}
					else {
						manager_linear.setVisibility(View.INVISIBLE);
						listView.setVisibility(View.VISIBLE);
					}
					handler.sendEmptyMessage(ApplicationConstants.cancel);
					break;
				case ApplicationConstants.cancel:
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case MessageControl.ADAPTER_CHANGE:
					softwareCollectionAdapter.notifyDataSetChanged();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_main);

		collectionDatabase = DatabaseManager.getCollectionDatabase(this);
		try {
			// Util.addActivityToStack(this, "SoftwareManagerActivity");

			// progressDialog =
			// Util.getProgressDialog(SoftwareManagerActivity.this,"加载中...");
			main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
//			colle_txt = (TextView) findViewById(R.id.colle_txt);
//			managerImageView = (ImageView)findViewById(R.id.manager_image);
			manager_linear = (LinearLayout)findViewById(R.id.manager_linear);
			// 初始化listview
			layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);
			listView = new ListView(SoftwareCollectionActivity.this);
			listView.setDivider(getResources().getDrawable(R.drawable.gallery_diver));
			listView.setVerticalFadingEdgeEnabled(false);
			layoutlistView.addView(listView, ApplicationConstants.LP_FF);

			listView.setOnItemClickListener(onItemClickListener);
			// main();
		} catch (Exception e) {
			e.printStackTrace();
		}
        main();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration", "con");
		try {
			softwareCollectionAdapter.changeItemNameWidthFlag(Util
					.checkCurrentDirection(this));
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onConfigurationChanged(newConfig);
	}

	public void setData() {

		try {
			cursor = collectionDatabase.queryAllData();
			ThridFloorData data;
			int size = cursor.getCount();
			Util.print("collection_size", ""+size);
			if (size > 0) {
				while (cursor.moveToNext()) {
					data = new ThridFloorData();
					String swid = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_SWID));
					data.setSwid(swid);
					Util.print("swid", swid);
					String name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_NAME));
					data.setName(name);
					Util.print("name", name);
					String downpath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_DOWNLOAD_PATH));
					data.setDownloadPath(downpath);
					Util.print("downpath", downpath);
					String imagepath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_IMAGE_PATH));
					data.setImagePath(imagepath);
					Util.print("imagepath", imagepath);
					String modifydata = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_DATE));
					data.setModifyData(modifydata);
					Util.print("modifydata", modifydata);
					String packagename = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_PACKAGE));
					data.setPackageName(packagename);
					Util.print("package", packagename);

					String software_size = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_SIZE));
					data.setSize(software_size);
					
					String software_version = "";
					try {
						if(cursor.getColumnIndexOrThrow(CollectionDatabase.KEY_VERSION) != -1)
						{
							software_version = cursor.getString(cursor
									.getColumnIndexOrThrow(CollectionDatabase.KEY_VERSION));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					data.setVersion(software_version);
					
					thridFloorDatas.add(data);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void main() {
		main_progressbar.setVisibility(View.VISIBLE);
		thridFloorDatas.clear();
		new Thread(new Runnable() {

			public void run() {
				try {

					setData();
					handler.sendEmptyMessage(ApplicationConstants.main);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			 if(secondLoading)
			 {
				Cursor tempCursor;
				if(thridFloorDatas.size()>0)
				{
					tempCursor = collectionDatabase.queryOneData(thridFloorDatas.get(currentSelectPosition).getName());
					int count = tempCursor.getCount();
					if(count<=0)
					{
						Util.print("remove one", "remove one");
						thridFloorDatas.remove(currentSelectPosition);
					}
					tempCursor.close();
				}

				
				tempCursor = collectionDatabase.queryAllData();				
				tempThridFloorDatas.addAll(thridFloorDatas);
				ThridFloorData data;
				int size = tempCursor.getCount();
				Util.print("resume_collection_size", ""+size);
				int realsize = 0;
				if (size > 1) {
					while (tempCursor.moveToNext()) {
						
						String tempname = tempCursor
						.getString(tempCursor
								.getColumnIndexOrThrow(CollectionDatabase.KEY_NAME));
						if(!checkCurrentSoftwareIsExistList(tempname))
						{
							data = new ThridFloorData();
							String swid = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_SWID));
							data.setSwid(swid);
							Util.print("swid", swid);
							String name = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_NAME));
							data.setName(name);
							Util.print("name", name);
							String downpath = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_DOWNLOAD_PATH));
							data.setDownloadPath(downpath);
							Util.print("downpath", downpath);
							String imagepath = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_IMAGE_PATH));
							data.setImagePath(imagepath);
							Util.print("imagepath", imagepath);
							String modifydata = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_DATE));
							data.setModifyData(modifydata);
							Util.print("modifydata", modifydata);
							String packagename = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_PACKAGE));
							data.setPackageName(packagename);
							Util.print("package", packagename);

							String software_size = tempCursor
									.getString(tempCursor
											.getColumnIndexOrThrow(CollectionDatabase.KEY_SIZE));
							data.setSize(software_size);
							realsize++;
							thridFloorDatas.add(data);
						}
					}
					
					tempCursor.close();
					tempThridFloorDatas.clear();
					Util.print("realsize", realsize+"");
					Util.print("sssssss", thridFloorDatas.size()+"");
					softwareCollectionAdapter.notifyDataSetChanged();
					
				}
				else 
				{
					main();
				}
				Util.print("temp thrid size", tempThridFloorDatas.size()+"");
			 }
			 secondLoading = true;
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				Intent intent = new Intent(SoftwareCollectionActivity.this,
						InformationTabHost.class);
				if (Util.checkCurrentSoftwareDownloading(
						SoftwareCollectionActivity.this,
						thridFloorDatas.get(arg2).getName()))
					intent.putExtra("downloading", true);
				// Util.print("swid-->name",
				currentSelectPosition = arg2;
				// thridFloorDatas.get(arg2).getSwid()+"##"+thridFloorDatas.get(arg2).getName());
				intent.putExtra("swid", thridFloorDatas.get(arg2).getSwid());
				intent.putExtra("name", thridFloorDatas.get(arg2).getName());
				// Util.print("categoryID",
				// thridFloorDatas.get(arg2).getCategoryID());
				startActivity(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};
	
	//exist return true
	public boolean checkCurrentSoftwareIsExistList(String name)
	{
		boolean result = false;
		int size = tempThridFloorDatas.size();
		for(int i=0;i<size;i++)
		{
			if(tempThridFloorDatas.get(i).getName().equals(name))
			{
				result = true;
				break;
			}
		}
		return result;
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
									SharedPrefsUtil.delValue(SoftwareCollectionActivity.this, "DomainName");
//									android.os.Process
//											.killProcess(android.os.Process
//													.myPid());
//									System.exit(0);
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

}