package com.lecootech.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lecootech.market.adapter.SoftwareSecondFloorAdapter;
import com.lecootech.market.common.ApplicationConstants;
import com.lecootech.market.common.SharedPrefsUtil;
import com.lecootech.market.common.Util;
import com.lecootech.market.common.WebAddress;
import com.lecootech.market.data.SecondFloorData;
import com.lecootech.market.handle.DataSet;

public class SoftwareCategoryActivity extends Activity {

	private LinearLayout layoutlistView;
	private ListView listView=null;
	private ProgressBar main_progressbar;
	private SoftwareSecondFloorAdapter secondFloorAdapter = null;
	private ArrayList<SecondFloorData> secondFloorDatas = null;

	private int count = 0;
	private int displayHasUsedHeight = 40 + 60 + 35;

	//network failed 
	private RelativeLayout networklayout = null;
	
	// page flag
	private int Currentposition = 1;

	// dataset
	private DataSet dataSet;

	/**
	 * @author zhaweijin
	 * @function UI线程的处理
	 */
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case ApplicationConstants.main:
					
					if(networklayout!=null)
						networklayout.setVisibility(View.INVISIBLE);
					
					Util.print("adepter",
							"" + Boolean.toString(secondFloorAdapter == null));
					secondFloorAdapter = new SoftwareSecondFloorAdapter(
							SoftwareCategoryActivity.this,
							SoftwareCategoryActivity.this,
							dataSet,
							Util.checkCurrentDirection(SoftwareCategoryActivity.this),listView);
					listView.setAdapter(secondFloorAdapter);
					main_progressbar.setVisibility(View.INVISIBLE);
					break;
				case ApplicationConstants.update:
					secondFloorAdapter.notifyDataSetChanged();
					// if(count>=ApplicationConstants.perPageSize)
					// listView.removeFooterView(progressView);
					break;
				case ApplicationConstants.webTimeout:
					networkException(getResources().getString(
							R.string.web_load_timeout));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Util.print("thread error", "thread error");
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.software_main);

		// Util.addActivityToStack(this, "SoftwareCategoryActivity");
		// Util.print("oncreate", "oncreate");
		// Currentposition = getIntent().getIntExtra("position",1);
		// Util.print("CU", ""+Currentposition);
		// init();
		//
		// main();

		Util.print("oncreate", "oncreate");
		Currentposition = getIntent().getIntExtra("position", 1);
		Util.print("cu", Currentposition + "");
		init();

		main();
	}

	public void init() {
		// progressDialog = Util.getProgressDialog(this.getParent(),"加载中...");
		main_progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
        
		// 初始化listview
		layoutlistView = (LinearLayout) findViewById(R.id.layout_listview);
		listView = new ListView(SoftwareCategoryActivity.this);
		layoutlistView.addView(listView, ApplicationConstants.LP_FF);
		listView.setDivider(getResources().getDrawable(R.drawable.gallery_diver));
		listView.setVerticalFadingEdgeEnabled(false);

		listView.setOnItemClickListener(onItemClickListener);

	}

	private DataSet setAdapterData() {
		try {
				if (Currentposition == 1) {
					dataSet = Util
							.getSoftwareWebData(this,SharedPrefsUtil.getValue(
									SoftwareCategoryActivity.this, "DomainName",
									WebAddress.comString)+WebAddress.cagegoryApplication);
				} else if (Currentposition == 2) {
					dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
							SoftwareCategoryActivity.this, "DomainName",
							WebAddress.comString)+WebAddress.categoryGame);
				} else if (Currentposition == 4) {
					dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
							SoftwareCategoryActivity.this, "DomainName",
							WebAddress.comString)+WebAddress.categorySubject);
		        } else if (Currentposition == 3) {
			        dataSet = Util.getSoftwareWebData(this,SharedPrefsUtil.getValue(
					        SoftwareCategoryActivity.this, "DomainName",
					        WebAddress.comString)+WebAddress.fiction_category);
		        }
			
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Util.navigationEvent(SoftwareCategoryActivity.this, 3);
		// //设置listview的高度
		// Util.setListViewHeight(SoftwareCategoryActivity.this, listView,
		// displayHasUsedHeight);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Util.print("cofiguration2", "con2");
		try {
			// Util.setListViewHeight(SoftwareCategoryActivity.this, listView,
			// displayHasUsedHeight);
			// secondFloorAdapter.changeItemNameWidthFlag(Util.checkCurrentDirection(this));
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onConfigurationChanged(newConfig);
	}

	public void main() {
		// if(Util.getNetworkState(SoftwareCategoryActivity.this))
		// {
		main_progressbar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			public void run() {
				try {

					if (setAdapterData() != null)
						myHandler.sendEmptyMessage(ApplicationConstants.main);
					else
						myHandler
								.sendEmptyMessage(ApplicationConstants.webTimeout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		// }
		// else {
		// networkException(getResources().getString(R.string.web_cannot_connection));
		// }
	}

	public void networkException(String title) {

		main_progressbar.setVisibility(View.INVISIBLE);
		networklayout = (RelativeLayout)findViewById(R.id.include_network_failed);
		networklayout.setVisibility(View.VISIBLE);
		
		
		Button flashButton = (Button)findViewById(R.id.reflash);
		flashButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.print("reflash", "reflash");
				networklayout.setVisibility(View.INVISIBLE);
				main();
			}
		});
	}

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				if (Currentposition == 4 && arg2 == 0) {
					Intent intent = new Intent(SoftwareCategoryActivity.this,
							SoftwareInstallActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else if (Currentposition == 1 || Currentposition == 2 || Currentposition == 3) {
				
					Intent intent = new Intent(SoftwareCategoryActivity.this,
							SoftwareThridFloorTabHost.class);
					intent.putExtra("name", dataSet.getSecondCates().get(arg2)
							.getName());
					intent.putExtra("ID", dataSet.getSecondCates().get(arg2)
							.getCategoryID());
					intent.putExtra("flag", "normal");
					startActivity(intent);
				}else {
					Intent intent = new Intent(SoftwareCategoryActivity.this,
							SoftwareThridFloorActivity.class);
					intent.putExtra("name", dataSet.getSecondCates().get(arg2)
							.getName());
					intent.putExtra("ID", dataSet.getSecondCates().get(arg2)
							.getCategoryID());
					intent.putExtra("flag", "normal");
					startActivity(intent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

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
									SharedPrefsUtil.delValue(SoftwareCategoryActivity.this, "DomainName");
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